#!/usr/bin/python3

# Android surface frame stats collector for Crosswalk/native performance
# comparison. Loosely based on build/android/surface_stats.py and
# build/android/pylib/perf/surface_stats_collector.py from Chromium 38.
#
# Copyright 2013 The Chromium Authors. All rights reserved.
# Copyright 2014 Intel Corporation. All rights reserved.
# License: BSD-3-clause-Google and BSD-3-clause-Intel, see LICENSE.txt

import argparse
import math
import statistics
import subprocess
import time

PENDING_FENCE_TIMESTAMP = (1 << 63) - 1

MS_PER_SECOND = 1000

US_PER_MS = 1000
US_PER_SECOND = (1000 ** 2)

NS_PER_US = 1000
NS_PER_MS = (1000 ** 2)
NS_PER_SECOND = (1000 ** 3)

NAN = float('NaN')

def mean(xs):
    if not xs:
        return NAN
    return statistics.mean(xs)

def median(xs):
    if not xs:
        return NAN
    return statistics.median(xs)

def min_(xs):
    if not xs:
        return NAN
    return min(xs)

def max_(xs):
    if not xs:
        return NAN
    return max(xs)

def pstdev(xs, mu=None):
    if not xs:
        return 0
    return statistics.pstdev(xs, mu)

def debug(s):
    #print(s)
    pass

class FrameInfo(object):
    __slots__ = ('app_started_to_draw_ns', 'vsync_before_submit_ns',
            'after_submit_ns', 'refresh_period_ns')

    def __init__(self, app_started_to_draw_ns, vsync_before_submit_ns,
            after_submit_ns, refresh_period_ns):
        # The time at which application code started to draw this frame.
        self.app_started_to_draw_ns = app_started_to_draw_ns

        # The last vsync before SurfaceFlinger submitted this frame
        # to the hardware.
        self.vsync_before_submit_ns = vsync_before_submit_ns

        # The time at which SurfaceFlinger finished submitting this
        # frame to the hardware. If this is after
        # (vsync_before_submit_ns + refresh_period_ns) then we have
        # missed our deadline.
        self.after_submit_ns = after_submit_ns

        # The time between vsyncs
        self.refresh_period_ns = refresh_period_ns

    @property
    def frame_latency_ns(self):
        """The total time to draw the frame, starting when application code
        started to draw, and ending when the frame is submitted to the
        hardware.

        Every time ceil(this / refresh_period_ns) changes, we have a "jank".
        """
        return self.after_submit_ns - self.app_started_to_draw_ns

    @property
    def vsync_before_draw_ns(self):
        """The last vsync before application code started drawing this frame.
        """
        prev_vsync = self.vsync_before_submit_ns
        while prev_vsync > self.app_started_to_draw_ns:
            prev_vsync -= self.refresh_period_ns
        return prev_vsync

    @property
    def vsync_after_submit_ns(self):
        """The next vsync before SurfaceFlinger finished submitting this
        frame to the hardware.
        """
        next_vsync = self.vsync_before_submit_ns

        while self.after_submit_ns < next_vsync:
            # FIXME: this makes no sense!
            next_vsync -= self.refresh_period_ns

        next_vsync = self.vsync_before_submit_ns
        while next_vsync < self.after_submit_ns:
            next_vsync += self.refresh_period_ns
        return next_vsync

class LatencyBatch(object):
    __slots__ = ('refresh_period_ns', 'frames')

    def __init__(self, refresh_period_ns):
        self.refresh_period_ns = refresh_period_ns
        self.frames = []

def clear_latency(surface):
    subprocess.call(['adb', '-d', 'shell', 'dumpsys', 'SurfaceFlinger',
        '--latency-clear', surface])

def dump_latency(surface, inp=None):
    if inp is None:
        proc = subprocess.Popen(['adb', '-d', 'shell', 'dumpsys', 'SurfaceFlinger',
            '--latency', surface], stdout=subprocess.PIPE)
        stdout, _ = proc.communicate()
    else:
        stdout = open(inp).read()

    stdout = stdout.splitlines()

    refresh_period_ns = int(stdout[0])
    ret = LatencyBatch(refresh_period_ns)

    for line in stdout:
        fields = line.split()

        if len(fields) != 3:
            continue

        draw_ns = int(fields[0])
        vsync_ns = int(fields[1])
        after_ns = int(fields[2])

        if draw_ns == 0 or vsync_ns == PENDING_FENCE_TIMESTAMP:
            continue

        debug(('%.3f %.3f %.3f') % (draw_ns / ret.refresh_period_ns,
            vsync_ns / ret.refresh_period_ns,
            after_ns / ret.refresh_period_ns))

        ret.frames.append(FrameInfo(draw_ns, vsync_ns, after_ns,
            refresh_period_ns))

    return ret

class FrameStats(object):
    def __init__(self):
        self.draw_to_draw_s = []
        self.draw_to_draw_vsyncs = []
        self.draw_to_draw_fps = []
        self.long_draw_to_draw = 0

        self.upload_to_upload_s = []
        self.upload_to_upload_vsyncs = []
        self.upload_to_upload_fps = []
        self.long_upload_to_upload = 0

        self.vsync_to_vsync_s = []
        self.vsync_to_vsync_fps = []
        self.vsync_to_vsync = []
        self.long_vsync_to_vsync = 0

        self.visible_frame_s = []
        self.visible_frame_fps = []
        self.visible_frame_vsyncs = []
        self.long_visible_frames = 0
        self.invisible_frames = 0

        self.frame_latency_s = []
        self.frame_latency_vsyncs = []
        self.frame_latency_increases = 0
        self.long_frame_latency = 0

        self.prev_frame = None
        self.prev_frame_latency_in_vsyncs = None

        self.anomalies = {}

    def add_anomaly(self, k):
        self.anomalies[k] = self.anomalies.get(k, 0) + 1

    def frame(self, refresh_period_ns, frame):
        prev_vsync = frame.vsync_before_draw_ns
        next_vsync = frame.vsync_after_submit_ns

        if self.prev_frame is None:
            self.last_change = prev_vsync
        else:
            dt = (frame.app_started_to_draw_ns -
                    self.prev_frame.app_started_to_draw_ns)
            self.draw_to_draw_s.append(dt / NS_PER_SECOND)
            self.draw_to_draw_vsyncs.append(dt / refresh_period_ns)
            if dt > 0:
                self.draw_to_draw_fps.append(NS_PER_SECOND / dt)
            else:
                self.add_anomaly('draw to draw = 0ns')

            if dt > refresh_period_ns:
                self.long_draw_to_draw += 1

            dt = (frame.after_submit_ns -
                    self.prev_frame.after_submit_ns)
            self.upload_to_upload_s.append(dt / NS_PER_SECOND)
            self.upload_to_upload_vsyncs.append(dt / refresh_period_ns)
            if dt > 0:
                self.upload_to_upload_fps.append(NS_PER_SECOND / dt)
            else:
                self.add_anomaly('upload to upload = 0ns')

            if dt > refresh_period_ns:
                self.long_upload_to_upload += 1

            dt = next_vsync - self.last_change

            # In principle this should be "> 0" but round to the nearest
            # vsync to avoid 1ns rounding errors throwing us off
            if dt >= refresh_period_ns / 2:
                self.visible_frame_s.append(dt / NS_PER_SECOND)
                if round(dt / refresh_period_ns) > 1:
                    self.long_visible_frames += 1
                self.visible_frame_vsyncs.append(dt / refresh_period_ns)
                self.visible_frame_fps.append(NS_PER_SECOND / dt)
                self.last_change = next_vsync
            else:
                self.invisible_frames += 1

        dt = next_vsync - prev_vsync

        if dt > refresh_period_ns:
            self.long_vsync_to_vsync += 1

        self.vsync_to_vsync_s.append(dt / NS_PER_SECOND)
        self.vsync_to_vsync.append(dt / refresh_period_ns)
        if dt > 0:
            self.vsync_to_vsync_fps.append(NS_PER_SECOND / dt)
        else:
            self.add_anomaly('vsync to vsync = 0ns')

        dt = frame.frame_latency_ns

        self.frame_latency_s.append(dt / NS_PER_SECOND)
        self.frame_latency_vsyncs.append(dt / refresh_period_ns)

        if dt > refresh_period_ns:
            self.long_frame_latency += 1

        if (self.prev_frame is not None and
                math.ceil(frame.frame_latency_ns / refresh_period_ns) >
                math.ceil(self.prev_frame.frame_latency_ns / refresh_period_ns)):
            self.frame_latency_increases += 1

        self.prev_frame = frame

    def print(self):
        if self.prev_frame is None:
            print('(no frames)')
            return

        print('frame time/rate based on draw callbacks for consecutive frames')
        mu = mean(self.draw_to_draw_s)
        mu1 = mean(self.draw_to_draw_vsyncs)
        print('  µ %.3f sec = %.3f vsyncs' % (mu, mu1))
        print('  σ %.3f sec = %.3f vsyncs' % (
            pstdev(self.draw_to_draw_s, mu),
            pstdev(self.draw_to_draw_vsyncs, mu1)))
        print('min/median/max %.3f/%.3f/%.3f sec = %.3f/%.3f/%.3f vsyncs' % (
            min_(self.draw_to_draw_s),
            median(self.draw_to_draw_s),
            max_(self.draw_to_draw_s),
            min_(self.draw_to_draw_vsyncs),
            median(self.draw_to_draw_vsyncs),
            max_(self.draw_to_draw_vsyncs),
            ))
        print('equivalent fps')
        mu = mean(self.draw_to_draw_fps)
        print('  µ %.3f fps' % mu)
        print('  σ %.3f fps' % pstdev(self.draw_to_draw_fps, mu))
        print('min/median/max %.3f/%.3f/%.3f fps' % (
            min_(self.draw_to_draw_fps),
            median(self.draw_to_draw_fps),
            max_(self.draw_to_draw_fps),
            ))
        print()

        print('frame time/rate based on consecutive frames reaching hardware')
        mu = mean(self.upload_to_upload_s)
        mu1 = mean(self.upload_to_upload_vsyncs)
        print('  µ %.3f sec = %.3f vsyncs' % (mu, mu1))
        print('  σ %.3f sec = %.3f vsyncs' % (
            pstdev(self.upload_to_upload_s, mu),
            pstdev(self.upload_to_upload_vsyncs, mu1)))
        print('min/median/max %.3f/%.3f/%.3f sec = %.3f/%.3f/%.3f vsyncs' % (
            min_(self.upload_to_upload_s),
            median(self.upload_to_upload_s),
            max_(self.upload_to_upload_s),
            min_(self.upload_to_upload_vsyncs),
            median(self.upload_to_upload_vsyncs),
            max_(self.upload_to_upload_vsyncs),
            ))
        print('equivalent fps')
        mu = mean(self.upload_to_upload_fps)
        print('  µ %.3f fps' % mu)
        print('  σ %.3f fps' % pstdev(self.upload_to_upload_fps, mu))
        print('min/median/max %.3f/%.3f/%.3f fps' % (
            min_(self.upload_to_upload_fps),
            median(self.upload_to_upload_fps),
            max_(self.draw_to_draw_fps),
            ))
        print()

        print('frame latency from drawing frame to reaching hardware')
        mu = mean(self.frame_latency_s)
        mu1 = mean(self.frame_latency_vsyncs)
        print('  µ %.3f sec = %.3f vsyncs' % (mu, mu1))
        print('  σ %.3f sec = %.3f vsyncs' % (
            pstdev(self.frame_latency_s, mu),
            pstdev(self.frame_latency_vsyncs, mu1)))
        print('min/median/max %.3f/%.3f/%.3f sec = %.3f/%.3f/%.3f vsyncs' % (
            min_(self.frame_latency_s),
            median(self.frame_latency_s),
            max(self.frame_latency_s),
            min_(self.frame_latency_vsyncs),
            median(self.frame_latency_vsyncs),
            max(self.frame_latency_vsyncs),
            ))
        print()

        print('frame latency from vsync before draw to vsync after reaching hardware')
        mu = mean(self.vsync_to_vsync_s)
        mu1 = mean(self.vsync_to_vsync)
        print('  µ %.3f sec = %.3f vsyncs' % (mu, mu1))
        print('  σ %.3f sec = %.3f vsyncs' % (
            pstdev(self.vsync_to_vsync_s, mu),
            pstdev(self.vsync_to_vsync, mu1)))
        print('min/median/max %.3f/%.3f/%.3f sec = %.1f/%.1f/%.1f vsyncs' % (
            min_(self.vsync_to_vsync_s),
            median(self.vsync_to_vsync_s),
            max_(self.vsync_to_vsync_s),
            min_(self.vsync_to_vsync),
            median(self.vsync_to_vsync),
            max_(self.vsync_to_vsync),
            ))
        print('equivalent fps')
        mu = mean(self.vsync_to_vsync_fps)
        print('  µ %.3f fps' % mu)
        print('  σ %.3f fps' % pstdev(self.vsync_to_vsync_fps, mu))
        print('min/median/max %.3f/%.3f/%.3f fps' % (
            min_(self.vsync_to_vsync_fps),
            median(self.vsync_to_vsync_fps),
            max(self.vsync_to_vsync_fps),
            ))
        print()

        print('user-visible frame times (vsyncs with new content)')
        mu = mean(self.visible_frame_s)
        mu1 = mean(self.visible_frame_vsyncs)
        print('  µ %.3f sec = %.3f vsyncs' % (mu, mu1))
        print('  σ %.3f sec = %.3f vsyncs' % (
            pstdev(self.visible_frame_s, mu),
            pstdev(self.visible_frame_vsyncs, mu1)))
        print('min/median/max %.3f/%.3f/%.3f sec = %.1f/%.1f/%.1f vsyncs' % (
            min_(self.visible_frame_s),
            median(self.visible_frame_s),
            max_(self.visible_frame_s),
            min_(self.visible_frame_vsyncs),
            median(self.visible_frame_vsyncs),
            max_(self.visible_frame_vsyncs),
            ))
        print('equivalent fps')
        mu = mean(self.visible_frame_fps)
        print('  µ %.3f fps' % mu)
        print('  σ %.3f fps' % pstdev(self.visible_frame_fps, mu))
        print('min/median/max %.3f/%.3f/%.3f fps' % (
            min_(self.visible_frame_fps),
            median(self.visible_frame_fps),
            max(self.visible_frame_fps),
            ))
        print('number of frames drawn but never shown: %d' %
                self.invisible_frames)
        print()

        print('possible jank metrics:')
        print('number of frame latency increases: %d' %
                self.frame_latency_increases)
        print('> 1 vsync between visible frame updates: %d' %
                self.long_visible_frames)
        print('> 1 vsync between starting to draw consecutive frames: %d' %
                self.long_draw_to_draw)
        print('> 1 vsync between consecutive frames reaching hardware: %d' %
                self.long_upload_to_upload)
        print('> 1 vsync frame latency (drawing frame to reaching hardware): %d' %
                self.long_frame_latency)
        print('> 1 vsync frame latency (whole vsyncs): %d' %
                self.long_vsync_to_vsync)
        print()
        for k, v in self.anomalies.items():
            print('%s: %d' % (k, v))

def frame_generator(batches):
    for batch in batches:
        for frame in batch.frames:
            yield frame

def main():

    parser = argparse.ArgumentParser(
            description='Summarize SurfaceFlinger statistics.',
            epilog='Use the --time option or press Ctrl+C to exit.')
    parser.add_argument('--surface', '-s', default='SurfaceView',
            help=('Android surface name (' +
                'run "adb shell dumpsys SurfaceFlinger | grep \'HWC |\'")'))
    parser.add_argument('--time', '-t', default=0, type=float,
            metavar='SECONDS',
            help='Terminate after seeing this many seconds of samples')
    parser.add_argument('--graph', metavar='FILE', default='', type=str,
            help='Save a SVG graph to FILE')
    parser.add_argument('--output', metavar='FILE', default='', type=str,
            help='Save raw data to FILE')
    parser.add_argument('--input', metavar='FILE', default='', type=str,
            help='Fetch raw data from FILE')
    options = parser.parse_args()

    surface = options.surface

    if options.time:
        measurement_length_ns = options.time * NS_PER_SECOND
    else:
        measurement_length_ns = None

    batches = []
    if not options.input:
        clear_latency(surface)
    oldest_ns = None
    newest_ns = None
    i = 0
    all_stats = FrameStats()

    while True:
        try:
            enough = False
            if options.input:
                batch = dump_latency(None, options.input)
            else:
                batch = dump_latency(surface)

            if batch.frames:
                batch_stats = FrameStats()

                if batches:
                    # for simplicity we assume that all batches have the
                    # same vsync rate (in practice this is true)
                    assert batches[0].refresh_period_ns == batch.refresh_period_ns

                if oldest_ns is None:
                    oldest_ns = batch.frames[0].app_started_to_draw_ns

            if newest_ns is not None:
                while (batch.frames and
                        batch.frames[0].app_started_to_draw_ns <= newest_ns):
                    batch.frames.pop(0)

            if measurement_length_ns is not None:
                while (batch.frames and
                        batch.frames[-1].app_started_to_draw_ns >
                        oldest_ns + measurement_length_ns):
                    batch.frames.pop(-1)
                    enough = True

            if batch.frames:
                newest_ns = batch.frames[-1].app_started_to_draw_ns

                for frame in batch.frames:
                    batch_stats.frame(batch.refresh_period_ns, frame)
                    all_stats.frame(batch.refresh_period_ns, frame)

                if not options.input:
                    print('\n-------- batch stats --------\n')
                    batch_stats.print()

                batches.append(batch)

            if options.input:
                break

            if enough:
                print('(%.3f seconds elapsed, stopping)' %
                        (measurement_length_ns / NS_PER_SECOND))
                break

            time.sleep(1)
            i += 1
        except KeyboardInterrupt:
            print('(Interrupted, stopping)')
            break

    if options.output and batches:
        f = open(options.output, 'w')
        f.write('%d\n' % batches[0].refresh_period_ns)
        for frame in frame_generator(batches):
            f.write('%d %d %d\n' % (frame.app_started_to_draw_ns,
                frame.vsync_before_submit_ns,
                frame.after_submit_ns))
        f.close()

    if options.graph and batches:
        f = open(options.graph, 'w')

        f.write('<svg xmlns="http://www.w3.org/2000/svg">\n')

        for frame in frame_generator(batches):
            origin = min([frame.app_started_to_draw_ns,
                frame.vsync_before_submit_ns,
                frame.after_submit_ns])
            break

        parity = 0
        offset = 0
        prev_frame = None
        last_change = 0

        SKY_BLUE = '#3465a4'
        BLUE = '#204a87'
        GREEN = '#4e9a06'
        PURPLE = '#5c3566'
        BROWN = '#8f5902'
        RED = '#a40000'

        for frame in frame_generator(batches):

            offset = (offset + 1) % 4

            vsync = (frame.vsync_before_submit_ns - origin) / NS_PER_MS
            prev_vsync = (frame.vsync_before_draw_ns - origin) / NS_PER_MS
            start_draw = (frame.app_started_to_draw_ns - origin) / NS_PER_MS
            after_submit = (frame.after_submit_ns - origin) / NS_PER_MS
            next_vsync = (frame.vsync_after_submit_ns - origin) / NS_PER_MS

            if prev_frame is not None:
                prev_start = (prev_frame.app_started_to_draw_ns - origin) / NS_PER_MS
                prev_after = (prev_frame.after_submit_ns - origin) / NS_PER_MS

            # Draw grid-lines for the vsyncs
            f.write('  <line x1="0px" y1="%fpx" x2="100px" y2="%fpx" stroke="%s" stroke-width="0.5" />\n'
                    % (prev_vsync, prev_vsync, SKY_BLUE))
            f.write('  <line x1="0px" y1="%fpx" x2="100px" y2="%fpx" stroke="%s" stroke-width="0.5" />\n'
                    % (vsync, vsync, SKY_BLUE))
            f.write('  <line x1="0px" y1="%fpx" x2="100px" y2="%fpx" stroke="%s" stroke-width="0.5" />\n'
                    % (next_vsync, next_vsync, SKY_BLUE))

            # Purple dotted: join to vsync immediately before starting to submit
            # We can't tell how long drawing and submission took, so guess
            # that submission started halfway through.
            f.write('  <line x1="0px" y1="%fpx" x2="50px" y2="%fpx" stroke="%s" stroke-width="1" stroke-dasharray="1,1" />\n'
                    % (vsync, (start_draw + after_submit) / 2, PURPLE))
            # Blue dotted: connect previous vsync to starting drawing
            f.write('  <line x1="0px" y1="%fpx" x2="25px" y2="%fpx" stroke="%s" stroke-width="1" stroke-dasharray="1,1" />\n'
                    % (prev_vsync, start_draw, BLUE))
            # Green, purple: draw and submit the frame.
            # We can't tell how long drawing and submission took, so guess
            # that submission started halfway through.
            f.write('  <line x1="25px" y1="%fpx" x2="50px" y2="%fpx" stroke="%s" stroke-width="1" />\n'
                    % (start_draw, (start_draw + after_submit) / 2, GREEN))
            f.write('  <line x1="50px" y1="%fpx" x2="75px" y2="%fpx" stroke="%s" stroke-width="1" />\n'
                    % ((start_draw + after_submit) / 2, after_submit, PURPLE))
            # Blue dotted: submit -> next vsync
            f.write('  <line x1="75px" y1="%fpx" x2="100px" y2="%fpx" stroke="%s" stroke-width="1" stroke-dasharray="1,1" />\n'
                    % (after_submit, next_vsync, BLUE))

            if prev_frame is not None:
                # Green: time between draw timestamps
                f.write('  <line x1="%dpx" y1="%fpx" x2="%dpx" y2="%fpx" stroke="%s" />\n'
                        % (120 + 30 * offset, prev_start,
                            120 + 30 * offset, start_draw, GREEN))

                # Purple: time between after-submit timestamps
                f.write('  <line x1="%dpx" y1="%fpx" x2="%dpx" y2="%fpx" stroke="%s" />\n'
                        % (125 + 30 * offset, prev_after,
                            125 + 30 * offset, after_submit, PURPLE))

            # Red: frame latency
            f.write('  <line x1="%dpx" y1="%fpx" x2="%dpx" y2="%fpx" stroke="%s" />\n'
                    % (130 + 30 * offset, start_draw,
                        130 + 30 * offset, after_submit, RED))

            # Blue: frame latency including vsyncs
            f.write('  <line x1="%dpx" y1="%fpx" x2="%dpx" y2="%fpx" stroke="%s" />\n'
                    % (135 + 30 * offset, prev_vsync,
                        135 + 30 * offset, next_vsync, BLUE))

            prev_frame = frame

            # Brown: user-visible frame changes
            dt = next_vsync - last_change
            if dt >= (frame.refresh_period_ns / NS_PER_MS) / 2:
                f.write('  <line x1="%dpx" y1="%fpx" x2="%dpx" y2="%fpx" stroke="%s" />\n'
                        % (105 + 5 * parity, last_change,
                            105 + 5 * parity, next_vsync, BROWN))
                last_change = next_vsync
                parity = 1 - parity

        f.write('</svg>\n')
        f.close()

    print('\n======== overall stats ========\n')
    all_stats.print()

if __name__ == '__main__':
    main()

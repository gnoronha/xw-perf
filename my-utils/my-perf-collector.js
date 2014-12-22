// Copyright 2014 Intel Corporation. All rights reserved.
//
// License: BSD-3-clause-Intel, see LICENSE.txt

window.MyPerfCollector || (function(exports){
'use strict';

performance.mark('mark_perf_collector_setup_begin');

var blinkenlight = document.createElement('div');
var blinkenlightAttached = false;

var blinkenlightStyle = blinkenlight.style;
blinkenlightStyle.zIndex = '100000';
blinkenlightStyle.position = 'fixed';
blinkenlightStyle.left = '0';
blinkenlightStyle.right = '2px';
blinkenlightStyle.top = '0';
blinkenlightStyle.bottom = '2px';
blinkenlightStyle.width = '2px';
blinkenlightStyle.height = '2px';
blinkenlightStyle.backgroundColor = '#000';
blinkenlightStyle.transform = 'translate3d(0px, 0px, 0px)';

var ringBuffer = new Float64Array(300);
var frameIndex = 0;
var firstTimeRound = true;

var g = exports.MyPerfCollector || {};
exports.MyPerfCollector = g;

g.alwaysAnimate = false;
g.animateCallback = function (isEvenFrame) {
};
g.ringBuffer = ringBuffer;
g.getFrameIndex = function () {
  return frameIndex;
};
g.drawStatsNextFrame = null;

function frame(now) {
  ringBuffer[frameIndex] = now;

  if (firstTimeRound) {
    if (frameIndex != 0)
      performance.measure('measure_to_next_frame', 'mark_perf_frame');
    performance.mark('mark_perf_frame');
  }

  if (g.alwaysAnimate) {
    blinkenlightStyle.backgroundColor = (frameIndex % 2 ? '#f0f' : '#0f0');
  }

  var callback = g.drawStatsNextFrame;

  if (callback) {
    callback(now);
  }

  frameIndex = (frameIndex + 1) % ringBuffer.length;

  if (firstTimeRound && frameIndex == 0) {
    performance.mark('finished doing first ' + ringBuffer.length +
        ' frames');
    firstTimeRound = false;
  }

  requestAnimationFrame(frame);
}

function documentStateChanged() {
  performance.mark('mark_perf_collector_state_' + document.readyState);
  if (document.body && !blinkenlightAttached) {
    performance.mark('mark_perf_collector_attach_blinkenlight');
    document.body.appendChild(blinkenlight);
    blinkenlightAttached = true;
  }
}

document.addEventListener('readystatechange', documentStateChanged);
documentStateChanged();

requestAnimationFrame(frame);

performance.mark('mark_perf_collector_setup_end');

})(window);

// vim:set sw=2 sts=2 et:

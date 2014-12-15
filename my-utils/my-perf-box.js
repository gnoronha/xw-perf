// Copyright 2014 Intel Corporation. All rights reserved.
//
// License: BSD-3-clause-Intel, see LICENSE.txt

(function(exports){
'use strict';

var g = window.MyPerfBoxGlobal;

function niceTime(t) {
  return (t / 1000).toFixed(6);
}

Polymer({
  drawStats: function (now) {
      g.drawStatsNextFrame = null;
      var ringBuffer = g.ringBuffer;
      var frameIndex = g.getFrameIndex();

      var nextTime = -1;
      var canvas = this.$['perf-canvas'];
      var ctx = canvas.getContext('2d');
      var HEIGHT = 64;
      var WIDTH = 300;
      ctx.fillStyle = "#000";
      ctx.fillRect(0, 0, 400, 64);

      var lt28 = 0;
      var lt30 = 0;
      var lt32 = 0;
      var lt58 = 0;
      var lt60 = 0;
      var lt62 = 0;
      var ge62 = 0;
      var total = 0;
      var samples = 0;

      nextTime = now;
      for (var i = frameIndex - 1; ; i--) {
        i = (ringBuffer.length + i) % ringBuffer.length;
        if (i == frameIndex)
          break;
        var thisTime = ringBuffer[i];

        if (thisTime == 0) {
          // skip unfilled portion of ring buffer
          break;
        }

        var dt = nextTime - thisTime;
        var fps = Infinity;
        if (dt != 0) {
          fps = 1000 / dt;
          total += fps;
          samples += 1;
        }

        nextTime = thisTime;

        if (fps < 28) {
          lt28++;
          ctx.fillStyle = "#f00";
        } else if (fps < 30) {
          lt30++;
          ctx.fillStyle = "#f80";
        } else if (fps < 32) {
          lt32++;
          ctx.fillStyle = "#ff0";
        } else if (fps < 58) {
          lt58++;
          ctx.fillStyle = "#bf0";
        } else if (fps < 60) {
          lt60++;
          ctx.fillStyle = "#8f0";
        } else if (fps < 62) {
          lt62++;
          ctx.fillStyle = "#0f8";
        } else {
          ge62++;
          ctx.fillStyle = "#0ff";
        }

        // 1 pixel per 1/60 second so we get 1px at 60fps
        var x = WIDTH - ((now - thisTime) * 60 / 1000);
        var w = dt * 60 / 1000;

        if (fps > 60)
          ctx.fillRect(x, HEIGHT - fps, w, fps - 60 + 1);
        else
          ctx.fillRect(x, HEIGHT - 60, w, 60 - fps + 1);

        // Discard samples older than 5 seconds for graphing and
        // statistics purposes
        if ((now - thisTime) > 5000)
          break;
      }

      var mean = total / samples;

      var squareDiffs = 0;
      var minFps = Infinity;
      var maxFps = 0;

      nextTime = now;
      for (var i = frameIndex - 1; ; i--) {
        i = (ringBuffer.length + i) % ringBuffer.length;
        if (i == frameIndex)
          break;
        var thisTime = ringBuffer[i];

        if (thisTime == 0) {
          // skip unfilled portion of ring buffer
          break;
        }

        var dt = nextTime - thisTime;
        var fps = Infinity;
        if (dt != 0) {
          fps = 1000 / dt;
          squareDiffs += ((fps - mean) * (fps - mean));

          if (fps < minFps)
            minFps = fps;

          if (fps > maxFps)
            maxFps = fps;
        }

        if ((now - thisTime) > 5000)
          break;

        nextTime = thisTime;
      }

      var stdDev = Math.sqrt(squareDiffs / samples);

      ctx.textBaseline = 'top';
      ctx.font = '9px sans-serif';

      ctx.fillStyle = '#f00';
      ctx.fillText(lt28 + ' <28fps', 308, 48);
      ctx.fillStyle = '#f80';
      ctx.fillText(lt30 + ' <30fps', 308, 40);
      ctx.fillStyle = '#ff0';
      ctx.fillText(lt32 + ' <32fps', 308, 32);
      ctx.fillStyle = '#bf0';
      ctx.fillText(lt58 + ' <58fps', 308, 24);
      ctx.fillStyle = '#8f0';
      ctx.fillText(lt60 + ' <60fps', 308, 16);
      ctx.fillStyle = '#0f8';
      ctx.fillText(lt62 + ' <62fps', 308, 8);
      ctx.fillStyle = '#0ff';
      ctx.fillText(ge62 + ' ≥62fps', 308, 0);

      ctx.fillStyle = '#fff';
      ctx.fillText('µ = ' + mean.toFixed(2) +
          ', σ = ' + stdDev.toFixed(2) +
          ', min = ' + minFps.toFixed(2) +
          ', max = ' + maxFps.toFixed(2),
          8, 48);
  },

  ready: function() {
    var canvas = this.$['perf-canvas'];
    var ctx = canvas.getContext('2d');
    ctx.textBaseline = 'top';
    ctx.fillText('touch to update graph', 8, 10);
    performance.mark('mark_my_perf_box_ready');

    this.drawStatsCallback = this.drawStats.bind(this);
  },

  tap: function () {
    g.drawStatsNextFrame = this.drawStatsCallback;
  },

  openPopup: function () {
    var s = '';
    var start = performance.timing.navigationStart;
    s += 'Start time (ms since the epoch): ' + start + '\n';
    // so we can compare it with the timestamp from notxw_starter
    s += 'Start time (ms since the last whole minute): ' + (start % 60000) + '\n';
    s += '\n';

    var entries = performance.getEntries();

    s += '  time   duration\n';
    s += '-------- --------\n';

    for (var i = 0; i < entries.length; i++) {
      var e = entries[i];
      s += '<span class="' + e.entryType + ' ' + e.name + '">' +
          (niceTime(e.startTime) + ' ' +
          niceTime(e.duration) + ' ' +
          e.entryType + ': ' +
          e.name + '</span>\n');
    }

    this.$.popupResults.innerHTML = s;
    this.$.popup.removeAttribute('hidden');
  },

  closePopup: function() {
    this.$.popup.setAttribute('hidden', '');
    this.$.popupResults.innerHTML = '';
  },

  fiveSecondTest: function () {
    var that = this;
    var canvas = this.$['perf-canvas'];
    var ctx = canvas.getContext('2d');

    ctx.fillStyle = "#800";
    ctx.fillRect(0, 0, 400, 64);

    ctx.fillStyle = "#fff";
    ctx.fillText('you have 5 seconds to reach the initial state', 8, 10);

    setTimeout(function () {
      ctx.fillStyle = "#880";
      ctx.fillRect(0, 0, 400, 64);

      ctx.fillStyle = "#fff";
      ctx.fillText('start testing now, you have 1 second', 8, 10);

      setTimeout(function () {
        ctx.fillStyle = "#0f0";
        ctx.fillRect(0, 0, 400, 64);

        ctx.fillStyle = "#000";
        ctx.fillText('testing... you can stop when the graph appears', 8, 10);

        setTimeout(function () {
          g.drawStatsNextFrame = that.drawStatsCallback;
        }, 5000);
      }, 1000);
    }, 3000);
  },
});

})(window);

// vim:set sw=2 sts=2 et:

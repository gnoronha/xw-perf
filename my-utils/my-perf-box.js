// Copyright 2014 Intel Corporation. All rights reserved.
//
// License: BSD-3-clause-Intel, see LICENSE.txt

window.MyPerfBox || (function(exports){
'use strict';

var g = exports.MyPerfBox || {};
exports.MyPerfBox = g;

function niceTime(t) {
  return (t / 1000).toFixed(6);
}

function ensureCss(id, href) {
  var doc = document;

  if (!doc.getElementById(id)) {
    var head = doc.head;
    var link = doc.createElement('link');
    link.id = id;
    link.rel = 'stylesheet';
    link.href = 'bower_components/my-utils/' + href;
    link.media = 'all';
    head.appendChild(link);
  }
}

g.openPopup = function () {
  ensureCss('perfBoxPopupCss', 'my-perf-box.css');

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

  var doc = document;
  var popup = doc.createElement('div');
  popup.classList.add('perfBoxPopup');

  var popupCloseButton = doc.createElement('div');
  popupCloseButton.classList.add('perfBoxPopupCloseButton');
  popupCloseButton.innerText = 'Close';

  var popupResults = doc.createElement('pre');
  popupResults.classList.add('perfBoxPopupResults');
  popupResults.innerHTML = s;

  popup.appendChild(popupCloseButton);
  popup.appendChild(popupResults);

  popupCloseButton.addEventListener('click', function () {
    doc.body.removeChild(popup);
  });

  doc.body.appendChild(popup);
};

function drawStats(now) {
  window.MyPerfCollector.drawStatsNextFrame = null;
  var ringBuffer = window.MyPerfCollector.ringBuffer;
  var frameIndex = window.MyPerfCollector.getFrameIndex();

  var nextTime = -1;
  var ctx = g.canvas.getContext('2d');
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
};

function setup() {
  ensureCss('perfBoxPopupCss', 'my-perf-box.css');

  var doc = document;
  var div = doc.createElement('div');
  div.id = 'perfBox';
  div.classList.add('perfBox');
  g.canvas = doc.createElement('canvas');
  g.canvas.height = 64;
  g.canvas.width = 400;
  g.canvas.classList.add('perfBoxCanvas');

  g.canvas.addEventListener('click', function () {
    window.MyPerfCollector.drawStatsNextFrame = drawStats;
  });

  div.appendChild(g.canvas);
  doc.body.appendChild(div);

  var ctx = g.canvas.getContext('2d');
  ctx.textBaseline = 'top';
  ctx.fillText('touch to update graph', 8, 10);
  performance.mark('mark_my_perf_box_ready');
}

g.fiveSecondTest = function () {
  var ctx = g.canvas.getContext('2d');

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
        window.MyPerfCollector.drawStatsNextFrame = drawStats;
      }, 5000);
    }, 1000);
  }, 3000);
};

setup();

})(window);

// vim:set sw=2 sts=2 et:

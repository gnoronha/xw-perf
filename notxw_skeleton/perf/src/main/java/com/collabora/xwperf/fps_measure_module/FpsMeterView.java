/*
 * Copyright 2014 Intel Corporation. All rights reserved.
 * License: BSD-3-clause-Intel, see LICENSE.txt
 */
package com.collabora.xwperf.fps_measure_module;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;

public class FpsMeterView extends View {
    private int counter;
    private boolean animateAlways = true;

    private final Paint paint;
    private IFpsListener fpsListener;

    public FpsMeterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        MeasurementLogger.getInstance().addMark(MeasurementLogger.PerformanceMarks.MARK_PERF_COLLECTOR_ATTACH_BLINKENLIGHT);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        postInvalidate();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (animateAlways) {
            paint.setColor(counter++ % 2 == 0 ? 0xFFFF00FF : 0xFF00FF00);
            canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
        }
        if (fpsListener != null) {
            fpsListener.onFpsCount(SystemClock.elapsedRealtime());
        }
    }

    public void setFpsListener(IFpsListener fpsListener) {
        this.fpsListener = fpsListener;
    }

    public void removeFpsListener() {
        fpsListener = null;
    }

    public boolean isAnimateAlways() {
        return animateAlways;
    }

    public void setAnimateAlways(boolean animateAlways) {
        this.animateAlways = animateAlways;
    }
}

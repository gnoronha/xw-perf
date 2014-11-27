package com.collabora.xwperf.notxw_social;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;

public class FpsMeterView extends View {
    private long startTime = -1;
    private int counter;

    private final Paint paint;
    private IFpsListener fpsListener;

    public FpsMeterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        postInvalidate();
    }

    @Override
    public void draw(Canvas canvas) {
        if (startTime == -1) {
            startTime = SystemClock.elapsedRealtime();
            counter = 0;
        }

        long now = SystemClock.elapsedRealtime();
        long delay = now - startTime;

        super.draw(canvas);
        paint.setColor(counter % 2 == 0 ? 0xFFFF00FF : 0xFF00FF00);
        canvas.drawLine(0, 0, 1, 1, paint);

        if (delay > 1000l) {
            startTime = now;
            if (fpsListener != null) {
                fpsListener.onFpsCount(counter);
            }
            counter = 0;
        }
        counter++;
    }

    public void setFpsListener(IFpsListener fpsListener) {
        this.fpsListener = fpsListener;
    }

    public void removeFpsListener() {
        fpsListener = null;
    }

}

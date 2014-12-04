package com.collabora.xwperf.notxw_social;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;


public class FpsGraphView extends View {

    private static final int MAGIC_NUMBER = 300;
    private final int graphWidth;

    private long startTime = 0;
    private long lastTime;

    private FifoFloatQueue fpsHistory;
    private Paint paint;
    private int[] colors;
    private final float textSize;
    private final int strokeWidth;

    public FpsGraphView(Context context) {
        this(context, null);
    }

    public FpsGraphView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FpsGraphView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs);
        textSize = context.getResources().getDimension(R.dimen.fps_graph_textsize);
        strokeWidth = (int) context.getResources().getDimension(R.dimen.fps_graph_stroke_width);
        graphWidth = (int) context.getResources().getDimension(R.dimen.fps_graph_width);
        fpsHistory = new FifoFloatQueue(MAGIC_NUMBER);

        paint = new Paint();
        colors = context.getResources().getIntArray(R.array.graphColors);

        startTime = SystemClock.elapsedRealtime();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (heightMeasureSpec < textSize * (colors.length + 1)) {
            heightMeasureSpec = (int) (textSize * (colors.length + 1));
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        FifoFloatQueue buffer = FifoFloatQueue.clone(fpsHistory);
        canvas.drawColor(Color.BLACK); //clear
        paint.reset();
        paint.setTextSize(textSize);
        int xOffset = 0;
        int yOffset = 4 * strokeWidth;
        int lt28 = 0;
        int lt30 = 0;
        int lt32 = 0;
        int lt58 = 0;
        int lt60 = 0;
        int lt62 = 0;
        int ge62 = 0;

        float mean = buffer.getTotal() / buffer.size();
        float squareDiffs = 0;
        float minFps = 10000;
        float maxFps = 0;

        for (float fpsValue : buffer) {
            if (fpsValue < 28) {
                lt28++;
                paint.setColor(colors[0]);
            } else if (fpsValue < 30) {
                lt30++;
                paint.setColor(colors[1]);
            } else if (fpsValue < 32) {
                lt32++;
                paint.setColor(colors[2]);
            } else if (fpsValue < 58) {
                lt58++;
                paint.setColor(colors[3]);
            } else if (fpsValue < 59) {
                lt60++;
                paint.setColor(colors[4]);
            } else if (fpsValue < 62) {
                lt62++;
                paint.setColor(colors[5]);
            } else {
                ge62++;
                paint.setColor(colors[6]);
            }

            if (fpsValue >= 59) {
                canvas.drawRect(xOffset, yOffset - (fpsValue - 60), xOffset + strokeWidth, yOffset, paint);
            } else {
                canvas.drawRect(xOffset, yOffset, xOffset + strokeWidth, yOffset + (60 - fpsValue) * strokeWidth, paint);
            }
            xOffset += strokeWidth;
            squareDiffs += (fpsValue - mean) * (fpsValue - mean);
            if (fpsValue < minFps)
                minFps = fpsValue;
            if (fpsValue > maxFps)
                maxFps = fpsValue;
        }
        double stdDev = Math.sqrt(squareDiffs / buffer.size());
        int legendStartPos = graphWidth + 12;
        paint.setColor(colors[0]);
        canvas.drawText("" + lt28 + "<28fps", legendStartPos, textSize * 7, paint);
        paint.setColor(colors[1]);
        canvas.drawText("" + lt30 + "<30fps", legendStartPos, textSize * 6, paint);
        paint.setColor(colors[2]);
        canvas.drawText("" + lt32 + "<32fps", legendStartPos, textSize * 5, paint);
        paint.setColor(colors[3]);
        canvas.drawText("" + lt58 + "<58fps", legendStartPos, textSize * 4, paint);
        paint.setColor(colors[4]);
        canvas.drawText("" + lt60 + "<60fps", legendStartPos, textSize * 3, paint);
        paint.setColor(colors[5]);
        canvas.drawText("" + lt62 + "<62fps", legendStartPos, textSize * 2, paint);
        paint.setColor(colors[6]);
        canvas.drawText("" + ge62 + "≥62fps", legendStartPos, textSize, paint);

        paint.setColor(Color.WHITE);
        canvas.drawText("µ = " + String.format("%.2f", mean) +
                ", σ = " + String.format("%.2f", stdDev) +
                ", min = " + String.format("%.2f", minFps) +
                ", max = " + String.format("%.2f", maxFps),
                8, textSize * 7, paint);
    }

    public void addValue(long frameTimestamp) {
        if (lastTime == 0)
            lastTime = startTime;

        float value = 1000f / (frameTimestamp - lastTime);
        lastTime = frameTimestamp;
        fpsHistory.add(value);
        if (lastTime - startTime > 5000) {
            postInvalidate();
            startTime = lastTime;//show every 5 seconds
        }
    }

}

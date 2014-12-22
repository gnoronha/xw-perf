/*
 * Copyright 2014 Intel Corporation. All rights reserved.
 * License: BSD-3-clause-Intel, see LICENSE.txt
 */
package com.collabora.xwperf.fps_measure_module;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;

import com.collabora.xwperf.fps_measure_module.MeasurementLogger.PerformanceMarks;

public class FpsGraphView extends View {
    private MeasurementLogger logger = MeasurementLogger.getInstance();
    private static final int NUMBER_OF_SAMPLES = 301;
    private final int graphWidth;
    private final int legendWidth;
    private final int desiredHeight;

    private long startTime = 0;
    private long lastTime;

    private FifoFloatQueue fpsHistory;
    private Paint paint;
    private int[] colors;
    private final float textSize;
    private final int strokeWidth;

    int yOffset, xOffset, lt28, lt30, lt32, lt58, lt60, lt62, ge62;


    public FpsGraphView(Context context) {
        this(context, null);
    }

    public FpsGraphView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FpsGraphView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs);
        logger.addMark(PerformanceMarks.MARK_PERF_COLLECTOR_SETUP_START);
        textSize = context.getResources().getDimension(R.dimen.fps_graph_textsize);
        strokeWidth = (int) context.getResources().getDimension(R.dimen.fps_graph_stroke_width);
        graphWidth = (int) context.getResources().getDimension(R.dimen.fps_graph_width);
        legendWidth = (int) context.getResources().getDimension(R.dimen.fps_legend_width);
        desiredHeight = (int) context.getResources().getDimension(R.dimen.fps_desired_height);
        fpsHistory = new FifoFloatQueue(NUMBER_OF_SAMPLES);

        paint = new Paint();
        colors = context.getResources().getIntArray(R.array.graphColors);

        startTime = SystemClock.elapsedRealtime();
        yOffset = 4 * strokeWidth;
        logger.addMark(PerformanceMarks.MARK_PERF_COLLECTOR_SETUP_END);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredWidth = graphWidth + legendWidth;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width, height;

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(desiredWidth, widthSize);
        } else {
            width = desiredWidth;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(desiredHeight, heightSize);
        } else {
            height = desiredHeight;
        }

        setMeasuredDimension(width, height);
    }

    @Override
    public void draw(Canvas canvas) {
        FifoFloatQueue buffer = FifoFloatQueue.clone(fpsHistory);
        canvas.drawColor(Color.BLACK); //clear
        paint.reset();
        paint.setTextSize(textSize);
        xOffset = lt28 = lt30 = lt32 = lt58 = lt60 = lt62 = ge62 = 0;

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
            } else if (fpsValue < 60) {
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
        canvas.drawText(String.format(getResources().getString(R.string.fps_graph_label_less), lt28, 28), legendStartPos, textSize * 7, paint);
        paint.setColor(colors[1]);
        canvas.drawText(String.format(getResources().getString(R.string.fps_graph_label_less), lt30, 30), legendStartPos, textSize * 6, paint);
        paint.setColor(colors[2]);
        canvas.drawText(String.format(getResources().getString(R.string.fps_graph_label_less), lt32, 32), legendStartPos, textSize * 5, paint);
        paint.setColor(colors[3]);
        canvas.drawText(String.format(getResources().getString(R.string.fps_graph_label_less), lt58, 58), legendStartPos, textSize * 4, paint);
        paint.setColor(colors[4]);
        canvas.drawText(String.format(getResources().getString(R.string.fps_graph_label_less), lt60, 60), legendStartPos, textSize * 3, paint);
        paint.setColor(colors[5]);
        canvas.drawText(String.format(getResources().getString(R.string.fps_graph_label_less), lt62, 62), legendStartPos, textSize * 2, paint);
        paint.setColor(colors[6]);
        canvas.drawText(String.format(getResources().getString(R.string.fps_graph_label_more), ge62, 62), legendStartPos, textSize, paint);

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
        fpsHistory.add(value);
        if (fpsHistory.size() < NUMBER_OF_SAMPLES) {
            logger.addMeasure(lastTime, frameTimestamp - lastTime, PerformanceMarks.MEASURE_TO_NEXT_FRAME);
            logger.addMark(frameTimestamp, PerformanceMarks.MARK_PERF_FRAME);
            if (fpsHistory.size() == (NUMBER_OF_SAMPLES - 1)) {
                logger.addMark(frameTimestamp, PerformanceMarks.MARK_FRAME_COLLECT_FINISH);
            }
        }
        lastTime = frameTimestamp;
    }
}

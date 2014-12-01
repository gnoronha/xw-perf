package com.collabora.xwperf.notxw_social;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;

public class FpsGraphView extends View {
    private static final int HIGH_FPS = 60;
    private static final int MEDIUM_FPS = 50;
    private static final int LOW_FPS = 24;
    private static final int UPDATE_TIMEOUT = 5;

    private FifoQueue<Integer> fpsHistory;
    private Paint paint;
    private int[] colors;
    private boolean refresh;
    private final float textSize;
    private final int legendWidth;
    private final int strokeWidth;
    private int updateTimer = 0;

    public FpsGraphView(Context context) {
        this(context, null);
    }

    public FpsGraphView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FpsGraphView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs);
        textSize = context.getResources().getDimension(R.dimen.fps_graph_textsize);
        legendWidth = (int) context.getResources().getDimension(R.dimen.fps_graph_legend_width);
        strokeWidth = (int) context.getResources().getDimension(R.dimen.fps_graph_stroke_width);
        paint = new Paint();
        colors = new int[]{
                0xFFCC0000,//red
                0xFFFF8A00,//orange
                0xFF669900,//green
                0xFF0099CC //blue
        };
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        refresh = prefs.getBoolean(context.getString(R.string.pref_update_key), false);
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
        int maxWidth = canvas.getWidth() - legendWidth;
        if (fpsHistory == null) {
            fpsHistory = new FifoQueue<>(maxWidth / strokeWidth);
        }
        canvas.drawColor(Color.BLACK);
        paint.reset();
        paint.setTextSize(textSize);
        int yOffset = canvas.getHeight() - (int) textSize;
        if (fpsHistory.size() == 0) {
            paint.setColor(Color.WHITE);
            canvas.drawText(getContext().getString(R.string.touch_to_update), textSize, textSize * 2, paint);
            return;
        }
        float multiplier = (canvas.getHeight() - textSize) / HIGH_FPS;
        paint.setStrokeWidth(1);
        paint.setColor(colors[3]);
        canvas.drawLine(0, yOffset, maxWidth, yOffset, paint);
        paint.setStrokeWidth(strokeWidth);
        int xOffset = 0, high = 0, middle = 0, low = 0, critical = 0;
        for (Integer current : fpsHistory) {
            if (current >= HIGH_FPS) {
                paint.setColor(colors[3]);
                high++;
            } else if (current >= MEDIUM_FPS) {
                paint.setColor(colors[2]);
                middle++;
            } else if (current >= LOW_FPS) {
                paint.setColor(colors[1]);
                low++;
            } else {
                paint.setColor(colors[0]);
                critical++;
            }
            xOffset += strokeWidth;
            canvas.drawLine(xOffset, yOffset, xOffset, yOffset - (HIGH_FPS - current) * multiplier, paint);
        }

        maxWidth += 8;
        //draw legend
        paint.setColor(colors[3]);
        canvas.drawText(String.format(getContext().getString(R.string.fps_graph_label_more), high, HIGH_FPS), maxWidth, textSize, paint);
        paint.setColor(colors[2]);
        canvas.drawText(String.format(getContext().getString(R.string.fps_graph_label_more), middle, MEDIUM_FPS), maxWidth, textSize * 2, paint);
        paint.setColor(colors[1]);
        canvas.drawText(String.format(getContext().getString(R.string.fps_graph_label_more), low, LOW_FPS), maxWidth, textSize * 3, paint);
        paint.setColor(colors[0]);
        canvas.drawText(String.format(getContext().getString(R.string.fps_graph_label_less), critical, LOW_FPS), maxWidth, textSize * 4, paint);
    }

    public void addValue(int fps) {
        fpsHistory.add(fps);
        if (refresh || (++updateTimer % UPDATE_TIMEOUT == 0))
            postInvalidate();
    }
}

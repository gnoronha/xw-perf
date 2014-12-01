package com.collabora.xwperf.notxw_social;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

public class RoundedAvatarDrawable extends Drawable {
    private final Bitmap bmp;
    private final Paint paint;
    private final RectF rectF;
    private final int bmpW;
    private final int bmpH;

    public RoundedAvatarDrawable(Bitmap bitmap) {
        this.bmp = bitmap;
        rectF = new RectF();
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        final BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        paint.setShader(shader);
        // NOTE: we assume bitmap is properly scaled to current density
        bmpW = this.bmp.getWidth();
        bmpH = this.bmp.getHeight();
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawOval(rectF, paint);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        rectF.set(bounds);
    }

    @Override
    public void setAlpha(int alpha) {
        if (paint.getAlpha() != alpha) {
            paint.setAlpha(alpha);
            invalidateSelf();
        }
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        paint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public int getIntrinsicWidth() {
        return bmpW;
    }

    @Override
    public int getIntrinsicHeight() {
        return bmpH;
    }

    @Override
    public void setFilterBitmap(boolean filter) {
        paint.setFilterBitmap(filter);
        invalidateSelf();
    }

    @Override
    public void setDither(boolean dither) {
        paint.setDither(dither);
        invalidateSelf();
    }
}

package com.airsoft.goodwin.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.airsoft.goodwin.R;

public class ImageryView extends ImageView {
    private Paint p;
    private float density;
    private String mainText;
    private String secondaryText;

    public ImageryView(Context context) {
        super(context);
        initialize();
    }

    public ImageryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public ImageryView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    private void initialize() {
        p = new Paint();
        density = getContext().getResources().getDisplayMetrics().density;
        mainText = "Main text";
        secondaryText = "Secondary text";
    }

    public void setMainText(String text) {
        this.mainText = text;
    }

    public void setSecondaryText(String text) {
        this.secondaryText = text;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        Drawable drawable = getDrawable();

        if (drawable == null) {
            return;
        }

        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }
        Bitmap b = ((BitmapDrawable) drawable).getBitmap();
        Bitmap bitmap = b.copy(Bitmap.Config.ARGB_8888, true);

        int w = getWidth(), h = getHeight();

        //int dominantColor = ColorUtils.getAverageColor(drawable);
        int backgroundColor = ContextCompat.getColor(getContext(), R.color.colorAccent);
        int gradientStartColor = ColorUtils.getSaturationDeltaColor(backgroundColor, 0.5f);

        float cropedBitmapRadius = density * 40;

        Shader shader = new RadialGradient(h / 2, h / 2, w, gradientStartColor, backgroundColor, Shader.TileMode.CLAMP);
        p.setAntiAlias(true);
        p.setShader(shader);
        p.setColor(backgroundColor);
        canvas.drawRect(0, 0, w, h, p);

        Bitmap cropped = RoundedImageView.getCroppedBitmap(bitmap, (int) cropedBitmapRadius * 2);
        int cx = (int) (density * 16 + cropedBitmapRadius);
        int cy = (int) (density * 16 + cropedBitmapRadius);
        p.reset();
        p.setAntiAlias(true);
        p.setColor(Color.WHITE);
        canvas.drawCircle(cx, cy, (int) (cropedBitmapRadius + cropedBitmapRadius * 0.05), p);
        canvas.drawBitmap(cropped, cx - cropped.getWidth() / 2, cy - cropped.getHeight() / 2, p);

        p.reset();
        p.setSubpixelText(true);
        p.setStyle(Paint.Style.FILL);
        p.setAntiAlias(true);
        p.setColor(ContextCompat.getColor(getContext(), R.color.textColor));
        p.setTextSize(18 * density + 0.5f);
        canvas.drawText(mainText, cx + density * 16 + cropedBitmapRadius, h - density * 22 - density * 30, p);

        p.setTextSize(14 * density + 0.5f);
        canvas.drawText(secondaryText, cx + density * 16 + cropedBitmapRadius, h - density * 18 - density * 12, p);
    }
}

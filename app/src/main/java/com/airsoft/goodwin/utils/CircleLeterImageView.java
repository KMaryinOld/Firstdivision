package com.airsoft.goodwin.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

public class CircleLeterImageView extends ImageView {
    private int circleColor;
    private char letter;

    Paint p;

    public CircleLeterImageView(Context context) {
        super(context);

        this.circleColor = 0xFFFF0000;
        this.letter = 'A';

        init();
    }

    public CircleLeterImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.circleColor = 0xFFFF0000;
        this.letter = 'A';

        init();
    }

    public CircleLeterImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        this.circleColor = 0xFFFF0000;
        this.letter = 'A';

        init();
    }

    public CircleLeterImageView(Context context, int circleColor, char letter) {
        super(context);

        this.circleColor = circleColor;
        this.letter = letter;

        init();
    }

    private void init() {
        p = new Paint();
    }

    public void setCircleColor(int color) {
        circleColor = color;
    }

    public void setLetter(char letter) {
        this.letter = letter;
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawARGB(0, 0, 0, 0);
        p.setAntiAlias(true);

        int w = canvas.getWidth();
        int h = canvas.getHeight();
        int radius = Math.min(w, h) / 2;
        p.setColor(circleColor);

        canvas.drawCircle(w / 2, h / 2, radius, p);

        p.setColor(Color.WHITE);
        p.setTextSize(radius);
        p.setTextAlign(Paint.Align.CENTER);
        int xPos = w / 2;
        int yPos = (int) ((h / 2) - ((p.descent() + p.ascent()) / 2));
        canvas.drawText(String.format("%c", letter), xPos, yPos, p);
    }
}

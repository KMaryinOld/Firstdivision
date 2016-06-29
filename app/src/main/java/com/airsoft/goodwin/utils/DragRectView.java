package com.airsoft.goodwin.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.airsoft.goodwin.R;

public class DragRectView extends View {
    private Paint mRectPaint;

    public int mStartX = 0;
    public int mStartY = 0;
    public int mEndX = 0;
    public int mEndY = 0;
    private boolean mDrawRect = false;

    private OnUpCallback mCallback = null;

    private Rect mImageRect;

    private boolean mIsLocked;

    public interface OnUpCallback {
        void onRectFinished(Rect rect);
    }

    public DragRectView(final Context context) {
        super(context);
        init();
    }

    public DragRectView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DragRectView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    /**
     * Sets callback for up
     *
     * @param callback {@link OnUpCallback}
     */
    public void setOnUpCallback(OnUpCallback callback) {
        mCallback = callback;
    }

    /**
     * Inits internal data
     */
    private void init() {
        mRectPaint = new Paint();
        mRectPaint.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        mRectPaint.setStyle(Paint.Style.STROKE);
        mRectPaint.setStrokeWidth(5);

        mImageRect = new Rect(0, 0, 0, 0);

        mIsLocked = false;
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        if (mIsLocked) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDrawRect = false;
                mStartX = (int) event.getX();
                mStartY = (int) event.getY();
                invalidate();
                break;

            case MotionEvent.ACTION_MOVE:
                final int x = (int) event.getX();
                final int y = (int) event.getY();

                if (mStartX < mImageRect.left || mStartY < mImageRect.top) {
                    break;
                }

                if (!mDrawRect || Math.abs(x - mEndX) > 5 || Math.abs(y - mEndY) > 5) {
                    int deltaX = x - mStartX, deltaY = y - mStartY;
                    int side = Math.max(Math.abs(deltaX), Math.abs(deltaY));
                    int sideX = side, sideY = side;
                    if (deltaX < 0) {
                        sideX = -side;
                    }
                    if (deltaY < 0) {
                        sideY = -side;
                    }

                    int endX = mStartX + sideX, endY = mStartY + sideY;
                    if (endX < mImageRect.left || endX > mImageRect.right ||
                            endY < mImageRect.top || endY > mImageRect.bottom) {
                        break;
                    }

                    mEndX = endX;
                    mEndY = endY;
                    invalidate();
                }

                mDrawRect = true;
                break;

            case MotionEvent.ACTION_UP:
                if (mCallback != null) {
                    mCallback.onRectFinished(new Rect(Math.min(mStartX, mEndX), Math.min(mStartY, mEndY),
                            Math.max(mEndX, mStartX), Math.max(mEndY, mStartX)));
                }
                invalidate();
                break;

            default:
                break;
        }

        return true;
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);

        if (mDrawRect) {
            canvas.drawRect(Math.min(mStartX, mEndX), Math.min(mStartY, mEndY),
                    Math.max(mEndX, mStartX), Math.max(mEndY, mStartY), mRectPaint);
        }
    }

    public void setImageRect(Rect rect) {
        mImageRect = rect;

        int halfSide = Math.min(Math.abs(rect.left - rect.right), Math.abs(rect.top - rect.bottom)) / 2;
        mStartX = rect.centerX() - halfSide;
        mStartY = rect.centerY() - halfSide;
        mEndX = rect.centerX() + halfSide;
        mEndY = rect.centerY() + halfSide;

        mDrawRect = true;
    }

    public Rect getSelectedRect() {
        return new Rect(mStartX - mImageRect.left, mStartY - mImageRect.top,
                mEndX - mImageRect.left, mEndY - mImageRect.top);
    }

    public void lock() {
        mIsLocked = true;
    }

    public void unlock() {
        mIsLocked = false;
    }
}

package com.example.android.displaying3dobject;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class MyGLSurfaceView extends GLSurfaceView {

    private MyRenderer mMyRenderer;

    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private float mPreviousX;
    private float mPreviousY;

    private float mDensity;

    public MyGLSurfaceView(Context context,float density) {
        super(context);

        setEGLContextClientVersion(2);

        mMyRenderer = new MyRenderer(context);

        mDensity = density;

        setRenderer(mMyRenderer);


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event != null) {
            float x = event.getX();
            float y = event.getY();

            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                if (mMyRenderer != null) {
                    float deltaX = (x - mPreviousX) / mDensity / 2f;
                    float deltaY = (y - mPreviousY) / mDensity / 2f;

                    mMyRenderer.mDeltaX += deltaX;
                    mMyRenderer.mDeltaY += deltaY;
                }
            }

            mPreviousX = x;
            mPreviousY = y;

            return true;
        } else {
            return super.onTouchEvent(event);
        }
    }

}

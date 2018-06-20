package com.example.android.displaying3dobject;

import android.content.Context;
import android.opengl.GLSurfaceView;

public class MyGLSurfaceView extends GLSurfaceView {

    private MyRenderer mMyRenderer;


    public MyGLSurfaceView(Context context) {
        super(context);

        setEGLContextClientVersion(2);

        mMyRenderer = new MyRenderer();

        setRenderer(mMyRenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);


    }
}

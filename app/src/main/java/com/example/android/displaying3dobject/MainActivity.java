package com.example.android.displaying3dobject;

import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;

public class MainActivity extends AppCompatActivity {


    private GLSurfaceView mGLSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        mGLSurfaceView = new MyGLSurfaceView(this, displayMetrics.density);
        setContentView(mGLSurfaceView);


//        mGLSurfaceView.setRenderer(new ModelRenderer(this));
    }
}

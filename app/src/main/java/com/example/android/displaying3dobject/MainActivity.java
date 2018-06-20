package com.example.android.displaying3dobject;

import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {


    private GLSurfaceView mGLSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGLSurfaceView = new MyGLSurfaceView(this);
        setContentView(mGLSurfaceView);


//        mGLSurfaceView.setRenderer(new ModelRenderer(this));
    }
}

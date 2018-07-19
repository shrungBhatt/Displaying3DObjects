package com.example.android.displaying3dobject;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glClearDepthf;
import static android.opengl.GLES20.glCullFace;
import static android.opengl.GLES20.glDepthFunc;
import static android.opengl.GLES20.glDepthMask;
import static android.opengl.GLES20.glDepthRangef;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glFrontFace;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.setLookAtM;

public class MyRenderer implements GLSurfaceView.Renderer {


    private final Context mContext;

    public MyRenderer(Context context){
        mContext = context;
    }

    private Triangle mTriangle;
    private Square mSquare;
    private Cube mCube;
    private Sprite mSprite;
    private Torus mTorus;
    private Cube3 mCube3;

    // mMVPMatrix is an abbreviation for "Model View Projection Matrix"
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private final float[] mViewProjectionMatrix = new float[16];
    private float[] mModelMatrix = new float[16];
    private final float[] mAccumulatedRotation = new float[16];
    private final float[] mCurrentRotation = new float[16];
    private float[] mTemporaryMatrix = new float[16];

    public volatile float mDeltaX;
    public volatile float mDeltaY;


    public volatile float mAngle;

    public float getAngle() {
        return mAngle;
    }

    public void setAngle(float angle) {
        mAngle = angle;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        mTriangle = new Triangle();
        mSquare = new Square();
        mCube = new Cube(mContext);
        mSprite = new Sprite(mContext);
        mTorus = new Torus(mContext);
        mCube3 = new Cube3(mContext);

        reset();

        Matrix.setIdentityM(mAccumulatedRotation, 0);


    }



    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {

        GLES20.glViewport(0, 0, width, height);


        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method

        MatrixHelper.perspectiveM(mProjectionMatrix, 45,
                (float) width / (float) height, 1f, 10f);

        setLookAtM(mViewMatrix, 0, 0f, 1.2f, 2.2f, 0f, 0f, 0f,
                0f, 1f, 0f);

    }

    @Override
    public void onDrawFrame(GL10 gl10) {

        // Create a rotation transformation for the triangle
        long time = SystemClock.uptimeMillis() % 4000L;
        float angle = 0.090f * ((int) time);


        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);


        multiplyMM(mViewProjectionMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);




        Matrix.setIdentityM(mModelMatrix,0);
//        Matrix.setRotateM(mModelMatrix, 0, getAngle(), 0f, 1f, 0f);
        Matrix.scaleM(mModelMatrix,0,0.3f,0.3f,0.3f);

        // Set a matrix that contains the current rotation.
        Matrix.setIdentityM(mCurrentRotation, 0);
        Matrix.rotateM(mCurrentRotation, 0, mDeltaX, 0.0f, 1.0f, 0.0f);
        Matrix.rotateM(mCurrentRotation, 0, mDeltaY, 1.0f, 0.0f, 0.0f);
        mDeltaX = 0.0f;
        mDeltaY = 0.0f;

        // Multiply the current rotation by the accumulated rotation, and then set the accumulated rotation to the result.
        Matrix.multiplyMM(mTemporaryMatrix, 0, mCurrentRotation, 0, mAccumulatedRotation, 0);
        System.arraycopy(mTemporaryMatrix, 0, mAccumulatedRotation, 0, 16);

        // Rotate the cube taking the overall rotation into account.
        Matrix.multiplyMM(mTemporaryMatrix, 0, mModelMatrix, 0, mAccumulatedRotation, 0);
        System.arraycopy(mTemporaryMatrix, 0, mModelMatrix, 0, 16);


        // Combine the rotation matrix with the projection and camera view
        // Note that the mMVPMatrix factor *must be first* in order
        // for the matrix multiplication product to be correct.
        Matrix.multiplyMM(mMVPMatrix, 0, mViewProjectionMatrix, 0, mModelMatrix, 0);


//        mCube.draw(mMVPMatrix);

//        mTorus.draw(mMVPMatrix);

        mCube3.draw(mMVPMatrix);
    }

    public static int loadShader(int type, String shaderCode) {

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    //These is to give initial settings to opengl.
    private void reset(){

        glEnable(GL10.GL_DEPTH_TEST);
        glClearDepthf(1.0f);
        glDepthFunc(GL10.GL_LESS);
        glDepthRangef(0, 1f);
        glDepthMask(true);

        glEnable(GLES20.GL_BLEND);
        glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        glFrontFace(GLES20.GL_CCW);
        glCullFace(GLES20.GL_BACK);
        glEnable(GLES20.GL_CULL_FACE);

    }
}

package com.example.android.displaying3dobject;

import android.opengl.GLES10;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Square {

    private FloatBuffer vertexBuffer;
    private ShortBuffer drawListBuffer;
    private final int mProgram;

    private int mMVPMatrixHandle;


    float color[] = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };


    private int mPositionHandle;
    private int mColorHandle;

    private final String vertexShaderCode =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;"+
                    "void main() {"+
                    " gl_Position = uMVPMatrix * vPosition;"+
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;"+
                    "uniform vec4 vColor;"+
                    "void main() {" +
                    " gl_FragColor = vColor;" +
                    "}";

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    static float squareCoords[] = {
            -0.2f,  0.2f, 0.0f,   // top left
            -0.2f, -0.2f, 0.0f,   // bottom left
            0.2f, -0.2f, 0.0f,   // bottom right
            0.2f,  0.1f, 0.0f }; // top right

    private short drawOrder[] = { 0, 1, 2, 0, 2, 3 }; // order to draw vertices

    private final int vertexCount = squareCoords.length / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    public Square() {

        int vertexShader = MyRenderer.loadShader(GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);

        int fragmentShader = MyRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();

        // add the vertex shader to program
        GLES20.glAttachShader(mProgram, vertexShader);

        // add the fragment shader to program
        GLES20.glAttachShader(mProgram, fragmentShader);

        // creates OpenGL ES program executables
        GLES20.glLinkProgram(mProgram);


        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 4 bytes per float)
                squareCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(squareCoords);
        vertexBuffer.position(0);

        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 2 bytes per short)
                drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);
    }

    public void draw(float[] mvpMatrix){

        GLES20.glUseProgram(mProgram);

        mPositionHandle = GLES20.glGetAttribLocation(mProgram,"vPosition");

        GLES20.glEnableVertexAttribArray(mPositionHandle);

        GLES20.glVertexAttribPointer(mPositionHandle,COORDS_PER_VERTEX,
                GLES20.GL_FLOAT,false,vertexStride,vertexBuffer);

        mColorHandle = GLES20.glGetUniformLocation(mProgram,"vColor");


        GLES20.glUniform4fv(mColorHandle,1,color,0);

        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        // Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);


        // Draw the triangle
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length,
                GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);

    }
}

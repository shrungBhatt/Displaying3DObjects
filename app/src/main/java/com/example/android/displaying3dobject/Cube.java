package com.example.android.displaying3dobject;

import android.opengl.GLES20;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.*;

public class Cube {

    private FloatBuffer vertexBuffer;
    private ShortBuffer drawListBuffer;
    private final int mProgram;

    private int mMVPMatrixHandle;


    float color[] = {
            1.0f, 0.5f, 0.0f, 1.0f,  // 0. orange
            1.0f, 0.0f, 1.0f, 1.0f,  // 1. violet
            0.0f, 1.0f, 0.0f, 1.0f,  // 2. green
            0.0f, 0.0f, 1.0f, 1.0f,  // 3. blue
            1.0f, 0.0f, 0.0f, 1.0f,  // 4. red
            1.0f, 1.0f, 0.0f, 1.0f   // 5. yellow
    };


    private int mPositionHandle;
    private int mColorHandle;

    private final String vertexShaderCode =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    " gl_Position = uMVPMatrix * vPosition;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    " gl_FragColor = vColor;" +
                    "}";

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    static float squareCoords[] = {  // Vertices of the 6 faces
            // FRONT
            -0.4f, -0.4f,  0.4f,  // 0. left-bottom-front
            0.4f, -0.4f,  0.4f,  // 1. right-bottom-front
            -0.4f,  0.4f,  0.4f,  // 2. left-top-front
            0.4f,  0.4f,  0.4f,  // 3. right-top-front
            // BACK
            0.4f, -0.4f, -0.4f,  // 6. right-bottom-back
            -0.4f, -0.4f, -0.4f,  // 4. left-bottom-back
            0.4f,  0.4f, -0.4f,  // 7. right-top-back
            -0.4f,  0.4f, -0.4f,  // 5. left-top-back
            // LEFT
            -0.4f, -0.4f, -0.4f,  // 4. left-bottom-back
            -0.4f, -0.4f,  0.4f,  // 0. left-bottom-front
            -0.4f,  0.4f, -0.4f,  // 5. left-top-back
            -0.4f,  0.4f,  0.4f,  // 2. left-top-front
            // RIGHT
            0.4f, -0.4f,  0.4f,  // 1. right-bottom-front
            0.4f, -0.4f, -0.4f,  // 6. right-bottom-back
            0.4f,  0.4f,  0.4f,  // 3. right-top-front
            0.4f,  0.4f, -0.4f,  // 7. right-top-back
            // TOP
            -0.4f,  0.4f,  0.4f,  // 2. left-top-front
            0.4f,  0.4f,  0.4f,  // 3. right-top-front
            -0.4f,  0.4f, -0.4f,  // 5. left-top-back
            0.4f,  0.4f, -0.4f,  // 7. right-top-back
            // BOTTOM
            -0.4f, -0.4f, -0.4f,  // 4. left-bottom-back
            0.4f, -0.4f, -0.4f,  // 6. right-bottom-back
            -0.4f, -0.4f,  0.4f,  // 0. left-bottom-front
            0.4f, -0.4f,  0.4f   // 1. right-bottom-front
    };

    private short drawOrder[] = {
            0, 1, 2, 2, 3, 0, 1, 4, 7, 7, 2, 1, 0, 3, 6, 6, 5, 0,
            3, 2, 7, 7, 6, 3, 0, 1, 4, 4, 5, 0, 5, 6, 7, 7, 4, 5
    }; // order to draw vertices

    private final int vertexCount = squareCoords.length / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    public Cube() {

        int vertexShader = MyRenderer.loadShader(GL_VERTEX_SHADER,
                vertexShaderCode);

        int fragmentShader = MyRenderer.loadShader(GL_FRAGMENT_SHADER,
                fragmentShaderCode);

        mProgram = glCreateProgram();

        // add the vertex shader to program
        glAttachShader(mProgram, vertexShader);

        // add the fragment shader to program
        glAttachShader(mProgram, fragmentShader);

        // creates OpenGL ES program executables
        glLinkProgram(mProgram);


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

    public void colorTheCube(int mColorHandle){

        glUniform4f(mColorHandle,1.0f, 0.0f, 0.0f, 0.0f); //RED
        glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
        //--- RIGHT
        glUniform4f(mColorHandle,0.0f, 1.0f, 0.0f, 0.0f); // GREEN
        glDrawArrays(GL10.GL_TRIANGLE_STRIP, 4, 4);
        //--- BACK
        glUniform4f(mColorHandle,0.0f, 0.0f, 1.0f, 0.0f); // BLUE
        glDrawArrays(GL10.GL_TRIANGLE_STRIP, 8, 4);
        //--- LEFT
        glUniform4f(mColorHandle,1.0f, 1.0f, 0.0f, 0.0f); // YELLOW
        glDrawArrays(GL10.GL_TRIANGLE_STRIP, 12, 4);
        //--- TOP
        glUniform4f(mColorHandle,1.0f, 0.5f, 0.0f, 0.0f); // ORANGE
        glDrawArrays(GL10.GL_TRIANGLE_STRIP, 16, 4);
        //--- BOTTOM
        glUniform4f(mColorHandle,1.0f, 0.0f, 1.0f, 0.0f); // PURPLE
        glDrawArrays(GL10.GL_TRIANGLE_STRIP, 20, 4);

    }

    public void draw(float[] mvpMatrix) {

        glUseProgram(mProgram);

        mPositionHandle = glGetAttribLocation(mProgram, "vPosition");

        glEnableVertexAttribArray(mPositionHandle);

        glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                GL_FLOAT, false, 0, vertexBuffer);

        mColorHandle = glGetUniformLocation(mProgram, "vColor");


        glUniform4fv(mColorHandle, 1, color, 0);

        // get handle to shape's transformation matrix
        mMVPMatrixHandle = glGetUniformLocation(mProgram, "uMVPMatrix");

        // Pass the projection and view transformation to the shader
        glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);


        // Draw the triangle
//        glDrawElements(GL_TRIANGLES, drawOrder.length,
//                GL_UNSIGNED_SHORT, drawListBuffer);
        colorTheCube(mColorHandle);


        // Disable vertex array
        glDisableVertexAttribArray(mPositionHandle);

    }
}

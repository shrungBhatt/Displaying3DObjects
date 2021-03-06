package com.example.android.displaying3dobject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;


import static android.opengl.GLES20.*;

public class Cube {

    private final FloatBuffer mCubeTextureCoordinates;
    private FloatBuffer vertexBuffer;
    private ShortBuffer drawListBuffer;
    private final int mProgram;

    private int mMVPMatrixHandle;

    private int mTextureHandle;
    private int mTextureUniformHandle;
    private int mTextureCoordinateHandle;
    private int mAttributeColorLocation;
    private final int mTextureCoordinateDataSize = 2;




    float color[] = {1.0f, 1.0f, 1.0f, 1.0f};


    private int mPositionHandle;
    private int mColorHandle;

    private final String vertexShaderCode =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec2 a_TexCoordinate;" +
                    "varying vec2 v_TexCoordinate;" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    "   gl_Position = uMVPMatrix * vPosition;" +
                    "   v_TexCoordinate = a_TexCoordinate;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "uniform sampler2D u_Texture;" +
                    "varying vec2 v_TexCoordinate;" +
                    "void main() {" +
                    "gl_FragColor = (vColor * texture2D(u_Texture, v_TexCoordinate));" +
                    "}";


/*    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "   uniform vec4 vColor;" +
                    "   void main() {" +
                    "   gl_FragColor = vColor;" +
                    "}";*/

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;


    static float squareCoords[] = {
            0.462329f, -0.462329f, -0.462329f,
            0.462329f, -0.462329f, 0.462329f,
            -0.462329f, -0.462329f, 0.462329f,
            -0.462329f, -0.462329f, -0.462330f,
            0.462330f, 0.462329f, -0.462329f,
            0.462329f, 0.462329f, 0.462330f,
            -0.462330f, 0.462329f, 0.462329f,
            -0.462329f, 0.462329f, -0.462329f
    };

    private short drawOrder[] = {
            1,3,0,
            7,6,4,
            4,1,0,
            5,2,1,
            2,7,3,
            0,7,4,
            1,2,3,
            7,6,5,
            4,5,1,
            5,6,2,
            2,6,7,
            0,3,7
    }; // order to draw vertices

    final float[] cubeTextureCoordinateData = {
            0.250280f, 0.333247f,
            0.502659f, 0.004190f,
            0.502659f, 0.331195f,
            0.247697f, 0.990145f,
            0.496003f, 0.666876f,
            0.495436f, 0.990144f,
            0.499570f, 0.655298f,
            0.250280f, 0.332395f,
            0.499570f, 0.336497f,
            0.242922f, 0.664524f,
            0.000087f, 0.332220f,
            0.242922f, 0.335476f,
            0.750272f, 0.667780f,
            0.993107f, 0.332220f,
            0.995559f, 0.661268f,
            0.749175f, 0.666759f,
            0.500473f, 0.332395f,
            0.749763f, 0.334087f,
            0.250280f, 0.002138f,
            0.248923f, 0.670866f,
            0.247191f, 0.663503f,
            0.002538f, 0.664524f,
            0.747820f, 0.335476f,
            0.501699f, 0.665913f
    };

    private final int vertexCount = squareCoords.length / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    public Cube(Context context) {

        int vertexShader = MyRenderer.loadShader(GL_VERTEX_SHADER,
                vertexShaderCode);

        int fragmentShader = MyRenderer.loadShader(GL_FRAGMENT_SHADER,
                fragmentShaderCode);

        mProgram = glCreateProgram();

        // add the vertex shader to program
        glAttachShader(mProgram, vertexShader);

        // add the fragment shader to program
        glAttachShader(mProgram, fragmentShader);

        GLES20.glBindAttribLocation(mProgram, 0, "a_TexCoordinate");


        // creates OpenGL ES program executables
        glLinkProgram(mProgram);

        mTextureHandle = loadTexture(context, R.drawable.images);


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

        mCubeTextureCoordinates = ByteBuffer.allocateDirect(cubeTextureCoordinateData.length * 4).
                order(ByteOrder.nativeOrder()).asFloatBuffer();
        mCubeTextureCoordinates.put(cubeTextureCoordinateData).position(0);
    }


    public void draw(float[] mvpMatrix) {

        glUseProgram(mProgram);

        mPositionHandle = glGetAttribLocation(mProgram, "vPosition");



        glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                GL_FLOAT, false, 0, vertexBuffer);

        glEnableVertexAttribArray(mPositionHandle);


        mColorHandle = glGetUniformLocation(mProgram, "vColor");

        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        mTextureUniformHandle = GLES20.glGetAttribLocation(mProgram, "u_Texture");
        mTextureCoordinateHandle = GLES20.glGetAttribLocation(mProgram, "a_TexCoordinate");


        //Set the active texture unit to texture unit 0.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        //Bind the texture to this unit.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureHandle);

        //Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        GLES20.glUniform1i(mTextureUniformHandle, 0);

        //Pass in the texture coordinate information
        mCubeTextureCoordinates.position(0);
        GLES20.glVertexAttribPointer(mTextureCoordinateHandle, mTextureCoordinateDataSize, GLES20.GL_FLOAT,
                false, 8, mCubeTextureCoordinates);
        GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);

        // get handle to shape's transformation matrix
        mMVPMatrixHandle = glGetUniformLocation(mProgram, "uMVPMatrix");

        // Pass the projection and view transformation to the shader
        glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);


//         Draw the triangle
        glDrawElements(GL_TRIANGLES, drawOrder.length,
                GL_UNSIGNED_SHORT, drawListBuffer);
//        colorTheCube(mColorHandle);


        // Disable vertex array
        glDisableVertexAttribArray(mPositionHandle);

    }

    public static int loadTexture(final Context context, final int resourceId) {
        final int[] textureHandle = new int[1];

        GLES20.glGenTextures(1, textureHandle, 0);

        if (textureHandle[0] != 0) {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;   // No pre-scaling

            // Read in the resource
            final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);

            // Bind to the texture in OpenGL
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

            // Set filtering
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

            // Load the bitmap into the bound texture.
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

            // Recycle the bitmap, since its data has been loaded into OpenGL.
            bitmap.recycle();
        }

        if (textureHandle[0] == 0) {
            throw new RuntimeException("Error loading texture.");
        }

        return textureHandle[0];
    }
}

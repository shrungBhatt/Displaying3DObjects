package com.example.android.displaying3dobject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import min3d.Shared;
import min3d.core.FacesBufferedList;
import min3d.core.Object3d;
import min3d.core.Object3dContainer;
import min3d.parser.IParser;
import min3d.parser.Parser;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_UNSIGNED_SHORT;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glDisableVertexAttribArray;
import static android.opengl.GLES20.glDrawElements;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;

public class Cube3 {

    private final FloatBuffer textureVerticesBuffer;
    private final FloatBuffer vertexBuffer;
    private final ShortBuffer drawListBuffer;
    private final int mProgram;
    private final Context mContext;

    private int mMVPMatrixHandle;

    private int mTextureHandle;
    private int mTextureUniformHandle;
    private int mTextureCoordinateHandle;
    private int mAttributeColorLocation;
    private final int mTextureCoordinateDataSize = 2;



    int pos, len;


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




//    private final int vertexCount = squareCoords.length / COORDS_PER_VERTEX;
//    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    public Cube3(Context context) {

        mContext = context;
        Shared.context(context);

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

        mTextureHandle = loadTexture(context, R.drawable.merge_from_ofoct);

        Object3d object3d = getObeject3d();

        vertexBuffer = object3d.vertices().points().buffer();
        vertexBuffer.position(0);

        if (!object3d.faces().renderSubsetEnabled()) {
            pos = 0;
            len = object3d.faces().size();
        } else {
            pos = object3d.faces().renderSubsetStartIndex() * FacesBufferedList.PROPERTIES_PER_ELEMENT;
            len = object3d.faces().renderSubsetLength();
        }

        drawListBuffer = object3d.faces().buffer();
        drawListBuffer.position(pos);

        textureVerticesBuffer = object3d.vertices().uvs().buffer();
        textureVerticesBuffer.position(0);

    }

    private Object3d getObeject3d(){
        IParser myParser = Parser.createParser(Parser.Type.OBJ, mContext.getResources(),
                "com.example.android.displaying3dobject:raw/brakes_obj", false);
        myParser.parse();
        Object3dContainer faceObject3D = myParser.getParsedObject();

        return faceObject3D.getChildAt(0);
    }


    public void draw(float[] mvpMatrix) {

        glUseProgram(mProgram);

        mPositionHandle = glGetAttribLocation(mProgram, "vPosition");



        glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                GL_FLOAT, false, 0, vertexBuffer);

        glEnableVertexAttribArray(mPositionHandle);


        mColorHandle = glGetUniformLocation(mProgram, "vColor");

        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        mTextureUniformHandle = GLES20.glGetUniformLocation(mProgram, "u_Texture");
        mTextureCoordinateHandle = GLES20.glGetAttribLocation(mProgram, "a_TexCoordinate");


        //Set the active texture unit to texture unit 0.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        //Bind the texture to this unit.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureHandle);

        //Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        GLES20.glUniform1i(mTextureUniformHandle, 0);

        //Pass in the texture coordinate information
        GLES20.glVertexAttribPointer(mTextureCoordinateHandle, mTextureCoordinateDataSize, GLES20.GL_FLOAT,
                false, 0, textureVerticesBuffer);
        GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);

        // get handle to shape's transformation matrix
        mMVPMatrixHandle = glGetUniformLocation(mProgram, "uMVPMatrix");

        // Pass the projection and view transformation to the shader
        glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);




//         Draw the triangle
        glDrawElements(GL_TRIANGLES,len * FacesBufferedList.PROPERTIES_PER_ELEMENT,
                GL_UNSIGNED_SHORT,drawListBuffer);

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
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
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

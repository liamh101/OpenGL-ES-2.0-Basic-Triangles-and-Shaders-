package liamhackett.com.opengltest;

import android.app.Activity;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Liam on 30/06/2015.
 */
public class OpenGLRenderer implements GLSurfaceView.Renderer{

    //Store Triangles points that will be rendered on the screen.
    private final FloatBuffer mTriangle1Vertices;
   // private final FloatBuffer mTriangle2Vertices;
   // private final FloatBuffer mTriangle3Vertices;

    private final int mBytesPerFloat = 4;

    public OpenGLRenderer(){
        mViewMatrix = new float[16];

        //Size and colour information for the buffer
        final float[] triangle1VerticicesData = {
                //X,Y,Z
                //R,G,B,A
                -0.5f, -0.25f, 0.0f,
                1.0f, 0.0f, 0.0f, 1.0f,

                0.5f, -0.25f, 0.0f,
                0.0f, 0.0f, 0.1f, 0.1f,

                0.0f, 0.559016994f, 0.0f,
                0.0f, 1.0f, 0.0f, 1.0f
        };

        //Instigates the buffers
        mTriangle1Vertices = ByteBuffer.allocateDirect(triangle1VerticicesData.length * mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();

        mTriangle1Vertices.put(triangle1VerticicesData).position(0);

    }

    private float[] mViewMatrix;

    private int mMVPMatrixHandle;

    private int mPositionHandle;

    private int mColourHandle;

    /**Store the view Matrix to use as are camera.
     *
     * @param gl
     * @param config
     */
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        // Set the background colour of the render
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 0.5f);

        //Set Camera behind the origin
        final float eyeX = 0.0f;
        final float eyeY = 0.0f;
        final float eyeZ = 1.5f;

        //Set up distance view
        final float lookX = 0.0f;
        final float lookY = 0.0f;
        final float lookZ = 5.0f;

        // Set our vector. This is where are head would be if we were holding the camera in real life.
        final float upX = 0.0f;
        final float upY = 1.0f;
        final float upZ = 0.0f;

        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);

        final String vertexShader =
                "uniform mat4 u_MVPMatrix;      \n" //A constant the represents the Model, View and Projection matrix
                        + "attribute vec4 a_Position;   \n" //Pre-vertex position information we're going to pass in
                        + "attribute vec4 a_Color;      \n" //Pre-vertex colour information we're going to pass in

                        + "varying vec4 v_Color;        \n"//This will be passed into the fragment shader

                + "void main(){     \n"
                + "     v_Color = a_Color;      \n"//Pass the colour through to the fragment shader, this will be spread across the triangle

                + "     gl_Position = u_MVPMatrix * a_Position;     \n" //gl_Position will be used to store the final position.
                + "}      \n"; //Multiply the vertex by the matrix to get the final point in normal screen position in normalized screen coordinates


        final String fragmentShader =
                "precision mediump float;   \n"// set the default position as medium. We don't need too much for a fragment shader
                + "varying vec4 v_Color;    \n"// This is the colour from the vertex shader used to be spread across the whole triangle.
                + "void main(){             \n"
                + "     gl_FragColor = v_Color;\n"//Pull the colour directory through the pipeline.
                + "}                        \n";


        int vertexShaderHandle = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);

        if(vertexShaderHandle != 0){
            //Pass in the shader source
            GLES20.glShaderSource(vertexShaderHandle, vertexShader);

            //Compile shader code
            GLES20.glCompileShader(vertexShaderHandle);


            final int[] compiledStatus = new int[1];

            //Check to see if shader has compiled properly
            GLES20.glGetShaderiv(vertexShaderHandle, GLES20.GL_COMPILE_STATUS, compiledStatus, 0);

            //Else remove references to shader
            if(compiledStatus[0] == 0){
                GLES20.glDeleteShader(vertexShaderHandle);
                vertexShaderHandle = 0;
            }
        }

        if(vertexShaderHandle == 0)
            throw new RuntimeException("Error creating vertex shader");

        int fragmentShaderHandle = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);

        if(fragmentShaderHandle != 0 ){

            //Pass in the Shader source
            GLES20.glShaderSource(fragmentShaderHandle, fragmentShader);

            //Compile shader
            GLES20.glCompileShader(fragmentShaderHandle);

            final int[] compiledStatus = new int[1];

            //Check to see if shader has compiled properly
            GLES20.glGetShaderiv(fragmentShaderHandle, GLES20.GL_COMPILE_STATUS, compiledStatus, 0);

            //Else remove references to shader
            if(compiledStatus[0] == 0){
                GLES20.glDeleteShader(fragmentShaderHandle);
                fragmentShaderHandle = 0;
            }
        }

        if(fragmentShaderHandle == 0)
            throw new RuntimeException("Error creating fragment shader");


        int programHandle = GLES20.glCreateProgram();

        if(programHandle != 0){

            //Bind vertex shader to program
            GLES20.glAttachShader(programHandle, vertexShaderHandle);

            //Bind fragment shader to program
            GLES20.glAttachShader(programHandle, fragmentShaderHandle);

            //Bind Attributes
            GLES20.glBindAttribLocation(programHandle, 0, "a_Position");
            GLES20.glBindAttribLocation(programHandle, 1, "a_Color");

            //Link the two shaders together into the same program
            GLES20.glLinkProgram(programHandle);

            final int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);

            if(linkStatus[0] == 0 ){
                GLES20.glDeleteProgram(programHandle);
                programHandle = 0;
            }
        }

        if(programHandle == 0)
            throw new RuntimeException("Error creating shader program.");


        //Set Program handles
        mMVPMatrixHandle = GLES20.glGetUniformLocation(programHandle, "u_MPVMatrix");
        mPositionHandle = GLES20.glGetAttribLocation(programHandle, "a_Position");
        mColourHandle = GLES20.glGetAttribLocation(programHandle, "a_Color");

        GLES20.glUseProgram(programHandle);

    }

    //Projection Matix is used to project 3D images onto a 2D view port
    private float[] mProjectionMatrix = new float[16];

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

        //Set the viewport to the same size of the phone screen
        GLES20.glViewport(0, 0, width, height);

        //Set new perceptive projection matix. The height will remain the same while the width will change based on aspect radio
        final float ratio = (float) width / height;
        final float left = -ratio;
        final float right = ratio;
        final float bottom = -1.0f;
        final float top = 1.0f;
        final float near = 1.0f;
        final float far = 10.0f;

        Matrix.frustumM(mProjectionMatrix, 0 , left, right, bottom, top, near, far);
    }

    private float[] mModelMatrix = new float[16];

    @Override
    public void onDrawFrame(GL10 gl) {

        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

        long time = SystemClock.uptimeMillis() % 10000L;
        float angleInDegrees = (360.0f / 10000.0f) *((int)time);

        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.rotateM(mModelMatrix, 0, angleInDegrees, 0.0f, 0.0f, 1.0f);
        DrawTriangle(mTriangle1Vertices);
    }

    private float[] mMVPMatrix = new float[16];

    // How many elements per Vertex
    private final int mStrideBytes = 7 * mBytesPerFloat;

    // offset of the position data
    private final int mPositionOffset = 0;

    //offset of the position elements in data
    private final int mPositionDataSize = 3;

    // Offset of colour data
    private final int mColourOffset = 3;

    // Size of the colour data in elements
    private final int mColourDataOffset = 4;

    private void DrawTriangle(final FloatBuffer aTriangleBuffer){

        // Pass in the position information
        aTriangleBuffer.position(mPositionOffset);
        GLES20.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT, false, mStrideBytes, aTriangleBuffer);

        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Pass in colour infomation
        aTriangleBuffer.position(mColourOffset);
        GLES20.glVertexAttribPointer(mColourHandle, mColourDataOffset, GLES20.GL_FLOAT, false, mStrideBytes, aTriangleBuffer);

        GLES20.glEnableVertexAttribArray(mColourHandle);

        //Multiply the view matrix by the model matrix and store the result in a MVP matrix
        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);

        //Multiply the projection matrix by the viewmodel matrix and stores the result back into the MVP matrix
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);

        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);




    }

}

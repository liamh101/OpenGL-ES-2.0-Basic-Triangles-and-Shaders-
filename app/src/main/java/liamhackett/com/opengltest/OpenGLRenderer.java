package liamhackett.com.opengltest;

import android.app.Activity;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

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
    private final FloatBuffer mTriangle2Vertices;
    private final FloatBuffer mTriangle3Vertices;

    private final int mBytesPerFloat;

    public OpenGLRenderer(){
        mBytesPerFloat = 4;
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
            GLES20.glAttachShader(fragmentShaderHandle, vertexShaderHandle);

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
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

    }

    @Override
    public void onDrawFrame(GL10 gl) {

    }
}

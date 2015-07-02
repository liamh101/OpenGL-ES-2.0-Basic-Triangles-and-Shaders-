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
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

    }

    @Override
    public void onDrawFrame(GL10 gl) {

    }
}

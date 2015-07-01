package liamhackett.com.opengltest;

import android.app.Activity;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

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

        GLES20.glClearColor(0.5f,0.5f,0.5f,0.5f);


    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

    }

    @Override
    public void onDrawFrame(GL10 gl) {

    }
}

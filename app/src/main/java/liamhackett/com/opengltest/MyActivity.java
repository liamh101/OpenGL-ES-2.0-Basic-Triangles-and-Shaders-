package liamhackett.com.opengltest;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

/**
 * Created by Liam on 30/06/2015.
 */
public class MyActivity extends Activity {

    private GLSurfaceView mGLSurfaceView;

    @Override
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);

        mGLSurfaceView = new GLSurfaceView(this);

        //Check to see if device supports OpenGL ES 2.0

        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        final Boolean supportsES2 = configurationInfo.reqGlEsVersion >= 0x20000;

        if(supportsES2){

            //Request GL compatible context
            mGLSurfaceView.setEGLContextClientVersion(2);

            //Set Renderer for App
            //mGLSurfaceView.setRenderer();


        }
        else{
            // Insert custom renderer here.
            return;
        }

        setContentView(mGLSurfaceView);



    }
}

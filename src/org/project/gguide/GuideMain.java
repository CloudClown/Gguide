package org.project.gguide;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.WindowManager;
import android.widget.FrameLayout;


public class GuideMain extends Activity {
	
    private FrameLayout layout;
    private CameraView cameraView;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Hide the window title.
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        layout = new FrameLayout(this);
        cameraView = new CameraView(this, null);
        Log.d("Camera","Adding Camera to the layout!");
        layout.addView(cameraView);
        setContentView(layout);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.guide_main, menu);
        return true;
    }
	
}


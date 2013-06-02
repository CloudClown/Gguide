package org.project.gguide;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.content.SharedPreferences;

public class GuideMain extends Activity {
	
    private FrameLayout layout;
    private CameraView cameraView;
    private MapDrawer drawer;
    
    public static final String PREFS_NAME = "GGuideData";
    
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        // Hide the window title.
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        // testing saving to a file
        SharedPreferences data = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = data.edit();
        String input = "testing saving to preferences.";
        editor.putString("string1", input);
        editor.commit();
        String s = data.getString("string1", "string not found");
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
        
        //add views
        layout = new FrameLayout(this);
        drawer = new MapDrawer(this);
        cameraView = new CameraView(this, drawer);
        Log.d("Main","Adding Camera to the layout!");
        layout.addView(drawer);
        layout.addView(cameraView);
        setContentView(layout);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.guide_main, menu);
        return true;
    }
	
    public boolean onOptionsItemSelected(MenuItem item) {
    	if (item.getItemId() == R.id.enter_guide) {
			Intent intent = new Intent(this, MapGuideView.class);
			startActivity(intent);
			finish();
		} else if (item.getItemId() == R.id.enter_map) {
			Intent intent = new Intent(this, CamToMap.class);
			startActivity(intent);
			finish();
		}
    	
    	return false;
    }
}


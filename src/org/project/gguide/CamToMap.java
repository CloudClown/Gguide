package org.project.gguide;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import android.location.Location;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

@SuppressLint("NewApi")
public class CamToMap extends Activity implements 
	GooglePlayServicesClient.ConnectionCallbacks,
	GooglePlayServicesClient.OnConnectionFailedListener,
	LocationListener {
	
	//variables
	private GoogleMap mMap;
	private LocationClient mLocationClient;
	public Location mCurrentLocation;
	public LocationRequest mLocationRequest;

	//constants
	// Milliseconds per second
    private static final int MILLISECONDS_PER_SECOND = 1000;
    // Update frequency in seconds
    public static final int UPDATE_INTERVAL_IN_SECONDS = 5;
    // Update frequency in milliseconds
    private static final long UPDATE_INTERVAL =
            MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
    // The fastest update frequency, in seconds
    private static final int FASTEST_INTERVAL_IN_SECONDS = 1;
    // A fast frequency ceiling in milliseconds
    private static final long FASTEST_INTERVAL =
            MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;
	
    
    //helper functions
    private void stopPeriodicUpdates() {
    	mLocationClient.removeLocationUpdates(this);
    }
    
    private void startPeriodicUpdates() {
    	mLocationClient.requestLocationUpdates(mLocationRequest, this);
    }
    
    public void changeFocus(Location location) {
    	// Report to the UI that the location was updated
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
    	Toast.makeText(this, "Focusing...", Toast.LENGTH_SHORT).show();
    	Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    	LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        
        // Move the camera instantly to Sydney with a zoom of 15.
    	CameraPosition cameraPosition = new CameraPosition.Builder()
    		.target(latLng)
    		.zoom(15)
    		.bearing(90)
    		.tilt(30)
    		.build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        this.stopPeriodicUpdates();
    }
    
    @SuppressLint("NewApi")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_cam_to_map);
        
        if (mMap == null) {
            mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.camMap)).getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                // The Map is verified. It is now safe to manipulate the map.
            	Log.d("MapViewGuide","MapRetrieved!");
            	
            	// Connect to new location client instance
            	mLocationClient = new LocationClient(this,this,this);
            	// Create the LocationRequest object
                mLocationRequest = LocationRequest.create();
                // Use high accuracy
                mLocationRequest.setPriority(
                        LocationRequest.PRIORITY_HIGH_ACCURACY);
                // Set the update interval to 5 seconds
                mLocationRequest.setInterval(UPDATE_INTERVAL);
                // Set the fastest update interval to 1 second
                mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
                
                // Connect to new location client instance
            	mLocationClient = new LocationClient(this,this,this);
            	
            	
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.cam_to_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.back_to_home) {
            Intent intent = new Intent(this, GuideMain.class);
            startActivity(intent);
            finish();
        }
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Connect the client.
        mLocationClient.connect();
    }

    @Override
    public void onResume() {
    	super.onResume();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
    }
    
    @Override
    protected void onStop() {
    	Log.d("OnStop","Stop Periodic Updates");
        // If the client is connected
        if (mLocationClient.isConnected()) {
            stopPeriodicUpdates();
        }
        /*
         * After disconnect() is called, the client is
         * considered "dead".
         */
        mLocationClient.disconnect();
        super.onStop();
    }
    
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// Display the connection status
        Toast.makeText(this, "Connection failed!",
                Toast.LENGTH_SHORT).show();
        Log.d("Location","Connection Failed!");
	}

	@Override
	public void onConnected(Bundle connectionHint) {
        // Display the connection status
        Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
        Log.d("Location","Connection Succeeded!");
        startPeriodicUpdates();
        
    }

	@Override
	public void onDisconnected() {
		// Display the connection status
        Toast.makeText(this, "Disconnected. Please re-connect.",
                Toast.LENGTH_SHORT).show();
        Log.d("Location","Disconnected!");
	}

	@Override
	public void onLocationChanged(final Location location) {
		
        this.changeFocus(location);
        
        //button event
        //button events
    	final Button meButton = (Button) findViewById(R.id.mecam);
        meButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                changeFocus(location);
            }
        });
        
	}

}

package org.project.gguide;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

@SuppressLint("NewApi")
public class MapGuideView extends Activity implements 
	GooglePlayServicesClient.ConnectionCallbacks,
	GooglePlayServicesClient.OnConnectionFailedListener,
	LocationListener,
	SensorEventListener {
	
	//variables
	private GoogleMap mMap;
	private Context context;
	private LocationClient mLocationClient;
	public Location mCurrentLocation;
	public LocationRequest mLocationRequest;
	
	private SensorManager mSensorManager;
	private Sensor rotationSensor;
	private float[] mRotationMatrix = new float[16];
	private float[] mValues = new float[3];
	private float mAzi;
	private float  mTilt;
	private boolean isRotationViewEnabled;
	private CameraPosition mCamPos;
	
	//gcm
	GoogleCloudMessaging gcm;
	SharedPreferences prefs;
	AtomicInteger msgId = new AtomicInteger();
	String regid;
	
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
    public static final String PROPERTY_REG_ID = "registration_id";
    String GCM_SENDER_ID = "68787639537";
    
    //helper functions
    private void stopPeriodicUpdates() {
    	mLocationClient.removeLocationUpdates(this);
    }
    
    private void startPeriodicUpdates() {
    	mLocationClient.requestLocationUpdates(mLocationRequest, this);
    }
    
    private float clamp (float val) {
    	if (val <= 0) {
    		return 0;
    	}
    	if (val >= 90) {
    		return (float) 90;
    	}
    	return val;
    }
    
    public void changeFocus(Location location, float tilt, float azi) {
    	
    	LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        
        if (!this.isRotationViewEnabled) {
        	String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        	Toast.makeText(this, "Focusing...", Toast.LENGTH_SHORT).show();
        	Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        	// Move the camera instantly to Sydney with a zoom of 15.
        	CameraPosition cameraPosition1 = new CameraPosition.Builder()
    			.target(latLng)
    			.zoom(20)
    			.bearing(90)
    			.tilt(90)
    			.build();
        	mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition1));
        	this.stopPeriodicUpdates();
        } else {
        	// Move the camera instantly to Sydney with a zoom of 15.
        	CameraPosition cameraPosition2 = new CameraPosition.Builder()
    			.target(latLng)
    			.zoom(14+5*(-tilt/90))
    			.bearing(-azi)
    			.tilt(clamp(-tilt))
    			.build();
        	mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition2));
        	//this.stopPeriodicUpdates();
        }
    }
    
    @SuppressLint("NewApi")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        prefs = getSharedPreferences(MapGuideView.class.getSimpleName(), 
                Context.MODE_PRIVATE);
        setContentView(R.layout.activity_map_guide_view);
        this.context = this;
        if (mMap == null) {
            mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.guideMap)).getMap();
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
            	
            	Toast.makeText(this, "Initializing", Toast.LENGTH_SHORT).show();
            	
            	//initialize sensor manager
            	mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
            	rotationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            	mSensorManager.registerListener(this, rotationSensor, SensorManager.SENSOR_DELAY_NORMAL);
            	
            	//Camera Listener on the Map view
            	mMap.setOnCameraChangeListener(new OnCameraChangeListener() {

					@Override
					public void onCameraChange(CameraPosition position) {
						mCamPos = position;
					}
            	});
            	
            	//GCM Registration
            	regid = prefs.getString(PROPERTY_REG_ID, null);
            	//register the sender if not already
            	if (regid == null) {
            		registerBackground();
            	}
            	gcm = GoogleCloudMessaging.getInstance(this);
            }
        }
    }

    private void registerBackground() {
    	Toast.makeText(context, "Starting Registering GCM...", Toast.LENGTH_SHORT).show();
    	String msg = "";
        try {
            regid = gcm.register(GCM_SENDER_ID);
            msg = "Device registered, registration id=" + regid;

            // You should send the registration ID to your server over HTTP, 
            // so it can use GCM/HTTP or CCS to send messages to your app.

            // For this demo: we don't need to send it because the device  
            // will send upstream messages to a server that will echo back 
            // the message using the 'from' address in the message. 
    
            // Save the regid for future use - no need to register again.
            //SharedPreferences.Editor editor = prefs.edit();
            //editor.putString(PROPERTY_REG_ID, regid);
            //editor.commit();
        } catch (IOException ex) {
            msg = "Error :" + ex.getMessage();
            Log.d("GCM","msg");
        }
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    	
    	/*
    	new AsyncTask <Void, Integer ,String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    regid = gcm.register(GCM_SENDER_ID);
                    msg = "Device registered, registration id=" + regid;

                    // You should send the registration ID to your server over HTTP, 
                    // so it can use GCM/HTTP or CCS to send messages to your app.

                    // For this demo: we don't need to send it because the device  
                    // will send upstream messages to a server that will echo back 
                    // the message using the 'from' address in the message. 
            
                    // Save the regid for future use - no need to register again.
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(PROPERTY_REG_ID, regid);
                    editor.commit();
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }
           
            // Once registration is done, display the registration status
            // string in the Activity's UI.
            @Override
            protected void onPostExecute(String msg) {
            	Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        }.execute(null, null, null);
        */
    }

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.map_guide_view, menu);
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
        Toast.makeText(this, "Starting Activity...", Toast.LENGTH_SHORT).show();
        mLocationClient.connect();
        final Button deButton = (Button) findViewById(R.id.debug);
        deButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Toast.makeText(context, 
        				"Azimuth:"+Float.toString(mAzi)+","+"Tilt:"+Float.toString(mTilt), 
        				Toast.LENGTH_SHORT).show();
            	Toast.makeText(context, 
            			"bearing:"+Float.toString(mCamPos.bearing) +
            			"tilt:"+Float.toString(mCamPos.tilt) +
            			"zoom"+Float.toString(mCamPos.zoom), 
            			Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
    	super.onResume();
    	Toast.makeText(this, "Sensor Registered", Toast.LENGTH_SHORT).show();
    	mSensorManager.registerListener(this, rotationSensor, 16000);
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        Toast.makeText(this, "Sensor Unregistered", Toast.LENGTH_SHORT).show();
        mSensorManager.unregisterListener(this);
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
		
		this.mCurrentLocation = location;
        this.changeFocus(location,0,0);
        
        //button events
    	final Button viewButton = (Button) findViewById(R.id.view);
        viewButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	isRotationViewEnabled = true;
            	startPeriodicUpdates();
            }
        });
        final Button meButton = (Button) findViewById(R.id.me);
        meButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	isRotationViewEnabled = false;
            	changeFocus(location, 0,0);
            }
        });
        
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		
		//calculate the rotation matrix
		SensorManager.getRotationMatrixFromVector(mRotationMatrix, event.values);
		SensorManager.getOrientation(mRotationMatrix, mValues);
		
		mAzi = (float) Math.toDegrees(mValues[0]);
		mTilt = (float) Math.toDegrees(mValues[1]);
		
		if (this.isRotationViewEnabled)
			this.changeFocus(mCurrentLocation, mTilt, mAzi);
	}

}

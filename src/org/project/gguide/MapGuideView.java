package org.project.gguide;

import java.util.ArrayList;
import java.util.Iterator;

import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
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
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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
    private boolean camera_change_init;
	
    public static final String PREFS_NAME = "GGuideData";
	
    ParseManager Messenger;
    ArrayList<Marker> markerList = new ArrayList<Marker>();
    
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
    //public static final String PROPERTY_REG_ID = "registration_id";
    //String GCM_SENDER_ID = "68787639537";
    
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
            /*
              String msg = "Updated Location: " +
              Double.toString(location.getLatitude()) + "," +
              Double.toString(location.getLongitude());
              Toast.makeText(this, "Focusing...", Toast.LENGTH_SHORT).show();
              Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            */
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
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition2));
            //this.stopPeriodicUpdates();
        }
    }
    
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        /*
          prefs = getSharedPreferences(MapGuideView.class.getSimpleName(), 
          Context.MODE_PRIVATE);
        */
        setContentView(R.layout.activity_map_guide_view);
        this.context = this;
        SharedPreferences data = getSharedPreferences(PREFS_NAME, 0);
        String s = data.getString("string1", "string not found");
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
        camera_change_init = false;
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
                mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
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
            	
            	//Parse for real time messaging
            	Messenger = new ParseManager(this);
            	//Map click listener on the Map view
                mMap.setOnMapClickListener(new OnMapClickListener() {
                        public void onMapClick(LatLng location) {
            	                        	
                            // Create Marker
                            Marker userMark = mMap.addMarker(new MarkerOptions()
                                                             .position(location) // Mountain View
                                                             .title("I am here!")
                                                             .snippet("Population: Happiness"));
            	                        	
                            // Add to global array
                            markerList.add(userMark);
                            Messenger.sMsg.put("markers", "MarkerList");
                            Toast.makeText(context, "Marker message sent", Toast.LENGTH_SHORT).show();
                            Messenger.sendMsg();
                        }
                    });
            	
            }
        }
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
            return true;
        } else if (item.getItemId() == R.id.tour_guide) {
            
        	
        	
        	isRotationViewEnabled = false;
        		    
            //changeFocus(location, 0,0);
            mapAnim anim = new mapAnim(mMap);
            Iterator<Marker> iterator = markerList.iterator();
            while (iterator.hasNext()) {
                Marker markStart = mMap.addMarker(new MarkerOptions()
                                                  .position(new LatLng(this.mCurrentLocation.getLatitude(), this.mCurrentLocation.getLongitude())) // Mountain View
                                                  .title("I am here!")
                                                  .snippet("Population: Happiness"));
                    		
                Marker markEnd = iterator.next();
            	
                anim = new mapAnim(mMap);
                anim.animateTo(markStart, markEnd);
            }        		           	
            
            return true;
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
        final ToggleButton guideButton = (ToggleButton) findViewById(R.id.toggleButton1);
        guideButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        Messenger.sMsg.put("isGuide", true);
                    } else {
                        // The toggle is disabled
                	Messenger.sMsg.put("isGuide", false);
                    }
                    Log.d("SENDER","sMsg");
                    Messenger.sendMsg();
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
                    //startPeriodicUpdates();
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
		
        if ( camera_change_init ) {
            float mAzi_tmp, mTilt_tmp;
            mAzi_tmp = (float) Math.toDegrees(mValues[0]);
            mTilt_tmp = (float) Math.toDegrees(mValues[1]);
						
            if (Math.abs(mAzi_tmp - mAzi) > 45.0f || Math.abs(mTilt_tmp - mTilt) > 45.0f ) {
                mTilt = mTilt_tmp;
                mAzi = mAzi_tmp;
			 				
                if (this.isRotationViewEnabled)
                    this.changeFocus(mCurrentLocation, mTilt, mAzi);
            }
			 			
			 			
        } else {
            camera_change_init = true;
            mAzi = (float) Math.toDegrees(mValues[0]);
            mTilt = (float) Math.toDegrees(mValues[1]);
			 			
            if (this.isRotationViewEnabled)
                this.changeFocus(mCurrentLocation, mTilt, mAzi);
        }
        Log.d("MapViewGuide","mTilt " +  mTilt + "mAzi " + mAzi);
    }

}

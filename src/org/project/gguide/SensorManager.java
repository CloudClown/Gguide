package org.project.gguide;

import java.text.DecimalFormat;
import java.util.List;

import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.android.gms.location.Geofence;
import android.support.v4.util.LruCache;
import android.text.format.DateUtils;
import android.widget.EditText;

public class SensorManager {
	
	 private static final long GEOFENCE_EXPIRATION_IN_HOURS = 12;
	 private static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS =
	            GEOFENCE_EXPIRATION_IN_HOURS * DateUtils.HOUR_IN_MILLIS;
	
	 // Persistent storage for geofences
	private SimpleGeofenceStore mPrefs;
	
	// Store a list of geofences to add
    List<Geofence> mCurrentGeofences;
    
 
    // Remove geofences handler
    private GeofenceRemover mGeofenceRemover;
    
    //Latitude and Longitude
    List<EditText> mLatitude;
    List<EditText> mLongitude;
    List<EditText> mRadius;
    
    private SimpleGeofence mUIGeofence1;
    private SimpleGeofence mUIGeofence2;

    // An intent filter for the broadcast receiver
    private IntentFilter mIntentFilter;

    // Store the list of geofences to remove
    private List<String> mGeofenceIdsToRemove;
	    
   
    // decimal formats for latitude, longitude, and radius
    private DecimalFormat mLatLngFormat;
    private DecimalFormat mRadiusFormat;
    
    
    mCurrentGeofences = new ArrayList<Geofence>();
    mGeofenceRequester.addGeofences(mCurrentGeofences);
    
    mUIGeofence1 = mPrefs.getGeofence("1");
    mUIGeofence2 = mPrefs.getGeofence("2");
    mUIGeofence3 = mPrefs.getGeofence("3");
    mUIGeofence4 = mPrefs.getGeofence("4");
    mUIGeofence5 = mPrefs.getGeofence("5");
    if (mUIGeofence1 != null) {
        mLatitude.get(1).setText(
                mLatLngFormat.format(
                        mUIGeofence1.getLatitude()));
        mLongitude.get(1).setText(
                mLatLngFormat.format(
                        mUIGeofence1.getLongitude()));
        mRadius.get(1).setText(
                mRadiusFormat.format(
                        mUIGeofence1.getRadius()));
    }
    
}

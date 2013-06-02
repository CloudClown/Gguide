package org.project.gguide;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONArray;
import org.json.JSONObject;

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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
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


public class mapAnim {
	private GoogleMap mMap;
	float tilt = 90;
	float azi = 0;
	float clamp = 0;
	float zoom = 20;
	ArrayList<LatLng> routeCoords = new ArrayList<LatLng>();
    JSONArray jArray;
    ProgressDialog pDialog;


	public mapAnim(GoogleMap map){
	    mMap = map;
	}
	
    /* Method to decode polyline points */
    private ArrayList<LatLng> decodePoly(String encoded) {

        ArrayList<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }
	
    class GetDirection extends AsyncTask<String, String, String> {

    	private LatLng from, to;
    	
    	public GetDirection(LatLng start, LatLng end) {
    		from = start;
    		to = end;
    	}
    	
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
 
        }

        protected String doInBackground(String... args) {
            LatLng startLocation = from;
            LatLng endLocation = to;
            String stringUrl = "http://maps.googleapis.com/maps/api/directions/json?origin=" + startLocation.latitude + "+" + startLocation.longitude + "&destination=" + endLocation.latitude + "+" + endLocation.longitude + "&sensor=false";

            StringBuilder response = new StringBuilder();
            try {
                URL url = new URL(stringUrl);
                HttpURLConnection httpconn = (HttpURLConnection) url
                        .openConnection();
                if (httpconn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader input = new BufferedReader(
                            new InputStreamReader(httpconn.getInputStream()),
                            8192);
                    String strLine = null;

                    while ((strLine = input.readLine()) != null) {
                        response.append(strLine);
                    }
                    input.close();
                }

                String jsonOutput = response.toString();

                JSONObject jsonObject = new JSONObject(jsonOutput);

                // routesArray contains ALL routes
                JSONArray routesArray = jsonObject.getJSONArray("routes");

                // Grab the first route
                JSONObject route = routesArray.getJSONObject(0);
                JSONArray legs = route.getJSONArray("legs");
                JSONObject leg = legs.getJSONObject(0);
            	JSONArray step = leg.getJSONArray("steps");

                for(int i = 0; i < step.length(); i++)
                {
                	JSONObject endarr = step.getJSONObject(i);
                	JSONObject end = endarr.getJSONObject("end_location");

                	LatLng coord = new LatLng(Float.parseFloat(end.getString("lat")), 
                							  Float.parseFloat(end.getString("lng")));
    

                	routeCoords.add(coord);
                }

            } catch (Exception e) {

            }

            return null;

        }

        protected void onPostExecute(String file_url) {

        }
    }
	
	public void animateTo(Marker from, Marker to) {
        new GetDirection(from.getPosition(), to.getPosition()).execute();
    	SystemClock.sleep(3000);
    	Iterator<LatLng> iterator = routeCoords.iterator();
    	while (iterator.hasNext()) {
    		CameraPosition cameraPosition = new CameraPosition.Builder()
			.target(iterator.next())
			//.zoom(14+5*(-tilt/90))
			.zoom(zoom)
			.bearing(-azi)
			.tilt(tilt)
			.build();
    		
    		mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 1000, null);
        	SystemClock.sleep(3000);
    	}
	}
	
	
}

package org.project.gguide;

/**
 * Created by jackie on 5/22/13.
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

 class StreetLatLongObject{
        public StreetLatLongObject(ArrayList<String> streets2,
                ArrayList<Double> longitudes2, ArrayList<Double> latitudes2) {
            streets = streets2;
            longitudes = longitudes2;
            latitudes = latitudes2;
        }
        public ArrayList<String> streets ;
        public ArrayList<Double> longitudes;
        public ArrayList<Double> latitudes;
    };
    

public class CameraView extends SurfaceView implements
        SurfaceHolder.Callback {
    //common variables
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private Camera.PreviewCallback camPreviewCallback;
    private Context mainActivity;
    
   
    public CameraView(Context context, Camera.PreviewCallback previewCallback) {
        super(context);
        //add surfaceview to the main context layer
        this.camPreviewCallback = previewCallback;
        mainActivity = context;
        //inspect the surface state
        mHolder = this.getHolder();
        mHolder.addCallback(this);
        
        
        Log.d("Camera:","Initialization Done!");
    }

    //calculate the optimal preview size for the camera device
    private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
        Log.d("Camera","Optimizing preview size");
        final double ASPECT_TOLERANCE = 0.05;
        double targetRatio = (double) w / h;
        if (sizes == null) return null;

        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Try to find an size match aspect ratio and size
        for (Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }
    
   
    public StreetLatLongObject FindStreets (double long_min, double long_max, double lat_min, double lat_max) throws IOException{
    	ArrayList<String>streets = new ArrayList<String>();
    	ArrayList<Double> longitudes = new ArrayList<Double>();
    	ArrayList<Double> latitudes = new ArrayList<Double>();
    	Geocoder myLocation = new Geocoder(getContext(), Locale.ENGLISH);
    	double diff_lat = lat_max-lat_min;
    	double diff_long = long_max-long_min;
    	double incremet_lat = diff_lat/3;
    	double incremet_long = diff_long/3;
    	for(double i = lat_min; i < lat_max; i+=incremet_lat){
    		for(double j = long_min; j < long_max; j+=incremet_long){
    			List<Address> myList;
				
				myList = myLocation.getFromLocation(i, j, 1);

    			Address add = myList.get(0);
    			String addressString = add.getAddressLine(0);
    			streets.add(addressString);
    			longitudes.add(i);
    			latitudes.add(j);
    		}
    	}
    	StreetLatLongObject Return_obj = new StreetLatLongObject(streets, longitudes, latitudes);
		return Return_obj;

	}
    @SuppressLint("NewApi")
    public void setCameraDisplayOrientation(Activity activity,
                                            int cameraId, android.hardware.Camera camera) {
        Log.d("Camera","Orientation Changed!");
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    @SuppressLint("NewApi")
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        Camera.Parameters parameters = mCamera.getParameters();

        List<Size> sizes = parameters.getSupportedPreviewSizes();
        Size optimalSize = getOptimalPreviewSize(sizes, width, height);
        parameters.setPreviewSize(optimalSize.width, optimalSize.height);

        mCamera.setParameters(parameters);
        if (camPreviewCallback != null) {
            mCamera.setPreviewCallbackWithBuffer(camPreviewCallback);
            Camera.Size size = parameters.getPreviewSize();
            byte[] data = new byte[size.width*size.height*
                    ImageFormat.getBitsPerPixel(parameters.getPreviewFormat())/8];
            mCamera.addCallbackBuffer(data);
        }
        mCamera.startPreview();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mCamera = Camera.open();
            setCameraDisplayOrientation((Activity)mainActivity, 0, mCamera);
            mCamera.setPreviewDisplay(holder);
        } catch (IOException exception) {
            mCamera.release();
            mCamera = null;

        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
    }

    @Override
    protected void onLayout(boolean arg0, int arg1, int arg2, int arg3, int arg4) {
        //do nothing
    }

}


package org.project.gguide;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.model.CameraPosition;

import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.content.Context;
import android.app.Activity;

//Map view draws street info on camera frame
import android.location.Location;
import android.os.Bundle;

@SuppressLint("DrawAllocation")
public class MapDrawer extends View implements 
Camera.PreviewCallback,
SensorEventListener {

	private Context mainContext;
	
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
    private boolean camera_up;
    
    
	public MapDrawer(Context context) {
		super(context);
		mainContext = context;
		camera_up = false;
		System.out.println(camera_up);
		
		mSensorManager = (SensorManager)mainContext.getSystemService(mainContext.SENSOR_SERVICE);
    	rotationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
    	mSensorManager.registerListener(this, rotationSensor, SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	@Override
	public void onPreviewFrame(byte[] arg0, Camera arg1) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// CameraView cv = new CameraView();
		
		Paint paintBuilding = new Paint();
        paintBuilding.setARGB(150,0,206,209);
        paintBuilding.setTextSize(40);
        paintBuilding.setAntiAlias(true);
        Paint paintStreet = new Paint();
        paintStreet.setARGB(150,165,42,42);
        paintStreet.setTextSize(40);
        paintStreet.setAntiAlias(true);
        Paint paintService = new Paint();
        paintService.setARGB(150,0,206,209);
        paintService.setTextSize(40);
        paintService.setAntiAlias(true);
        String s1 = "B.H.Tech Youth Center";
        String s2 = "S Pecan St";
        String s3 = "Trucking Service";
        float textWidth1 = paintBuilding.measureText(s1);
        float textHeight1 = 40.0f;
        float textHeight2 = 40.0f;
        float textWidth2 = paintStreet.measureText(s2);
        float textWidth3 = paintService.measureText(s3);
        float textHeight3 = 40.0f;
        Paint paintRect2 = new Paint();
        paintRect2.setARGB(100,0,0,0);
        RectF r2 = new RectF((getWidth()-textWidth1)/2.0f, (getHeight()-textHeight1-60.0f)/2.0f, (getWidth()-textWidth1)/2.0f+textWidth1+10.0f, (getHeight()-textHeight1-60.0f)/2.0f+textHeight1+10.0f);
        canvas.drawRoundRect(r2, 10.0f, 10.0f, paintRect2);
        Paint paintRect4 = new Paint();
        paintRect4.setARGB(100,0,0,0);
        RectF r4 = new RectF((getWidth()-textWidth2)/2.0f-150.0f, (getHeight()-textHeight2-60.0f)/2.0f-150.0f, (getWidth()-textWidth2)/2.0f+textWidth2+10.0f-150.0f, (getHeight()-textHeight2-60.0f)/2.0f+textHeight2+10.0f-150.0f);
        canvas.drawRoundRect(r4, 10.0f, 10.0f, paintRect4);
        Paint paintRect5 = new Paint();
        paintRect5.setARGB(100,0,0,0);
        RectF r5 = new RectF((getWidth()-textWidth3)/2.0f+100.0f, (getHeight()-textHeight3-60.0f)/2.0f-400.0f, (getWidth()-textWidth3)/2.0f+textWidth3+10.0f+100.0f, (getHeight()-textHeight3-60.0f)/2.0f+textHeight3+10.0f-400.0f);
        canvas.drawRoundRect(r5, 10.0f, 10.0f, paintRect5);
        Paint paintRect = new Paint();
        paintRect.setARGB(255,245,245,245);
        RectF r = new RectF((getWidth()-textWidth1)/2.0f-5.0f, (getHeight()-textHeight1-60.0f)/2.0f-5.0f, (getWidth()-textWidth1)/2.0f+textWidth1+5.0f, (getHeight()-textHeight1-60.0f)/2.0f+textHeight1+5.0f);
        canvas.drawRoundRect(r, 10.0f, 10.0f, paintRect);
        canvas.drawText(s1, (getWidth()-textWidth1)/2.0f, (getHeight()-textHeight1)/2.0f, paintBuilding);
        Paint paintRect1 = new Paint();
        paintRect1.setARGB(255,245,245,245);
        RectF r1 = new RectF((getWidth()-textWidth2)/2.0f-5.0f-150.0f, (getHeight()-textHeight2-60.0f)/2.0f-5.0f-150.0f, (getWidth()-textWidth2)/2.0f+textWidth2+5.0f-150.0f, (getHeight()-textHeight2-60.0f)/2.0f+textHeight2+5.0f-150.0f);
        canvas.drawRoundRect(r1, 10.0f, 10.0f, paintRect1);
        canvas.drawText(s2, (getWidth()-textWidth2)/2-150.0f, (getHeight()-textHeight2)/2.0f-150.0f , paintStreet);
        Paint paintRect3 = new Paint();
        paintRect3.setARGB(255,245,245,245);
        RectF r3 = new RectF((getWidth()-textWidth3)/2.0f-5.0f+100.0f, (getHeight()-textHeight3-60.0f)/2.0f-5.0f-400.0f, (getWidth()-textWidth3)/2.0f+textWidth3+5.0f+100.0f, (getHeight()-textHeight3-60.0f)/2.0f+textHeight3+5.0f-400.0f);
        canvas.drawRoundRect(r3, 10.0f, 10.0f, paintRect3);
        canvas.drawText(s3, (getWidth()-textWidth3)/2.0f+100.0f, (getHeight()-textHeight3)/2.0f-400.0f, paintService);
		
	}


	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		SensorManager.getRotationMatrixFromVector(mRotationMatrix, event.values);
        SensorManager.getOrientation(mRotationMatrix, mValues);

        if (camera_change_init) {
            float mAzi_tmp, mTilt_tmp;
            mAzi_tmp = (float) Math.toDegrees(mValues[0]);
            mTilt_tmp = (float) Math.toDegrees(mValues[1]);
            if (Math.abs(mAzi_tmp - mAzi) > 45.0f || Math.abs(mTilt_tmp - mTilt) > 45.0f ) {
                mTilt = mTilt_tmp;
                mAzi = mAzi_tmp;
                camera_up = true;   
            } else {
            	camera_up = false;
            }
        } else {
            camera_change_init = true;
            mAzi = (float) Math.toDegrees(mValues[0]);
            mTilt = (float) Math.toDegrees(mValues[1]);
        }
        Log.d("CAMSENSOR",Boolean.toString(camera_up) + " mAzi " + mAzi + " mTilt" + mTilt);
    }
}
package org.project.gguide;

import com.google.android.gms.maps.GoogleMap;

import android.opengl.GLSurfaceView;
import android.content.Context;
import android.graphics.Canvas;
import android.hardware.Camera;
//Map view draws street info on camera frame

public class MapDrawer extends GLSurfaceView implements Camera.PreviewCallback {

	Context mainContext;
	GoogleMap map;
	
	public MapDrawer(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onPreviewFrame(byte[] arg0, Camera arg1) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		
	}
}
package org.project.gguide;

import android.view.View;
import android.content.Context;
import android.graphics.Canvas;
import android.hardware.Camera;
//Map view draws street info on camera frame

public class MapDrawer extends View implements Camera.PreviewCallback {

	private Context mainContext;
	
	public MapDrawer(Context context) {
		super(context);
		mainContext = context;
	}

	@Override
	public void onPreviewFrame(byte[] arg0, Camera arg1) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		
	}
}
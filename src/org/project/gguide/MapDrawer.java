package org.project.gguide;

import android.view.View;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.hardware.Camera;
//Map view draws street info on camera frame

@SuppressLint("DrawAllocation")
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
		Paint paintBuilding = new Paint();
        paintBuilding.setARGB(150,0,206,209);
        paintBuilding.setTextSize(40);
        paintBuilding.setAntiAlias(true);
        Paint paintStreet = new Paint();
        paintStreet.setARGB(150,165,42,42);
        paintStreet.setTextSize(40);
        paintStreet.setAntiAlias(true);
        String s1 = "Building";
        String s2 = "Street";
        float textWidth1 = paintBuilding.measureText(s1);
        float textHeight1 = 40.0f;
        float textHeight2 = 40.0f;
        Paint paintRect2 = new Paint();
        paintRect2.setARGB(100,0,0,0);
        RectF r2 = new RectF((getWidth()-textWidth1)/2.0f, (getHeight()-textHeight1-60.0f)/2.0f, (getWidth()-textWidth1)/2.0f+textWidth1+10.0f, (getHeight()-textHeight1-60.0f)/2.0f+textHeight1+10.0f);
        canvas.drawRoundRect(r2, 10.0f, 10.0f, paintRect2);
        Paint paintRect = new Paint();
        paintRect.setARGB(255,245,245,245);
        RectF r = new RectF((getWidth()-textWidth1)/2.0f-5.0f, (getHeight()-textHeight1-60.0f)/2.0f-5.0f, (getWidth()-textWidth1)/2.0f+textWidth1+5.0f, (getHeight()-textHeight1-60.0f)/2.0f+textHeight1+5.0f);
        canvas.drawRoundRect(r, 10.0f, 10.0f, paintRect);
        canvas.drawText(s1, (getWidth()-textWidth1)/2.0f, (getHeight()-textHeight1)/2.0f, paintBuilding);
        float textWidth2 = paintStreet.measureText(s2);
        Paint paintRect1 = new Paint();
        paintRect1.setARGB(255,245,245,245);
        RectF r1 = new RectF((getWidth()-textWidth2)/2.0f-5.0f, (getHeight()-textHeight2-60.0f)/2.0f-5.0f+60.0f, (getWidth()-textWidth2)/2.0f+textWidth2+5.0f, (getHeight()-textHeight2-60.0f)/2.0f+textHeight2+5.0f+60.0f);
        canvas.drawRoundRect(r1, 10.0f, 10.0f, paintRect1);
        canvas.drawText(s2, (getWidth()-textWidth2)/2, (getHeight()-textHeight2)/2.0f+60.0f , paintStreet);
	}
}
package org.project.gguide;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class GcmBroadcastReceiver extends BroadcastReceiver{

	Context ctx;
	public static final int NOTIFICATION_ID = 1;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
		ctx = context;
		String messageType = gcm.getMessageType(intent);
		Toast.makeText(ctx, messageType, Toast.LENGTH_SHORT).show();
	}
}

package com.jaragua.avlmobile.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.jaragua.avlmobile.activities.SplashActivity;

public class BootBroadcastReceiver extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
			Intent intentActivity = new Intent(context, SplashActivity.class);
			intentActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intentActivity.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			context.startActivity(intentActivity);
		}
	}

}

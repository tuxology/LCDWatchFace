package com.suchakra.lcdwatchface;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class LCDClockService extends Service {

    @Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	public void onCreate() {
		super.onCreate();
	}

	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

}
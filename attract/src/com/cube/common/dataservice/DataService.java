package com.cube.common.dataservice;

import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class DataService extends Service {
	private static final String TAG = "DataService";

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		Log.d(TAG, "onCreate");
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestroy");
	}

	@Override
	public void onStart(Intent intent, int startid) {
		Log.d(TAG, "onStart");
		initializeServerData();
	}

	public String urlServerData = "http://cubeservice.sinaapp.com/girls/attrat/serverdata.json";

	public void initializeServerData() {
//		String serverDataStr = WebInterface.get(urlServerData);
//		Log.d(TAG, "ServerData is: " + str);
		JSONObject serverDataJSON = WebInterface.getJSON(urlServerData);
		Log.d(TAG, "ServerData is: " + serverDataJSON);
	}
}

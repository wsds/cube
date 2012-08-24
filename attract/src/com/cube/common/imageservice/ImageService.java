package com.cube.common.imageservice;

import java.util.Date;

import com.cube.common.LocalData;
import com.cube.common.ServerData;
import com.cube.common.Settings;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class ImageService extends IntentService {

	private static final String TAG = "ImageService";

	public ImageService() {
		super(TAG);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "onCreate");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		String message = intent.getStringExtra("message");

		long time = intent.getLongExtra("time", 0);
		Date date = new Date(time);

		Log.d(TAG, message + settings.SDF_DATE_FORMAT.format(date));
		Log.d(TAG, "onHandleIntent");
		initializeImage();
		Log.d(TAG, "onHandleIntented");

	}

	Settings settings = Settings.getInstance();
	ServerData serverData = ServerData.getInstance();
	LocalData localData = LocalData.getInstance();
	BitmapPool bitmapPool = BitmapPool.getInstance();

	public void initializeImage() {

		for (ServerData.Girl girl : serverData.girls) {
			WebImage.initializeWebData(this);
			for (ServerData.Girl.Picture picture : girl.pictures) {
				String url = picture.url;
				String filename = url.substring(url.lastIndexOf("/") + 1);

				if (!localData.game.loadedPictures.contains(filename)) {
					boolean isLoaded = WebImage.getBitmap(url, filename);
					if (isLoaded == true) {
						localData.game.loadedPictures.add(filename);
					}
					else{
						Log.d(TAG, filename+" cannot be loaded !");
					}
				}
			}
		}
	}

}

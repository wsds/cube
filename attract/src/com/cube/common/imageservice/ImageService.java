package com.cube.common.imageservice;

import java.util.Date;

import com.cube.common.LocalData;
import com.cube.common.ServerData;
import com.cube.common.Settings;


import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
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

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		WebImage webImage = new WebImage();
		webImage.initializeWebData(this);
		for (ServerData.Girl girl : serverData.girls) {
			for (ServerData.Girl.Picture picture : girl.pictures) {
				String url = picture.url;
				String filename = url.substring(url.lastIndexOf("/") + 1);

				if (!bitmapPool.map.containsKey(filename) || bitmapPool.map.get(filename) == null) {
					Bitmap bitmap = webImage.getBitmap(url, filename);
					if (bitmap != null) {
						bitmapPool.map.put(filename, bitmap);
						localData.game.downloadedPictures.add(filename);
					}
				}

				// LocalData.Game.ActiveGirl activeGirl = new LocalData().new Game().new ActiveGirl();
				// localData.game.cubeGirls.add(activeGirl);
				//
				// activeGirl.id = localData.game.cubeGirls.size();
				// activeGirl.girl = girl;
			}
		}
	}

}

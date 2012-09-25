package com.cube.attract;

import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.cube.common.LocalData;
import com.cube.common.Settings;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;

public class MainActivity extends Activity {
	private static final String TAG = "MainActivity";

	Settings settings = Settings.getInstance();
	LocalData localData = LocalData.getInstance();
	Context mContext = null;
	Activity mActivity = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		UmengUpdateAgent.update(this);
		UmengUpdateAgent.setUpdateAutoPopup(false);
		UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
			@Override
			public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
				switch (updateStatus) {
				case 0:
					Log.d(TAG, "Umeng Update");
					UmengUpdateAgent.showUpdateDialog(mContext, updateInfo);
					break;
				case 1:
					Log.d(TAG, "no update in Umeng server");
					break;
				case 2:
					Log.d(TAG, "none wifi for update");
					break;
				case 3:
					Log.d(TAG, "time out");
					break;
				}
			}
		});

		mContext = this;
		mActivity = this;
//		setContentView(R.layout.activity_main);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);

//		ImageView mImageView2 = (ImageView) findViewById(R.id.imageView2);
//		Animation translate_title2Animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate_title2);
//		mImageView2.setAnimation(translate_title2Animation);

		TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);

		localData.nativePhoneNumber = telephonyManager.getLine1Number();
		localData.IMSI = telephonyManager.getSubscriberId();

		Date now = new Date(System.currentTimeMillis());
		int data = now.getDate();
		if (localData.game.lastGameDate != data) {
			localData.game.choice = 3;
		}

		Log.d(TAG, "nativePhoneNumber is " + localData.nativePhoneNumber + " and IMSI is " + localData.IMSI);
		startServices();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

			Intent entry = new Intent(Intent.ACTION_MAIN);
			entry.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			entry.setClassName("com.cube.attract", "com.cube.attract.entry.EntryActivity");
			mContext.startActivity(entry);
			((Activity) mContext).finish();
			



	}

	public void startServices() {
		Intent dataService = new Intent();
		dataService.setClassName("com.cube.attract", "com.cube.common.dataservice.DataService");
		Log.d(TAG, "dataService is Starting");
		mContext.startService(dataService);
		Log.d(TAG, "dataService Started");

		Intent imageService = new Intent();
		imageService.setClassName("com.cube.attract", "com.cube.common.imageservice.ImageService");
		imageService.putExtra("time", System.currentTimeMillis());
		imageService.putExtra("message", "The time now is ");
		Log.d(TAG, "imageService is Starting");
		mContext.startService(imageService);
		Log.d(TAG, "imageService Started");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}

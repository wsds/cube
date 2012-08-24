package com.cube.attract;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;

import com.cube.common.Settings;

public class MainActivity extends Activity {
	private static final String TAG = "MainActivity";

	Settings settings = Settings.getInstance();
	Context mContext = null;
	Activity mActivity = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		mActivity = this;
		setContentView(R.layout.activity_main);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		ImageView mImageView2 = (ImageView) findViewById(R.id.imageView2);
		Animation translate_title2Animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate_title2);
		mImageView2.setAnimation(translate_title2Animation);
		startServices();

		translate_title2Animation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				try {
					Thread.sleep(1500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (settings.isLogoin == "false") {
					Intent about = new Intent(Intent.ACTION_MAIN);
					about.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					about.setClassName("com.cube.attract", "com.cube.attract.game.cupidcannon.CupidCannonActivity");
//					about.setClassName("com.cube.attract", "com.cube.attract.about.AboutActivity");
					mContext.startActivity(about);
					mActivity.finish();
				} else if (settings.isLogoin == "") {

				}

			}
		});

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

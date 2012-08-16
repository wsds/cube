package com.cube.common.test;

import com.cube.attract.R;
import com.cube.common.Settings;
import com.cube.common.imageservice.BitmapPool;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class TestApp extends Activity {
	Context mContext = null;

	RelativeLayout canvasContainer = null;
	public boolean initailized = false;
	private static final String TAG = "TestApp";

	public Settings settings = Settings.getInstance();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		setContentView(R.layout.testapp);

		Button buttonTest1 = (Button) findViewById(R.id.test1);
		buttonTest1.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Intent apk1 = new Intent();
				apk1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				apk1.setClassName("Neibu.main.beta", "com.cube.common.test.DownloadImage");
				mContext.startActivity(apk1);
			}
		});

		Button buttonTest2 = (Button) findViewById(R.id.test2);
		buttonTest2.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				LinearLayout linearLayout = (LinearLayout) findViewById(R.id.testLinearLayout);
				BitmapPool bitmapPool = BitmapPool.getInstance();
				for (String filename : bitmapPool.map.keySet()) {
					ImageView imageView = new ImageView(mContext);
					Bitmap bitmap = bitmapPool.map.get(filename);
					imageView.setImageBitmap(bitmap);
					linearLayout.addView(imageView);
				}
			}
		});

		Button buttonTest3 = (Button) findViewById(R.id.test3);
		buttonTest3.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Intent dataService = new Intent(Intent.ACTION_MAIN);
				dataService.setClassName("com.cube.attract", "com.cube.common.dataservice.DataService");

				mContext.startService(dataService);
				
				Intent imageService = new Intent();
				imageService.setClassName("com.cube.attract", "com.cube.common.imageservice.ImageService");
				imageService.putExtra("time", System.currentTimeMillis());
				imageService.putExtra("message", "The time now is ");
				mContext.startService(imageService);
				Log.d(TAG, "imageService Started");
			}
		});

	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}


}
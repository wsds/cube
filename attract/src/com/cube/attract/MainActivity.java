package com.cube.attract;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;

import com.cube.common.Settings;

public class MainActivity extends Activity {

	Settings settings = Settings.getInstance();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Context mContext = this;
		final Activity mActivity = this;
		setContentView(R.layout.activity_main);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); 

		ImageView mImageView2 = (ImageView) findViewById(R.id.imageView2);
		Animation translate_title2Animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate_title2);
		mImageView2.setAnimation(translate_title2Animation);

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
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if(settings.isLogoin=="false"){
					Intent about = new Intent(Intent.ACTION_MAIN);
					about.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//					about.setClassName("com.cube.attract", "com.cube.attract.game.cupidcannon.CupidCannonActivity");
					about.setClassName("com.cube.attract", "com.cube.attract.about.AboutActivity");
//					about.setClassName("com.cube.attract", "com.cube.common.test.TestApp");
					
					mContext.startActivity(about);
					mActivity.finish();
					
				}
				else if(settings.isLogoin==""){
					
				}

			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}

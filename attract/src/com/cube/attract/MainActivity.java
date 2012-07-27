package com.cube.attract;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;

import com.cube.attract.about.MyScrollLayout;
import com.cube.common.Settings;

public class MainActivity extends Activity {

	Settings settings = Settings.getInstance();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Context mContext = this;
		setContentView(R.layout.activity_main);

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
					about.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					about.addCategory(Intent.CATEGORY_HOME);
					about.setClassName("com.cube.attract", "com.cube.attract.about.AboutActivity");
					mContext.startActivity(about);
					System.exit(0);
					
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

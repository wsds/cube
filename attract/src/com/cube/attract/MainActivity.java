package com.cube.attract;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;

import com.cube.attract.game2.R;

public class MainActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Context mContext = this;
		final Activity mActivity = this;
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
				Intent about = new Intent(Intent.ACTION_MAIN);
				about.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

				about.setClassName("com.cube.attract.game2", "com.cube.attract.game.mosquito.MosquitoActivity");
				mContext.startActivity(about);
				mActivity.finish();

			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}

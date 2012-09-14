package com.cube.attract.about;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.cube.attract.R;
import com.cube.common.Settings;

public class AboutActivity extends Activity implements OnViewChangeListener {

	Settings settings = Settings.getInstance();

	private MyScrollLayout mScrollLayout;
	private ImageView[] imageViews;
	private int count;
	private int currentItem;
	ZipperView zipperView;

	// private RelativeLayout mainRLayout;
	private LinearLayout pointLLayout;
	final Context mContext = this;
	final Activity mActivity = this;

	private SoundPool soundPool;

	private int effect_tick;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		initView();
		soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 100);
		effect_tick = soundPool.load(this, R.raw.effect_tick, 1);
	}

	private void initView() {
		mScrollLayout = (MyScrollLayout) findViewById(R.id.ScrollLayout);
		pointLLayout = (LinearLayout) findViewById(R.id.llayout);
		// mainRLayout = (RelativeLayout) findViewById(R.id.mainRLayout);
		count = mScrollLayout.getChildCount();
		imageViews = new ImageView[count];
		for (int i = 0; i < count; i++) {
			imageViews[i] = (ImageView) pointLLayout.getChildAt(i);
			imageViews[i].setEnabled(true);
			imageViews[i].setTag(i);
		}
		if (settings.localData.isFisrtRun == "true") {
			currentItem = 0;
			settings.localData.isFisrtRun="false";
		} else {
			currentItem = 3;
		}

		imageViews[currentItem].setEnabled(false);
		mScrollLayout.SetOnViewChangeListener(this);
	}

	@Override
	public void OnViewChange(int position) {
		setcurrentPoint(position);

		soundPool.play(effect_tick, 0.2f, 0.2f, 1, 0, 1f);
	}

	private void setcurrentPoint(int position) {
		if (position < 0 || position > count - 1 || currentItem == position) {
			return;
		}
		imageViews[currentItem].setEnabled(true);
		imageViews[position].setEnabled(false);
		currentItem = position;
	}
}
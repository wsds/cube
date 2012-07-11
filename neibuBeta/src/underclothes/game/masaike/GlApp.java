package underclothes.game.masaike;

import Neibu.main.beta.R;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class GlApp extends Activity {
	Context mContext = null;
	CanvasSurfaceView canvasSurfaceView = null;

	RelativeLayout canvasContainer = null;
	public boolean initailized = false;
	LocalSettings localSettings = null;

	/** 动画 **/
	Animation logoshrinkAnimation = null;
	Animation logoenlargeAnimation = null;

	Animation toleftAnimation = null;
	Animation torightAnimation = null;
	Animation fromleftAnimation = null;
	Animation fromrightAnimation = null;
	Animation takepictureAnimation = null;
	Animation torightAnimation2 = null;
	Animation fromrightAnimation2 = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;

		localSettings = LocalSettings.getInstance();
		localSettings.ispoped = true;// 此处如果不手动初始化，就会导致每次启动数据未被修改，然后执行showElement。原因未知。
		localSettings.ispoping = false;
		localSettings.isCanvasSurfaceReady = false;

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		setContentView(R.layout.canvas3);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		if (initailized == false) {

			this.findElement();

			canvasSurfaceView = new CanvasSurfaceView(this);
			canvasContainer.addView(canvasSurfaceView, 0);
			localSettings.isCanvasSurfaceReady = true;
			// canvasSurfaceView.setZOrderOnTop(true);
			canvasSurfaceView.getHolder().setFormat(PixelFormat.TRANSPARENT);

			this.registEvent();
		}
		initailized = true;
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		long[] pattern = { 100, 200 }; // 停止 开启 停止 开启
		vibrator.vibrate(pattern, 2);
		return true;
	}

	public void findElement() {

		canvasContainer = (RelativeLayout) findViewById(R.id.CanvasContainer);
		/** 显示element **/

		next = (ImageView) findViewById(R.id.next);
		eyeshrink = (ImageView) findViewById(R.id.eyeshrinking);
		facered = (ImageView) findViewById(R.id.facered);
		impression = (ImageView) findViewById(R.id.impression);
		needcomments = (ImageView) findViewById(R.id.needcomments);

		/** 动画 **/
		logoshrinkAnimation = AnimationUtils.loadAnimation(mContext, R.anim.logoshrink);
		logoenlargeAnimation = AnimationUtils.loadAnimation(mContext, R.anim.logoenlarge);
		toleftAnimation = AnimationUtils.loadAnimation(mContext, R.anim.toleft);
		torightAnimation = AnimationUtils.loadAnimation(mContext, R.anim.toright);
		fromleftAnimation = AnimationUtils.loadAnimation(mContext, R.anim.fromleft);
		fromrightAnimation = AnimationUtils.loadAnimation(mContext, R.anim.fromright);
		takepictureAnimation = AnimationUtils.loadAnimation(mContext, R.anim.takepictureenlarge);
		torightAnimation2 = AnimationUtils.loadAnimation(mContext, R.anim.toright2);
		fromrightAnimation2 = AnimationUtils.loadAnimation(mContext, R.anim.fromright2);
	}

	public void registEvent() {

		/*
		 * takepicture.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View arg0) {
		 * 
		 * } });
		 * 
		 * settings.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View arg0) { } });
		 * 
		 * browse.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View arg0) {
		 * 
		 * } });
		 */
		eyeshrink.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				hideElement();
				showNext();
			}
		});

		facered.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				hideElement();
				showNext();
			}
		});

		impression.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				hideElement();
				showNext();
			}
		});

		next.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				hideNext();
			}
		});
		// logo.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View arg0) {
		//
		// triggerElement();
		// }
		// });
		// logoshrinkAnimation.setAnimationListener(new AnimationListener() {
		// public void onAnimationStart(Animation anim) {
		// };
		//
		// public void onAnimationRepeat(Animation anim) {
		// };
		//
		// public void onAnimationEnd(Animation anim) {
		// if (localSettings.ispoped == true) {
		// logo.startAnimation(logoenlargeAnimation);
		//
		// logo.setVisibility(0);
		// } else {
		// logochildren1.startAnimation(logoenlargeAnimation);
		//
		// logochildren1.setVisibility(0);
		// }
		// };
		// });
		// logoenlargeAnimation.setAnimationListener(new AnimationListener() {
		// public void onAnimationStart(Animation anim) {
		// };
		//
		// public void onAnimationRepeat(Animation anim) {
		// };
		//
		// public void onAnimationEnd(Animation anim) {
		// if (localSettings.ispoped == true) {
		// localSettings.ispoped = false;
		// } else {
		// localSettings.ispoped = true;
		// }
		// localSettings.ispoping = false;
		// };
		// });

	}

	public void triggerElement() {
		Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		long[] pattern = { 5, 100 }; // 停止 开启 停止 开启
		vibrator.vibrate(pattern, -1);
		if (this.localSettings.ispoped == true) {

			hideElement();
			drawLog("执行hideElement");

			// vibrator.cancel();
		} else {
			showElement();

			drawLog("执行showElement");
		}

	}

	public void hideElement() {
		if (this.localSettings.ispoped == false) {
			return;
		}
		this.localSettings.ispoped = false;
		eyeshrink.startAnimation(toleftAnimation);
		eyeshrink.setVisibility(8);
		facered.startAnimation(torightAnimation2);
		facered.setVisibility(8);
		impression.startAnimation(toleftAnimation);
		impression.setVisibility(8);
		needcomments.startAnimation(logoshrinkAnimation);
		needcomments.setVisibility(8);
	}

	/** 显示element **/
	ImageView next = null;
	ImageView eyeshrink = null;
	ImageView facered = null;
	ImageView impression = null;
	ImageView needcomments = null;

	public void showElement() {
		if (this.localSettings.ispoped == true) {
			return;
		}
		this.localSettings.ispoped = true;
		eyeshrink.startAnimation(fromleftAnimation);
		eyeshrink.setVisibility(0);
		facered.startAnimation(fromrightAnimation2);
		facered.setVisibility(0);
		impression.startAnimation(fromleftAnimation);
		impression.setVisibility(0);
		needcomments.startAnimation(logoenlargeAnimation);
		needcomments.setVisibility(0);
	}

	public void showNext() {
		next.startAnimation(fromrightAnimation);
		next.setVisibility(0);
	}

	public void hideNext() {
		hideElement();
		next.startAnimation(torightAnimation);
		next.setVisibility(8);
		canvasSurfaceView.initailizeGirls();
		canvasSurfaceView.surfaceCreated(null);
	}

	public void drawLog(String log) {
		if (localSettings.isCanvasSurfaceReady == true) {
			// canvasSurfaceView.drawLog(log);
		}
	}

}
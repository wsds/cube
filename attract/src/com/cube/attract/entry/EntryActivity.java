package com.cube.attract.entry;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.cube.attract.R;
import com.cube.attract.entry.ShakeListener.OnShakeListener;
import com.cube.common.LocalData;
import com.cube.common.LocalData.Game.ActiveGirl;

public class EntryActivity extends Activity {
	String TAG = "EntryActivity";
	private GLSurfaceView surface;
	private GlRenderer renderer;
	Context context;

	private GestureDetector gestureDetector;
	public SceneState sceneState = SceneState.getInstance();
	ShakeListener mShakeListener = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		context = this;

		add3ActiveGirls();

		gestureDetector = new GestureDetector(this, new GlAppGestureListener());

		surface = new GLSurfaceView(this);
		renderer = new GlRenderer(this);
		surface.setRenderer(renderer);
		setContentView(surface);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		final SoundPool soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 100);
		final int loadId1 = soundPool.load(this, R.raw.shake, 1);

		mShakeListener = new ShakeListener(this);
		mShakeListener.setOnShakeListener(new OnShakeListener() {
			public void onShake(double speed, float deltaX, float deltaY, float deltaZ) {

				soundPool.play(loadId1, 0.2f, 0.2f, 1, 0, 1f);
				sceneState.isShaked = true;
				sceneState.saveRotation();
				sceneState.dxSpeed_CUB = -(deltaX) / 2;
				sceneState.dySpeed_CUB = (deltaY) / 2;
				Log.v(TAG, "sceneState.dxSpeed_CUB = " + sceneState.dxSpeed_CUB + " $$ sceneState.dySpeed_CUB = " + sceneState.dySpeed_CUB);
			}
		});
	}

	void add3ActiveGirls() {
		int i = 0;
		for (ActiveGirl girl : localData.game.loadedGirls) {
			for (ActiveGirl activeGirl : localData.game.activeGirls) {
				if (activeGirl.id == girl.id) {
					return;
				}
			}
			localData.game.activeGirls.add(girl);
			i++;
			if (i >= 3) {
				break;
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		surface.onResume();
		mShakeListener.start();
	}

	@Override
	protected void onPause() {
		super.onPause();
		surface.onPause();
		mShakeListener.stop();
	}

	private float startX, startY;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (gestureDetector.onTouchEvent(event)) {
			return true;
		}

		switch (event.getAction()) {

		case MotionEvent.ACTION_DOWN:
			sceneState.isClicked = true;

			startX = event.getX();
			startY = event.getY();
			Log.d("Point:", "startY:" + startY + "  startX:" + startX);
			if (startY > 240) {
				sceneState.eventType = sceneState.CUB;
				sceneState.saveRotation();
			} else {
				renderer.logo.addAnimation(renderer.rotateLogo);
			}
			break;
		case MotionEvent.ACTION_UP:
			sceneState.x = event.getX();
			sceneState.y = event.getY();
			if (sceneState.isClicked == true && sceneState.eventType == sceneState.CUB) {
				sceneState.gbNeedPick = true;
			}
			Log.i("sceneState.isClicked", String.valueOf(sceneState.isClicked));
			break;
		case MotionEvent.ACTION_MOVE:
			float dx = event.getX() - startX;
			float dy = event.getY() - startY;
			if (sceneState.eventType == sceneState.BRAND) {
			} else if (sceneState.eventType == sceneState.CUB) {
				sceneState.dx_CUB = dx;
				sceneState.dy_CUB = dy;
			}

			if (sceneState.isClicked == true) {
				float delta = (dx * dx + dy * dy);
				if (delta > 1600) {
					sceneState.isClicked = false;
				}
			}
			break;
		case MotionEvent.ACTION_CANCEL:
			sceneState.gbNeedPick = false;
			break;
		}
		if (sceneState.gbNeedPick == true && renderer.isPicking == false) {
			startActivity t1 = new startActivity();
			t1.start();
		}

		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			new AlertDialog.Builder(this).setIcon(R.drawable.cupid).setTitle(R.string.app_name).setMessage("真的要走吗，亲！").setNegativeButton("取消", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			}).setPositiveButton("确定", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					finish();
				}
			}).show();

			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	private class GlAppGestureListener extends GestureDetector.SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			// measure speed in milliseconds
			if (sceneState.eventType == sceneState.BRAND) {
			} else if (sceneState.eventType == sceneState.CUB) {
				sceneState.dxSpeed_CUB = velocityX / 1000;
				sceneState.dySpeed_CUB = velocityY / 1000;
			}

			return super.onFling(e1, e2, velocityX, velocityY);
		}
	}

	public LocalData localData = LocalData.getInstance();

	public class startActivity extends Thread {

		boolean isRunning = true;

		int timer = 0;
		Thread mThread;

		@Override
		public void run() {
			mThread = this;
			try {
				Thread.sleep(500);

				if (sceneState.picked != -1) {
					Looper.prepare();
					showPrompt();
					Looper.loop();
					sceneState.picked = -1;
				}

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		int pickIndex[] = { 2, 3, 0, 1, 5, 4 };

		void showPrompt() {
			Log.v(TAG, "showPrompt");
			if (sceneState.picked != -1) {
				if (localData.game.choice > 0) {

					Log.i(TAG, "sceneState.picked=" + sceneState.picked);
					final ActiveGirl girl = renderer.cubeGirls.get(pickIndex[sceneState.picked]);
					// String url = girl.girl.pictures.get(0).url;
					new AlertDialog.Builder(context).setIcon(R.drawable.cupid).setTitle(R.string.app_name).setMessage("你今日还有" + localData.game.choice + "次选择机会，确认选择该美女吗，亲？！").setNegativeButton("取消", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							renderer.isShownPrompt = false;
							renderer.isPicking = false;
						}
					}).setPositiveButton("确定", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							renderer.promptID = renderer.ADDGIRL;
							renderer.isShownPrompt = true;
							renderer.promptAnimation1.reset();
							localData.game.choice--;
							for (ActiveGirl activeGirl : localData.game.activeGirls) {
								if (activeGirl.id == girl.id) {
									return;
								}
							}
							localData.game.activeGirls.add(girl);
						}
					}).show();
				} else {
					renderer.promptID = renderer.RULESELECTED;
					renderer.isShownPrompt = true;
					renderer.promptAnimation1.reset();
				}
			}
		}
	}
}
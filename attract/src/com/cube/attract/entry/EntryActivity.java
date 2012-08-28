package com.cube.attract.entry;

import com.cube.attract.R;
import com.cube.attract.entry.ShakeListener.OnShakeListener;

import android.app.Activity;
import android.media.AudioManager;
import android.media.SoundPool;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.view.Window;

public class EntryActivity extends Activity {
	String TAG = "EntryActivity";
	private GLSurfaceView surface;
	private GlRenderer renderer;

	private GestureDetector gestureDetector;
//	private GLSurfaceView mGLSurfaceView;
	public SceneState sceneState = SceneState.getInstance();
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		gestureDetector = new GestureDetector(this, new GlAppGestureListener());

		surface = new GLSurfaceView(this);
		renderer = new GlRenderer(this);
		surface.setRenderer(renderer);
		setContentView(surface);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		final SoundPool soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 100);
		final int loadId1 = soundPool.load(this, R.raw.shake, 1);

		ShakeListener mShakeListener = new ShakeListener(this);
		mShakeListener.setOnShakeListener(new OnShakeListener() {
			public void onShake(double speed, float deltaX, float deltaY, float deltaZ) {

				soundPool.play(loadId1, 0.2f, 0.2f, 1, 0, 1f);
				sceneState.isShaked = true;
				sceneState.saveRotation();
//				sceneState.dxSpeed = -(deltaX) / 2;
//				sceneState.dySpeed = (deltaY) / 2;
//				Log.v(TAG, "sceneState.dxSpeed = " + sceneState.dxSpeed + " $$ sceneState.dySpeed = " + sceneState.dySpeed);
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		surface.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		surface.onPause();
	}

	private float startX, startY;

	/**
	 * ��Ӧ�����¼�
	 */
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
			}
			break;
		case MotionEvent.ACTION_UP:
			sceneState.x = event.getX();
			sceneState.y = event.getY();
			if (sceneState.isClicked == true && sceneState.eventType == sceneState.CUB) {
				sceneState.gbNeedPick = true;
			}
			Log.i("sceneState.isClicked",String.valueOf(sceneState.isClicked));
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
				float delta = (dx * dx +  dy * dy);
				if (delta > 1600) {
					sceneState.isClicked = false;
				}
			}
			break;
		case MotionEvent.ACTION_CANCEL:
			sceneState.gbNeedPick = false;
			break;
		}
		Log.i("sceneState.gbNeedPick",String.valueOf(sceneState.gbNeedPick));
		if (sceneState.gbNeedPick == true) {
			startActivity t1 = new startActivity();
			t1.start();
		}

		return true;
	}

	private class GlAppGestureListener extends GestureDetector.SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			// measure speed in milliseconds
			if (sceneState.eventType == sceneState.BRAND) {
			} else if (sceneState.eventType == sceneState.CUB) {
				sceneState.dxSpeed_CUB = velocityX / 1000;
				sceneState.dySpeed_CUB = velocityY / 1000;
//				Log.v(TAG, "sceneState.dxSpeed = " + sceneState.dxSpeed + " $$ sceneState.dySpeed = " + sceneState.dySpeed);
			}

			return super.onFling(e1, e2, velocityX, velocityY);
		}
	}

	public class startActivity extends Thread {

		boolean isRunning = true;

		int timer = 0;

		/**
		 * �߳������
		 */
		@Override
		public void run() {
			try {
				Thread.sleep(500);

				if (sceneState.picked != -1) {
					if (sceneState.picked == 0) {
//						context.startActivity(new Intent(context, com.cube.attract.gameEntry.GameEntryActivity.class));
					} else if (sceneState.picked == 1) {
						//context.startActivity(new Intent(context, underclothes.game.flowers.GlApp.class));
					} else if (sceneState.picked == 2) {
						//context.startActivity(new Intent(context, underclothes.android.pleasewait.GlApp.class));
					} else if (sceneState.picked == 3) {
						//context.startActivity(new Intent(context, underclothes.android.pleasewait.GlApp.class));
					} else if (sceneState.picked == 4) {
						//context.startActivity(new Intent(context, underclothes.game.masaike.GlApp.class));
					} else if (sceneState.picked == 5) {
						//context.startActivity(new Intent(context, underclothes.android.pleasewait.GlApp.class));
					}
					sceneState.picked = -1;
				}

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
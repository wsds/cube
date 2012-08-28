package com.cube.attract.entry;

import com.cube.attract.R;
import com.cube.attract.entry.ShakeListener.OnShakeListener;

import android.app.Activity;
import android.media.AudioManager;
import android.media.SoundPool;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.WindowManager;

public class EntryActivity extends Activity {
	String TAG = "EntryActivity";
	private GLSurfaceView surface;
	private GlRenderer renderer;

	private GestureDetector gestureDetector;

	SceneState sceneState = SceneState.getInstance();

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
				sceneState.dxSpeed = -(deltaX) / 2;
				sceneState.dySpeed = (deltaY) / 2;
				Log.v(TAG, "sceneState.dxSpeed = " + sceneState.dxSpeed + " $$ sceneState.dySpeed = " + sceneState.dySpeed);
			}
		});
	}

	@Override
	protected void onPause() {
		super.onPause();
		surface.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		surface.onResume();
	}

	private float startX, startY;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (gestureDetector.onTouchEvent(event)) {
			return true;
		}

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			sceneState.dxSpeed = 0.0f;
			sceneState.dySpeed = 0.0f;
			startX = event.getX();
			startY = event.getY();

			if (startY * 800 / sceneState.screenHeight < 150 && startY * 800 / sceneState.screenHeight > 80) {
				sceneState.eventType = sceneState.LOGO;
				renderer.logoDown.start(true);
			} else {
				sceneState.eventType = sceneState.CUB;
				sceneState.saveRotation();
			}

			break;
		case MotionEvent.ACTION_MOVE:

			if (sceneState.eventType == sceneState.CUB) {
				sceneState.dx = event.getX() - startX;
				sceneState.dy = event.getY() - startY;
			}
			break;

		case MotionEvent.ACTION_UP:
			if (sceneState.eventType == sceneState.LOGO) {
				renderer.logoUp.start(true);
			}
			break;
		}

		return super.onTouchEvent(event);
	}

	private class GlAppGestureListener extends GestureDetector.SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			if (sceneState.eventType == sceneState.LOGO) {
			} else if (sceneState.eventType == sceneState.CUB) {
				sceneState.dxSpeed = velocityX / 1000;
				sceneState.dySpeed = velocityY / 1000;
				Log.v(TAG, "sceneState.dxSpeed = " + sceneState.dxSpeed + " $$ sceneState.dySpeed = " + sceneState.dySpeed);
			}

			return super.onFling(e1, e2, velocityX, velocityY);
		}
	}

}

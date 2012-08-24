package com.cube.attract.gameEntry;

import com.cube.attract.gameEntry.SceneState;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.WindowManager;

public class GameEntryActivity extends Activity {
	private GLSurfaceView surface;
	private GlRenderer renderer;

	Context mContext;
	Activity mActivity;
	private GestureDetector gestureDetector;

	SceneState sceneState = SceneState.getInstance();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		gestureDetector = new GestureDetector(this, new GlAppGestureListener());

		mContext = this;
		mActivity = this;

		surface = new GLSurfaceView(this);
		renderer = new GlRenderer(this);
		surface.setRenderer(renderer);
		setContentView(surface);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);

	}

	@Override
	protected void onPause() {
		super.onPause();
		surface.onPause();
	}

	@Override
	protected void onResume() {
		sceneState.notJustComeIn = false;
		super.onResume();
		surface.onResume();
	}

	private float startX, startY;
	float TOUCH_SCAL_FACTOR = 180f / 320;
	private int GAMENUMBER = 7;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (gestureDetector.onTouchEvent(event)) {
			return true;
		}

		switch (event.getAction()) {
		case MotionEvent.ACTION_MOVE:
			if (sceneState.eventType == sceneState.GIRL) {

				sceneState.pictureViewGallary.dx = event.getX() - startX;
				sceneState.pictureViewGallary.dy = event.getY() - startY;
				sceneState.pictureViewGallary.dAngle = sceneState.pictureViewGallary.dx * TOUCH_SCAL_FACTOR;
				float path = sceneState.pictureViewGallary.dx * sceneState.pictureViewGallary.dx + sceneState.pictureViewGallary.dy * sceneState.pictureViewGallary.dy;
				if (path > 1600) {
					if (sceneState.backAnimaLock) {
						renderer.girlGoBack.start(true);
						renderer.girlRotateBack.start(true);
						sceneState.backAnimaLock = false;
						sceneState.goFrontPermit = true;
						sceneState.notJustComeIn = true;
					}

				}
			}
			break;
		case MotionEvent.ACTION_DOWN:
			sceneState.isTouchUp = false;
			startX = event.getX();
			startY = event.getY();

			float normalY = startY * 800 / sceneState.screenHeight;
			float normalX = startX * 480 / sceneState.screenWidth;

			if (normalY < 430) {
				sceneState.eventType = sceneState.GIRL;

				sceneState.pictureViewGallary.dxSpeed = 0.0f;
				sceneState.pictureViewGallary.isStopping = false;
				sceneState.pictureViewGallary.saveMovement();

			} else if (normalY < 505) {
				if (normalX > 181 && normalX < 300 && !sceneState.isLocked[1]) {
					GAMENUMBER = 2;
					Log.i("come in game", "2");
					sceneState.isSelected[1] = true;

					// 2

				} else if (normalX > 60 && normalX < 181 && !sceneState.isLocked[0]) {
					GAMENUMBER = 1;
					Log.i("come in game", "1");
					// 1
					sceneState.isSelected[0] = true;

				} else if (normalX < 420 && normalX > 300 && !sceneState.isLocked[2]) {
					GAMENUMBER = 3;
					Log.i("come in game", "3");
					sceneState.isSelected[2] = true;

					// 3
				}
			} else if (normalY < 620 && normalY > 545) {
				if (normalX > 120 && normalX < 239 && !sceneState.isLocked[3]) {
					GAMENUMBER = 4;
					Log.i("come in game", "4");
					sceneState.isSelected[3] = true;

					// 4
				} else if (normalX > 239 && normalX < 361 && !sceneState.isLocked[4]) {
					GAMENUMBER = 5;
					Log.i("come in game", "5");
					sceneState.isSelected[4] = true;

					// 5
				}
			} else if (normalY > 650 && normalX < 737) {
				if (normalX > 181 && normalX < 300 && !sceneState.isLocked[5]) {
					Log.i("come in game", "6");
					GAMENUMBER = 6;
					sceneState.isSelected[5] = true;
					// 6
				}
			}
			break;

		case MotionEvent.ACTION_UP:
			sceneState.isTouchUp = true;
			if (sceneState.eventType == sceneState.GIRL) {
			} else {
				switch (GAMENUMBER) {
				case 1:
					sceneState.isSelected[0] = false;
					Intent game1 = new Intent(Intent.ACTION_MAIN);
					game1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					game1.putExtra("picture1", "girl_4_1.jpg");
					game1.putExtra("picture2", "girl_4_2.jpg");
					game1.putExtra("picture3", "girl_4_3.jpg");
					game1.putExtra("weibo", "@小悦悦");
					// game1.setClassName("com.cube.attract",
					// "com.cube.attract.game.cupidcannon.CupidCannonActivity");
					game1.setClassName("com.cube.attract", "com.cube.attract.game.mosquito.MosquitoActivity");
					mContext.startActivity(game1);
					mActivity.finish();
					break;
				case 2:
					sceneState.isSelected[1] = false;

					Intent about = new Intent(Intent.ACTION_MAIN);
					about.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					about.putExtra("picture1", "girl_4_1.jpg");
					about.putExtra("picture2", "girl_4_2.jpg");
					about.putExtra("picture3", "girl_4_3.jpg");
					about.putExtra("weibo", "@小悦悦");
					about.setClassName("com.cube.attract", "com.cube.attract.game.mosquito.MosquitoActivity");
					mContext.startActivity(about);
					mActivity.finish();

					break;
				case 3:
					sceneState.isSelected[2] = false;

					break;
				case 4:
					sceneState.isSelected[3] = false;

					break;
				case 5:
					sceneState.isSelected[4] = false;

					break;
				case 6:
					sceneState.isSelected[5] = false;

					break;
				}
			}
			sceneState.eventType = sceneState.NONE;
			break;
		case MotionEvent.ACTION_CANCEL:
			sceneState.eventType = sceneState.NONE;
			break;
		}

		return super.onTouchEvent(event);
	}

	private class GlAppGestureListener extends GestureDetector.SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

			if (sceneState.eventType == sceneState.GIRL) {
				sceneState.pictureViewGallary.dxSpeed = velocityX / 1100;
				sceneState.pictureViewGallary.dySpeed = velocityY / 1100;
			}

			return super.onFling(e1, e2, velocityX, velocityY);
		}
	}

}

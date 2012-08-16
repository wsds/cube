package com.cube.attract.gameEntry;

import com.cube.attract.gameEntry.SceneState;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

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
				sceneState.pictureViewGallary.dAngle = sceneState.pictureViewGallary.dx
						* TOUCH_SCAL_FACTOR;
				float path = sceneState.pictureViewGallary.dx
						* sceneState.pictureViewGallary.dx
						+ sceneState.pictureViewGallary.dy
						* sceneState.pictureViewGallary.dy;
				if (path > 1600) {
					renderer.girlGoBack.start(true);
					renderer.girlRotateBack.start(true);
					sceneState.pictureViewGallary.once = true;
				}
			}
			break;
		case MotionEvent.ACTION_DOWN:
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
					// 2

				} else if (normalX > 60 && normalX < 181
						&& !sceneState.isLocked[0]) {
					GAMENUMBER = 1;
					Log.i("come in game", "1");
					// 1
					sceneState.isSelected[0] = true;
					renderer.ploygonColor.start(true);

				} else if (normalX < 420 && normalX > 300
						&& !sceneState.isLocked[2]) {
					GAMENUMBER = 3;
					Log.i("come in game", "3");
					// 3
				}
			} else if (normalY < 620 && normalY > 545) {
				if (normalX > 120 && normalX < 239 && !sceneState.isLocked[3]) {
					GAMENUMBER = 4;
					Log.i("come in game", "4");
					// 4
				} else if (normalX > 239 && normalX < 361
						&& !sceneState.isLocked[4]) {
					GAMENUMBER = 5;
					Log.i("come in game", "5");
					// 5
				}
			} else if (normalY > 650 && normalX < 737) {
				if (normalX > 181 && normalX < 300 && !sceneState.isLocked[5]) {
					Log.i("come in game", "6");
					GAMENUMBER = 6;
					// 6
				}
			}
			break;

		case MotionEvent.ACTION_UP:
			if (sceneState.eventType == sceneState.GIRL) {
//				renderer.girlGoFront.start(true);
//				renderer.girlRotateFront.start(true);
			} else {
				switch (GAMENUMBER) {
				case 1:
					sceneState.isSelected[0] = false;
					Intent about = new Intent(Intent.ACTION_MAIN);
					about.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					about.setClassName("com.cube.attract",
							"com.cube.attract.entry.EntryActivity");
					mContext.startActivity(about);
					mActivity.finish();
					break;
				case 2:

					break;
				case 3:
				case 4:
				case 5:
				case 6:
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

	private class GlAppGestureListener extends
			GestureDetector.SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {

			if (sceneState.eventType == sceneState.GIRL) {
				sceneState.pictureViewGallary.dxSpeed = velocityX / 1000;
				sceneState.pictureViewGallary.dySpeed = velocityY / 1000;
			}

			return super.onFling(e1, e2, velocityX, velocityY);
		}
	}

}

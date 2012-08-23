package com.cube.attract.entry;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Window;

public class EntryActivity extends Activity {
//	private GLSurfaceView surface;
//	private GlRenderer renderer;

//	private GestureDetector gestureDetector;

//	SceneState sceneState = SceneState.getInstance();
	MyGLSurfaceView mGLSurfaceView;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		mGLSurfaceView = new MyGLSurfaceView(this);
		mGLSurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
		setContentView(mGLSurfaceView);
		mGLSurfaceView.requestFocus();
		mGLSurfaceView.setFocusableInTouchMode(true);
//		
//		gestureDetector = new GestureDetector(this, new GlAppGestureListener());
//
//		surface = new GLSurfaceView(this);
//		renderer = new GlRenderer(this);
//		surface.setRenderer(renderer);
//		setContentView(surface);

	}

	@Override
	protected void onPause() {
		super.onPause();
		mGLSurfaceView.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mGLSurfaceView.onResume();
	}
//
//
//	private float startX, startY;
//
//	@Override
//	public boolean onTouchEvent(MotionEvent event) {
//		if (gestureDetector.onTouchEvent(event)) {
//			return true;
//		}
//
//		switch (event.getAction()) {
//		case MotionEvent.ACTION_DOWN:
//			sceneState.isClicked = true;
//			sceneState.dxSpeed = 0.0f;
//			sceneState.dySpeed = 0.0f;
//			startX = event.getX();
//			startY = event.getY();
//
//			if (startY * 800 / sceneState.screenHeight < 150 && startY * 800 / sceneState.screenHeight > 80) {
//				sceneState.eventType = sceneState.LOGO;
////				renderer.logoDown.start(true);
//			} else {
//				sceneState.eventType = sceneState.CUB;
//				sceneState.saveRotation();
//			}
//
//			break;
//		case MotionEvent.ACTION_MOVE:
//
//			if (sceneState.eventType == sceneState.CUB) {
//				sceneState.dx = event.getX() - startX;
//				sceneState.dy = event.getY() - startY;
//			}
//			break;
//			
//		case MotionEvent.ACTION_UP:
//			sceneState.gbNeedPick = true;
////			if (sceneState.eventType == sceneState.LOGO) {
////				renderer.logoUp.start(true);
////			} 
//			break;
//		}
//
//		return super.onTouchEvent(event);
//	}
//
//
//	private class GlAppGestureListener extends GestureDetector.SimpleOnGestureListener {
//		@Override
//		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//			if (sceneState.eventType == sceneState.LOGO) {
//			} else if (sceneState.eventType == sceneState.CUB) {
//				sceneState.dxSpeed = velocityX / 1000;
//				sceneState.dySpeed = velocityY / 1000;
//			}
//
//			return super.onFling(e1, e2, velocityX, velocityY);
//		}
//	}

}

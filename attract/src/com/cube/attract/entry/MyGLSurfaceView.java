package com.cube.attract.entry;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class MyGLSurfaceView extends GLSurfaceView {

	// private final float TOUCH_SCALE_FACTOR = 180.0f / 480;

	/**
	 * 具体实现的渲染器
	 */
	private RayPickRenderer mRenderer;
	public SceneState sceneState = SceneState.getInstance();
	/**
	 * 记录上次触屏位置的坐标
	 */
	private GestureDetector gestureDetector;
	private Context context;

	public MyGLSurfaceView(Context context) {
		super(context);
		this.context = context;
		gestureDetector = new GestureDetector(new GlAppGestureListener());

		// 设置渲染器
		mRenderer = new RayPickRenderer(context);

		// 透视上一个View
		setZOrderOnTop(true);
		setEGLConfigChooser(8, 8, 8, 8, 16, 0);
		// 透视上一个Activity
		getHolder().setFormat(PixelFormat.TRANSLUCENT);

		setRenderer(mRenderer);
		// 设置渲染模式为主动渲染
		setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

	}

	public void onPause() {
		super.onPause();
	}

	public void onResume() {
		super.onResume();
	}

	private float startX, startY;

	/**
	 * 响应触屏事件
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
				sceneState.dxSpeed_BRAND = 0.0f;
				sceneState.dySpeed_BRAND = 0.0f;
				sceneState.eventType = sceneState.BRAND;
				sceneState.saveMovement();
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
				sceneState.dx_BRAND = dx;
				sceneState.dy_BRAND = dy;
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
				sceneState.dxSpeed_BRAND = velocityX / 1000;
				sceneState.dySpeed_BRAND = velocityY / 1000;
			} else if (sceneState.eventType == sceneState.CUB) {
				sceneState.dxSpeed_CUB = velocityX / 1000;
				sceneState.dySpeed_CUB = velocityY / 1000;
			}

			return super.onFling(e1, e2, velocityX, velocityY);
		}
	}

	public class startActivity extends Thread {

		boolean isRunning = true;

		int timer = 0;

		/**
		 * 线程体代码
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

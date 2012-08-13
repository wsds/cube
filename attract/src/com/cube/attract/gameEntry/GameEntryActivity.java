package com.cube.attract.gameEntry;

import com.cube.attract.gameEntry.SceneState;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
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

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (gestureDetector.onTouchEvent(event)) {
			return true;
		}

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
//			sceneState.pictureViewGallary.dxSpeed = 0.0f;
//			sceneState.pictureViewGallary.dySpeed = 0.0f;
			startX = event.getX();
			startY = event.getY();

			float normalY = startY * 800 / sceneState.screenHeight;
			float normalX = startX * 480 / sceneState.screenWidth;
			if (normalY < 430) {
				sceneState.eventType = sceneState.GIRL;

//				sceneState.pictureViewGallary.dxSpeed = 0.0f;
//				sceneState.pictureViewGallary.saveMovement();
				renderer.girlGoBack.start(true);
				renderer.girlRotateBack.start(true);
			}
			else if(normalY<505){
				if(normalX>181 && normalX<300){
					//2
					Intent about = new Intent(Intent.ACTION_MAIN);
					about.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					about.setClassName("com.cube.attract", "com.cube.attract.entry.EntryActivity");
					mContext.startActivity(about);
					mActivity.finish();
				}
				else if(normalX>60)
				{
					//1
				}
				else if(normalX<420)
				{
					//3
				}
			}
			else if(normalY<620 && normalY>545){
				if(normalX>120 && normalX<239){
					//4
				}
				else if(normalX>239 && normalX<361){
					//5
				}
			}
			else if(normalY>650 && normalX<737){
				if(normalX>181 && normalX<300){
					//6
				}
			}


			break;
		case MotionEvent.ACTION_MOVE:

			if (sceneState.eventType == sceneState.GIRL) {
				sceneState.pictureViewGallary.dx = event.getX() - startX;
				sceneState.pictureViewGallary.dy = event.getY() - startY;
				sceneState.pictureViewGallary.dAngle = sceneState.pictureViewGallary.dx
						* TOUCH_SCAL_FACTOR;
			}
			break;

		case MotionEvent.ACTION_UP:
			if (sceneState.eventType == sceneState.GIRL) {
				renderer.girlGoFront.start(true);
				renderer.girlRotateFront.start(true);
			}
			sceneState.eventType = sceneState.NONE;
			break;
		case MotionEvent.ACTION_CANCEL:
			break;
		}

		return super.onTouchEvent(event);
	}

	private class GlAppGestureListener extends
			GestureDetector.SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {

			sceneState.pictureViewGallary.dxSpeed = velocityX / 1000;
			sceneState.pictureViewGallary.dySpeed = velocityY / 1000;

			return super.onFling(e1, e2, velocityX, velocityY);
		}
	}

}

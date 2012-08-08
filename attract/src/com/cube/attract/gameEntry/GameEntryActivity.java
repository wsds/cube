package com.cube.attract.gameEntry;


import com.cube.attract.gameEntry.SceneState;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class GameEntryActivity extends Activity{
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
	float TOUCH_SCAL_FACTOR = 180f/320;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (gestureDetector.onTouchEvent(event)) {
			return true;
		}
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			sceneState.pictureViewGallary.dxSpeed = 0.0f;
			sceneState.pictureViewGallary.dySpeed = 0.0f;
			startX = event.getX();
			startY = event.getY();

			if (startY * 800 / sceneState.screenHeight < 450) {
				sceneState.eventType = sceneState.GIRL;
				
				sceneState.pictureViewGallary.dxSpeed = 0.0f;
				sceneState.pictureViewGallary.saveMovement();
				renderer.girlGoBack.start(true);
				renderer.girlRotateBack.start(true);
			} else {
			}

			break;
		case MotionEvent.ACTION_MOVE:
			sceneState.pictureViewGallary.dx = event.getX() - startX;
			sceneState.pictureViewGallary.dy = event.getY() - startY;
			if (sceneState.eventType == sceneState.GIRL) {
				sceneState.pictureViewGallary.dAngle = sceneState.pictureViewGallary.dx*TOUCH_SCAL_FACTOR;
			}
			break;
			
		case MotionEvent.ACTION_UP:
			if (sceneState.eventType == sceneState.GIRL) {
				renderer.girlGoFront.start(true);
				renderer.girlRotateFront.start(true);
			} 
			break;
		}
		
		return super.onTouchEvent(event);
	}

	private class GlAppGestureListener extends GestureDetector.SimpleOnGestureListener
    {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

			sceneState.pictureViewGallary.dxSpeed = velocityX / 1000;
			sceneState.pictureViewGallary.dySpeed = velocityY / 1000;
			

			return super.onFling(e1, e2, velocityX, velocityY);
		}
    }
	
}

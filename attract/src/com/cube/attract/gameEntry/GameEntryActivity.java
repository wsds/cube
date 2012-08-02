package com.cube.attract.gameEntry;


import com.cube.attract.gameEntry.SceneState;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Toast;

public class GameEntryActivity extends Activity{
	private GLSurfaceView surface;
	private GlRenderer renderer;
	
	private GestureDetector gestureDetector;
	
	SceneState sceneState = SceneState.getInstance();
	
	private static boolean toasted;
	
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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode)
		{
		case KeyEvent.KEYCODE_L:
			sceneState.toggleLighting();
			break;
		case KeyEvent.KEYCODE_F:
			sceneState.switchToNextFilter();
			break;
		case KeyEvent.KEYCODE_DPAD_CENTER:
			sceneState.saveRotation();
			sceneState.dxSpeed = 0.0f;
			sceneState.dySpeed = 0.0f;
			break;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			sceneState.saveRotation();
			sceneState.dxSpeed -= 0.1f;
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			sceneState.saveRotation();
			sceneState.dxSpeed += 0.1f;
			break;
		case KeyEvent.KEYCODE_DPAD_UP:
			sceneState.saveRotation();
			sceneState.dySpeed -= 0.1f;
			break;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			sceneState.saveRotation();
			sceneState.dySpeed += 0.1f;
			break;
		}
		return super.onKeyDown(keyCode, event);
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
			sceneState.saveRotation();
			startX = event.getX();
			startY = event.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			sceneState.dx = event.getX() - startX;
			sceneState.dy = event.getY() - startY;
			break;
		}
		
		return super.onTouchEvent(event);
	}

	private class GlAppGestureListener extends GestureDetector.SimpleOnGestureListener
    {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			// measure speed in milliseconds
			sceneState.dxSpeed = velocityX / 1000;
			sceneState.dySpeed = velocityY / 1000;
			return super.onFling(e1, e2, velocityX, velocityY);
		}
    }
	
}

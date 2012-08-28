package com.cube.attract.entry;

import javax.microedition.khronos.opengles.GL10;

import com.cube.opengl.common.GlMatrix;


final class SceneState {
	
	static SceneState instance = null;

	static public SceneState getInstance() {
		if (instance == null)
			instance = new SceneState();
		return instance;
	}
	
	static final float angleFactor = 0.40f;
	float dx, dy;
	float dxSpeed, dySpeed;
	GlMatrix baseMatrix = new GlMatrix();
	
	boolean lighting = true;
	boolean blending = true;
	int filter = 2;
	
	public boolean isClicked = false;
	public boolean isShaked = false;
	public boolean isShaking = false;

	public int NONE = 0;
	public int CUB = 1;
	public int LOGO = 2;

	public int eventType = CUB;
	
	int screenWidth; 
	int screenHeight;
	
	public void toggleLighting() {
		lighting = !lighting;
	}

	public void switchToNextFilter() {
		filter = (filter + 1) % 3;
	}

	void saveRotation() {
		float r = (float)Math.sqrt(dx * dx + dy * dy);
		if (r != 0) {
			GlMatrix rotation = new GlMatrix();
			rotation.rotate(r * angleFactor, dy / r, dx / r, 0);
			baseMatrix.premultiply(rotation);
		}
		dx = 0.0f;
		dy = 0.0f;
	}
	 
	void rotateModel(GL10 gl) {
		float r = (float)Math.sqrt(dx * dx + dy * dy);
		if (r != 0) {
			gl.glRotatef(r * angleFactor, dy / r, dx / r, 0);
		}
		gl.glMultMatrixf(baseMatrix.data);
	}

	public void dampenSpeed(long deltaMillis) {
		if (dxSpeed != 0.0f) {
			dxSpeed *= (1.0f - 0.001f * deltaMillis);
			if (Math.abs(dxSpeed) < 0.001f) dxSpeed = 0.0f;
		}
		
		if (dySpeed != 0.0f) {
			dySpeed *= (1.0f - 0.001f * deltaMillis);
			if (Math.abs(dySpeed) < 0.001f) dySpeed = 0.0f;
		}
	}
}

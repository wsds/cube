package com.cube.attract.entry;

import javax.microedition.khronos.opengles.GL10;

import com.cube.common.pickup.Matrix4f;
import com.cube.opengl.common.GlMatrix;


public final class SceneState {
	
	static SceneState instance = null;

	static public SceneState getInstance() {
		if (instance == null)
			instance = new SceneState();
		return instance;
	}
	
	static final float angleFactor = 0.40f;
	float dx, dy;
	public float x, y;
	float dxSpeed, dySpeed;
	GlMatrix baseMatrix = new GlMatrix();
	
	boolean lighting = true;
	boolean blending = true;
	int filter = 2;
	
	public boolean isClicked = false;

	public int NONE = 0;
	public int CUB = 1;
	public int LOGO = 2;

	public int eventType = CUB;
	
	int screenWidth; 
	int screenHeight;
	public float zoom = -6.0f - 0.6f;
	/**
	 * 投影矩阵
	 */
	public Matrix4f gMatProject = new Matrix4f();
	/**
	 * 视图矩阵
	 */
	public Matrix4f gMatView = new Matrix4f();
	/**
	 * 模型矩阵
	 */
	public Matrix4f gMatModel = new Matrix4f();
	/**
	 * 视口参数
	 */
	public int[] gpViewport = new int[4];
	/**
	 * 当前系统的投影矩阵，列序填充
	 */
	public float[] gpMatrixProjectArray = new float[16];
	/**
	 * 当前系统的视图矩阵，列序填充
	 */
	public float[] gpMatrixViewArray = new float[16];

	/**
	 * 是否有三角形被选中
	 */
	public boolean gbTrianglePicked = false;
	/**
	 * 是否需要进行拾取检测（当触摸事件发生时）
	 */
	public boolean gbNeedPick = false;
	public int picked = -1;
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

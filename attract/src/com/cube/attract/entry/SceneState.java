package com.cube.attract.entry;

import javax.microedition.khronos.opengles.GL10;

import com.cube.common.pickup.Matrix4f;
import com.cube.opengl.common.GlMatrix;

public class SceneState {

	private static SceneState instance = new SceneState();

	public static SceneState getInstance() {
		return instance;
	}

	public float angleFactor = 0.35f;
	public float x, y;
	public float dx_CUB = 0, dy_CUB = 0;
	public float dxSpeed_CUB = 0.1f, dySpeed_CUB = 0.1f;
	public GlMatrix baseMatrix = new GlMatrix();
	public boolean isClicked = false;
	public float zoom = -6.0f - 0.6f;
	boolean blending = true;
	int filter = 2;
	boolean lighting =true;
	
	public boolean isShaked = false;
	public boolean isShaking = false;

	public int NONE = 0;
	public int CUB = 1;
	public int BRAND = 2;

	public int eventType = NONE;

	public boolean gbNeedPick = false;
	public int picked = -1;

	public void saveRotation() {
		float r = (float) Math.sqrt(dx_CUB * dx_CUB + dy_CUB * dy_CUB);
		if (r != 0) {
			GlMatrix rotation = new GlMatrix();
			rotation.rotate(r * angleFactor, dy_CUB / r, dx_CUB / r, 0);
			baseMatrix.premultiply(rotation);
		}
		dx_CUB = 0.0f;
		dy_CUB = 0.0f;
		dxSpeed_CUB = 0.0f;
		dySpeed_CUB = 0.0f;
	}

	public void rotateModel(GL10 gl) {
		float r = (float) Math.sqrt(dx_CUB * dx_CUB + dy_CUB * dy_CUB);
		if (r != 0) {
			gl.glRotatef(r * angleFactor, dy_CUB / r, dx_CUB / r, 0);
		}
		gl.glMultMatrixf(baseMatrix.data);
	}


	public void dampenSpeed_CUB(long deltaMillis) {
		if (dxSpeed_CUB != 0.0f) {
			dxSpeed_CUB *= (1.0f - 0.001f * deltaMillis);

			if (Math.abs(dxSpeed_CUB) < 0.001f) {
				dxSpeed_CUB = 0.0f;
			}
		}

		if (dySpeed_CUB != 0.0f) {
			dySpeed_CUB *= (1.0f - 0.001f * deltaMillis);
			if (Math.abs(dySpeed_CUB) < 0.001f)
				dySpeed_CUB = 0.0f;
		}
	}

	/**
	 * ͶӰ����
	 */
	public Matrix4f gMatProject = new Matrix4f();
	/**
	 * ��ͼ����
	 */
	public Matrix4f gMatView = new Matrix4f();
	/**
	 * ģ�;���
	 */
	public Matrix4f gMatModel = new Matrix4f();
	/**
	 * �ӿڲ���
	 */
	public int[] gpViewport = new int[4];
	/**
	 * ��ǰϵͳ��ͶӰ�����������
	 */
	public float[] gpMatrixProjectArray = new float[16];
	/**
	 * ��ǰϵͳ����ͼ�����������
	 */
	public float[] gpMatrixViewArray = new float[16];

	/**
	 * �Ƿ�������α�ѡ��
	 */
	public boolean gbTrianglePicked = false;


	public SceneState() {
	}

	public float stopFactor = 0.312f;
	public int minNum = 0;
	public float minX = 10000;
	public float minXabs = 10000;


	public boolean isStopping = false;
	public int stop = 10;
	public int screenWidth;
	public int screenHeight;





}

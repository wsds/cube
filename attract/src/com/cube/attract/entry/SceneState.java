package com.cube.attract.entry;

import javax.microedition.khronos.opengles.GL10;

import com.cube.common.pickup.Matrix4f;
import com.cube.opengl.common.GlMatrix;

public class SceneState {

	private static SceneState instance = new SceneState();

	// 公开，静态的工厂方法
	public static SceneState getInstance() {
		return instance;
	}

	public float angleFactor = 0.35f;
	public float x, y;
	public float dx_BRAND = 0, dy_BRAND = 0;
	public float dx_CUB = 0, dy_CUB = 0;
	public float dxSpeed_BRAND = -0.1f, dySpeed_BRAND = 0;
	public float dxSpeed_CUB = 0.1f, dySpeed_CUB = 0.1f;
	public GlMatrix baseMatrix = new GlMatrix();
	public boolean isClicked = false;
	public float zoom = -6.0f - 0.6f;

	public int NONE = 0;
	public int CUB = 1;
	public int BRAND = 2;

	public int eventType = NONE;

	/**
	 * 是否需要进行拾取检测（当触摸事件发生时）
	 */
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
	}

	public void rotateModel(GL10 gl) {
		float r = (float) Math.sqrt(dx_CUB * dx_CUB + dy_CUB * dy_CUB);
		if (r != 0) {
			gl.glRotatef(r * angleFactor, dy_CUB / r, dx_CUB / r, 0);
		}
		gl.glMultMatrixf(baseMatrix.data);
	}

	public void dampenSpeed_BRAND(long deltaMillis) {
		if (dxSpeed_BRAND != 0.0f) {
			dxSpeed_BRAND *= (1.0f - 0.001f * deltaMillis);

			if (Math.abs(dxSpeed_BRAND) < 0.061f) {
				stopmove();
			}

		}

		if (dySpeed_BRAND != 0.0f) {
			dySpeed_BRAND *= (1.0f - 0.001f * deltaMillis);
			if (Math.abs(dySpeed_BRAND) < 0.001f)
				dySpeed_BRAND = 0.0f;
		}
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

	// public float gScreenX, gScreenY;

	// public void setTouchPosition(float x, float y) {
	// gScreenX = x;
	// gScreenY = y;
	// }

	public SceneState() {
		initailizeBrandViews();
	}

	public float stopFactor = 0.312f;
	public int minNum = 0;
	public float minX = 10000;
	public float minXabs = 10000;

	public void stopmove() {
		isStopping = true;
		stop = 10;
		for (int i = 0; i < viewsNum; i++) {
			if (Math.abs(brandViews[i].x) < minXabs) {
				minXabs = Math.abs(brandViews[i].x);
				minX = brandViews[i].x;
				minNum = i;
			}
		}
		dxSpeed_BRAND = 0.0f;
	}

	public boolean isStopping = false;
	public int stop = 10;

	public boolean isStopmoving() {
		if (isStopping == false) {
			return false;
		}
		dx_BRAND = dx_BRAND + (minX) / (10 * moveFactor);
		// float minXabs = 10000;
		// for (int i = 0; i < viewsNum; i++) {
		// if (Math.abs(brandViews[i].x) < minXabs) {
		// minXabs = Math.abs(brandViews[i].x);
		// }
		// }
		// if (minXabs < 0.01) {
		// isStopping = false;
		// return true;
		// }
		stop--;
		if (stop < 1) {
			isStopping = false;
			return true;
		}
		return false;
	}

	public BrandView[] brandViews;
	public int viewsNum = 120;
	public int index;

	public void initailizeBrandViews() {
		// setup stars
		brandViews = new BrandView[viewsNum];

		for (int i = 0; i < viewsNum; i++) {
			brandViews[i] = new BrandView();
			brandViews[i].brand = i % 6;
			brandViews[i].angle = 0;
			brandViews[i].z = -0.0f;
		}
		index = 0;
		brandViews[index].x = 0;
		for (int i = 0; i <= viewsNum / 2; i++) {
			int relativeIndex = (index + i) % viewsNum;
			brandViews[relativeIndex].x = brandViews[index].x + i * 1.0f;
			if (i > 1) {
				brandViews[relativeIndex].x -= 0.3f;
			}
		}
		for (int i = 1; i < viewsNum / 2; i++) {
			int relativeIndex = (index - i) % viewsNum;
			brandViews[(viewsNum + relativeIndex) % viewsNum].x = brandViews[index].x - i * 1.0f;
			if (i > 1) {
				brandViews[(viewsNum + relativeIndex) % viewsNum].x += 0.3f;
			}
		}
		for (int i = 0; i < viewsNum; i++) {
			brandViews[i].px = brandViews[i].x;
		}
		movementZ();
		brandViews[0].angle = 0;
		int j = 1;
		j++;
	}

	public float moveFactor = 0.012f;

	public void movement() {
		float movedx = (float) moveFactor * dx_BRAND;
		brandViews[index].x = brandViews[index].px + movedx;
		// for (int i = 0; i < viewsNum; i++) {
		// brandViews[i].x = brandViews[i].px + movedx;
		// }
		// if(Math.abs(movedx-reOrganizeDx)>(viewsNum / 3)){
		// reOrganizeDx=movedx;
		// reOrganizeViews();
		// }//此处还需在深入
		movementX();
		movementZ();
	}

	public void saveMovement() {
		movement();
		reOrganizeViews();

		for (int i = 0; i < viewsNum; i++) {
			brandViews[i].px = brandViews[i].x;
		}
		// reOrganizeDx=0.0f;
		dx_BRAND = 0.0f;
		dx_BRAND = 0.0f;
	}

	public void movementX() {
		for (int i = 0; i <= viewsNum / 2; i++) {
			int relativeIndex = (index + i) % viewsNum;
			brandViews[relativeIndex].x = brandViews[index].x + i * 1.0f;
			// if (i > 1) {
			// brandViews[relativeIndex].x -= 0.3f;
			// }
		}

		for (int i = 1; i < viewsNum / 2; i++) {
			int relativeIndex = (index - i) % viewsNum;
			brandViews[(viewsNum + relativeIndex) % viewsNum].x = brandViews[index].x - i * 1.0f;
			// if (i > 1) {
			// brandViews[(viewsNum + relativeIndex) % viewsNum].x += 0.3f;
			// }
		}
	}

	public void movementZ() {
		for (int i = 0; i < viewsNum; i++) {
			float x = brandViews[i].x;
			float z;
			if (x > -2.8 && x < 2.8) {
				// brandViews[i].z = (0.36f - x * x-0.3f);
				brandViews[i].z = (float) (1.9f * Math.exp(-x * x / (2 * 0.25)));
				brandViews[i].y = -brandViews[i].z * 0.16f;
				z = brandViews[i].z;
				brandViews[i].angle = 70.f * (1.9f - z) / 1.9f * x / Math.abs(x);
			}
		}
	}

	public void reOrganizeViews() {
		boolean needReorganized = false;
		for (int i = 0; i < viewsNum; i++) {
			if (brandViews[i].x > -0.8 && brandViews[i].x <= 0.8 && index != i) {
				index = i;
				needReorganized = true;
			}
		}
		if (needReorganized == false) {
			return;
		}
		movementX();
	}

	public class BrandView {
		public float px;
		public float x;
		public float y;
		public float z;
		public int brand;
		public float angle;
	}

}

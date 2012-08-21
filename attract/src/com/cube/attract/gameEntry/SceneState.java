package com.cube.attract.gameEntry;


import android.util.Log;


final class SceneState {

	static SceneState instance = null;

	static public SceneState getInstance() {
		if (instance == null)
			instance = new SceneState();
		return instance;
	}

	public GlRenderer render = null;
	boolean blending = false;

	public boolean backAnimaLock = true;
	public boolean goFrontPermit = false;
	public boolean isTouchUp = false;
	public boolean notJustComeIn = false;

	public int NONE = 0;
	public int CUB = 1;
	public int GIRL = 2;
	public int POLYGONBUTTON = 3;
	boolean[] isLocked = new boolean[6];
	boolean[] isSelected = new boolean[6];

	public int eventType = CUB;

	int screenWidth;
	int screenHeight;

	PictureViewGallary pictureViewGallary = new PictureViewGallary();

	public class PictureViewGallary {
		PictureViewGallary() {
			initailizepictureView();
		}

		float dySpeed;

		float dx;

		public float stopFactor = 0.312f;
		public int minNum = 0;
		public double minX = 10000;
		public double minXabs = 10000;
		public float dxSpeed = 0;
		public float dAngle = 0;
		public float dy = 0;

		public int totalGirls = 3;
		public double diff = 0;
		public int frontViewIndex = 0;
		public boolean isStopping = false;
		public boolean isPostive = true;
		public boolean once = false;

		public void stopmove() {
			dxSpeed = 0;
			dAngle = 0;
			for (int i = 0; i < viewsNum; i++) {
				int temp = (i + frontViewIndex) % viewsNum;
				pictureView[temp].radian = 2 * PI / viewsNum * i;
				pictureView[temp].pAngle = pictureView[temp].radian;
			}
			if (goFrontPermit) {
				if(notJustComeIn){//刚进入该activity,并且没有onTouchMove事件时，不执行以下代码
					render.girlGoFront.start(true);
					render.girlRotateFront.start(true);
				}
				notJustComeIn = true;
				backAnimaLock = true;
				goFrontPermit = false;
			}

		}

		public boolean isPosCorrect(long delta) {
			// for (int i = 0; i < viewsNum; i++) {
			// // double tempRadian = pictureView[i].radian
			// // - Math.floor(pictureView[i].radian / (2 * PI)) * 2 * PI;
			// if (pictureView[i].radian > 2 * PI - 0.005) {
			// Log.i("radian[" + String.valueOf(i) + "] and postive",
			// String.valueOf(pictureView[i].radian));
			// isStopping = false;
			// stopmove();
			// return true;
			// } else if (pictureView[i].radian < 0.005) {
			// Log.i("radian[" + String.valueOf(i) + "] and negtive",
			// String.valueOf(pictureView[i].radian));
			// isStopping = false;
			// stopmove();
			// return true;
			// }
			// } this is other way to achieve the same function as below
			Log.i("diff and isPostive and dxSpeed", String.valueOf(diff)
					+ String.valueOf(isPostive) + String.valueOf(dxSpeed));
			if (isPostive) {
				diff -= dxSpeed * delta * moveFactor;
			} else {
				diff += dxSpeed * delta * moveFactor;
			}
			if (diff <= 0) {// when the galley rotate to the correct postion or
							// just beyond the correct postion
				isStopping = false;
				stopmove();
			}
			return false;
		}

		public PictureView[] pictureView;
		public int viewsNum = 9;

		float radius = 25f;
		double PI = Math.PI;

		public void initailizepictureView() {
			// setup stars
			pictureView = new PictureView[viewsNum];

			for (int i = 0; i < viewsNum; i++) {
				pictureView[i] = new PictureView();
				pictureView[i].girlNumber = i % totalGirls;
				double radian = 2 * PI / viewsNum * i;
				pictureView[i].radian = radian;
				pictureView[i].pAngle = radian;

				pictureView[i].x = radius * Math.sin(radian);
				pictureView[i].y = 0;
				pictureView[i].z = radius * Math.cos(radian);
			}

		}

		public float moveFactor = 0.002f;

		public void movement() {
			float moveAngle = (float) moveFactor * dAngle;

			double halfRefRadian = PI / viewsNum;
			for (int i = 0; i < viewsNum; i++) {
				pictureView[i].radian = pictureView[i].pAngle + moveAngle;
				pictureView[i].radian = pictureView[i].radian
						- Math.floor(pictureView[i].radian / (2 * PI)) * 2 * PI;
				// pictureView[i].pAngle = pictureView[i].radian;
				if (pictureView[i].radian > 2 * PI - halfRefRadian) {
					frontViewIndex = i;
					isPostive = true;

					diff = 2 * PI - pictureView[i].radian;
				} else if (pictureView[i].radian < halfRefRadian) {
					frontViewIndex = i;
					isPostive = false;
					diff = pictureView[i].radian;
				}
				if (dxSpeed == 0 && isTouchUp) {
					if (pictureView[frontViewIndex].radian != 0) {
						stopmove();
						isTouchUp = false;
					}
				}
				pictureView[i].x = radius * Math.sin(pictureView[i].radian);
				pictureView[i].y = 0;
				pictureView[i].z = radius * Math.cos(pictureView[i].radian);
			}
		}

		public void saveMovement() {
			for (int i = 0; i < viewsNum; i++) {
				pictureView[i].pAngle = pictureView[i].radian;
			}
			dAngle = 0.0f;
		}

		public void dampenSpeed(long deltaMillis) {
			if (dxSpeed != 0.0f) {
				if (isStopping) {
					dxSpeed = Math.abs(dxSpeed);
					if (isPostive) {
						dxSpeed *= (1.0f + 0.006f * deltaMillis);
					} else {
						dxSpeed *= (-0.006f * deltaMillis - 1.0f);
					}
					isPosCorrect(deltaMillis);
				} else {
					dxSpeed *= (1.0f - 0.0009f * deltaMillis);
				}
				if (Math.abs(dxSpeed) < 0.261f && !isStopping) {
					isStopping = true;

					Log.i("isStopping and dxSpeed", String.valueOf(isStopping)
							+ "and" + String.valueOf(dxSpeed));
				}
			}
		}

		public class PictureView {
			public double pAngle;
			public double x;
			public double y;
			public double z;
			public int girlNumber;
			public double radian;
		}
	}

}

package com.cube.attract.gameEntry;

import javax.microedition.khronos.opengles.GL10;

import android.util.Log;

import com.cube.common.Settings;
import com.cube.opengl.common.GlMatrix;

final class SceneState {

	static SceneState instance = null;

	static public SceneState getInstance() {
		if (instance == null)
			instance = new SceneState();
		return instance;
	}

	public GlRenderer render = null;
	boolean blending = false;

	public boolean isClicked = false;

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

		}

		public boolean isPosCorrect() {
			double halfRefRadian = PI / viewsNum;

			for (int i = 0; i < viewsNum; i++) {
				// double tempRadian = pictureView[i].radian
				// - Math.floor(pictureView[i].radian / (2 * PI)) * 2 * PI;
				if (pictureView[i].radian > 2 * PI - 0.005) {
					Log.i("radian[" + String.valueOf(i) + "] and postive",
							String.valueOf(pictureView[i].radian));
					isStopping = false;
					stopmove();
					return true;
				} else if (pictureView[i].radian < 0.005) {
					Log.i("radian[" + String.valueOf(i) + "] and negtive",
							String.valueOf(pictureView[i].radian));
					isStopping = false;
					stopmove();
					return true;
				}
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

		public float moveFactor = 0.0025f;

		public void movement() {
			float moveAngle = (float) moveFactor * dAngle;

			double halfRefRadian = PI / viewsNum;
			for (int i = 0; i < viewsNum; i++) {
				pictureView[i].radian = pictureView[i].pAngle + moveAngle;

				pictureView[i].x = radius * Math.sin(pictureView[i].radian);
				pictureView[i].y = 0;
				pictureView[i].z = radius * Math.cos(pictureView[i].radian);
			}
			for (int i = 0; i < viewsNum; i++) {
				pictureView[i].radian = pictureView[i].radian
						- Math.floor(pictureView[i].radian / (2 * PI)) * 2 * PI;
				if(dxSpeed ==0){
					
				}
				if (pictureView[i].radian > 2 * PI - halfRefRadian) {
					frontViewIndex = i;
					isPostive = true;
					// diff = 2 * PI - pictureView[i].radian;
				} else if (pictureView[i].radian < halfRefRadian) {
					frontViewIndex = i;
					isPostive = false;
					// diff = -pictureView[i].radian;
				}

			}

			// pictureView[start].radian-2 * PI / viewsNum * start;

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
						dxSpeed *= (1.0f + 0.002f * deltaMillis);
					} else {
						dxSpeed *= (-0.002f * deltaMillis - 1.0f);
					}
					isPosCorrect();
				} else {
					dxSpeed *= (1.0f - 0.002f * deltaMillis);
				}
				if (Math.abs(dxSpeed) < 0.161f && !isStopping) {
					isStopping = true;

					Log.i("isStopping and dxSpeed", String.valueOf(isStopping)
							+ "and" + String.valueOf(dxSpeed));
					// stopmove();
				}
			} else {

				if (once) {
					render.girlGoFront.start(true);
					render.girlRotateFront.start(true);
					once = false;
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

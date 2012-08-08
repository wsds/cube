package com.cube.attract.gameEntry;

import javax.microedition.khronos.opengles.GL10;

import com.cube.common.Settings;
import com.cube.opengl.common.GlMatrix;

final class SceneState {

	static SceneState instance = null;

	static public SceneState getInstance() {
		if (instance == null)
			instance = new SceneState();
		return instance;
	}

	boolean blending = true;

	public boolean isClicked = false;

	public int NONE = 0;
	public int CUB = 1;
	public int GIRL = 2;

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

		public void stopmove() {
			isStopping = true;
			stop = 10;
			for (int i = 0; i < viewsNum; i++) {
				if (Math.abs(pictureView[i].x) < minXabs) {
					minXabs = Math.abs(pictureView[i].x);
					minX = pictureView[i].x;
					minNum = i;
				}
			}
			dxSpeed = 0.0f;
		}

		public boolean isStopping = false;
		public int stop = 10;

		public boolean isStopmoving() {
			if (isStopping == false) {
				return false;
			}
			dAngle = (float) (dAngle + (minX) / (10 * moveFactor));
			// float minXabs = 10000;
			// for (int i = 0; i < viewsNum; i++) {
			// if (Math.abs(pictureView[i].x) < minXabs) {
			// minXabs = Math.abs(pictureView[i].x);
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

		public PictureView[] pictureView;
		public int viewsNum = 8;

		float radius = 25f;
		double PI = Math.PI;

		public void initailizepictureView() {
			// setup stars
			pictureView = new PictureView[viewsNum];

			for (int i = 0; i < viewsNum; i++) {
				pictureView[i] = new PictureView();
				pictureView[i].girlNumber = i % 3;
				double angle = 2 * PI / viewsNum * i;
				pictureView[i].angle = angle;
				pictureView[i].pAngle = angle;

				pictureView[i].x = radius * Math.sin(angle);
				pictureView[i].y = 0;
				pictureView[i].z = radius * Math.cos(angle);
			}
			int i=0;
			i++;
		}

		public float moveFactor = 0.002f;

		public void movement() {
			float moveAngle = (float) moveFactor * dAngle;

			for (int i = 0; i < viewsNum; i++) {
				pictureView[i].angle = pictureView[i].pAngle + moveAngle;

				pictureView[i].x = radius * Math.sin(pictureView[i].angle);
				pictureView[i].y = 0;
				pictureView[i].z = radius * Math.cos(pictureView[i].angle);
			}
		}

		public void saveMovement() {
			for (int i = 0; i < viewsNum; i++) {
				pictureView[i].pAngle = pictureView[i].angle;
			}
			dAngle = 0.0f;
		}

		public void dampenSpeed(long deltaMillis) {
			if (dxSpeed != 0.0f) {
				dxSpeed *= (1.0f - 0.001f * deltaMillis);

				if (Math.abs(dxSpeed) < 0.061f) {
					stopmove();
				}
			}
		}

		public class PictureView {
			public double pAngle;
			public double x;
			public double y;
			public double z;
			public int girlNumber;
			public double angle;
		}
	}

}

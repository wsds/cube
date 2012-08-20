package com.cube.canvas.common;

import java.util.ArrayList;

import android.graphics.Matrix;

public class CanvasAnimation2 {
	ArrayList<CanvasAnimation2> next = new ArrayList<CanvasAnimation2>();
	ArrayList<CanvasAnimation2> children = null;

	// inner class && data structure
	public interface Callback {
		public void onEnd();
	}

	public class Translate {
		public float dx = 0;
		public float dy = 0;
	}

	public class Rotate {
		public float dr = 0;
		public float fx = 0;
		public float fy = 0;
	}

	public class Scale {
		public float ds = 0;
		public float fx = 0;
		public float fy = 0;
	}

	public class Accelerate {

		public float dx = 0;
		public float dy = 0;
		public float a = 0;
		public float v0 = 0;

	}

	// data
	public String[] types = { "Translate", "Rotate", "Scale", "Accelerate" };

	public int TRANSLATE = 0;
	public int ROTATE = 1;
	public int SCALE = 2;
	public int ACCELERATE = 3;
	public int type = TRANSLATE;

	public Callback callback = null;

	public static long INFINITE = -9999;
	public long repeatTimes = 0;
	public Translate translate = new Translate();
	public Rotate rotate = new Rotate();
	public Scale scale = new Scale();
	public Accelerate accelerate = new Accelerate();
	public float dt = 0;

	public boolean isActive = false;

	// initialize method
	public void addNextAnimation(CanvasAnimation2 animation) {
		this.next.add(animation);
	}

	public void setCallback(Callback callback) {
		this.callback = callback;
	}

	public void setRepeatTimes(long repeatTimes) {
		this.repeatTimes = repeatTimes;
		this.remainRepeatTimes = repeatTimes;
	}

	public void setRepeatSelfTimes(long repeatTimes) {
		this.repeatTimes = repeatTimes;
		this.remainRepeatTimes = repeatTimes;
		addNextAnimation(this);
	}

	public void setTranslate(float dx, float dy, float dt) {
		this.translate.dx = dx;
		this.translate.dy = dy;
		this.dt = dt;

		this.type = TRANSLATE;
	}

	public void setRotate(float dr, float fx, float fy, float dt) {
		this.rotate.dr = dr;
		this.rotate.fx = fx;
		this.rotate.fy = fy;
		this.dt = dt;

		this.type = ROTATE;
	}

	public void setScale(float ds, float fx, float fy, float dt) {
		this.scale.ds = ds;
		this.dt = dt;
		this.scale.fx = fx;
		this.scale.fy = fy;
		this.type = SCALE;
	}

	// public void setAccelerate(float dx, float dy, float a, float dt) {
	// this.accelerate.dx = (float) (dx / Math.sqrt(dx * dx + dy * dy));
	// this.accelerate.dy = (float) (dy / Math.sqrt(dx * dx + dy * dy));
	// this.accelerate.a = a;
	// this.dt = dt;
	// this.accelerate.v0 = -a * dt;
	//
	// this.type = ACCELERATE;
	// }

	// logic

	long lastMillis = 0;
	long remainTime = 0;
	long remainRepeatTimes = 1;
	public long transformCount = 0;

	public boolean transformModel(Matrix matrix) {
		boolean isFinished = false;

		if (isActive == false && (remainRepeatTimes > 0 || remainRepeatTimes < -9998)) {
			remainTime = (long) dt;
			isActive = true;
			lastMillis = 0;
		}

		if (remainTime == 0) {
			if (this.callback != null) {
				this.callback.onEnd();
			}
			if (isActive == true) {
				isActive = false;
				if (remainRepeatTimes > 0 || remainRepeatTimes < -9998) {
					children = next;
				}
				remainRepeatTimes--;
			} else {
				children = null;
			}
			isFinished = true;

		} else {

			long currentMillis = System.currentTimeMillis();

			if (lastMillis != 0 && remainTime > 0) {
				long delta = currentMillis - lastMillis;
				if (remainTime > delta) {
					remainTime = remainTime - delta;
				} else {
					delta = remainTime;
					remainTime = 0;
				}

				if (this.type == TRANSLATE) {
					matrix.postTranslate(translate.dx / dt * delta, translate.dy / dt * delta);
				} else if (this.type == ROTATE) {
					matrix.preRotate(rotate.dr / dt * delta, rotate.fx, rotate.fy);
				} else if (this.type == SCALE) {
					// traceMatrix.postScale(1 + delta * (scale.ds - 1) / scale.dt, 1 + delta * (scale.ds - 1) / scale.dt, scale.fx, scale.fy);
				} else if (this.type == ACCELERATE) {
					// float ds = (float) ((accelerate.v0 + 0.5 * accelerate.a * delta) * delta);
					// traceMatrix.postTranslate(accelerate.dx * ds, accelerate.dy * ds);
					// accelerate.v0 -= accelerate.a * delta;
				}
			}

			lastMillis = currentMillis;
			transformCount++;
		}

		return isFinished;
	}
}

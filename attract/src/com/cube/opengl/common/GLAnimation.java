package com.cube.opengl.common;

import javax.microedition.khronos.opengles.GL10;

public class GLAnimation {
	// inner class && data structure
	public interface Callback {
		public void onEnd();
	}

	public class Translate {
		public float dx = 0;
		public float dy = 0;
		public float dz = 0;
		public float dt = 0;
	}

	public class Rotate {
		public float dr = 0;
		public float fx = 0;
		public float fy = 0;
		public float fz = 0;
		public float dt = 0;
	}

	public class Scale {
		public float ds = 0;
		public float dt = 0;
	}

	// data
	public String[] types = { "Translate", "Rotate", "Scale" };
	public String type = types[0];

	public Callback callback = null;
	public Translate translate = new Translate();
	public Rotate rotate = new Rotate();
	public Scale scale = new Scale();

	public GlMatrix transform = new GlMatrix();

	// initialize method
	public void setCallback(Callback callback) {
		this.callback = callback;
	}

	public void setTranslate(float dx, float dy, float dz, float dt) {
		this.translate.dx = dx;
		this.translate.dy = dy;
		this.translate.dz = dz;
		this.translate.dt = dt;

		this.type = "Translate";
	}

	public void setRotate(float dr, float fx, float fy, float fz, float dt) {
		this.rotate.dr = dr;
		this.rotate.fx = fx;
		this.rotate.fy = fy;
		this.rotate.fz = fz;
		this.rotate.dt = dt;

		this.type = "Rotate";
	}

	public void setScale(float ds, float dt) {
		this.scale.ds = ds;
		this.scale.dt = dt;

		this.type = "Scale";

	}

	// logic

	long lastMillis = 0;
	long remainTime = 0;
	long transformCount = 0;

	public void transformModel(GL10 gl) {
		if (transformCount == 0) {
			remainTime = (long) translate.dt;
		}

		long currentMillis = System.currentTimeMillis();

		if (lastMillis != 0 && remainTime > 0) {
			long delta = currentMillis - lastMillis;
			if (remainTime > delta) {
				remainTime = remainTime - delta;
			} else {
				delta = remainTime;
				remainTime = 0;
			}

			transform.translate(translate.dx / translate.dt * delta, translate.dy / translate.dt * delta, translate.dz / translate.dt * delta);
		}

		lastMillis = currentMillis;

		gl.glMultMatrixf(transform.data);
		transformCount++;
	}

}

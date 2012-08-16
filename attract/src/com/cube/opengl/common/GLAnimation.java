package com.cube.opengl.common;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

public class GLAnimation {
	ArrayList<GLAnimation> next = new ArrayList<GLAnimation>();

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

	public class Color {
		public float red = 0;
		public float green = 0;
		public float blue = 0;
		public float blend = 0;
		public float dt = 0;
	}

	// data
	public String[] types = { "Translate", "Rotate", "Scale", "Color" };

	public int TRANSLATE = 0;
	public int ROTATE = 1;
	public int SCALE = 2;
	public int COLOR = 3;
	public int type = TRANSLATE;

	public Callback callback = null;

	public static long INFINITE = -9999;
	public long repeatTimes = 0;
	public Translate translate = new Translate();
	public Rotate rotate = new Rotate();
	public Scale scale = new Scale();
	public Color color = new Color();

	public GlMatrix transform = new GlMatrix();

	boolean isStarted = true;

	// initialize method
	public void addNextAnimation(GLAnimation animation) {
		this.next.add(animation);
	}

	public void setColor(float r, float g, float b, float blend,float dt) {
		this.color.red = r;
		this.color.green = g;
		this.color.blue = b;
		this.color.blend = blend;
		this.color.dt = dt;
	}

	public void setCallback(Callback callback) {
		this.callback = callback;
	}

	public void setRepeatTimes(long repeatTimes) {
		this.repeatTimes = repeatTimes;
	}

	public void setTranslate(float dx, float dy, float dz, float dt) {
		this.translate.dx = dx;
		this.translate.dy = dy;
		this.translate.dz = dz;
		this.translate.dt = dt;

		this.type = TRANSLATE;
	}

	public void setRotate(float dr, float fx, float fy, float fz, float dt) {
		this.rotate.dr = dr;
		this.rotate.fx = fx;
		this.rotate.fy = fy;
		this.rotate.fz = fz;
		this.rotate.dt = dt;

		this.type = ROTATE;
	}

	public void setScale(float ds, float dt) {
		this.scale.ds = ds;
		this.scale.dt = dt;

		this.type = SCALE;
	}

	// logic

	long lastMillis = 0;
	long remainTime = 0;
	long remainRepeatTimes = -9999;
	public long transformCount = 0;
	boolean isReset = true;

	public long transformModel(GL10 gl) {
		if (this.isStarted == false) {
			return 0;
		}
		long nextRemainTime = 0;
		if (isReset == true) {
			isReset = false;
			if (remainRepeatTimes < -9998) {
				remainRepeatTimes = this.repeatTimes;
			}

			if (this.type == TRANSLATE) {
				remainTime = (long) translate.dt;
			} else if (this.type == ROTATE) {
				remainTime = (long) rotate.dt;
			} else if (this.type == SCALE) {
				remainTime = (long) scale.dt;
			} else if (this.type == COLOR) {
				remainTime = (long) color.dt;
			}
		}

		if (remainTime == 0) {
			if (this.callback != null) {
				this.isStarted = false;
				this.callback.onEnd();
			}

			for (int i = 0; i < this.next.size(); i++) {
				GLAnimation nextAnimation = this.next.get(i);
				nextRemainTime = nextRemainTime
						+ nextAnimation.transformModel(gl);
			}

			if (nextRemainTime == 0) {
				if (remainRepeatTimes > 1 || remainRepeatTimes < -9998) {
					reset();
					remainRepeatTimes--;
				} else {
					// this.isStarted = false;
				}
			}

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
					transform.translate(translate.dx / translate.dt * delta,
							translate.dy / translate.dt * delta, translate.dz
									/ translate.dt * delta);
				} else if (this.type == ROTATE) {
					transform.rotate(rotate.dr / rotate.dt * delta, rotate.fx,
							rotate.fy, rotate.fz);
				} else if (this.type == SCALE) {

				} else if (this.type == COLOR){
					gl.glColor4f(color.red, color.green, color.blue, color.blend);
				}
			}

			lastMillis = currentMillis;
			transformCount++;
		}
		gl.glMultMatrixf(transform.data);

		return remainTime + nextRemainTime;
	}

	public void start(boolean isStarted) {
		reset();
		this.isStarted = isStarted;
	}

	public void reset() {
		lastMillis = 0;
		isReset = true;
		for (int i = 0; i < this.next.size(); i++) {
			GLAnimation nextAnimation = this.next.get(i);
			nextAnimation.reset();
		}
	}
}

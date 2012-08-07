package com.example.common;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

public class CanvasAnimation {
	ArrayList<CanvasAnimation> next = new ArrayList<CanvasAnimation>();
	// inner class && data structure
		public interface Callback {
			public void onEnd();
		}
		
		public class CurrentPosition {
			public float CurrentX = 0;
			public float CurrentY = 0;
			public float CurrentAngle = 0;
		}
		public class Translate {
			public float dx = 0;
			public float dy = 0;
			//public float dz = 0;
			public float dt = 0;
		}

		public class Rotate {
			public float dr = 0;
			public float fx = 0;
			public float fy = 0;
			//public float fz = 0;
			public float dt = 0;
		}

		public class Scale {
			public float ds = 0;
			public float dt = 0;
		}

		// data
		public String[] types = { "Translate", "Rotate", "Scale" };

		public int TRANSLATE = 0;
		public int ROTATE = 1;
		public int SCALE = 2;
		public int type = TRANSLATE;

		public Callback callback = null;

		public static long INFINITE = -9999;
		public long repeatTimes = 0;
		public Translate translate = new Translate();
		public Rotate rotate = new Rotate();
		public Scale scale = new Scale();
		public CurrentPosition currentPosition = new CurrentPosition();
		
		public Bitmap mAnimBitmap = null;
		public Paint mAnimPaint = null;
		public Matrix transformMatrix = new Matrix();

		boolean isStarted = true;

		// initialize method
		public void addNextAnimation(CanvasAnimation animation) {
			this.next.add(animation);
		}

		public void setCallback(Callback callback) {
			this.callback = callback;
		}

		public void setElements(Bitmap bitmap, Paint paint){	
			mAnimBitmap = bitmap;
			mAnimPaint = paint;
		}
		public void setCurrentPosition(float currentX, float currentY, float currentAngle){
			
			currentPosition.CurrentX = currentX;
			currentPosition.CurrentY = currentY;
			currentPosition.CurrentAngle = currentAngle;
		}
		public void setRepeatTimes(long repeatTimes) {
			this.repeatTimes = repeatTimes;
		}

		public void setTranslate(float dx, float dy, float dt) {
			this.translate.dx = dx;
			this.translate.dy = dy;
			/*this.translate.dz = dz;*/
			this.translate.dt = dt;

			this.type = TRANSLATE;
		}

		public void setRotate(float dr, float fx, float fy, float dt) {
			this.rotate.dr = dr;
			this.rotate.fx = fx;
			this.rotate.fy = fy;
			/*this.rotate.fz = fz;*/
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

		public long transformModel(Canvas canvas) {
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
				}
			}

			if (remainTime == 0) {
				if (this.callback != null) {
					this.callback.onEnd();
				}

				for (int i = 0; i < this.next.size(); i++) {
					CanvasAnimation nextAnimation = this.next.get(i);
					nextRemainTime = nextRemainTime + nextAnimation.transformModel(canvas);
				}

				if (nextRemainTime == 0) {
					if (remainRepeatTimes > 1 || remainRepeatTimes < -9998) {
						reset();
						if (remainRepeatTimes > 1)
							remainRepeatTimes--;	//I wonder if this will overflow
					}
					else{
//						this.isStarted = false;
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
						transformMatrix.postTranslate(translate.dx / translate.dt * delta, translate.dy / translate.dt * delta );
						//renew current position
						currentPosition.CurrentX += translate.dx / translate.dt * delta;
						currentPosition.CurrentY += translate.dy / translate.dt * delta;
					} else if (this.type == ROTATE) {
						transformMatrix.postRotate(rotate.dr / rotate.dt * delta, rotate.fx, rotate.fy);
						//renew current position
						//currentPosition.CurrentAngle = 
					} else if (this.type == SCALE) {

					}
				}

				lastMillis = currentMillis;
				transformCount++;
			}
			canvas.drawBitmap(mAnimBitmap, transformMatrix, mAnimPaint);
			//gl.glMultMatrixf(transform.data);

			return remainTime + nextRemainTime;
		}

		public void start(boolean isStarted) {
			reset();
			this.isStarted = isStarted;
		}

		public void reset() {
			lastMillis=0;
			isReset = true;
			for (int i = 0; i < this.next.size(); i++) {
				CanvasAnimation nextAnimation = this.next.get(i);
				nextAnimation.reset();
			}
		}	

}

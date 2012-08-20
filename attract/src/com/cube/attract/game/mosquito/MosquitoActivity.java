package com.cube.attract.game.mosquito;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.cube.attract.R;
import com.cube.canvas.common.CanvasAnimation;

public class MosquitoActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(new AnimView(this));
	}

	public class AnimView extends SurfaceView implements SurfaceHolder.Callback {

		private int mWidth = 0;
		private int mHeight = 0;
		private SurfaceHolder mHolder = null;
		private Thread mThread = null;

		private boolean isRunning = true;
		private CanvasAnimation bulletAnim = null;
		private CanvasAnimation boomAnim = null;
		private CanvasAnimation artilleryAnimOdd = null;
		private CanvasAnimation artilleryAnimEven = null;
		private CanvasAnimation batteryAnimOdd = null;
		private CanvasAnimation batteryAnimEven = null;

		public Bitmap memBm = null;
		private Canvas mCanvas = null;
		public Bitmap initBackgroundBm = null;
		public Bitmap backgroundBm = null;
		public Bitmap powerTube1 = null;
		public Bitmap powerTube2 = null;
		public Bitmap powerTube3 = null;
		public Bitmap heartBm = null;
		public Bitmap bulletBm = null;
		public Bitmap boomBm = null;

		public float[] powerTubeBaseAdress = { 0.0f, 0.0f };
		public final int POWERSENSITY = 15;
		public final int RADIUS = 80;
		public float[] rotateCenter = { 0.0f, 0.0f };
		public float[] targetCenter = { 0.0f, 0.0f };
		public float[] boomCenter = { 0.0f, 0.0f };
		public Bitmap backgroundStage = null;
		public Matrix testMatrix = new Matrix();
		public float[] testMatrixArray;

		public AnimView(Context context) {
			super(context);
			mHolder = this.getHolder();
			mHolder.addCallback(this);
			initBackgroundBm = BitmapFactory.decodeResource(getResources(), R.drawable.welcome_background);
			backgroundBm = BitmapFactory.decodeResource(getResources(), R.drawable.girl_4_3);
		}

		private void initAnimationInstance() {

		}

		private void drawBackground() {
			Paint paint= new Paint();
			mCanvas.drawBitmap(initBackgroundBm, 0, 0, new Paint());
			if (achievedCounter == -1)
				mCanvas.drawBitmap(backgroundBm, 0, 0, new Paint());
			Matrix testMatrix = new Matrix();
			testMatrix.setTranslate(-(480 - mWidth) / 2, mHeight - 290);
			
			mCanvas.drawBitmap(backgroundStage, testMatrix, new Paint());
		}

		public int achievedCounter = -1;

		private void drawAnimationInstance() {

			drawBackground();
//			if (bulletAnim != null)
//				bulletAnim.transformModel(mCanvas);
//
//			batteryAnimOdd.transformModel(mCanvas);
//			batteryAnimEven.transformModel(mCanvas);
//			batteryAnimOdd.transformModel(mCanvas);
//
//			artilleryAnimOdd.transformModel(mCanvas);
//			artilleryAnimEven.transformModel(mCanvas);
//			artilleryAnimOdd.transformModel(mCanvas);
//			if (boomAnim != null)
//				boomAnim.transformModel(mCanvas);

		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			mWidth = this.getWidth();
			mHeight = this.getHeight();
			memBm = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.RGB_565);
			mCanvas = new Canvas(memBm);
			initAnimationInstance();

			isRunning = true;
			DrawThread drawThread = new DrawThread();
			mThread = new Thread(drawThread);
			mThread.start();

		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			isRunning = false;

		}

		public float startX = 0;
		public float startY = 0;
		public double moveLength = 0;

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			int action = event.getAction();
			float currentX = 0;
			float currentY = 0;
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				Log.v("test", "ACTION_DOWN");
				startX = event.getX();
				startY = event.getY();
				break;
			case MotionEvent.ACTION_MOVE:
				Log.v("test", "ACTION_MOVE");
				currentX = event.getX();
				currentY = event.getY();
				// moveLength = Math.sqrt((currentX - startX) * (currentX - startX) + (currentY - startY) * (currentY - startY));
				break;
			case MotionEvent.ACTION_UP:
				Log.v("test", "ACTION_UP");
				currentX = event.getX();
				currentY = event.getY();
				// moveLength = Math.sqrt((currentX - startX) * (currentX - startX) + (currentY - startY) * (currentY - startY));
				break;
			}

			return true;
		}

		class DrawThread implements Runnable {
			@Override
			public void run() {
				while (isRunning) {

					drawAnimationInstance();

					try {
						Thread.sleep(3);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					Canvas renderer = null;
					synchronized (mHolder) {
						try {
							renderer = mHolder.lockCanvas();
							if (renderer != null) {
								renderer.drawBitmap(memBm, 0, 0, null);
							}
						} finally {
							if (renderer != null)
								mHolder.unlockCanvasAndPost(renderer);
						}
					}

				}
			}
		}

	}

}

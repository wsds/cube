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
import com.cube.canvas.common.AnimationManager;
import com.cube.canvas.common.AnimationManager.AnimationBitmap;

public class MosquitoActivity extends Activity {

	private static final String TAG = "MosquitoActivity";

	Context context;
	public Bitmap memBitmap;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(new AnimView(this));
	}

	public class AnimView extends SurfaceView implements SurfaceHolder.Callback {

		private int mWidth = 0;
		private int mHeight = 0;
		private SurfaceHolder mHolder = null;
		private Thread mThread = null;
		private Canvas mCanvas = null;

		public AnimationBitmap background = null;
		public AnimationBitmap cannon_based = null;
		public AnimationBitmap cannon = null;
		public AnimationBitmap mosquito1 = null;
		public AnimationBitmap mosquito2 = null;
		public AnimationBitmap shell = null;

		public AnimationManager animationManager = null;

		DrawThread drawThread = null;

		public AnimView(Context context) {
			super(context);
			mHolder = this.getHolder();
			mHolder.addCallback(this);
			animationManager = new AnimationManager(context);

		}

		private void initAnimationInstance() {
			background = animationManager.addAnimationBitmap(R.drawable.game2_background);
			background.matrix.setTranslate(-(480 - mWidth) / 2, mHeight - 290);
			cannon_based = animationManager.addAnimationBitmap(R.drawable.game2_cannon_based);
			cannon_based.matrix.setTranslate((mWidth - 271) / 2, mHeight - 122);
			cannon = animationManager.addAnimationBitmap(R.drawable.game2_cannon2);
			cannon.matrix.setTranslate((mWidth - 149) / 2, mHeight - 142);
			mosquito1 = animationManager.addAnimationBitmap(R.drawable.game2_mosquito1);
			mosquito1.matrix.setTranslate((mWidth - 36) / 2, 360);
			mosquito2 = animationManager.addAnimationBitmap(R.drawable.game2_mosquito2);
			mosquito2.matrix.setTranslate((mWidth - 500) / 2, 250);

			shell = animationManager.addAnimationBitmap(R.drawable.game2_shell);
			shell.matrix.setTranslate(260, 650);
		}



		private void drawAnimationInstance() {
			animationManager.draw();


		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			mWidth = this.getWidth();
			mHeight = this.getHeight();

			memBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.RGB_565);
			mCanvas = new Canvas(memBitmap);
			animationManager.mCanvas = mCanvas;
			initAnimationInstance();

			drawThread = new DrawThread();
			mThread = new Thread(drawThread);

		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			mThread.start();
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			drawThread.isRunning = false;
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
			public Boolean isRunning = true;

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
								renderer.drawBitmap(memBitmap, 0, 0, null);
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

package com.cube.attract.game.mosquito;

import java.util.ArrayList;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.cube.attract.R;
import com.cube.canvas.common.AnimationManager;
import com.cube.canvas.common.AnimationManager.AnimationBitmap;
import com.cube.canvas.common.CanvasAnimation2;
import com.cube.canvas.common.CanvasAnimation2.Callback;

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

		DrawThread drawThread = null;

		public AnimView(Context context) {
			super(context);
			mHolder = this.getHolder();
			mHolder.addCallback(this);

		}

		class MosquitosPool {
			public class Mosquito {
				public int blood = 2;
				public AnimationBitmap animationBitmap = null;
				int x;
				int y;
				int type;
				int directionX;
				int directionY;
				int direction;
				int mWidth = 0;
				int mHeight = 0;
			}

			public int count = 5;
			public int type = 2;
			public ArrayList<Mosquito> mosquitos = new ArrayList<Mosquito>();
			Random random = null;

			public void initaize() {
				random = new Random(System.currentTimeMillis());
				for (int i = 0; i < count; i++) {
					Mosquito mosquito = new Mosquito();
					int randomType = random.nextInt(1000) % type;
					mosquito.type = randomType;
					if (randomType == 0) {
						mosquito.animationBitmap = animationManager.addAnimationBitmap(R.drawable.game2_mosquito1);
					} else if (randomType >= 1) {
						mosquito.animationBitmap = animationManager.addAnimationBitmap(R.drawable.game2_mosquito2);
					}
					Log.v(TAG, "randomType is " + randomType);
					mosquito.x = random.nextInt(mWidth * 10) % mWidth;
					mosquito.y = random.nextInt(600 * 10) % 600;
					mosquito.direction = random.nextInt(1000) % 180;
					// mosquito.direction = 45;
					mosquito.animationBitmap.matrix.setTranslate(mosquito.x, mosquito.y);
					mosquito.animationBitmap.matrix.preScale(0.25f, 0.25f);
					mosquitos.add(mosquito);
					fly(mosquito);

				}
			}

			void fly(final Mosquito mosquito) {
				// double factor = (random.nextInt(200) - 200) / 100;
				// factor = 1;
				mosquito.directionX = (int) (50 * Math.cos(mosquito.direction * Math.PI / 180));
				mosquito.directionY = (int) (50 * Math.sin(mosquito.direction * Math.PI / 180));
				Log.v(TAG, "direction is " + mosquito.direction + " and directionX is " + mosquito.directionX + " and directionY is " + mosquito.directionY);
				CanvasAnimation2 flyAnimation = new CanvasAnimation2();
				flyAnimation.setTranslate(mosquito.directionX, mosquito.directionY, 90);
				flyAnimation.setCallback(new Callback() {
					@Override
					public void onEnd() {
						mosquito.x = mosquito.x + mosquito.directionX;
						mosquito.y = mosquito.y + mosquito.directionY;
						if (mosquito.x < -50 || mosquito.x > mWidth + 50) {
							mosquito.direction = (180 - mosquito.direction) % 360 + (random.nextInt(20) - 10);
							// if ((0 < mosquito.direction && mosquito.direction < 90) || (0 >= mosquito.direction && mosquito.direction > -90)) {
							mosquito.animationBitmap.matrix.preScale(-1, 1);
							// }
						}
						if (mosquito.y < -50 || mosquito.y > mHeight * 0.8 - 150) {
							mosquito.direction = -mosquito.direction + (random.nextInt(20) - 10);
						}
						fly(mosquito);
					}
				});
				mosquito.animationBitmap.addAnimation(flyAnimation);
			}
		}

		public AnimationBitmap background = null;
		public AnimationBitmap cannon_based = null;
		public AnimationBitmap cannon = null;
		public AnimationBitmap mosquito1 = null;
		public AnimationBitmap mosquito2 = null;
		public AnimationBitmap shell = null;

		public AnimationManager animationManager = null;
		public MosquitosPool mosquitosPool = null;

		private void initAnimationInstance() {
			animationManager = new AnimationManager(context);
			animationManager.mCanvas = mCanvas;
			mosquitosPool = new MosquitosPool();

			background = animationManager.addAnimationBitmap(R.drawable.game2_background);
			background.matrix.setTranslate(-(480 - mWidth) / 2, mHeight - 290);
			cannon_based = animationManager.addAnimationBitmap(R.drawable.game2_cannon_based);
			cannon_based.matrix.setTranslate((mWidth - 271) / 2, mHeight - 122);
			cannon = animationManager.addAnimationBitmap(R.drawable.game2_cannon2);
			cannon.matrix.setTranslate((mWidth - 149) / 2, mHeight - 142);

			mosquitosPool.initaize();
			shell = animationManager.addAnimationBitmap(R.drawable.game2_shell);
			shell.matrix.setTranslate(260, 550);

			CanvasAnimation2 up = new CanvasAnimation2();
			up.setTranslate(-100, -200, 500);
			// up.setRepeatSelfTimes(5);
			up.setRepeatTimes(CanvasAnimation2.INFINITE);

			CanvasAnimation2 down = new CanvasAnimation2();
			down.setTranslate(100, 200, 500);
			down.setRepeatTimes(CanvasAnimation2.INFINITE);
			up.addNextAnimation(down);
			down.addNextAnimation(up);

			shell.addAnimation(up);

			// CanvasAnimation2 turn = new CanvasAnimation2();
			// turn.setRotate(360, 23, 66, 1000);
			// turn.setRepeatSelfTimes(5);
			// // turn.setRepeatTimes(5);
			// up.addNextAnimation(turn);
		}

		private void drawAnimationInstance() {
			mCanvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
			animationManager.draw();

		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			mWidth = this.getWidth();
			mHeight = this.getHeight();

			memBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.RGB_565);
			mCanvas = new Canvas(memBitmap);

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
				startX = event.getX();
				startY = event.getY();

				currentX = startX;
				currentY = startX;
				break;
			case MotionEvent.ACTION_MOVE:
				currentX = event.getX();
				currentY = event.getY();

				break;
			case MotionEvent.ACTION_UP:
				currentX = event.getX();
				currentY = event.getY();
				CanvasAnimation2 turn1 = new CanvasAnimation2();
				turn1.setRotate(1440, 23, 66, 1000);
				turn1.setRepeatSelfTimes(5);
				shell.addAnimation(turn1);
				// moveLength = Math.sqrt((currentX - startX) * (currentX - startX) + (currentY - startY) * (currentY - startY));
				break;
			}
			turnCannon(currentX, currentY);
			return true;
		}

		void turnCannon(float currentX, float currentY) {
			double x = currentX - mWidth / 2;
			double y = mHeight - 40 - currentY;
			double theta = Math.atan2(x, y);
			double angle = theta / Math.PI * 180;
			cannon.matrix.setRotate((float) angle, 75, 106);
			cannon.matrix.postTranslate((mWidth - 149) / 2, mHeight - 142);
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

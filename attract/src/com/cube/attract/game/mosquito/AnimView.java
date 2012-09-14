package com.cube.attract.game.mosquito;

import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.cube.attract.R;
import com.cube.canvas.common.AnimationManager;
import com.cube.canvas.common.AnimationManager.AnimationBitmap;
import com.cube.canvas.common.CanvasAnimation2;
import com.cube.canvas.common.CanvasAnimation2.Callback;
import com.cube.common.LocalData;
import com.cube.common.LocalData.Game.ActiveGirl;
import com.cube.common.ServerData.Girl.Picture;
import com.cube.common.imageservice.BitmapPool;
import com.cube.opengl.common.Utils;

public class AnimView extends SurfaceView implements SurfaceHolder.Callback {

	private static final String TAG = "MosquitoActivity";

	Context context;
	MosquitoActivity mosquitoActivity = null;
	public Bitmap memBitmap;

	private int mWidth = 0;
	private int mHeight = 0;
	private SurfaceHolder mHolder = null;
	private Thread mThread = null;
	private Canvas mCanvas = null;

	DrawThread drawThread = null;

	public AnimView(Context context) {
		super(context);
		this.context = context;
		mosquitoActivity = (MosquitoActivity) context;
		mHolder = this.getHolder();
		mHolder.addCallback(this);

	}

	public ArrayList<Bitmap> girlBitmaps = new ArrayList<Bitmap>();
	public LocalData localData = LocalData.getInstance();
	public SceneState sceneState = SceneState.getInstance();
	public BitmapPool bitmapPool = BitmapPool.getInstance();

	void initGirlBitmaps() {
		ActiveGirl activegirl = localData.game.activeGirls.get(sceneState.girlNumber);
		for (Bitmap bitmap : girlBitmaps) {
			bitmap.recycle();
		}
		girlBitmaps.clear();
		for (int i = 3; i <= 3; i++) {
			Picture pictures = activegirl.girl.pictures.get(i);
			String url = pictures.url;
			String filename = url.substring(url.lastIndexOf("/") + 1);
			Bitmap bitmap = null;

			if (!localData.game.loadedPictures.contains(filename)) {

			} else {
				bitmap = bitmapPool.get(filename);
				Log.v(TAG, "texture is loaded: " + filename);
			}
			if (bitmap == null) {
				bitmap = Utils.getTextureFromBitmapResource(context, R.drawable.heart_1_s);
			}
			girlBitmaps.add(bitmap);
		}

		Picture picture = activegirl.girl.pictures.get(1);

		sceneState.x1 = picture.points.get(0).x;
		sceneState.y1 = picture.points.get(0).y;
		sceneState.x2 = picture.points.get(1).x;
		sceneState.y2 = picture.points.get(1).y;

		sceneState.weibo = activegirl.girl.weibo;
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

			public void die(final float currentX, final float currentY, float angle1) {
				soundPool.play(flyawaySound, 0.2f, 0.2f, 1, 0, 1f);
				Log.v(TAG, "mosquito is dieing!");
				double dx = currentX - mWidth / 2;
				double dy = mHeight - 40 - currentY;

				CanvasAnimation2 flyAwayAnimation = new CanvasAnimation2();
				flyAwayAnimation.setTranslate(-(float) dx / 5, -(float) dy / 5, 100);
				animationBitmap.addAnimation(flyAwayAnimation);

				CanvasAnimation2 flyAwayAnimation1 = new CanvasAnimation2();
				flyAwayAnimation1.setTranslate((float) dx, (float) dy, 500);
				flyAwayAnimation.addNextAnimation(flyAwayAnimation1);

				CanvasAnimation2 flyAwayAnimation2 = new CanvasAnimation2();
				flyAwayAnimation2.setRotate(1440, 256, 256, 600);
				animationBitmap.addAnimation(flyAwayAnimation2);

				final Mosquito mosquito = this;
				flyAwayAnimation2.setCallback(new Callback() {
					@Override
					public void onEnd() {
						Log.v(TAG, "mosquito is dead!");
						animationDynamicManager.removeAnimationBitmap(animationBitmap);
						mosquitos.remove(mosquito);

						int remainMosquitos = mosquitos.size();
						if (remainMosquitos < 5) {
							AnimationBitmap cover = covers.get(random.nextInt(covers.size()));
							animationManager.removeAnimationBitmap(cover);
							covers.remove(cover);
						}
						if (remainMosquitos == 0) {
							winGame();

						} else {

							Log.v(TAG, "There are " + remainMosquitos + " mosquitos remained.");
						}
					}
				});

			}
		}

		public int count = 15;
		public int type = 2;
		public ArrayList<Mosquito> mosquitos = new ArrayList<Mosquito>();
		ArrayList<AnimationBitmap> covers = new ArrayList<AnimationBitmap>();
		Random random = null;

		public void initaize() {
			Bitmap mosquito1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.game2_mosquito3);
			Bitmap mosquito2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.game2_mosquito4);
			random = new Random(System.currentTimeMillis());
			for (int i = 0; i < count; i++) {
				Mosquito mosquito = new Mosquito();
				int randomType = random.nextInt(1000) % type;
				mosquito.type = randomType;
				if (randomType == 0) {
					mosquito.animationBitmap = animationDynamicManager.addAnimationBitmap(mosquito1);
				} else if (randomType >= 1) {
					mosquito.animationBitmap = animationDynamicManager.addAnimationBitmap(mosquito2);
				}
				Log.v(TAG, "randomType is " + randomType);
				mosquito.x = random.nextInt(mWidth * 10) % mWidth;
				mosquito.y = (int) (random.nextInt(600 * 10) % (mHeight * 0.8 - 150));
				mosquito.direction = random.nextInt(1000) % 180;
				// mosquito.direction = 45;
				mosquito.animationBitmap.matrix.setTranslate(mosquito.x - 64, mosquito.y - 64);
				mosquito.animationBitmap.matrix.preScale(0.5f, 0.5f);
				if (Math.cos(mosquito.direction * Math.PI / 180) < 0) {
					mosquito.animationBitmap.matrix.preScale(-1, 1);
				}
				mosquitos.add(mosquito);
				fly(mosquito);

			}
		}

		void fly(final Mosquito mosquito) {
			mosquito.directionX = (int) (50 * Math.cos(mosquito.direction * Math.PI / 180));
			mosquito.directionY = (int) (50 * Math.sin(mosquito.direction * Math.PI / 180));
			CanvasAnimation2 flyAnimation = new CanvasAnimation2();
			flyAnimation.setTranslate(mosquito.directionX, mosquito.directionY, 90);
			flyAnimation.setCallback(new Callback() {
				@Override
				public void onEnd() {
					mosquito.x = mosquito.x + mosquito.directionX;
					mosquito.y = mosquito.y + mosquito.directionY;
					if (mosquito.x < -50 || mosquito.x > mWidth + 50) {
						mosquito.direction = (180 - mosquito.direction) % 360 + (random.nextInt(10) - 5);
						mosquito.animationBitmap.matrix.preScale(-1, 1);
					}
					if (mosquito.y < -150 || mosquito.y > mHeight * 0.8 - 150) {
						mosquito.direction = -mosquito.direction + (random.nextInt(10) - 5);
					}
					fly(mosquito);
				}
			});
			mosquito.animationBitmap.addAnimation(flyAnimation);
		}

		void hit(final float currentX, final float currentY, float angle1) {
			@SuppressWarnings("unchecked")
			ArrayList<Mosquito> mosquitos = (ArrayList<Mosquito>) this.mosquitos.clone();
			for (Mosquito mosquito : mosquitos) {
				float distance = (mosquito.x - currentX) * (mosquito.x - currentX) + (mosquito.y - currentY) * (mosquito.y - currentY);
				if (distance < 10000) {
					mosquito.blood--;
					if (mosquito.blood == 0) {
						mosquito.die(currentX, currentY, angle1);
					}
				}
			}
		}
	}

	public AnimationBitmap background = null;
	public AnimationBitmap cannon_based = null;
	public AnimationBitmap cannon = null;
	public AnimationBitmap mosquito1 = null;
	public AnimationBitmap mosquito2 = null;
	// public AnimationBitmap shell = null;

	public AnimationManager animationManager = null;
	// for dynamic element
	public AnimationManager animationDynamicManager = null;
	public AnimationManager animationDynamicManager1 = null;

	public MosquitosPool mosquitosPool = null;

	SoundPool soundPool = null;
	int fireSound = 0;
	int explodeSound = 0;
	int passSound = 0;
	int startSound = 0;
	int flyawaySound = 0;

	int id = 0;

	void initSoundPool() {
		soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 100);
		fireSound = soundPool.load(context, R.raw.fire, 1);
		explodeSound = soundPool.load(context, R.raw.explode, 1);
		passSound = soundPool.load(context, R.raw.pass, 1);
		startSound = soundPool.load(context, R.raw.start, 1);
		flyawaySound = soundPool.load(context, R.raw.flyaway, 1);
	}

	private void initAnimationInstance() {
		initGirlBitmaps();
		animationManager = new AnimationManager(context, mHeight, mWidth);

		animationManager.mCanvas = mCanvas;
		animationDynamicManager = new AnimationManager(context, mHeight, mWidth);
		animationDynamicManager.mCanvas = mCanvas;
		animationDynamicManager1 = new AnimationManager(context, mHeight, mWidth);
		animationDynamicManager1.mCanvas = mCanvas;
		mosquitosPool = new MosquitosPool();

		background = animationManager.addAnimationBitmap(girlBitmaps.get(0));
		id = (id + 1) % 6;
		float sx = ((float) mWidth / 720f);
		Log.v(TAG, "sx is " + sx);
		background.matrix.setScale(sx, sx);

		AnimationBitmap cover1 = animationManager.addAnimationBitmap(R.drawable.game2_cover1);
		mosquitosPool.covers.add(cover1);
		AnimationBitmap cover2 = animationManager.addAnimationBitmap(R.drawable.game2_cover2);
		mosquitosPool.covers.add(cover2);
		AnimationBitmap cover3 = animationManager.addAnimationBitmap(R.drawable.game2_cover3);
		mosquitosPool.covers.add(cover3);
		AnimationBitmap cover4 = animationManager.addAnimationBitmap(R.drawable.game2_cover4);
		mosquitosPool.covers.add(cover4);
		AnimationBitmap cover5 = animationManager.addAnimationBitmap(R.drawable.game2_cover5);
		mosquitosPool.covers.add(cover5);

		cannon_based = animationManager.addAnimationBitmap(R.drawable.game2_cannon_based);
		cannon_based.matrix.setTranslate((mWidth - 271) / 2, mHeight - 122);

		cannon = animationDynamicManager1.addAnimationBitmap(R.drawable.game2_cannon2);
		cannon.matrix.setTranslate((mWidth - 149) / 2, mHeight - 142);

		mosquitosPool.initaize();
		soundPool.play(startSound, 0.2f, 0.2f, 1, 0, 1f);
	}

	long drawCount = 0;
	long lastMillis = 0;

	private void drawAnimationInstance() {

		mCanvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
		animationManager.drawStatic();
		animationDynamicManager.draw();
		animationDynamicManager1.draw();

		drawCount++;
		if (drawCount > 50) {
			long currentMillis = System.currentTimeMillis();

			if (lastMillis != 0) {
				// long delta = currentMillis - lastMillis;
				// float fps = (float) drawCount / ((float) delta / 1000f);
				// Log.v(TAG, "fps is " + fps);
				drawCount = 0;
			}
			lastMillis = currentMillis;
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Log.v(TAG, "ondraw");
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mWidth = this.getWidth();
		mHeight = this.getHeight();

		memBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.RGB_565);
		mCanvas = new Canvas(memBitmap);
		shellBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.game2_shell);
		cloudBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.game2_cloud);
		initSoundPool();
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

	public double moveLength = 0;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		float currentX = 0;
		float currentY = 0;
		float angle = 0;
		boolean isfire = false;
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			currentX = event.getX();
			currentY = event.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			currentX = event.getX();
			currentY = event.getY();

			break;
		case MotionEvent.ACTION_UP:
			currentX = event.getX();
			currentY = event.getY();
			isfire = true;
			CanvasAnimation2 turn1 = new CanvasAnimation2();
			turn1.setRotate(1440, 23, 66, 1000);
			turn1.setRepeatSelfTimes(5);
			break;
		}
		angle = turnCannon(currentX, currentY);
		if (isfire == true) {
			fire(currentX, currentY, angle);
		}
		return true;
	}

	float turnCannon(float currentX, float currentY) {
		double x = currentX - mWidth / 2;
		double y = mHeight - 40 - currentY;
		double theta = Math.atan2(x, y);
		double angle = theta / Math.PI * 180;
		cannon.matrix.setRotate((float) angle, 75, 106);
		cannon.matrix.postTranslate((mWidth - 149) / 2, mHeight - 142);
		return (float) angle;
	}

	Bitmap shellBitmap = null;
	Bitmap cloudBitmap = null;

	void fire(final float currentX, final float currentY, float angle1) {
		double dx = currentX - mWidth / 2;
		double dy = mHeight - 40 - currentY;
		double theta = Math.atan2(dx, dy);
		final double angle = theta / Math.PI * 180;
		double speed = (dx * dx + dy * dy) / (mWidth * mWidth + mHeight * mHeight) * 500;
		final AnimationBitmap shell = animationDynamicManager.addAnimationBitmap(shellBitmap);

		shell.matrix.setRotate((float) angle, 23, 66);
		shell.matrix.postTranslate((mWidth - 23) / 2, mHeight - 92);
		shell.matrix.preScale(0.5f, 0.8f);

		CanvasAnimation2 fireAnimation = new CanvasAnimation2();
		fireAnimation.setTranslate((float) dx, -(float) dy, (float) speed);
		shell.addAnimation(fireAnimation);
		fireAnimation.setCallback(new Callback() {
			@Override
			public void onEnd() {
				animationDynamicManager.removeAnimationBitmap(shell);
				final AnimationBitmap cloud = animationDynamicManager.addAnimationBitmap(cloudBitmap);

				cloud.matrix.setRotate((float) angle, 23, 66);
				cloud.matrix.postTranslate(currentX - 41, currentY - 48);
				cloud.matrix.preScale(0.5f, 0.5f);
				explode(cloud);
				mosquitosPool.hit(currentX, currentY, (float) angle);
			}
		});

		soundPool.play(fireSound, 0.2f, 0.2f, 1, 0, 1f);
	}

	void explode(final AnimationBitmap cloud) {
		CanvasAnimation2 explodeAnimation = new CanvasAnimation2();
		explodeAnimation.setScale(3f, 82, 96, 200);
		cloud.addAnimation(explodeAnimation);
		explodeAnimation.setCallback(new Callback() {
			@Override
			public void onEnd() {
				animationDynamicManager.removeAnimationBitmap(cloud);
			}
		});
		soundPool.play(explodeSound, 0.2f, 0.2f, 1, 0, 1f);
	}

	void winGame() {
		Log.v(TAG, "game is win!");
		soundPool.play(passSound, 0.2f, 0.2f, 1, 0, 1f);
		final AnimationBitmap win = animationDynamicManager.addAnimationBitmap(R.drawable.game2_pass);

		win.matrix.setTranslate((mWidth - 378) / 2, (mHeight - 332) / 2);
		win.matrix.preScale(0.5f, 0.5f, 189, 166);

		CanvasAnimation2 enlarge = new CanvasAnimation2();
		enlarge.setScale(2f, 189, 166, 200);
		win.addAnimation(enlarge);

		CanvasAnimation2 shrink = new CanvasAnimation2();
		shrink.setScale(0.8f, 189, 166, 200);
		enlarge.addNextAnimation(shrink);

		CanvasAnimation2 up = new CanvasAnimation2();
		up.setTranslate(100, -250, 2500);
		shrink.addNextAnimation(up);

		CanvasAnimation2 shrink1 = new CanvasAnimation2();
		shrink1.setScale(0.1f, 189, 166, 1500);
		shrink.addNextAnimation(shrink1);
		up.setCallback(new Callback() {
			@Override
			public void onEnd() {
				Message msgMessage = new Message();
				msgMessage.what = WIN;
				handler.sendMessage(msgMessage);
			}
		});

	}

	void next() {
		isShow = false;
		toNext = true;
		sceneState.girlNumber = (sceneState.girlNumber + 1) % sceneState.girlsSize;
		// initAnimationInstance();
	}

	boolean toNext = false;
	private final static int WIN = 0;
	private boolean isShow = false;

	Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case WIN:
				if (!isShow) {
					mosquitoActivity.showImage();
					isShow = true;
				}
				break;
			}
			super.handleMessage(msg);
		}
	};

	class DrawThread implements Runnable {
		public Boolean isRunning = true;

		@Override
		public void run() {

			while (isRunning) {

				if (toNext == true) {
					initAnimationInstance();
					toNext = false;
				}
				drawAnimationInstance();
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

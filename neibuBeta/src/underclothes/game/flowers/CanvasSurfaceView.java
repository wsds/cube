package underclothes.game.flowers;

import java.util.Random;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.SurfaceHolder.Callback;
import Neibu.main.beta.R;

public class CanvasSurfaceView extends SurfaceView implements Callback {

	/** 触摸后绘制的图片 **/
	// Bitmap mBitmap = null;
	int px = 0;
	int py = 0;
	long pmillsTime = 0;

	Bitmap girl_back = null;
	// Bitmap girl_up = null;
	Bitmap[] flowerBMP = new Bitmap[3];

	/** 游戏画笔 **/
	Paint mPaint = null;

	SurfaceHolder mSurfaceHolder = null;
	int[] pixels = new int[40 * 40];
	/** 控制游戏更新循环 **/
	boolean mRunning = false;

	/** 游戏画布 **/
	Canvas mCanvas = null;
	// GrowthActivity cameraDemoActivity = null;

	public SoundPool soundPool;
	public int loadId1, loadId2, loadId3;

	public int[][] sensitivePoint = { { 85, 170, 0 }, { 180, 190, 0 }, { 90, 305, 0 } };
	
	public int flowersCenterX = 0;
	public int flowersCenterY = 0;
	Context context;
	GlApp glApp = null;

	public CanvasSurfaceView(Context context) {

		super(context);
		this.context = context;
		glApp = (GlApp) context;
		soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 100);
		loadId1 = soundPool.load(context, R.raw.sound, 1);
		pmillsTime = System.currentTimeMillis();
		// for(int i=0;i<1600;i++){
		// pixels[i]= Color.TRANSPARENT;
		// }
		// cameraDemoActivity = (GrowthActivity) context;
		/** 设置当前View拥有控制焦点 **/
		this.setFocusable(true);
		/** 设置当前View拥有触摸事件 **/
		this.setFocusableInTouchMode(true);
		/** 加载图片 **/
		initailizeGirls();
		initailizeFlowers();
		/** 拿到SurfaceHolder对象 **/
		mSurfaceHolder = this.getHolder();
		/** 将mSurfaceHolder添加到Callback回调函数中 **/
		mSurfaceHolder.addCallback(this);
		/** 创建画布 **/
		mCanvas = new Canvas();

		/** 创建画笔 **/
		mPaint = new Paint();
		mPaint.setColor(Color.WHITE);

	}

	int girlNumber = 0;
	int flowersNum = 60;
	public Flower[] flowers = new Flower[flowersNum];

	public void initailizeFlowers() {

		flowersCenterX = ((sensitivePoint[0][0] + sensitivePoint[1][0]) / 2 + sensitivePoint[2][0]) / 2;
		flowersCenterY = ((sensitivePoint[0][1] + sensitivePoint[1][1]) / 2 + sensitivePoint[2][1]) / 2;

		Random ran = new Random(System.currentTimeMillis());
		for (int i = 0; i < flowersNum; i++) {
			flowers[i] = new Flower();
			flowers[i].kind = ran.nextInt(3);
			int r = ran.nextInt(3);
			flowers[i].x = sensitivePoint[r][0];
			flowers[i].y = sensitivePoint[r][1];
			flowers[i].x = flowers[i].x - 90 + ran.nextInt(180);
			flowers[i].y = flowers[i].y - 60 + ran.nextInt(120);

			double dx = flowers[i].x - flowersCenterX;
			double dy = flowers[i].y - flowersCenterY;
			int speed = 10;

			flowers[i].dx = (int) (speed * dx / (Math.sqrt(dx * dx + dy * dy)));
			flowers[i].dy = (int) (speed * dy / (Math.sqrt(dx * dx + dy * dy)));

		}
		flowerBMP[0] = BitmapFactory.decodeResource(getResources(), R.drawable.flower1);
		flowerBMP[1] = BitmapFactory.decodeResource(getResources(), R.drawable.flower2);
		flowerBMP[2] = BitmapFactory.decodeResource(getResources(), R.drawable.flower3);

	}

	public void initailizeGirls() {
		toNext = false;
		girlNumber = (girlNumber + 1) % 5;
		if (girlNumber == 1) {
			int[][] sensitivePoint = { { 120, 397, 0 }, { 270, 400, 0 }, { 228, 670, 0 } };
			this.sensitivePoint = sensitivePoint;
			girl_back = BitmapFactory.decodeResource(getResources(), R.drawable.mag1_back);

		} else if (girlNumber == 2) {
			int[][] sensitivePoint = { {154, 346, 0 }, { 284, 341, 0 }, {261, 673, 0 } };
			this.sensitivePoint = sensitivePoint;
			girl_back = BitmapFactory.decodeResource(getResources(), R.drawable.mag2_back);

		} else if (girlNumber == 3) {
			int[][] sensitivePoint = { {172, 425, 0 }, { 319, 393, 0 }, { 215, 722, 0 } };
			this.sensitivePoint = sensitivePoint;
			girl_back = BitmapFactory.decodeResource(getResources(), R.drawable.mag3_back);
		}

		else if (girlNumber == 4) {
			int[][] sensitivePoint = { { 138, 482, 0 }, { 200, 440, 0 }, {392, 728, 0 } };
			this.sensitivePoint = sensitivePoint;
			girl_back = BitmapFactory.decodeResource(getResources(), R.drawable.mag4_back);
		}

		else if (girlNumber == 0) {
			int[][] sensitivePoint = { { 209, 379, 0 }, { 344, 375, 0 }, { 243, 732, 0 } };
			this.sensitivePoint = sensitivePoint;
			girl_back = BitmapFactory.decodeResource(getResources(), R.drawable.mag5_back);
		} else {
			return;
		}

		int[][] girlSensitivePoint = { { 124, 394, 0 }, { 270, 428, 0 }, { 231, 743, 0 } };
		sensitivePoint = girlSensitivePoint;
	}
	boolean reset = false;
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		/** 拿到触摸的状态 **/
		int action = event.getAction();
		/** 控制当触摸抬起时清屏 **/
		switch (action) {
		// 触摸按下的事件
		case MotionEvent.ACTION_DOWN:
			reset = false;
			soundPool.play(loadId1, 0.2f, 0.2f, 1, 0, 1f);
			triggeAnimation();
			glApp.hideElement();
			if (toNext == true) {
				longshake();
			}
			Log.v("test", "ACTION_DOWN");
			break;
		// 触摸移动的事件
		case MotionEvent.ACTION_MOVE:
			Log.v("test", "ACTION_MOVE");
			// soundPool.play(loadId1, 0.2f, 0.2f, 1, 0, 1f);
			break;
		// 触摸抬起的事件
		case MotionEvent.ACTION_UP:
			Log.v("test", "ACTION_UP");
			reset = true;
			break;
		}
//		// 在这里加上线程安全锁
//		synchronized (mSurfaceHolder) {
//			/** 拿到当前画布 然后锁定 **/
//			mCanvas = mSurfaceHolder.lockCanvas();
//			/** 清屏 **/
//			mCanvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
//
//			if (!reset) {
//				/** 在屏幕中拿到同时触碰的点的数量 **/
//				int pointCount = event.getPointerCount();
//
//				/** 使用循环将每个触摸点图片都绘制出来 **/
//				for (int i = 0; i < pointCount; i++) {
//					/** 根据触摸点的ID 可以讲每个触摸点的X Y坐标拿出来 **/
//					int x = (int) event.getX(i);
//					int y = (int) event.getY(i);
//					int showX = i * 150;
//					mCanvas.drawText("当前X坐标：" + x, showX, 20, mPaint);
//					mCanvas.drawText("当前Y坐标：" + y, showX, 40, mPaint);
//					mCanvas.drawText("事件触发时间：" + event.getEventTime(), showX, 60, mPaint);
//				}
//			} else {
//				mCanvas.drawText("请多点触摸当前手机屏幕", 0, 20, mPaint);
//			}
//			/** 绘制结束后解锁显示在屏幕上 **/
//			mSurfaceHolder.unlockCanvasAndPost(mCanvas);
//		}

		return true;
	}

	// public void shake() {
	// Vibrator vibrator = (Vibrator)
	// context.getSystemService(Context.VIBRATOR_SERVICE);
	// long[] pattern = { 5, 80 }; // 停止 开启 停止 开启
	// vibrator.vibrate(pattern, -1);
	// }
	//
	public void longshake() {
		Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
		long[] pattern = { 5, 300 }; // 停止 开启 停止 开启
		vibrator.vibrate(pattern, -1);
		glApp.showElement();

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	public int movement = 0;

	public void moveFlower(Flower flower) {

	}

	boolean moving = false;
	boolean animationThreadRunning = false;

	public boolean drawFlowers() {

		synchronized (mSurfaceHolder) {
			mCanvas = mSurfaceHolder.lockCanvas();
			/** 清屏 **/
			mCanvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
			mCanvas.drawBitmap(girl_back, 0, 0, mPaint);
			moving = false;
			for (int j = 0; j < flowersNum; j++) {

				if (flowers[j].state == 1) {
					flowers[j].x = flowers[j].x + flowers[j].dx;
					flowers[j].y = flowers[j].y + flowers[j].dy;
					moving = true;
				} else if (flowers[j].state == 2) {
					continue;
				}

				int flowerX = flowers[j].x;
				int flowerY = flowers[j].y;

				if (flowerX < 0 || flowerX > 420 || flowerY < 0 || flowerY > 780) {
					flowers[j].state = 2;
					continue;
				}
				int flowerKind = flowers[j].kind;
				int heightBMP = flowerBMP[flowerKind].getHeight();
				int widthBMP = flowerBMP[flowerKind].getWidth();

				mCanvas.drawBitmap(flowerBMP[flowerKind], (flowerX - widthBMP / 2), (flowerY - heightBMP / 2), mPaint);
			}

			mSurfaceHolder.unlockCanvasAndPost(mCanvas);
		}
		return moving;
	}

	boolean toNext = false;

	public void triggeNext() {
		toNext = true;
		for (int j = 0; j < flowersNum; j++) {
			if (flowers[j].state != 2) {
				toNext = false;
			}
		}

	}

	public void triggeAnimation() {

		long millsTime = System.currentTimeMillis();
		if ((millsTime - pmillsTime) < 1000) {
			return;
		}
		pmillsTime = millsTime;
		Flower[] staticFlowers = new Flower[flowersNum];
		int i = 0;
		for (int j = 0; j < flowersNum; j++) {
			if (flowers[j].state == 0) {
				staticFlowers[i] = flowers[j];
				i++;
			}
		}
		if (i == 0) {
			return;
		}

		Random ran = new Random(System.currentTimeMillis());

		for (int k = 0; k < 10; k++) {
			int r = ran.nextInt(i);
			staticFlowers[r].state = 1;
		}
		if (moving == false && animationThreadRunning == false) {
			trigger = new animationTrigger();
			trigger.start();
		}
	}

	animationTrigger trigger = null;

	DetectAudio detectAudio = null;

	@Override
	public void surfaceCreated(SurfaceHolder holder) {

		drawFlowers();
		trigger = new animationTrigger();
		trigger.start();

		detectAudio = new DetectAudio(this);
		detectAudio.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if (trigger != null && trigger.isRunning == true) {
			trigger.stop();
		}
		if (detectAudio != null && detectAudio.isRun == true) {
			detectAudio.stop();
		}

		synchronized (mSurfaceHolder) {
			mCanvas = mSurfaceHolder.lockCanvas();
		}
		mSurfaceHolder.unlockCanvasAndPost(mCanvas);
	}

	public String[] logRecord = new String[20];
	public int logIndex = 0;

	public void drawLog(String log) {
		logRecord[logIndex] = log;

		mCanvas = mSurfaceHolder.lockCanvas();
		/** 清屏 **/
		mCanvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);

		for (int i = logIndex; i >= 0; i--) {
			mCanvas.drawText(logRecord[i], 100, 100 + 15 * (logIndex - i), mPaint);
		}
		mSurfaceHolder.unlockCanvasAndPost(mCanvas);
		logIndex = (logIndex + 1) % 20;

	}

	public class Flower {
		public int x = 0;
		public int y = 0;
		public int dx = 0;
		public int dy = 0;
		public int kind = 0;
		public int state = 0;// 0是静止；1是运动；2是飞出
	}

	public class animationTrigger extends Thread {

		public boolean isRunning = true;

		int timer = 0;

		/**
		 * 线程体代码
		 */
		@Override
		public void run() {
			isRunning = true;
			animationThreadRunning = true;
			boolean moving = true;
			while (moving == true) {
				try {
					Thread.sleep(50);
					moving = drawFlowers();

				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			triggeNext();
			animationThreadRunning = false;
			isRunning = false;

		}
	}

	public class girl {
		public int[][] sensitivePoint = { { 85, 170, 0 }, { 180, 190, 0 }, { 90, 305, 0 } };
	}

}
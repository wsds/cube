package underclothes.game.masaike;

import android.content.Context;
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
	long pmillsTime1 = 0;

	Bitmap girl_back = null;
	Bitmap girl_up = null;

	/** 游戏画笔 **/
	Paint mPaint = null;

	SurfaceHolder mSurfaceHolder = null;
	int[] pixels = new int[40 * 40];
	/** 控制游戏更新循环 **/
	boolean mRunning = false;

	/** 游戏画布 **/
	Canvas mCanvas = null;
	// GrowthActivity cameraDemoActivity = null;

	private SoundPool soundPool;
	private int loadId1, loadId2, loadId3;

	public int[][] sensitivePoint = { { 165, 230, 0 }, { 220, 230, 0 }, { 200, 345, 0 } };
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
		// mBitmap = BitmapFactory.decodeResource(getResources(),
		// R.drawable.item);
		initailizeGirls();
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

	public void initailizeGirls() {
		girlNumber = (girlNumber + 1) % 3;
		Bitmap girl_up_unMutable = null;
		if (girlNumber == 1) {
			girl_back = BitmapFactory.decodeResource(getResources(), R.drawable.mag1_back);
			girl_up_unMutable = BitmapFactory.decodeResource(getResources(), R.drawable.mag1_up);

		} else if (girlNumber == 2) {
			girl_back = BitmapFactory.decodeResource(getResources(), R.drawable.mag2_back);
			girl_up_unMutable = BitmapFactory.decodeResource(getResources(), R.drawable.mag2_up);

		} else if (girlNumber == 0) {
			girl_back = BitmapFactory.decodeResource(getResources(), R.drawable.mag3_back);
			girl_up_unMutable = BitmapFactory.decodeResource(getResources(), R.drawable.mag3_up);

		} else {
			return;
		}

		girl_up = girl_up_unMutable.copy(Bitmap.Config.ARGB_4444, true);
		int[][] girlSensitivePoint = { { 165, 210, 0 }, { 220, 210, 0 }, { 200, 345, 0 } };
		sensitivePoint = girlSensitivePoint;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		/** 拿到触摸的状态 **/
		int action = event.getAction();
		/** 控制当触摸抬起时清屏 **/
		boolean reset = false;
		switch (action) {
		// 触摸按下的事件
		case MotionEvent.ACTION_DOWN:
			soundPool.play(loadId1, 0.2f, 0.2f, 1, 0, 1f);
			glApp.hideElement();
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

		// 在这里加上线程安全锁
		synchronized (mSurfaceHolder) {
			/** 拿到当前画布 然后锁定 **/
			mCanvas = mSurfaceHolder.lockCanvas();
			/** 清屏 **/
			mCanvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
			// mCanvas.drawBitmap(girl_back, 0, 0, mPaint);

			if (!reset) {
				/** 在屏幕中拿到同时触碰的点的数量 **/
				int pointCount = event.getPointerCount();

				/** 使用循环将每个触摸点图片都绘制出来 **/
				for (int i = 0; i < pointCount; i++) {
					/** 根据触摸点的ID 可以讲每个触摸点的X Y坐标拿出来 **/
					int x = (int) event.getX(i);
					int y = (int) event.getY(i);
					int showX = i * 150;
					// mCanvas.drawBitmap(girl_up, 0, 0, mPaint);

					if (0 < x && x < (girl_up.getWidth() - 40) && 0 < y && y < (girl_up.getHeight() - 40)) {
						if ((((x - px) * (x - px) + (y - py) * (y - py)) > 1600)) {
							long millsTime = System.currentTimeMillis();
							// if (((pmillsTime1 - millsTime) * (pmillsTime1 -
							// millsTime)) > 10000) {
							pmillsTime1 = millsTime;

							for (int j = 0; j < 3; j++) {
								int sensitivePointX = sensitivePoint[j][0];

								int sensitivePointY = sensitivePoint[j][1];
								if ((((x - sensitivePointX) * (x - sensitivePointX) + (y - sensitivePointY) * (y - sensitivePointY)) < 1600)) {
									sensitivePoint[j][2]++;
									int sensitive = sensitivePoint[j][2];
									if (sensitive % 4 == 0) {
										shake();
										int sensitivity = 0;
										for (int k = 0; k < 3; k++) {
											sensitivity = sensitivity + sensitivePoint[k][2];
										}
										if (sensitivity > 15) {
											longshake();
										}
									}
								}
							}
							// }

							if (((pmillsTime - millsTime) * (pmillsTime - millsTime)) > 100000) {
								pmillsTime = millsTime;
								px = x;
								py = y;
								soundPool.play(loadId1, 0.2f, 0.2f, 1, 0, 1f);
							}
						}
						girl_back.getPixels(pixels, 0, 40, x, y, 40, 40);
						girl_up.setPixels(pixels, 0, 40, x, y, 40, 40);
					}
//
//					mCanvas.drawText("当前X坐标：" + x, showX, 20, mPaint);
//					mCanvas.drawText("当前Y坐标：" + y, showX, 40, mPaint);
//					mCanvas.drawText("事件触发时间：" + event.getEventTime(), showX, 60, mPaint);
				}
			} else {
//				mCanvas.drawText("请多点触摸当前手机屏幕,test1", 0, 20, mPaint);
			}
			mCanvas.drawBitmap(girl_up, 0, 0, mPaint);

			int x = (int) event.getX(0);
			int y = (int) event.getY(0);
//			mCanvas.drawText("方差wsdsad是：" + ((x - px) * (x - px) + (y - py) * (y - py)), 150, 20, mPaint);
//			mCanvas.drawText("当前px：" + px + " x是：" + x, 150, 40, mPaint);
//			mCanvas.drawText("当前py：" + py + " y是：" + y, 150, 60, mPaint);
//			long millsTime = System.currentTimeMillis();
//			mCanvas.drawText("时间差是：" + ((pmillsTime - millsTime) * (pmillsTime - millsTime)), 150, 80, mPaint);
//			// mCanvas.drawText("millsTime：" + millsTime, 150, 80, mPaint);
			/** 绘制结束后解锁显示在屏幕上 **/
			mSurfaceHolder.unlockCanvasAndPost(mCanvas);
		}

		// return super.onTouchEvent(event);

		return true;
	}

	public void shake() {
		Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
		long[] pattern = { 5, 80 }; // 停止 开启 停止 开启
		vibrator.vibrate(pattern, -1);
	}

	public void longshake() {
		Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
		long[] pattern = { 5, 800 }; // 停止 开启 停止 开启
		vibrator.vibrate(pattern, -1);
		glApp.showElement();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {

		synchronized (mSurfaceHolder) {
			mCanvas = mSurfaceHolder.lockCanvas();
			/** 清屏 **/
			mCanvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
			mCanvas.drawBitmap(girl_up, 0, 0, mPaint);
			mSurfaceHolder.unlockCanvasAndPost(mCanvas);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {

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

}
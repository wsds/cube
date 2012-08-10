package com.cube.attract.game.cupidcannon;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.cube.attract.R;
import com.cube.canvas.common.CanvasAnimation;

public class CupidCannonActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 强制为竖屏
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(new AnimView(this));
	}

	public class AnimView extends SurfaceView implements
			SurfaceHolder.Callback, Runnable {

		private int mWidth = 0;
		private int mHeight = 0;
		private int mBitmapWidth = 0;
		private int mBitmapHeight = 0;
		private SurfaceHolder mHolder = null;
		private Thread mThread = null;
		private Bitmap mBitmap = null;
		
		private Paint mPaint = null;
		private boolean isRunning = true;
		private CanvasAnimation bulletAnim;
		private CanvasAnimation artilleryAnim;
		private CanvasAnimation batteryAnim;
		
		public Bitmap memBm = null;
		private Canvas mCanvas = null;
		public Bitmap backgroundBm = null;
		public float[] rotateCenter = {0.0f, 0.0f};
		public Bitmap backgroundStage = null;
		public Matrix testMatrix = new Matrix();
		public float [] testMatrixArray; 
		
		//puzzle. I have no idea about the definite meaning of the flags.
		//I use it to create the second CanvasLayer.
		private static final int LAYERS_FLAGS=Canvas.MATRIX_SAVE_FLAG|  
                Canvas.CLIP_SAVE_FLAG |  
                Canvas.HAS_ALPHA_LAYER_SAVE_FLAG |  
                Canvas.FULL_COLOR_LAYER_SAVE_FLAG |  
                Canvas.CLIP_TO_LAYER_SAVE_FLAG; 
		
		public AnimView(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
			mHolder = this.getHolder();
			mHolder.addCallback(this);
			backgroundBm = BitmapFactory.decodeResource(getResources(),
					R.drawable.girl_4_2);
			backgroundStage = BitmapFactory.decodeResource(getResources(),
					R.drawable.button_bar);
			//mThread = new Thread(this);// 创建一个绘图线程

		}

		private void initDraw() {
			
			//testMatrix.getValues(testMatrixArray);
			
			//Draw elements on the first layer.
			mBitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.girl_4_2);
			
			mBitmapWidth = mBitmap.getWidth();
			mBitmapHeight = mBitmap.getHeight();
			mCanvas.drawRect(0, 0, mBitmapWidth, mBitmapHeight, mPaint);
			Matrix matrix = new Matrix();
			matrix.setScale(0.67f, 0.67f);
			matrix.postTranslate(0, -29);
			mCanvas.drawBitmap(mBitmap, matrix, mPaint);
			if (mBitmap != null)
				mBitmap.recycle();
			
			mBitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.girl_4_1);
			mCanvas.drawRect(50, 50, 200, 200, mPaint);
			mCanvas.drawColor(Color.TRANSPARENT);
			mCanvas.drawBitmap(mBitmap, 100, 100, mPaint);
			if (mBitmap != null)
				mBitmap.recycle();
			
			
			mBitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.girl_4_1);
			mCanvas.drawColor(Color.TRANSPARENT);
			mPaint.setColor(Color.BLUE);
			mCanvas.drawRect(50, 50, 200, 200, mPaint);
			mCanvas.drawColor(Color.TRANSPARENT);
			mPaint.setAlpha(0x40);
			mCanvas.drawBitmap(mBitmap, 100, 100, mPaint);
			if (mBitmap != null)
				mBitmap.recycle();
			
		
		//Draw elements on the second layer.	
           mCanvas.saveLayerAlpha(0, 0, mWidth, mHeight, 0x88, LAYERS_FLAGS);
           mPaint.setColor(Color.BLUE); 
           mCanvas.drawCircle(300, 300, 75, mPaint);
           mBitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.ic_launcher);
           mCanvas.drawBitmap(mBitmap, 300, 500, mPaint);
           mCanvas.restore();

           //Double bitmap represent method to solve the screen twinkle frequently problem.
           Canvas renderer = null;
           renderer = mHolder.lockCanvas();
           renderer.drawBitmap(memBm, 0, 0, null);
           mHolder.unlockCanvasAndPost(renderer);
         
          
		}
		
		private void initAnimationInstance(){
			
			Matrix initMatrix = new Matrix();
			
			bulletAnim = new CanvasAnimation();
			bulletAnim.setElements(BitmapFactory.decodeResource(getResources(),
					R.drawable.bullet), new Paint());
			bulletAnim.setCurrentPosition(300, 100, 0);
			bulletAnim.setTranslate(0, 200, 10000);
			bulletAnim.setRepeatTimes(3);
			bulletAnim.start(true);
			
			artilleryAnim = new CanvasAnimation();
			artilleryAnim.setElements(BitmapFactory.decodeResource(getResources(),
					R.drawable.artillery), new Paint());
			initMatrix.setTranslate((mWidth - artilleryAnim.mAnimBitmapWidth)/2,
					mHeight + 5 - artilleryAnim.mAnimBitmapHeight + 20);
			initMatrix.postRotate(-90, rotateCenter[0], rotateCenter[1]);
			artilleryAnim.setStartMatrix(initMatrix);
			/*artilleryAnim.setCurrentPosition(mWidth/2 - artilleryAnim.mAnimBitmapWidth,
					mHeight -artilleryAnim.mAnimBitmapHeight/2, -90);*/
			artilleryAnim.setRotate(180, rotateCenter[0], rotateCenter[1], 10000);
			artilleryAnim.setRepeatTimes(3);
			artilleryAnim.start(true);
			
			batteryAnim = new CanvasAnimation();
			batteryAnim.setElements(BitmapFactory.decodeResource(getResources(),
					R.drawable.battery), new Paint());
			initMatrix.setTranslate(mWidth/2 + 4 - batteryAnim.mAnimBitmapWidth/2,
					mHeight + 5 - batteryAnim.mAnimBitmapHeight/2);
			initMatrix.postRotate(-90, rotateCenter[0], rotateCenter[1]);
			batteryAnim.setStartMatrix(initMatrix);
			/*batteryAnim.setCurrentPosition(mWidth/2 - batteryAnim.mAnimBitmapWidth/2,
					mHeight - batteryAnim.mAnimBitmapHeight/2, -90);*/
			batteryAnim.setRotate(180, rotateCenter[0], rotateCenter[1], 10000);
			batteryAnim.setRepeatTimes(3);
			batteryAnim.start(true);
			
		}
		private void drawAinmationInstance() {
			//mCanvas.
			drawBackground();
			bulletAnim.transformModel(mCanvas);
			/*mCanvas.drawBitmap(BitmapFactory.decodeResource(getResources(),
					R.drawable.battery), rotateCenter[0] - batteryAnim.mAnimBitmapWidth/2,
					rotateCenter[1] - batteryAnim.mAnimBitmapHeight/2, new Paint());
			mCanvas.drawBitmap(BitmapFactory.decodeResource(getResources(),
					R.drawable.artillery), mWidth/2 - artilleryAnim.mAnimBitmapWidth/2, 
					rotateCenter[1] - artilleryAnim.mAnimBitmapHeight + 20, new Paint());*/
			batteryAnim.transformModel(mCanvas);
			artilleryAnim.transformModel(mCanvas);
		}
		private void drawBackground() {
			
			mCanvas.drawBitmap(backgroundBm, 0, 0, new Paint());
			Matrix testMatrix = new Matrix();
			testMatrix.postTranslate(-(480-mWidth)/2, mHeight-290);
			mCanvas.drawBitmap(backgroundStage, testMatrix, new Paint());
		}
		
		private void testDraw() {
		mBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.girl_4_2);
		mBitmapWidth = mBitmap.getWidth();
		mBitmapHeight = mBitmap.getHeight();
		mCanvas.drawRect(0, 0, mBitmapWidth, mBitmapHeight, mPaint);
		Matrix matrix = new Matrix();
		matrix.setScale(0.67f, 0.67f);
		matrix.postTranslate(0, -29);
		mCanvas.drawBitmap(mBitmap, matrix, mPaint);
		if (mBitmap != null)
			mBitmap.recycle();
		}
		

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			mWidth = this.getWidth();
			mHeight = this.getHeight();
			rotateCenter[0] = mWidth/2 + 3;
			rotateCenter[1] = mHeight + 3;
			mPaint=new Paint();
			memBm = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.RGB_565);
			mCanvas = new Canvas(memBm);
			initAnimationInstance();
			//initDraw();
			//Optimize mThread start
			isRunning = true;
			mThread = new Thread(this);// 创建一个绘图线程
			mThread.start();

		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			// TODO Auto-generated method stub

		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			// TODO Auto-generated method stub
			isRunning = false;

		}

		@Override
		public void run() {
			while (isRunning) {
				
				//testDraw();

				drawAinmationInstance();
				
	
				try {
					Thread.sleep(3);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
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
		
		float startX = 0;
	    float startY = 0;
	    double moveLongth = 0;
		@Override
		public boolean onTouchEvent(MotionEvent event) {
		    /** 拿到触摸的状态 **/
		    int action = event.getAction();
		    float currentX = 0;
			float currentY = 0;
		    switch (action) {
		    // 触摸按下的事件
		    case MotionEvent.ACTION_DOWN:
			Log.v("test", "ACTION_DOWN");
			startX = event.getX();
			startY = event.getY();
			break;
		    // 触摸移动的事件
		    case MotionEvent.ACTION_MOVE:
			Log.v("test", "ACTION_MOVE");
			currentX = event.getX();
			currentY = event.getY();
			moveLongth = Math.sqrt((currentX - startX)*(currentX - startX)
					+ (currentY - startY)*(currentY - startY));
			break;
		    // 触摸抬起的事件
		    case MotionEvent.ACTION_UP:
			Log.v("test", "ACTION_UP");
			currentX = event.getX();
			currentY = event.getY();
			moveLongth = Math.sqrt((currentX - startX)*(currentX - startX)
					+ (currentY - startY)*(currentY - startY));
			break;
		    }

		    // return super.onTouchEvent(event);
		    return true;
		}

	}

}

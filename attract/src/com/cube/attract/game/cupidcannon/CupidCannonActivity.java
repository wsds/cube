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
		private CanvasAnimation testAnim;
		private CanvasAnimation cupidAnim;
		
		public Bitmap memBm = null;
		private Canvas mCanvas = null;
		public Bitmap backgroundBm = null;
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
			testAnim = new CanvasAnimation();
			/*mBitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.girl_4_1);*/
			testAnim.setElements(BitmapFactory.decodeResource(getResources(),
					R.drawable.ic_launcher), new Paint());
			testAnim.setCurrentPosition(300, 100, 60);
			testAnim.setTranslate(0, 200, 10000);
			//testAnim.setRotate(60, 0, 0, 10000);
			testAnim.setRepeatTimes(3);
			testAnim.start(true);
			
			cupidAnim = new CanvasAnimation();
			cupidAnim.setElements(BitmapFactory.decodeResource(getResources(),
					R.drawable.cupid), new Paint());
			cupidAnim.setCurrentPosition(mWidth/2 - cupidAnim.mAnimBitmapWidth,
					mHeight -cupidAnim.mAnimBitmapHeight/2, -90);
			cupidAnim.setRotate(180, mWidth/2, mHeight, 10000);
			cupidAnim.setRepeatTimes(3);
			cupidAnim.start(true);
			
		}
		private void drawAinmationInstance() {
			//mCanvas.
			drawBackground();
			testAnim.transformModel(mCanvas);
			cupidAnim.transformModel(mCanvas);
		}
		private void drawBackground() {
			mCanvas.drawBitmap(backgroundBm, 0, 0, new Paint());	
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
				//Acquire start time.
				long startTime = System.currentTimeMillis();
				drawAinmationInstance();
				
			    //Acquire end time.  
		/*	    long endTime = System.currentTimeMillis();  
			      
			    //Calculate the interval time  
			    int diffTime  = (int)(endTime - startTime);  
			      
			    //Ensure every update interval is 20 millseconds.  
			    while(diffTime <= 20) {  
			        diffTime = (int)(System.currentTimeMillis() - startTime);  
			        //Thread wait  
			        Thread.yield();  
			    }*/
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

package com.example.cupidcannon;

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
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CupidCannon extends Activity {

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
		private Canvas mCanvas = null;
		private Paint mPaint = null;
		private boolean isRunning = true;
		
		//puzzle. I have no idea about the definite meaning of the flags.
	/*	private static final int LAYER_FLAGS = //Canvas.MATRIX_SAVE_FLAG | 
                Canvas.CLIP_SAVE_FLAG | 
                 //Canvas.HAS_ALPHA_LAYER_SAVE_FLAG 
           Canvas.FULL_COLOR_LAYER_SAVE_FLAG | 
                Canvas.CLIP_TO_LAYER_SAVE_FLAG;*/
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
			mThread = new Thread(this);// 创建一个绘图线程

		}

		private void initDraw() {

			//Draw elements on the first layer.
			mCanvas = mHolder.lockCanvas();
			mBitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.girl_4_2);
			mBitmapWidth = mBitmap.getWidth();
			mBitmapHeight = mBitmap.getHeight();
			
			mCanvas.drawRect(0, 0, mBitmapWidth, mBitmapHeight, mPaint);
			Matrix matrix = new Matrix();
			matrix.setScale(0.67f, 0.67f);
			matrix.postTranslate(0, -29);
			mCanvas.drawBitmap(mBitmap, matrix, mPaint);
			
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
           mHolder.unlockCanvasAndPost(mCanvas);
      

		}
		private void testDraw() {
		/*	mCanvas = mHolder.lockCanvas();
			
			mCanvas.saveLayerAlpha(0, 0, mWidth, mHeight, 0x88, LAYER_FLAGS);

			mBitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.ic_launcher);
			mBitmapWidth = mBitmap.getWidth();
			mBitmapHeight = mBitmap.getHeight();
			mCanvas.drawColor(Color.TRANSPARENT);
			mCanvas.drawRect(0, 0, mWidth, mHeight,
					new Paint());
			Matrix matrix = new Matrix();
			matrix.setScale(1.0f, 1.0f);
			matrix.postTranslate(0, 0);
			mCanvas.drawBitmap(mBitmap, matrix, mPaint);
			mCanvas.restore();
			mHolder.unlockCanvasAndPost(mCanvas);
			if (mBitmap != null)
				mBitmap.recycle();	*/
/*			mCanvas = mHolder.lockCanvas();
            mCanvas.saveLayerAlpha(0, 0, mWidth, mHeight, 0x00, LAYERS_FLAGS);  
            mPaint.setColor(Color.BLUE); 
            mCanvas.drawColor(Color.TRANSPARENT);
            mCanvas.drawCircle(125, 125, 75, mPaint);  
            mCanvas.restore();
            mHolder.unlockCanvasAndPost(mCanvas);*/
			
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			mWidth = this.getWidth();
			mHeight = this.getHeight();
			mPaint=new Paint();
			initDraw();
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
				testDraw();
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

}

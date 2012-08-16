package com.cube.attract.game.cupidcannon;

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
		private CanvasAnimation bulletAnim = null;
		private CanvasAnimation boomAnim = null;
		private CanvasAnimation artilleryAnimOdd = null;
		private CanvasAnimation artilleryAnimEven = null;
		private CanvasAnimation batteryAnimOdd = null;
		private CanvasAnimation batteryAnimEven = null;
		
		public Bitmap memBm = null;
		private Canvas mCanvas = null;
		public Bitmap backgroundBm = null;
		public Bitmap powerTube1 = null;
		public Bitmap powerTube2 = null;
		public Bitmap powerTube3 = null;
		public Bitmap bulletBm = null;
		public Bitmap boomBm = null;
		public float[] powerTubeBaseAdress = {0.0f, 0.0f};
		public final  int POWERSENSITY = 15;
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
			powerTube1 = BitmapFactory.decodeResource(getResources(),
					R.drawable.blue_part1);
			powerTube2 = BitmapFactory.decodeResource(getResources(),
					R.drawable.blue_part2);
			powerTube3 = BitmapFactory.decodeResource(getResources(),
					R.drawable.blue_part3);
			bulletBm = BitmapFactory.decodeResource(getResources(),
					R.drawable.bullet);
			boomBm =  BitmapFactory.decodeResource(getResources(),
					R.drawable.cupid);
			
		}
		
		private void initAnimationInstance(){
			
			Matrix initMatrix = new Matrix();
			
			boomAnim = new CanvasAnimation();
			boomAnim.setElements(boomBm, new Paint());
			boomAnim.setStartMatrix(initMatrix);
			
			boomAnim.setScale(0.5f, 0, 0, 3000);
			boomAnim.setRepeatTimes(3);
			boomAnim.start(true);
			
			artilleryAnimOdd = new CanvasAnimation();
			artilleryAnimOdd.setCallback(new CanvasAnimation.Callback() {
				
				@Override
				public void onEnd() {
					// TODO Auto-generated method stub
					
					artilleryAnimEven.start(true);
					artilleryAnimOdd.start(false);
					
				}
			});
			artilleryAnimOdd.setElements(BitmapFactory.decodeResource(getResources(),
					R.drawable.artillery), new Paint());
			initMatrix.setTranslate((mWidth - artilleryAnimOdd.mAnimBitmapWidth)/2,
					mHeight + 5 - artilleryAnimOdd.mAnimBitmapHeight + 20);
			initMatrix.postRotate(-90, rotateCenter[0], rotateCenter[1]);
			artilleryAnimOdd.setStartMatrix(initMatrix);
			//configure the artillery even trace matrix
			initMatrix.setTranslate(207, 650);
			initMatrix.postRotate(-90, rotateCenter[0], rotateCenter[1]);
			artilleryAnimOdd.setTraceMatrix(initMatrix);
			
			artilleryAnimOdd.setRotate(180, rotateCenter[0], rotateCenter[1], 5000);
			artilleryAnimOdd.setRepeatTimes(artilleryAnimOdd.INFINITE);
			artilleryAnimOdd.start(true);
			
			artilleryAnimEven = new CanvasAnimation();
			artilleryAnimEven.setElements(BitmapFactory.decodeResource(getResources(),
					R.drawable.artillery), new Paint());
			artilleryAnimEven.setCallback(new CanvasAnimation.Callback() {
				
				@Override
				public void onEnd() {
					// TODO Auto-generated method stub
					
					artilleryAnimOdd.start(true);
					artilleryAnimEven.start(false);
				}
			});
			initMatrix.setTranslate((mWidth - artilleryAnimEven.mAnimBitmapWidth)/2,
					mHeight + 5 - artilleryAnimEven.mAnimBitmapHeight + 20);
			initMatrix.postRotate(90, rotateCenter[0], rotateCenter[1]);
			artilleryAnimEven.setStartMatrix(initMatrix);
			//configure the artillery even trace matrix
			initMatrix.setTranslate(207, 650);
			initMatrix.postRotate(90, rotateCenter[0], rotateCenter[1]);
			artilleryAnimEven.setTraceMatrix(initMatrix);
			
			artilleryAnimEven.setRotate(-180, rotateCenter[0], rotateCenter[1], 5000);
			artilleryAnimEven.setRepeatTimes(artilleryAnimEven.INFINITE);
			artilleryAnimEven.start(false);
			
			batteryAnimOdd = new CanvasAnimation();
			batteryAnimOdd.setElements(BitmapFactory.decodeResource(getResources(),
					R.drawable.battery), new Paint());
			batteryAnimOdd.setCallback(new CanvasAnimation.Callback() {
				
				@Override
				public void onEnd() {
					// TODO Auto-generated method stub
					
					batteryAnimEven.start(true);
					batteryAnimOdd.start(false);
				}
			});
			initMatrix.setTranslate(mWidth/2 + 4 - batteryAnimOdd.mAnimBitmapWidth/2,
					mHeight + 5 - batteryAnimOdd.mAnimBitmapHeight/2);
			initMatrix.postRotate(-90, rotateCenter[0], rotateCenter[1]);
			batteryAnimOdd.setStartMatrix(initMatrix);
			//configure the battery odd trace matrix
			initMatrix.setTranslate(rotateCenter[0], 670);
			initMatrix.postRotate(-90, rotateCenter[0], rotateCenter[1]);
			batteryAnimOdd.setTraceMatrix(initMatrix);
			
			batteryAnimOdd.setRotate(180, rotateCenter[0], rotateCenter[1], 5000);
			batteryAnimOdd.setRepeatTimes(batteryAnimOdd.INFINITE);
			batteryAnimOdd.start(true);
			
			batteryAnimEven = new CanvasAnimation();
			batteryAnimEven.setElements(BitmapFactory.decodeResource(getResources(),
					R.drawable.battery), new Paint());
			batteryAnimEven.setCallback(new CanvasAnimation.Callback() {
				
				@Override
				public void onEnd() {
					// TODO Auto-generated method stub
					
					batteryAnimOdd.start(true);
					batteryAnimEven.start(false);
				}
			});
			initMatrix.setTranslate(mWidth/2 + 4 - batteryAnimEven.mAnimBitmapWidth/2,
					mHeight + 5 - batteryAnimEven.mAnimBitmapHeight/2);
			initMatrix.postRotate(90, rotateCenter[0], rotateCenter[1]);
			batteryAnimEven.setStartMatrix(initMatrix);
			//configure the battery even trace matrix
			initMatrix.setTranslate(rotateCenter[0], 670);
			initMatrix.postRotate(90, rotateCenter[0], rotateCenter[1]);
			batteryAnimEven.setTraceMatrix(initMatrix);
			
			batteryAnimEven.setRotate(-180, rotateCenter[0], rotateCenter[1], 5000);
			batteryAnimEven.setRepeatTimes(batteryAnimEven.INFINITE);
			batteryAnimEven.start(false);
			
		}
		
		private void drawBackground() {
			
			mCanvas.drawBitmap(backgroundBm, 0, 0, new Paint());
			Matrix testMatrix = new Matrix();
			testMatrix.setTranslate(-(480-mWidth)/2, mHeight-290);
			mCanvas.drawBitmap(backgroundStage, testMatrix, new Paint());
			if (powerTubeEnable){
				int tubeLength = (int) moveLength/POWERSENSITY;
				if (tubeLength > 32)
					tubeLength = 32;
				testMatrix.setTranslate(powerTubeBaseAdress[0], powerTubeBaseAdress[1]);
				mCanvas.drawBitmap(powerTube1, testMatrix, new Paint());
				for (int i=1; i<= tubeLength; i++){
					testMatrix.setTranslate(powerTubeBaseAdress[0], powerTubeBaseAdress[1] - i*7);
					mCanvas.drawBitmap(powerTube2, testMatrix, new Paint());
				}
				testMatrix.setTranslate(powerTubeBaseAdress[0], powerTubeBaseAdress[1] - tubeLength*7 - 14);
				mCanvas.drawBitmap(powerTube3, testMatrix, new Paint());
			}
			
			mCanvas.drawBitmap(BitmapFactory.decodeResource(getResources(),
					R.drawable.artillery),(mWidth - artilleryAnimEven.mAnimBitmapWidth)/2,
					mHeight + 5 - artilleryAnimEven.mAnimBitmapHeight + 20, new Paint());
			mCanvas.drawBitmap(bulletBm, 207, 650, new Paint());
			
			//位置检测，矩阵运算测试
			Matrix matrix = new Matrix();
	    	float [] vector = {0.0f, 0.0f};
	    	float [] array1 = {	0.0f, 0.0f, 0.0f,
	    						0.0f, 0.0f, 0.0f,
	    						0.0f, 0.0f, 0.0f,
	    	};
	    	float [] array2 = {	1.0f, 0.0f, 100.0f,
								0.0f, 1.0f, 0.0f,
								0.0f, 0.0f, 1.0f
	    	};
	    	
	    	float [] array3 = {	1.0f, 0.0f, 100.0f,
								0.0f, 1.0f, 100.0f,
								0.0f, 0.0f, 1.0f
	    	};
	    	matrix.setValues(array3);
	    	mCanvas.drawBitmap(bulletBm, matrix, new Paint());
	    	matrix.postTranslate(100, 100);
	    	mCanvas.drawBitmap(bulletBm, matrix, new Paint());
	    	matrix.getValues(array1);
	    	for (int i=0; i<3; i++){
	    		for (int j=0; j<3; j++){
	    			array3[3*i + j] =0.0f;
	    			for (int k=0; k<3; k++){
	    				array3[3*i + j] += array2[3*i + k] * array1[3*k + j];
	    			}
	    		}
	    	}
	    	matrix.setValues(array3);
	    	mCanvas.drawBitmap(bulletBm, matrix, new Paint());
		}
		private void drawAnimationInstance() {
			
			drawBackground();
			if (bulletAnim != null)
				bulletAnim.transformModel(mCanvas);

			batteryAnimOdd.transformModel(mCanvas);	
			batteryAnimEven.transformModel(mCanvas);
			batteryAnimOdd.transformModel(mCanvas);	
			
			
			artilleryAnimOdd.transformModel(mCanvas);
			artilleryAnimEven.transformModel(mCanvas);
			artilleryAnimOdd.transformModel(mCanvas);
			
			boomAnim.transformModel(mCanvas);
			
		}
		
		
	
		

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			mWidth = this.getWidth();
			mHeight = this.getHeight();
			powerTubeBaseAdress[0] = 28;
			powerTubeBaseAdress[1] = mHeight - 30;
			rotateCenter[0] = mWidth/2 + 3;
			rotateCenter[1] = mHeight + 3;
			mPaint=new Paint();
			memBm = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.RGB_565);
			mCanvas = new Canvas(memBm);
			initAnimationInstance();
		
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
				

				drawAnimationInstance();
	
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
		
		public boolean powerTubeEnable = false;
		public boolean bulletEnable = false;
		public float startX = 0;
		public float startY = 0;
		public double moveLength = 0;
		@SuppressWarnings("null")
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
			powerTubeEnable = true;
			startX = event.getX();
			startY = event.getY();
			break;
		    // 触摸移动的事件
		    case MotionEvent.ACTION_MOVE:
			Log.v("test", "ACTION_MOVE");
			currentX = event.getX();
			currentY = event.getY();
			moveLength = Math.sqrt((currentX - startX)*(currentX - startX)
					+ (currentY - startY)*(currentY - startY));
			break;
		    // 触摸抬起的事件
		    case MotionEvent.ACTION_UP:
			Log.v("test", "ACTION_UP");
			currentX = event.getX();
			currentY = event.getY();
			moveLength = Math.sqrt((currentX - startX)*(currentX - startX)
					+ (currentY - startY)*(currentY - startY));
			powerTubeEnable = false;
			bulletEnable = true;
			break;
		    }

		    if (bulletEnable == true){
		    	Matrix matrix = new Matrix();
		    	float [] vector = {0.0f, 0.0f};
		    	float [] array1 = {	1.0f, 0.0f, 0.0f,
		    						0.0f, 1.0f, 0.0f,
		    						0.0f, 0.0f, 1.0f
		    	};
		    	float [] array2 = {	1.0f, 0.0f, 0.0f,
									0.0f, 1.0f, 0.0f,
									0.0f, 0.0f, 1.0f
		    	};
				
		    	if (artilleryAnimOdd.isStarted == true){
		    		artilleryAnimOdd.traceMatrix.getValues(array1);
		    		batteryAnimOdd.traceMatrix.getValues(array2);
		    	}
		    	else{
		    		artilleryAnimEven.traceMatrix.getValues(array1);
		    		batteryAnimEven.traceMatrix.getValues(array2);	
		    	}

		    	matrix.setValues(array1);
		    	vector[0] = array2[2] - rotateCenter[0];
		    	vector[1] = array2[5] - rotateCenter[1];
		    	bulletAnim = new CanvasAnimation();
		    	bulletAnim.setCallback(new CanvasAnimation.Callback() {
					
					@Override
					public void onEnd() {
						// TODO Auto-generated method stub
						Matrix matrix = new Matrix();
						float [] array = {	1.0f, 0.0f, 0.0f,
	    									0.0f, 1.0f, 0.0f,
	    									0.0f, 0.0f, 1.0f
						};
						boomAnim = new CanvasAnimation();
						boomAnim.setElements(boomBm, new Paint());
						bulletAnim.transformMatrix.getValues(array);
						matrix.setValues(array);
						boomAnim.setStartMatrix(matrix);
//						if (batteryAnimOdd.isStarted == true){
//				    		batteryAnimOdd.traceMatrix.getValues(array);
//				    	}
//				    	else{
//				    		batteryAnimEven.traceMatrix.getValues(array);	
//				    	}
						bulletAnim.traceMatrix.getValues(array);
						boomAnim.setScale(2, array[2], array[5], 3000);
						//boomAnim.setScale(2, mWidth/2, mHeight/2, 3000);
						boomAnim.setRepeatTimes(1);
						boomAnim.start(true);
						bulletAnim.start(false);
					}
				});
		    	bulletAnim.setElements(bulletBm, new Paint());
		    	bulletAnim.setStartMatrix(matrix);
		    	//trace the centre of the bullet
		    	matrix.setValues(array2);
		    	bulletAnim.setTraceMatrix(matrix);
		    	float tubeLength = (float) moveLength/POWERSENSITY;
				if (tubeLength > 32)
					tubeLength = 32;
				if (tubeLength == 0)
					tubeLength = 1;
				bulletAnim.setAccelerate(vector[0], vector[1], -0.0004f, 200*(float)Math.sqrt(tubeLength));
				bulletAnim.setRepeatTimes(1);
				bulletAnim.start(true);
				bulletEnable = false;		
		    }
		    // return super.onTouchEvent(event);
		    return true;
		}

	}

}

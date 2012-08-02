package com.example.cupidcannon;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;



public class AnimView extends SurfaceView implements SurfaceHolder.Callback,Runnable {
	
	private int mWidth = 0;
	private int mHeight = 0;
	private int mBitmapWidth = 0;
	private int mBitmapHeight = 0;
	private SurfaceHolder mHolder = null;
    private Thread mThread = null; 
    private Bitmap mBitmap = null;
    private Canvas mCanvas = null;
    private Paint mPaint = null;
    public AnimView(Context context) {
	     super(context);
	     // TODO Auto-generated constructor stub
	     mHolder = this.getHolder();
	     mHolder.addCallback(this);
	     mThread = new Thread(this);//创建一个绘图线程
	          
    }
    
    private void initDraw(){
    
    	mCanvas=mHolder.lockCanvas();
    	
    	mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.girl_4_2);
    	mBitmapWidth = mBitmap.getWidth();
    	mBitmapHeight = mBitmap.getHeight();
    	
    	mCanvas.drawRect(0, 0, mBitmap.getWidth(), mBitmap.getHeight(), new Paint());
    	Matrix matrix = new Matrix();
    	matrix.setScale(1.0f, 1.0f);
    	matrix.postTranslate(-120, -240);
    	mCanvas.drawBitmap(mBitmap, matrix, mPaint);
    	mHolder.unlockCanvasAndPost(mCanvas);
    	if(mBitmap!=null)
    		mBitmap.recycle();

    }
     
     
    @Override  
    public void surfaceCreated(SurfaceHolder holder) {  
         mWidth = this.getWidth();  
         mHeight = this.getHeight();
         //mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
         //mCanvas.drawBitmap(mBitmap, 0, 0, mPaint);
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
           
    }  
    
    @Override  
    public void run() {  
    	while(true){  
  
            try {  
                 	Thread.sleep(100);  
             } catch (InterruptedException e) {  
                 // TODO Auto-generated catch block  
                 e.printStackTrace();  
             }  
         }  
     } 
	
	
}


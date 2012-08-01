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
    	/*
    	 mCanvas=mHolder.lockCanvas();
    	 mCanvas.drawRect(0, 0, this.getWidth(), this.getHeight(), new Paint());
    	 mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
    	 mCanvas.drawBitmap(mBitmap, 0, 0, mPaint);*/
    	Canvas canvas=mHolder.lockCanvas();
    	canvas.drawRect(0, 0, this.getWidth(), this.getHeight(), new Paint());
    	Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.girl_4_2);
    	Matrix matrix = new Matrix();
    	matrix.setScale(1.0f, 1.0f);
    	matrix.postTranslate(0, 0);
    	canvas.drawBitmap(bmp, matrix, mPaint);
    /*	matrix.postTranslate(0, 450);
    	bmp = BitmapFactory.decodeResource(getResources(), R.drawable.girl_4_3);
    	canvas.drawBitmap(bmp, matrix, mPaint);
    	matrix.postTranslate(0, 500);
    	bmp = BitmapFactory.decodeResource(getResources(), R.drawable.girl_4_1);
    	canvas.drawBitmap(bmp, matrix, mPaint);
    	matrix.postTranslate(0, 550);
    	bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
    	canvas.drawBitmap(bmp, matrix, mPaint);*/
    	mHolder.unlockCanvasAndPost(canvas);
    	if(bmp!=null)
    		bmp.recycle();

    }
     
     
    @Override  
    public void surfaceCreated(SurfaceHolder holder) {  
         mWidth=this.getWidth();  
         mHeight=this.getHeight();
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


package com.example.cupidcannon;

import android.content.Context;
import android.view.SurfaceHolder;
import android.view.SurfaceView;



public class AnimView extends SurfaceView implements SurfaceHolder.Callback,Runnable {
	
	private int mWidth;
	private int mHeight;
	private SurfaceHolder mHolder;
    private Thread mThread; 
    public AnimView(Context context) {
	     super(context);
	     // TODO Auto-generated constructor stub
	     mHolder = this.getHolder();
	     mHolder.addCallback(this);
	     mThread = new Thread(this);//创建一个绘图线程
    }
     
     
    @Override  
    public void surfaceCreated(SurfaceHolder holder) {  
         mWidth=this.getWidth();  
         mHeight=this.getHeight();  
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


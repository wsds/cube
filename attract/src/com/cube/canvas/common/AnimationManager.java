package com.cube.canvas.common;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

public class AnimationManager {
	public ArrayList<AnimationBitmap> animationBitmaps = new ArrayList<AnimationBitmap>();
	public Context context = null;
	public Paint paint = null;
	public Canvas mCanvas = null;

	public AnimationManager(Context context) {
		this.context = context;
		paint = new Paint();
	}

	public AnimationBitmap addAnimationBitmap(int id) {
		AnimationBitmap animationBitmap = new AnimationBitmap();
		animationBitmap.bitmap = BitmapFactory.decodeResource(context.getResources(), id);
		animationBitmap.matrix = new Matrix();
		animationBitmaps.add(animationBitmap);
		return animationBitmap;
	}

	public void draw() {
		for(AnimationBitmap animationBitmap : animationBitmaps){
			mCanvas.drawBitmap(animationBitmap.bitmap, animationBitmap.matrix, paint);
		}
	}

	public class AnimationBitmap {
		public Bitmap bitmap = null;
		public Matrix matrix = null;
	}

}

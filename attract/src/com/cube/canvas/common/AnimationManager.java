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
	public Bitmap memoryBitmap = null;
	public Canvas memoryCanvas = null;
	public Paint memoryPaint = null;

	public Matrix memoryMatrix = null;
	public boolean needRefreshMemory = false;

	public AnimationManager(Context context, int mHeight, int mWidth) {

		memoryBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.RGB_565);
		memoryCanvas = new Canvas(memoryBitmap);
		this.context = context;
		paint = new Paint();
		memoryPaint = new Paint();
		memoryMatrix = new Matrix();
	}

	public AnimationBitmap addAnimationBitmap(Bitmap bitmap) {
		AnimationBitmap animationBitmap = new AnimationBitmap();
		animationBitmap.bitmap = bitmap;
		animationBitmap.matrix = new Matrix();
		animationBitmaps.add(animationBitmap);

		needRefreshMemory = true;
		return animationBitmap;
	}

	public AnimationBitmap addAnimationBitmaps(ArrayList<Bitmap> bitmaps) {
		AnimationBitmap animationBitmap = new AnimationBitmap();
		for (Bitmap bitmap : bitmaps) {
			animationBitmap.addBitmap(bitmap);
		}
		// animationBitmap.bitmaps = bitmaps;
		animationBitmap.matrix = new Matrix();
		animationBitmaps.add(animationBitmap);

		needRefreshMemory = true;
		return animationBitmap;
	}

	public AnimationBitmap addAnimationBitmap(int id) {
		AnimationBitmap animationBitmap = new AnimationBitmap();
		animationBitmap.bitmap = BitmapFactory.decodeResource(context.getResources(), id);
		animationBitmap.matrix = new Matrix();
		animationBitmaps.add(animationBitmap);

		needRefreshMemory = true;
		return animationBitmap;
	}

	public void removeAnimationBitmap(AnimationBitmap animationBitmap) {
		animationBitmaps.remove(animationBitmap);
		needRefreshMemory = true;
	}

	public void draw() {
		@SuppressWarnings("unchecked")
		ArrayList<AnimationBitmap> animationBitmaps = (ArrayList<AnimationBitmap>) this.animationBitmaps.clone();
		for (AnimationBitmap animationBitmap : animationBitmaps) {
			if (animationBitmap.frameNum > 0) {
				animationBitmap.frameFunction();
				mCanvas.drawBitmap(animationBitmap.bitmaps.get(animationBitmap.frameID), animationBitmap.matrix, paint);
			} else {
				mCanvas.drawBitmap(animationBitmap.bitmap, animationBitmap.matrix, paint);
			}
			@SuppressWarnings("unchecked")
			ArrayList<CanvasAnimation2> animationPool = (ArrayList<CanvasAnimation2>) animationBitmap.animationPool.clone();
			// Double-buffering here to resolve the ConcurrentModificationException, which to caused by multiple thread accessing.
			for (CanvasAnimation2 animation : animationPool) {
				boolean isFinished = animation.transformModel(animationBitmap.matrix);
				if (isFinished == true) {
					animationBitmap.removeAnimation(animation);
					if (animation.children != null) {
						for (CanvasAnimation2 child : animation.children) {
							animationBitmap.addAnimation(child);
						}
					}
				}
			}
			animationPool.clear();
		}
		animationBitmaps.clear();
	}

	public void drawStatic() {
		if (needRefreshMemory == true) {
			refreshMemBitmap();
			needRefreshMemory = false;
		}
		mCanvas.drawBitmap(memoryBitmap, memoryMatrix, paint);

	}

	public void refreshMemBitmap() {
		@SuppressWarnings("unchecked")
		ArrayList<AnimationBitmap> animationBitmaps = (ArrayList<AnimationBitmap>) this.animationBitmaps.clone();
		for (AnimationBitmap animationBitmap : animationBitmaps) {
			memoryCanvas.drawBitmap(animationBitmap.bitmap, animationBitmap.matrix, memoryPaint);
		}
	}

	public class AnimationBitmap {
		public int width = 0;
		public int height = 0;
		public Bitmap bitmap = null;

		public Matrix matrix = null;
		ArrayList<CanvasAnimation2> animationPool = new ArrayList<CanvasAnimation2>();

		public ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>();
		int frameID = -1;
		int frameNum = 0;

		int flag = 0;

		void frameFunction() {
			if (flag == 1) {
				frameID = (frameID + 1) % frameNum;
				flag = 0;
			}
			flag++;
		}

		public void addBitmap(Bitmap bitmap) {
			bitmaps.add(bitmap);
			frameNum++;
			frameID = 0;
		}

		public void addAnimation(CanvasAnimation2 animation) {
			animationPool.add(animation);
		}

		public void removeAnimation(CanvasAnimation2 animation) {
			animationPool.remove(animation);
		}
	}

}

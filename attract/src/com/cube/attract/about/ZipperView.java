package com.cube.attract.about;

import com.cube.attract.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class ZipperView extends View {

	private Bitmap mDrawbleBackground;
	private Bitmap mDrawbleZipperBack;
	private Bitmap mZipper;
	private Bitmap mLeft;
	private Bitmap mRight;
	private int mWidth;
	private int mHeight;
	public float movedY = 0;

	private int[] backgroundByte;

	private int[] zipperBackByte;

	private int[] newBackgroundByte;
	Context context = null;

	Bitmap leftBitmap = null;
	Bitmap rightBitmap = null;
	Bitmap zipperbitmap = null;

	public ZipperView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init();
	}

	private void init() {
		mDrawbleBackground = BitmapFactory.decodeResource(getResources(), R.drawable.welcome);
		mDrawbleZipperBack = BitmapFactory.decodeResource(getResources(), R.drawable.zipper_back);
		mZipper = BitmapFactory.decodeResource(getResources(), R.drawable.zipper);
		mLeft = BitmapFactory.decodeResource(getResources(), R.drawable.zipper_left);
		mRight = BitmapFactory.decodeResource(getResources(), R.drawable.zipper_right);

		zipperbitmap = Bitmap.createScaledBitmap(mZipper, 50, 100, false);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		mWidth = getWidth();
		mHeight = getHeight();

		backgroundByte = getBitmapByte(mDrawbleBackground);
		zipperBackByte = getBitmapByte(mDrawbleZipperBack);
		newBackgroundByte = new int[zipperBackByte.length];

	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		drawSlide(canvas);
		drawZipper(canvas);
		drawLeft(canvas);
		drawRight(canvas);
	}

	private void drawLeft(Canvas canvas) {
		leftBitmap = Bitmap.createScaledBitmap(mLeft, mWidth / 2, mHeight, false);
		canvas.drawBitmap(leftBitmap, 0, movedY - leftBitmap.getHeight(), new Paint());
	}

	private void drawRight(Canvas canvas) {
		rightBitmap = Bitmap.createScaledBitmap(mRight, mWidth / 2, mHeight, false);
		canvas.drawBitmap(rightBitmap, rightBitmap.getWidth(), movedY - rightBitmap.getHeight(), new Paint());
	}

	private float startY;
	private float startMovedY;

	String status = "None";

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_MOVE:
			Log.d("ZipperView", "ACTION_MOVE");
			float dy = event.getY() - startY;
			if (status == "ZipperPressed" || status == "ZipperOpened") {
				float moveY = startMovedY + dy;
				Log.d("ZipperView", "moving");
				float line = 600f / 800f * (float) mHeight;
				if (moveY > line) {
					status = "ZipperOpened";
				} else {
					status = "ZipperPressed";
				}
				movedY = moveY;
				postInvalidate();
			}

			break;
		case MotionEvent.ACTION_DOWN:
			Log.d("ZipperView", "ACTION_DOWN");
			startY = event.getY();
			startMovedY = movedY;
			if (status == "None" && (movedY + 22 - startY) * (movedY + 22 - startY) < 1936 * 4) {
				status = "ZipperPressed";
				Log.d("ZipperView", "ZipperPressed");
			}
			break;

		case MotionEvent.ACTION_UP:
			if (status == "ZipperPressed") {
				status = "None";
			} else if (status == "ZipperOpened") {
				((AboutActivity) context).soundPool.play(((AboutActivity) context).effect_tick, 0.2f, 0.2f, 1, 0, 1f);
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				Intent entry = new Intent(Intent.ACTION_MAIN);
				entry.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				entry.setClassName("com.cube.attract", "com.cube.attract.entry.EntryActivity");
				context.startActivity(entry);
				((Activity) context).finish();
			}
			break;
		default:
			break;
		}

		// return super.onTouchEvent(event);
		return true;
	}

	private void drawZipper(Canvas canvas) {

		canvas.drawBitmap(zipperbitmap, (mWidth - 50) / 2, movedY, new Paint());
	}

	private int[] getBitmapByte(Bitmap bitmap) {
		int[] bytes;

		int bitmapWidth = bitmap.getWidth();
		int bitmapHeight = bitmap.getHeight();
		float scaleWidth = Float.intBitsToFloat(mWidth) / Float.intBitsToFloat(bitmapWidth);
		float scaleHeight = Float.intBitsToFloat(mHeight) / Float.intBitsToFloat(bitmapHeight);
		Matrix mBackgroundMatrix;
		mBackgroundMatrix = new Matrix();
		mBackgroundMatrix.setScale(scaleWidth, scaleHeight);
		Bitmap bmp = Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, bitmapHeight, mBackgroundMatrix, true);
		bytes = new int[mWidth * mHeight];
		bmp.getPixels(bytes, 0, mWidth, 0, 0, mWidth, mHeight);
		return bytes;
	}

	private void drawSlide(Canvas canvas) {
		int centerX = mWidth / 2;

		newBackgroundByte = zipperBackByte.clone();
		if (movedY > mHeight - 20) {
			movedY = mHeight - 20;
		}
		for (int y = 0; y < movedY; y++) {
			int halfLengthX = (int) (200 * (movedY - y) / 730);
			int leftX = centerX - halfLengthX;
			if (leftX < 0) {
				leftX = 0;
			}
			int ringtX = centerX + halfLengthX;
			if (ringtX > mWidth) {
				ringtX = mWidth;
			}
			for (int x = leftX; x <= ringtX; x++) {
				int exchangeByteIndex = y * mWidth + x;
				try {
					newBackgroundByte[exchangeByteIndex] = backgroundByte[exchangeByteIndex];
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		Bitmap bmp = Bitmap.createBitmap(newBackgroundByte, mWidth, mHeight, Config.ARGB_8888);
		canvas.drawBitmap(bmp, 0, 0, new Paint());
	}
}

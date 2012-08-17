package com.cube.attract.about;

import com.cube.attract.R;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.YuvImage;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class ZipperView extends View
{

	private Bitmap mDrawbleBg;
	private Bitmap mDrawbleZb;
	private Bitmap mZipper;
	private Bitmap mLeft;
	private Bitmap mRight;
	private int mWidth;
	private int mHeight;
	public float movedY = 0;

	private Path mPath;
	private int[] backgroundByte;

	private int[] zipperBackByte;

	private int[] newBackgroundByte;
	Context context = null;

	public ZipperView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		this.context = context;
		init();
	}

	private void init()
	{
		mDrawbleBg = BitmapFactory.decodeResource(getResources(), R.drawable.background);
		mDrawbleZb = BitmapFactory.decodeResource(getResources(), R.drawable.zipper_back);
		mZipper = BitmapFactory.decodeResource(getResources(), R.drawable.zipper);
		mLeft = BitmapFactory.decodeResource(getResources(), R.drawable.zipper_left);
		mRight = BitmapFactory.decodeResource(getResources(), R.drawable.zipper_right);
		mPath = new Path();

	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom)
	{
		super.onLayout(changed, left, top, right, bottom);
		mWidth = getWidth();
		mHeight = getHeight();
		getBgByte();
		getZbByte();
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		drawSlide(canvas);
		drawzipper(canvas);
		drawleft(canvas);
		drawright(canvas);
	}

	private void drawleft(Canvas canvas)
	{
		Bitmap left = Bitmap.createScaledBitmap(mLeft, mWidth / 2, mHeight, false);
		canvas.drawBitmap(left, 0, movedY - left.getHeight(), new Paint());
	}

	private void drawright(Canvas canvas)
	{
		Bitmap right = Bitmap.createScaledBitmap(mRight, mWidth / 2, mHeight, false);
		canvas.drawBitmap(right, right.getWidth(), movedY - right.getHeight(), new Paint());
	}

	private float startX, startY;
	Boolean istartingActivityBoolean = false;

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		switch (event.getAction()) {
			case MotionEvent.ACTION_MOVE:
				Log.d("ZipperView", "ACTION_MOVE");
				float dx = event.getX() - startX;
				float dy = event.getY() - startY;
				movedY = dy;

				if (movedY > 600) {
					if (istartingActivityBoolean == false) {
						istartingActivityBoolean = true;
						Intent about = new Intent(Intent.ACTION_MAIN);
						about.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						about.setClassName("com.cube.attract", "com.cube.attract.entry.EntryActivity");
						context.startActivity(about);
						((Activity) context).finish();
					}
				}
				else {
					istartingActivityBoolean = false;
					postInvalidate();

				}
				break;
			case MotionEvent.ACTION_DOWN:
				Log.d("ZipperView", "ACTION_DOWN");
				startX = event.getX();
				startY = event.getY();
				break;
			default:
				break;
		}

//		return super.onTouchEvent(event);
		return true;
	}

	private void drawzipper(Canvas canvas)
	{
		Bitmap zipperbitmap = Bitmap.createScaledBitmap(mZipper, 50, 100, false);
		canvas.drawBitmap(zipperbitmap, (mWidth - 50) / 2, movedY, new Paint());
	}

	private void drawZb(Canvas canvas)
	{
		int bmwidth = mDrawbleZb.getWidth();
		int bmheight = mDrawbleZb.getHeight();
		float scalewidth = Float.intBitsToFloat(mWidth) / Float.intBitsToFloat(bmwidth);
		float scaleheight = Float.intBitsToFloat(mHeight) / Float.intBitsToFloat(bmheight);
		Matrix mBgMatrix;
		mBgMatrix = new Matrix();
		mBgMatrix.postScale(scalewidth, scaleheight);
		canvas.drawBitmap(mDrawbleZb, mBgMatrix, new Paint());
	}

	private void drawbg(Canvas canvas)
	{
		int bmwidth = mDrawbleBg.getWidth();
		int bmheight = mDrawbleBg.getHeight();
		float scalewidth = Float.intBitsToFloat(mWidth) / Float.intBitsToFloat(bmwidth);
		float scaleheight = Float.intBitsToFloat(mHeight) / Float.intBitsToFloat(bmheight);
		Matrix mBgMatrix;
		mBgMatrix = new Matrix();
		mBgMatrix.postScale(scalewidth, scaleheight);
		canvas.drawBitmap(mDrawbleBg, mBgMatrix, new Paint());
	}

	private void getZbByte()
	{
		int bmwidth = mDrawbleZb.getWidth();
		int bmheight = mDrawbleZb.getHeight();
		float scalewidth = Float.intBitsToFloat(mWidth) / Float.intBitsToFloat(bmwidth);
		float scaleheight = Float.intBitsToFloat(mHeight) / Float.intBitsToFloat(bmheight);
		Matrix mBgMatrix;
		mBgMatrix = new Matrix();
		mBgMatrix.setScale(scalewidth, scaleheight);
		Bitmap bmp = Bitmap.createBitmap(mDrawbleZb, 0, 0, bmwidth, bmheight, mBgMatrix, true);
		zipperBackByte = new int[mWidth * mHeight];
		bmp.getPixels(zipperBackByte, 0, mWidth, 0, 0, mWidth, mHeight);
		newBackgroundByte = new int[mWidth * mHeight];
	}

	private void getBgByte()
	{
		int bmwidth = mDrawbleBg.getWidth();
		int bmheight = mDrawbleBg.getHeight();
		float scalewidth = Float.intBitsToFloat(mWidth) / Float.intBitsToFloat(bmwidth);
		float scaleheight = Float.intBitsToFloat(mHeight) / Float.intBitsToFloat(bmheight);
		Matrix mBgMatrix;
		mBgMatrix = new Matrix();
		mBgMatrix.setScale(scalewidth, scaleheight);
		Bitmap bmp = Bitmap.createBitmap(mDrawbleBg, 0, 0, bmwidth, bmheight, mBgMatrix, true);
		backgroundByte = new int[mWidth * mHeight];
		bmp.getPixels(newBackgroundByte, 0, mWidth, 0, 0, mWidth, mHeight);
	}

	private void drawSlide(Canvas canvas)
	{
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
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		Bitmap bmp = Bitmap.createBitmap(newBackgroundByte, mWidth, mHeight, Config.ARGB_8888);
		canvas.drawBitmap(bmp, 0, 0, new Paint());
	}
}

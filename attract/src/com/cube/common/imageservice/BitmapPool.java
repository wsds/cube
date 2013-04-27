package com.cube.common.imageservice;

import java.util.HashMap;
import java.util.Map;

import android.graphics.Bitmap;

public class BitmapPool {
	public Map<String, Bitmap> map = new HashMap<String, Bitmap>();

	static BitmapPool instance = null;

	static public BitmapPool getInstance() {
		if (instance == null)
			instance = new BitmapPool();
		return instance;
	}

	public Bitmap get(String filename) {
		Bitmap bitmap = null;
		if (!map.containsKey(filename)) {
			bitmap = WebImage.loadBitmapFromSDCard(filename);
			if (bitmap != null) {
				map.put(filename, bitmap);
			}
		} else {
			bitmap = map.get(filename);
			if (bitmap.isRecycled()) {
				bitmap = WebImage.loadBitmapFromSDCard(filename);
			}
		}
		return bitmap;
	}

}

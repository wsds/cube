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
}

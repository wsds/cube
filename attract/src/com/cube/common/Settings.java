package com.cube.common;

import java.text.SimpleDateFormat;

import com.cube.common.imageservice.BitmapPool;

public class Settings {
	static Settings instance = null;

	static public Settings getInstance() {
		if (instance == null)
			instance = new Settings();
		return instance;
	}

	public String isLogoin = "false";
	
	public SimpleDateFormat SDF_DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd hh:mm:ss.SSS");
	public ServerData serverData = ServerData.getInstance();
	public LocalData localData = LocalData.getInstance();
	public BitmapPool bitmapPool = BitmapPool.getInstance();
}

package com.cube.common;

public class Settings {
	static Settings instance = null;

	static public Settings getInstance() {
		if (instance == null)
			instance = new Settings();
		return instance;
	}
	
	public String isLogoin = "false";

}

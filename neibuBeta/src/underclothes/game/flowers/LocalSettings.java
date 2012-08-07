package underclothes.game.flowers;

import java.util.Date;

import android.text.format.DateFormat;

public class LocalSettings {

	public boolean ispoped = true;	
	public boolean ispoping = false;
	public boolean isCanvasSurfaceReady = false;

	LocalSettings() {
//		ispoped = true;	
//		ispoping = false;
		
		String today = DateFormat.format("yyyyMMdd", new Date()).toString();
	}

	static LocalSettings instance = null;

	static public LocalSettings getInstance() {
		if (instance == null)
			instance = new LocalSettings();
		return instance;
	}

}

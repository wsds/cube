package com.cube.attract.game.cupidcannon;

import android.content.Intent;

final class SceneState {

	static SceneState instance = null;

	static public SceneState getInstance() {
		if (instance == null)
			instance = new SceneState();
		return instance;
	}

	Intent intent = null;
	String weibo = null;
	int girlNumber = -1;
	int girlsSize = 0;
	long girlID = -1;
	long x1=0;
	long y1=0;
	long x2=0;
	long y2=0;

}

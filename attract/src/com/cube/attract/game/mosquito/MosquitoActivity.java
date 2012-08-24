package com.cube.attract.game.mosquito;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;

public class MosquitoActivity extends Activity {

	private static final String TAG = "MosquitoActivity";

	Context context;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(new AnimView(this));
		Log.v(TAG, "MosquitoActivity");
	}

}

package com.cube.attract.entry;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.widget.Toast;

public class EntryActivity extends Activity {

	private GLSurfaceView mGLSurfaceView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		mGLSurfaceView = new MyGLSurfaceView(this);
		mGLSurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
		setContentView(mGLSurfaceView);
		mGLSurfaceView.requestFocus();
		mGLSurfaceView.setFocusableInTouchMode(true);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mGLSurfaceView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mGLSurfaceView.onPause();
	}

	private Handler myHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Toast.makeText(EntryActivity.this, "选中了" + msg.what + "面", Toast.LENGTH_SHORT).show();
		}
	};
}
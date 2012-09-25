package com.cube.attract.gameEntry;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.cube.attract.R;
import com.cube.common.LocalData;
import com.cube.common.LocalData.Game.ActiveGirl;

public class GameEntryActivity extends Activity {
	private GLSurfaceView surface;
	private GlRenderer render;

	Context mContext;
	Activity mActivity;
	private GestureDetector gestureDetector;

	SceneState sceneState = SceneState.getInstance();
	public LocalData localData = LocalData.getInstance();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		gestureDetector = new GestureDetector(this, new GlAppGestureListener());

		mContext = this;
		mActivity = this;

		surface = new GLSurfaceView(this);
		render = new GlRenderer(this);
		surface.setRenderer(render);
		setContentView(surface);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);

	}

	@Override
	protected void onPause() {
		super.onPause();
		surface.onPause();
	}

	@Override
	protected void onResume() {
		sceneState.notJustComeIn = false;
		super.onResume();
		surface.onResume();
	}

	private float startX, startY;
	float TOUCH_SCAL_FACTOR = 180f / 320;
	private int GAMENUMBER = -1;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (gestureDetector.onTouchEvent(event)) {
			return true;
		}

		switch (event.getAction()) {
		case MotionEvent.ACTION_MOVE:
			if (sceneState.state == "TouchDown_Girl" || sceneState.state == "TouchMove") {

				sceneState.pictureViewGallary.dx = event.getX() - startX;
				sceneState.pictureViewGallary.dy = event.getY() - startY;
				sceneState.pictureViewGallary.dAngle = sceneState.pictureViewGallary.dx * TOUCH_SCAL_FACTOR;
				float path = sceneState.pictureViewGallary.dx * sceneState.pictureViewGallary.dx + sceneState.pictureViewGallary.dy * sceneState.pictureViewGallary.dy;
				if (path > 1600) {
					if (sceneState.state == "TouchDown_Girl") {

					}
					sceneState.state = "TouchMove";
					if (sceneState.backAnimaLock) {
						render.girls.addAnimation(render.girlGoBack);
						render.girls.addAnimation(render.girlRotateBack);

						// renderer.girlGoBack.start(true);
						// renderer.girlRotateBack.start(true);
						sceneState.backAnimaLock = false;
						sceneState.goFrontPermit = true;
						sceneState.notJustComeIn = true;
					}

				}
			}
			break;
		case MotionEvent.ACTION_DOWN:
			sceneState.isTouchUp = false;
			startX = event.getX();
			startY = event.getY();

			float normalY = startY * 800 / sceneState.screenHeight;
			float normalX = startX * 480 / sceneState.screenWidth;
			
			if(normalY < 105&&normalY>50&&normalX>65 &&normalX<181){
				GAMENUMBER = 7;
				sceneState.isReturn = true;
			}

			if ((normalY < 440)||(normalY>2.09*normalX+369)||(1258.7-1.77*normalX<normalY)) {
				sceneState.eventType = sceneState.GIRL;
				if (sceneState.state == "None") {
					sceneState.state = "TouchDown_Girl";
				}

				sceneState.pictureViewGallary.dxSpeed = 0.0f;
				sceneState.pictureViewGallary.isStopping = false;
				sceneState.pictureViewGallary.saveMovement();

			} else if (normalY < 505) {
				if (normalX > 181 && normalX < 300 && !sceneState.isLocked[1]) {
					GAMENUMBER = 2;
					Log.i("come in game", "2");
					sceneState.isSelected[1] = true;

					// 2

				} else if (normalX > 60 && normalX < 181 && !sceneState.isLocked[0]) {
					GAMENUMBER = 1;
					Log.i("come in game", "1");
					// 1
					sceneState.isSelected[0] = true;

				} else if (normalX < 420 && normalX > 300 && !sceneState.isLocked[2]) {
					GAMENUMBER = 3;
					Log.i("come in game", "3");
					sceneState.isSelected[2] = true;

					// 3
				}
			} else if (normalY < 620 && normalY > 545) {
				if (normalX > 120 && normalX < 239 && !sceneState.isLocked[3]) {
					GAMENUMBER = 4;
					Log.i("come in game", "4");
					sceneState.isSelected[3] = true;

					// 4
				} else if (normalX > 239 && normalX < 361 && !sceneState.isLocked[4]) {
					GAMENUMBER = 5;
					Log.i("come in game", "5");
					sceneState.isSelected[4] = true;

					// 5
				}
			} else if (normalY > 650 && normalX < 737) {
				if (normalX > 181 && normalX < 300 && !sceneState.isLocked[5]) {
					Log.i("come in game", "6");
					GAMENUMBER = 6;
					sceneState.isSelected[5] = true;
					// 6
				}
			}
			break;

		case MotionEvent.ACTION_UP:
			sceneState.isTouchUp = true;
			if (sceneState.state == "TouchMove") {
				sceneState.state = "Moving";
			} else {
				switch (GAMENUMBER) {
				case 1:
					sceneState.isSelected[0] = false;
					ActiveGirl girl1 = localData.game.activeGirls.get(sceneState.girlNumber);
					long girlID1 = girl1.id;
					String weibo1 = girl1.girl.weibo;

					Intent game1 = new Intent(Intent.ACTION_MAIN);
					game1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					game1.putExtra("girlNumber", sceneState.girlNumber);
					game1.putExtra("girlID", girlID1);
					game1.putExtra("weibo", weibo1);

					game1.setClassName("com.cube.attract", "com.cube.attract.game.mosquito.MosquitoActivity");
					mContext.startActivity(game1);
					mActivity.finish();
					break;
				case 2:
					sceneState.isSelected[1] = false;
					ActiveGirl girl2 = localData.game.activeGirls.get(sceneState.girlNumber);
					long girlID2 = girl2.id;
					String weibo2 = girl2.girl.weibo;

					Intent game2 = new Intent(Intent.ACTION_MAIN);
					game2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					game2.putExtra("girlNumber", sceneState.girlNumber);
					game2.putExtra("girlID", girlID2);
					game2.putExtra("weibo", weibo2);
					game2.setClassName("com.cube.attract", "com.cube.attract.game.cupidcannon.CupidCannonActivity");
					mContext.startActivity(game2);
					mActivity.finish();

					break;
				case 3:
					sceneState.isSelected[2] = false;

					break;
				case 4:
					sceneState.isSelected[3] = false;

					break;
				case 5:
					sceneState.isSelected[4] = false;

					break;
				case 6:
					sceneState.isSelected[5] = false;

					break;
				case 7:
					sceneState.isReturn = false;
					Intent entry = new Intent(Intent.ACTION_MAIN);
					entry.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					entry.setClassName("com.cube.attract", "com.cube.attract.entry.EntryActivity");
					mContext.startActivity(entry);
					finish();
					break;
				}
			}
			sceneState.eventType = sceneState.NONE;
			break;
		case MotionEvent.ACTION_CANCEL:
			sceneState.eventType = sceneState.NONE;
			break;
		}

		return super.onTouchEvent(event);
	}

	private class GlAppGestureListener extends GestureDetector.SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

			if (sceneState.eventType == sceneState.GIRL) {
				sceneState.pictureViewGallary.dxSpeed = velocityX / 1100;
				sceneState.pictureViewGallary.dySpeed = velocityY / 1100;
			}

			return super.onFling(e1, e2, velocityX, velocityY);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			new AlertDialog.Builder(this).setIcon(R.drawable.cupid).setTitle(R.string.app_name).setMessage("真的要走吗，亲！").setNegativeButton("返回魔方石", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {

					Intent entry = new Intent(Intent.ACTION_MAIN);
					entry.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					entry.setClassName("com.cube.attract", "com.cube.attract.entry.EntryActivity");
					mContext.startActivity(entry);
					finish();

				}
			}).setPositiveButton("退出游戏", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					finish();
				}
			}).show();

			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

}

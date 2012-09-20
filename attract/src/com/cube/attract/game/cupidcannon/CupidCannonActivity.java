package com.cube.attract.game.cupidcannon;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.cube.attract.R;
import com.cube.common.LocalData;
import com.umeng.analytics.MobclickAgent;
import com.umeng.api.sns.UMSnsService;

public class CupidCannonActivity extends Activity {
	private static final String TAG = "CupidCannonActivity";

	AnimView animView = null;
	Activity mActivity;
	Context mContext;
	RelativeLayout canvasContainer = null;
	ImageView shareSina, againChallenge, button_return;
	Animation toleftAnimation = null;
	Animation torightAnimation = null;
	Animation fromleftAnimation = null;
	Animation fromrightAnimation = null;
	public String onClickButton = "null";

	public final static int WIN = 0;
	public final static int TIME_OUT = 1;
	public final static int NO_BULLET_LEFT = 2;
	public String gameTime = "";
	public String weibo = "";
	public int gameState = TIME_OUT;
	public boolean isShared = false;

	public LocalData localData = LocalData.getInstance();
	public SceneState sceneState = SceneState.getInstance();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mContext = this;
		mActivity = this;
		// 强制为竖屏
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		Intent intent = getIntent();
		sceneState.weibo = intent.getStringExtra("weibo");
		sceneState.girlNumber = intent.getIntExtra("girlNumber", -1);
		sceneState.girlID = intent.getLongExtra("girlID", -1);

		setContentView(R.layout.game);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		animView = new AnimView(this);
		canvasContainer = (RelativeLayout) findViewById(R.id.CanvasContainer);
		canvasContainer.addView(animView, 0);
		animView.getHolder().setFormat(PixelFormat.TRANSPARENT);

		MobclickAgent.onEvent(mContext, "cupidCannonStart");
		shareSina = (ImageView) findViewById(R.id.sharesina);
		shareSina.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onClickButton = "shareSina";
				hideImage();
				MobclickAgent.onEvent(mContext, "event");
			}
		});
		button_return = (ImageView) findViewById(R.id.button_return);
		button_return.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onClickButton = "button_return";
				hideImage();
			}
		});
		againChallenge = (ImageView) findViewById(R.id.againchallenge);
		againChallenge.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onClickButton = "againChallenge";
				hideImage();
				MobclickAgent.onEvent(mContext, "cupidCannonStart");

			}
		});

		toleftAnimation = AnimationUtils.loadAnimation(mContext, R.anim.toleft);
		torightAnimation = AnimationUtils.loadAnimation(mContext, R.anim.toright);
		fromleftAnimation = AnimationUtils.loadAnimation(mContext, R.anim.fromleft);
		fromrightAnimation = AnimationUtils.loadAnimation(mContext, R.anim.fromright);

		toleftAnimation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				MobclickAgent.onEvent(mContext, "event");
				if (onClickButton == "shareSina") {
					isShared = true;
					if (gameState == WIN) {
						UMSnsService.shareToSina(CupidCannonActivity.this, "我在玩‘魔方石de诱惑’，使用丘比特之炮，只用了" + gameTime + "秒就获得了美女" + weibo + " 的芳心，成功搭讪，展现了超人的魅力，哇哈哈哈。", null);
						Log.v("SINA", "Share with sina");
					} else if (gameState == TIME_OUT) {
						UMSnsService.shareToSina(CupidCannonActivity.this, "我在玩‘魔方石de诱惑’，使用丘比特之炮，展现了超人的魅力，哇哈哈哈。", null);
					} else if (gameState == NO_BULLET_LEFT){
						UMSnsService.shareToSina(CupidCannonActivity.this, "我在玩‘魔方石de诱惑’，使用丘比特之炮，展现了超人的魅力，哇哈哈哈。", null);
					}
				} else if (onClickButton == "button_return") {
					Intent about = new Intent(Intent.ACTION_MAIN);
					about.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					about.setClassName("com.cube.attract", "com.cube.attract.gameEntry.GameEntryActivity");
					mContext.startActivity(about);
					mActivity.finish();

				} else if (onClickButton == "againChallenge") {
					animView.againChallenge();

				}

			}
		});
	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		Log.v(TAG, "Run in onResume");
		if (isShared == false){
			animView.againChallenge();
		}else{
			isShared = true;
			Intent gameEntry = new Intent(Intent.ACTION_MAIN);
			gameEntry.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			gameEntry.setClassName("com.cube.attract", "com.cube.attract.gameEntry.GameEntryActivity");
			mContext.startActivity(gameEntry);
			((Activity) mContext).finish();
		}
		
//		animView.initGirlBitmaps();
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
		Log.v(TAG, "Run in onPause");

	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		Log.v("TAG", "Run in onRestart");

	}

	public void hideImage() {
		shareSina.startAnimation(torightAnimation);
		shareSina.setVisibility(8);
		againChallenge.startAnimation(toleftAnimation);
		againChallenge.setVisibility(8);
		button_return.startAnimation(toleftAnimation);
		button_return.setVisibility(8);
	}

	public void showImage() {
		shareSina.startAnimation(fromrightAnimation);
		shareSina.setVisibility(0);
		againChallenge.startAnimation(fromleftAnimation);
		againChallenge.setVisibility(0);
		button_return.startAnimation(fromleftAnimation);
		button_return.setVisibility(0);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			new AlertDialog.Builder(this).setIcon(R.drawable.cupid).setTitle(R.string.app_name).setMessage("真的要走吗，亲！").setNegativeButton("取消", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			}).setPositiveButton("确定", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					Intent gameEntry = new Intent(Intent.ACTION_MAIN);
					gameEntry.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					gameEntry.setClassName("com.cube.attract", "com.cube.attract.gameEntry.GameEntryActivity");
					mContext.startActivity(gameEntry);
					finish();
				}
			}).show();

			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

}

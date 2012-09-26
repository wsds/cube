package com.cube.attract.game.mosquito;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.cube.common.LocalData.Game.ActiveGirl;
import com.cube.common.ServerData.Girl.Picture;
import com.cube.common.imageservice.BitmapPool;
import com.umeng.analytics.MobclickAgent;
import com.umeng.api.exp.UMSNSException;
import com.umeng.api.sns.UMSnsService;
import com.umeng.api.sns.UMSnsService.DataSendCallbackListener;
import com.umeng.api.sns.UMSnsService.RETURN_STATUS;

public class MosquitoActivity extends Activity {

	private static final String TAG = "MosquitoActivity";
	AnimView animView = null;
	Activity mActivity;;
	Context mContext;
	RelativeLayout canvasContainer = null;
	ImageView shareSina, againChallenge, button_return = null;
	Animation toleftAnimation = null;
	Animation torightAnimation = null;
	Animation fromleftAnimation = null;
	Animation fromrightAnimation = null;
	public String onClickButton = "null";
	public String isShared = "None";

	public ArrayList<Bitmap> girlBitmaps = new ArrayList<Bitmap>();
	public LocalData localData = LocalData.getInstance();
	public SceneState sceneState = SceneState.getInstance();
	public BitmapPool bitmapPool = BitmapPool.getInstance();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		mActivity = this;
		setContentView(R.layout.game);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		Log.v(TAG, "MosquitoActivity");
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		Intent intent = getIntent();
		sceneState.girlsSize = localData.game.activeGirls.size();
		sceneState.weibo = intent.getStringExtra("weibo");
		sceneState.girlNumber = intent.getIntExtra("girlNumber", -1);
		sceneState.girlID = intent.getLongExtra("girlID", -1);

		animView = new AnimView(this);
		canvasContainer = (RelativeLayout) findViewById(R.id.CanvasContainer);
		canvasContainer.addView(animView, 0);
		animView.getHolder().setFormat(PixelFormat.TRANSPARENT);

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

					DataSendCallbackListener listener = new DataSendCallbackListener() {
						@Override
						public void onDataSendFailedWithException(UMSNSException exception, UMSnsService.SHARE_TO userPlatform) {
							if (isShared == "None") {
								isShared = "false";
							}
						}

						@Override
						public void onDataSendFinished(RETURN_STATUS returnStatus, UMSnsService.SHARE_TO userPlatform) {
							switch (returnStatus) {
							case UPDATED:
								if (isShared == "None") {
									isShared = "true";
								}
								Log.i("Log", "Success!");
								break;
							case REPEATED:
								if (isShared == "None") {
									isShared = "false";
								}
								Log.i("Log", "Repeated!");
								break;
							default:
								if (isShared == "None") {
									isShared = "false";
								}
								break;
							}
						}
					};
					
					ConnectivityManager cwjManager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE); 
					NetworkInfo info = cwjManager.getActiveNetworkInfo();
					String typeName = "none";
					if (info != null)
						typeName = info.getTypeName().toLowerCase(); // WIFI/MOBILE
					if (typeName.equals("wifi")){
						ActiveGirl activegirl = localData.game.activeGirls.get(sceneState.girlNumber);
						Picture pictures = activegirl.girl.pictures.get(2);
						String url = pictures.url;
						String filename = url.substring(url.lastIndexOf("/") + 1);
						Bitmap sharedBm = bitmapPool.get(filename);
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						sharedBm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
						byte[] picture = baos.toByteArray();
						UMSnsService.shareToSina(MosquitoActivity.this, picture, "我在玩@魔方石诱惑 ，使用激光大炮，获得了美女" + sceneState.weibo + " 的芳心，成功搭讪，展现了超人的魅力，哇哈哈哈。http://cubeservice.sinaapp.com/attract/", listener);
					} else {
						UMSnsService.shareToSina(MosquitoActivity.this, "我在玩@魔方石诱惑 ，使用激光大炮，获得了美女" + sceneState.weibo + " 的芳心，成功搭讪，展现了超人的魅力，哇哈哈哈。http://cubeservice.sinaapp.com/attract/", listener);
					}
				} else if (onClickButton == "button_return") {
					Intent gameEntry = new Intent(Intent.ACTION_MAIN);
					gameEntry.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					gameEntry.setClassName("com.cube.attract", "com.cube.attract.gameEntry.GameEntryActivity");
					mContext.startActivity(gameEntry);
					mActivity.finish();
				} else if (onClickButton == "againChallenge") {
					animView.gameEnded = false;
					animView.next();
				}

			}
		});

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
		Log.v(TAG, "showImage");
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
			if (animView.gameEnded == true) {
				Intent gameEntry = new Intent(Intent.ACTION_MAIN);
				gameEntry.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				gameEntry.setClassName("com.cube.attract", "com.cube.attract.gameEntry.GameEntryActivity");
				mContext.startActivity(gameEntry);
				finish();
				return true;
			} else {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);

				builder.setIcon(R.drawable.cupid);

				builder.setTitle("再点击一次退出");

				builder.setMessage("真的要走吗，亲？");

				builder.setPositiveButton("返回", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int whichButton) {
						Intent gameEntry = new Intent(Intent.ACTION_MAIN);
						gameEntry.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						gameEntry.setClassName("com.cube.attract", "com.cube.attract.gameEntry.GameEntryActivity");
						mContext.startActivity(gameEntry);
						finish();
					}
				});

				builder.setNeutralButton("继续", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int whichButton) {
					}
				});

				builder.setNegativeButton("重试", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int whichButton) {
						animView.next();
					}

				});

				builder.setOnCancelListener(new OnCancelListener() {

					@Override
					public void onCancel(DialogInterface arg0) {
						Intent gameEntry = new Intent(Intent.ACTION_MAIN);
						gameEntry.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						gameEntry.setClassName("com.cube.attract", "com.cube.attract.gameEntry.GameEntryActivity");
						mContext.startActivity(gameEntry);
						finish();
					}

				});
				builder.create().show();
				return true;
			}

		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		Log.v(TAG, "Run in onResume");
		if (isShared == "None") {

		} else if (isShared == "false") {
			isShared = "None";
		} else if (isShared == "true") {
			isShared = "None";
			Intent gameEntry = new Intent(Intent.ACTION_MAIN);
			gameEntry.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			gameEntry.setClassName("com.cube.attract", "com.cube.attract.gameEntry.GameEntryActivity");
			mContext.startActivity(gameEntry);
			((Activity) mContext).finish();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		System.gc();
	}

}

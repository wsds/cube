package com.cube.attract.game.cupidcannon;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.cube.attract.R;
import com.cube.canvas.common.CanvasAnimation;
import com.cube.common.LocalData;
import com.cube.common.LocalData.Game.ActiveGirl;
import com.cube.common.ServerData.Girl.Picture;
import com.cube.common.imageservice.BitmapPool;
import com.cube.opengl.common.Utils;
import com.umeng.analytics.MobclickAgent;

@SuppressLint({ "FloatMath", "FloatMath", "FloatMath", "FloatMath", "FloatMath", "FloatMath" })
public class AnimView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

	private static final String TAG = "CupidCannonAnimView";

	Context mContext = null;
	CupidCannonActivity cupidCannonActivity = null;
	private int mWidth = 0;
	private int mHeight = 0;
	private SurfaceHolder mHolder = null;
	private Thread mThread = null;

	private boolean isRunning = true;
	private CanvasAnimation girlAnim = null;
	private CanvasAnimation hintAnim = null;
	private CanvasAnimation heartAnim = null;
	private CanvasAnimation bulletAnim = null;
	private CanvasAnimation boomAnim = null;
	private CanvasAnimation artilleryAnimOdd = null;
	private CanvasAnimation artilleryAnimEven = null;
	private CanvasAnimation batteryAnimOdd = null;
	private CanvasAnimation batteryAnimEven = null;
	public BitmapPool bitmapPool = BitmapPool.getInstance();
	public LocalData localData = LocalData.getInstance();
	public SceneState sceneState = SceneState.getInstance();

	public Bitmap memBm = null;
	private Canvas mCanvas = null;
	public Bitmap initBackgroundBm = null;
	// public Bitmap backgroundBm = null;

	public Bitmap hintLeftUpBm = null;
	public Bitmap hintLeftDownBm = null;
	public Bitmap hintRightUpBm = null;
	public Bitmap hintRightDownBm = null;
	public ArrayList<Bitmap> girlBitmaps = new ArrayList<Bitmap>();

	public Bitmap powerTube1 = null;
	public Bitmap powerTube2 = null;
	public Bitmap powerTube3 = null;
	public Bitmap heartBm = null;
	public Bitmap bulletBm = null;
	public Bitmap bulletTrackBm = null;
	public Bitmap boomBm = null;
	public Bitmap popMaskBm = null;

	public float[] powerTubeBaseAdress = { 0.0f, 0.0f };
	public final int POWERSENSITY = 10;
	public final int RADIUS = 80;
	public float[] rotateCenter = { 0.0f, 0.0f };
	public float[] targetCenter = { 0.0f, 0.0f };
	public float[] lastTargetCenter = { 0.0f, 0.0f };
	public float[] boomCenter = { 0.0f, 0.0f };
	public int[] backgroundStagePosition = { 0, 0 };
	
	public float targetTranslateDistance = 0.0f;
	public float remainTranslateDistance = 0.0f;
	public float[] targetNewStartPosition = {0.0f, 0.0f};	
	public boolean targetIsInScreen = false;
	public boolean targetIsMoving = false;
	public double[] targetMoveDirection = {0.0f, 0.0f};
	public boolean insecuritySpaceUp = false;
	public boolean insecuritySpaceDown = false;
	public boolean insecuritySpaceLeft = false;
	public boolean insecuritySpaceRight = false;
	
	private static final int LEFTUP = 0;
	private static final int LEFTDOWN = 1;
	private static final int RIGHTUP = 2;
	private static final int RIGHTDOWN = 3;
	private int targetLocationState = 0;
	
	public Bitmap backgroundStage = null;
	public int backgroundStageWidth = 0;
	public int backgroundStageHeight = 0;
	public Matrix testMatrix = new Matrix();
	public float[] testMatrixArray;

	public AnimView(Context context) {
		super(context);
		mHolder = this.getHolder();
		mHolder.addCallback(this);
		mContext = context;
		cupidCannonActivity = (CupidCannonActivity) context;

		initBackgroundBm = BitmapFactory.decodeResource(getResources(), R.drawable.welcome_background);

		backgroundStage = BitmapFactory.decodeResource(getResources(), R.drawable.button_bar);

		hintLeftUpBm = BitmapFactory.decodeResource(getResources(), R.drawable.hint_left_up);
		hintLeftDownBm = BitmapFactory.decodeResource(getResources(), R.drawable.hint_left_down);
		hintRightUpBm = BitmapFactory.decodeResource(getResources(), R.drawable.hint_right_up);
		hintRightDownBm = BitmapFactory.decodeResource(getResources(), R.drawable.hint_right_down);
		
		powerTube1 = BitmapFactory.decodeResource(getResources(), R.drawable.blue_part1);
		powerTube2 = BitmapFactory.decodeResource(getResources(), R.drawable.blue_part2);
		powerTube3 = BitmapFactory.decodeResource(getResources(), R.drawable.blue_part3);
		heartBm = BitmapFactory.decodeResource(getResources(), R.drawable.heart_3_s);
		bulletBm = BitmapFactory.decodeResource(getResources(), R.drawable.bullet);
		boomBm = BitmapFactory.decodeResource(getResources(), R.drawable.blast_f09);
		popMaskBm = BitmapFactory.decodeResource(getResources(), R.drawable.popmask);
		
		numbersBm[0] = BitmapFactory.decodeResource(getResources(), R.drawable.little_w_num_0);
		numbersBm[1] = BitmapFactory.decodeResource(getResources(), R.drawable.little_w_num_1);
		numbersBm[2] = BitmapFactory.decodeResource(getResources(), R.drawable.little_w_num_2);
		numbersBm[3] = BitmapFactory.decodeResource(getResources(), R.drawable.little_w_num_3);
		numbersBm[4] = BitmapFactory.decodeResource(getResources(), R.drawable.little_w_num_4);
		numbersBm[5] = BitmapFactory.decodeResource(getResources(), R.drawable.little_w_num_5);
		numbersBm[6] = BitmapFactory.decodeResource(getResources(), R.drawable.little_w_num_6);
		numbersBm[7] = BitmapFactory.decodeResource(getResources(), R.drawable.little_w_num_7);
		numbersBm[8] = BitmapFactory.decodeResource(getResources(), R.drawable.little_w_num_8);
		numbersBm[9] = BitmapFactory.decodeResource(getResources(), R.drawable.little_w_num_9);

		redNumbersBm[0] = BitmapFactory.decodeResource(getResources(), R.drawable.little_r_num_0);
		redNumbersBm[1] = BitmapFactory.decodeResource(getResources(), R.drawable.little_r_num_1);
		redNumbersBm[2] = BitmapFactory.decodeResource(getResources(), R.drawable.little_r_num_2);
		redNumbersBm[3] = BitmapFactory.decodeResource(getResources(), R.drawable.little_r_num_3);
		redNumbersBm[4] = BitmapFactory.decodeResource(getResources(), R.drawable.little_r_num_4);
		redNumbersBm[5] = BitmapFactory.decodeResource(getResources(), R.drawable.little_r_num_5);
		redNumbersBm[6] = BitmapFactory.decodeResource(getResources(), R.drawable.little_r_num_6);
		redNumbersBm[7] = BitmapFactory.decodeResource(getResources(), R.drawable.little_r_num_7);
		redNumbersBm[8] = BitmapFactory.decodeResource(getResources(), R.drawable.little_r_num_8);
		redNumbersBm[9] = BitmapFactory.decodeResource(getResources(), R.drawable.little_r_num_9);
	}

	void initGirlBitmaps() {
		ActiveGirl activegirl = localData.game.activeGirls.get(sceneState.girlNumber);
		for (Bitmap bitmap : girlBitmaps) {
			bitmap.recycle();
		}
		girlBitmaps.clear();
		for (int i = 1; i <= 3; i++) {
			Picture pictures = activegirl.girl.pictures.get(i);
			String url = pictures.url;
			String filename = url.substring(url.lastIndexOf("/") + 1);
			Bitmap bitmap = null;

			if (!localData.game.loadedPictures.contains(filename)) {

			} else {
				bitmap = bitmapPool.get(filename);
				Log.v(TAG, "texture is loaded: " + filename);
			}
			if (bitmap == null) {
				bitmap = Utils.getTextureFromBitmapResource(mContext, R.drawable.heart_1_s);
			}
			girlBitmaps.add(bitmap);
		}

		Picture picture = activegirl.girl.pictures.get(1);

		sceneState.x1 = picture.points.get(0).x;
		sceneState.y1 = picture.points.get(0).y;
		sceneState.x2 = picture.points.get(1).x;
		sceneState.y2 = picture.points.get(1).y;

	}

	private void initAnimationInstance() {

		Matrix initMatrix = new Matrix();

		girlAnim = new CanvasAnimation();
		girlAnim.setElements(girlBitmaps.get(0), new Paint());
		initMatrix.setTranslate(0, 0);
		girlAnim.setStartMatrix(initMatrix);
		girlAnim.setTranslate(0, 0, 0);
		girlAnim.setRepeatTimes(1);
//		initMatrix.setTranslate(sceneState.x2, sceneState.y2);
//		girlAnim.setTraceMatrix(initMatrix);
		girlAnim.start(true);

		heartAnim = new CanvasAnimation();
		heartAnim.setElements(heartBm, new Paint());
		targetCenter[0] = sceneState.x1;
		targetCenter[1] = sceneState.y1;
		int heartBmWidth = heartBm.getWidth();
		int heartBmHeight = heartBm.getHeight();
		initMatrix.setTranslate(targetCenter[0] - heartBmWidth / 2, targetCenter[1] - heartBmHeight / 2);
		lastTargetCenter[0] = targetCenter[0];
		lastTargetCenter[1] = targetCenter[1];
		heartAnim.setStartMatrix(initMatrix);
		heartAnim.setTranslate(0, 0, 0);
		heartAnim.setRepeatTimes(1);
		heartAnim.start(true);

		artilleryAnimOdd = new CanvasAnimation();
		artilleryAnimOdd.setCallback(new CanvasAnimation.Callback() {

			@Override
			public void onEnd() {

				artilleryAnimEven.start(true);
				artilleryAnimOdd.start(false);

			}
		});
		artilleryAnimOdd.setElements(BitmapFactory.decodeResource(getResources(), R.drawable.artillery), new Paint());
		initMatrix.setTranslate((mWidth - artilleryAnimOdd.mAnimBitmapWidth) / 2, mHeight + 5 - artilleryAnimOdd.mAnimBitmapHeight + 20);
		initMatrix.postRotate(-90, rotateCenter[0], rotateCenter[1]);
		artilleryAnimOdd.setStartMatrix(initMatrix);
		// configure the artillery even trace matrix
		initMatrix.setTranslate(207, 650);
		initMatrix.postRotate(-90, rotateCenter[0], rotateCenter[1]);
		artilleryAnimOdd.setTraceMatrix(initMatrix);

		artilleryAnimOdd.setRotate(180, rotateCenter[0], rotateCenter[1], 2000);
		artilleryAnimOdd.setRepeatTimes(artilleryAnimOdd.INFINITE);
		artilleryAnimOdd.start(true);

		artilleryAnimEven = new CanvasAnimation();
		artilleryAnimEven.setElements(BitmapFactory.decodeResource(getResources(), R.drawable.artillery), new Paint());
		artilleryAnimEven.setCallback(new CanvasAnimation.Callback() {

			@Override
			public void onEnd() {

				artilleryAnimOdd.start(true);
				artilleryAnimEven.start(false);
			}
		});
		initMatrix.setTranslate((mWidth - artilleryAnimEven.mAnimBitmapWidth) / 2, mHeight + 5 - artilleryAnimEven.mAnimBitmapHeight + 20);
		initMatrix.postRotate(90, rotateCenter[0], rotateCenter[1]);
		artilleryAnimEven.setStartMatrix(initMatrix);
		// configure the artillery even trace matrix
		initMatrix.setTranslate(207, 650);
		initMatrix.postRotate(90, rotateCenter[0], rotateCenter[1]);
		artilleryAnimEven.setTraceMatrix(initMatrix);

		artilleryAnimEven.setRotate(-180, rotateCenter[0], rotateCenter[1], 2000);
		artilleryAnimEven.setRepeatTimes(artilleryAnimEven.INFINITE);
		artilleryAnimEven.start(false);

		batteryAnimOdd = new CanvasAnimation();
		batteryAnimOdd.setElements(BitmapFactory.decodeResource(getResources(), R.drawable.battery), new Paint());
		batteryAnimOdd.setCallback(new CanvasAnimation.Callback() {

			@Override
			public void onEnd() {

				batteryAnimEven.start(true);
				batteryAnimOdd.start(false);
			}
		});
		initMatrix.setTranslate(mWidth / 2 + 4 - batteryAnimOdd.mAnimBitmapWidth / 2, mHeight + 5 - batteryAnimOdd.mAnimBitmapHeight / 2);
		initMatrix.postRotate(-90, rotateCenter[0], rotateCenter[1]);
		batteryAnimOdd.setStartMatrix(initMatrix);
		// configure the battery odd trace matrix
		initMatrix.setTranslate(rotateCenter[0], rotateCenter[1]-133);
		initMatrix.postRotate(-90, rotateCenter[0], rotateCenter[1]);
		batteryAnimOdd.setTraceMatrix(initMatrix);

		batteryAnimOdd.setRotate(180, rotateCenter[0], rotateCenter[1], 2000);
		batteryAnimOdd.setRepeatTimes(batteryAnimOdd.INFINITE);
		batteryAnimOdd.start(true);

		batteryAnimEven = new CanvasAnimation();
		batteryAnimEven.setElements(BitmapFactory.decodeResource(getResources(), R.drawable.battery), new Paint());
		batteryAnimEven.setCallback(new CanvasAnimation.Callback() {

			@Override
			public void onEnd() {

				batteryAnimOdd.start(true);
				batteryAnimEven.start(false);
			}
		});
		initMatrix.setTranslate(mWidth / 2 + 4 - batteryAnimEven.mAnimBitmapWidth / 2, mHeight + 5 - batteryAnimEven.mAnimBitmapHeight / 2);
		initMatrix.postRotate(90, rotateCenter[0], rotateCenter[1]);
		batteryAnimEven.setStartMatrix(initMatrix);
		// configure the battery even trace matrix
		initMatrix.setTranslate(rotateCenter[0], rotateCenter[1]-133);
		initMatrix.postRotate(90, rotateCenter[0], rotateCenter[1]);
		batteryAnimEven.setTraceMatrix(initMatrix);

		batteryAnimEven.setRotate(-180, rotateCenter[0], rotateCenter[1], 2000);
		batteryAnimEven.setRepeatTimes(batteryAnimEven.INFINITE);
		batteryAnimEven.start(false);

	}

	SoundPool soundPool = null;
	int bombSound = 0;

	private void initSound() {
		soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 100);
		bombSound = soundPool.load(mContext, R.raw.bomb, 1);
	}

	private void initConflictData(){
		targetTranslateDistance = 5 * mWidth / 2;
		remainTranslateDistance = targetTranslateDistance;
		if ((sceneState.x2>= heartAnim.mAnimBitmapWidth/2+5)&&(sceneState.x2<=mWidth-heartAnim.mAnimBitmapWidth/2-5)
				&&(sceneState.y2<=mHeight-200-5)&&(sceneState.x2>= heartAnim.mAnimBitmapHeight/2+5))
			targetIsInScreen = true;
		else
			targetIsInScreen = false;
		targetNewStartPosition[0] = sceneState.x2;
		targetNewStartPosition[1] = sceneState.y2;	
		
		insecuritySpaceUp = false;
		insecuritySpaceDown = false;
		insecuritySpaceLeft = false;
		insecuritySpaceRight = false;
	}
	
	private void initBulletCounter(){
		bulletCounter = 50;
		bulletCounterBm[0] = numbersBm[5];
		bulletCounterBm[1] = numbersBm[0];
	}
	
	private void targetLocationStateJudge(){
		if (targetCenter[0] < mWidth/2){
			if (targetCenter[1] < mHeight/2){
				targetLocationState = LEFTUP;
			}else {
				targetLocationState = LEFTDOWN;
			}
		}else {
			if (targetCenter[1] < mHeight/2){
				targetLocationState = RIGHTUP;
			}else {
				targetLocationState = RIGHTDOWN;
			}
		}
	}
	private void drawBackground() {
		Matrix testMatrix = new Matrix();
		testMatrix.setTranslate(backgroundStagePosition[0], backgroundStagePosition[1]);
		mCanvas.drawBitmap(backgroundStage, testMatrix, new Paint());
		if (powerTubeEnable) {
			int tubeLength = (int) moveLength / POWERSENSITY;
			if (tubeLength > 32)
				tubeLength = 32;
			testMatrix.setTranslate(powerTubeBaseAdress[0], powerTubeBaseAdress[1]);
			mCanvas.drawBitmap(powerTube1, testMatrix, new Paint());
			for (int i = 1; i <= tubeLength; i++) {
				testMatrix.setTranslate(powerTubeBaseAdress[0], powerTubeBaseAdress[1] - i * 7);
				mCanvas.drawBitmap(powerTube2, testMatrix, new Paint());
			}
			testMatrix.setTranslate(powerTubeBaseAdress[0], powerTubeBaseAdress[1] - tubeLength * 7 - 14);
			mCanvas.drawBitmap(powerTube3, testMatrix, new Paint());
		}
		
		//Draw bulletCounter
		testMatrix.setScale(1.0f, 1.0f);
		testMatrix.postTranslate((float) (backgroundStagePosition[0] + 0.22 * backgroundStageWidth), (float) (backgroundStagePosition[1] + 0.9 * backgroundStageHeight));				
		if (bulletCounterBm[0] != null)
			mCanvas.drawBitmap(bulletCounterBm[0], testMatrix, new Paint());
		testMatrix.postTranslate(10, 0);
		if (bulletCounterBm[1] != null)
			mCanvas.drawBitmap(bulletCounterBm[1], testMatrix, new Paint());
		
		// Draw timer
		testMatrix.setScale(1.0f, 1.0f);
		testMatrix.postTranslate((float) (backgroundStagePosition[0] + 0.85 * backgroundStageWidth), (float) (backgroundStagePosition[1] + 0.9 * backgroundStageHeight));
		if (timerBm[0] != null)
			mCanvas.drawBitmap(timerBm[0], testMatrix, new Paint());
		testMatrix.postTranslate(20, 0);
		if (timerBm[1] != null)
			mCanvas.drawBitmap(timerBm[1], testMatrix, new Paint());	
		
//		bulletTrackBm = createBulletTrackBitmap(bulletTrackBm, 100, 100);
//		mCanvas.drawBitmap(bulletTrackBm, 0, 0, new Paint());
	}

	public int achievedCounter = -1;
	public Matrix reconfigureMatrix = new Matrix();

	private int reconfigureAnimationInstance() {
		
		if (achieved == false)
			return 0;
		achievedCounter++;
		int flag = achievedCounter % 2;
		switch (flag) {
		case 0:
			girlAnim.setElements(girlBitmaps.get(1), new Paint());
			reconfigureMatrix.setTranslate(0, 0);
			girlAnim.setStartMatrix(reconfigureMatrix);
			girlAnim.setTranslate(0, 0, 1000);
			girlAnim.setRepeatTimes(1);
			reconfigureMatrix.setTranslate(sceneState.x2, sceneState.y2);
			girlAnim.setTraceMatrix(reconfigureMatrix);
			girlAnim.start(true);
			girlAnim.setCallback(new CanvasAnimation.Callback() {

				@Override
				public void onEnd() {
					targetIsMoving = true;
					lastTargetCenter[0] = targetCenter[0];
					lastTargetCenter[1] = targetCenter[1];
					targetCenter[0] = sceneState.x2;
					targetCenter[1] = sceneState.y2;
					girlAnim.setElements(girlBitmaps.get(1), new Paint());
					float[] array = { 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f };
					girlAnim.transformMatrix.getValues(array);
					reconfigureMatrix.setValues(array);
					girlAnim.setStartMatrix(reconfigureMatrix);
					
					double vectorLength = Math.sqrt((0.5f*mWidth-sceneState.x2)*(0.5f*mWidth-sceneState.x2)
							+ (0.5f*(mHeight-200)-sceneState.y2)*(0.5f*(mHeight-200)-sceneState.y2));
					targetMoveDirection[0] = (0.5f*mWidth-sceneState.x2)/vectorLength;
					targetMoveDirection[1] = (0.5f*(mHeight-200)-sceneState.y2)/vectorLength;
					double vectorX = targetTranslateDistance*targetMoveDirection[0];
					double vectorY = targetTranslateDistance*targetMoveDirection[1];	
					girlAnim.setTranslate((int)vectorX, (int)vectorY, 1000);
					
					girlAnim.setRepeatTimes(1);
					girlAnim.start(true);
					girlAnim.setCallback(new CanvasAnimation.Callback() {

						@Override
						public void onEnd() {
							targetIsMoving = false;							
//							targetCenter[0] = sceneState.x2-200;
//							targetCenter[1] = sceneState.y2-400;
							girlAnim.setCallback(null);
						}
					});
				}
			});
			Paint paint = new Paint();
			paint.setAlpha(0x00);
			heartAnim.setElements(heartBm, paint);
			float[] array1 = { 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f };
			heartAnim.transformMatrix.getValues(array1);
			reconfigureMatrix.setValues(array1);
			heartAnim.setStartMatrix(reconfigureMatrix);
			heartAnim.setTranslate(0, 0, 3200);
			heartAnim.setRepeatTimes(1);
			heartAnim.start(true);
			heartAnim.setCallback(new CanvasAnimation.Callback() {

				@Override
				public void onEnd() {
					float[] array = { 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f };
					heartAnim.transformMatrix.getValues(array);
					reconfigureMatrix.setValues(array);
					heartAnim.setStartMatrix(reconfigureMatrix);
					heartAnim.setElements(heartBm, new Paint());
					heartAnim.setTranslate(targetCenter[0] - lastTargetCenter[0], targetCenter[1] - lastTargetCenter[1], 1000);
					heartAnim.setRepeatTimes(1);
					heartAnim.start(true);
					heartAnim.setCallback(null);
					targetLocationStateJudge();

				}
			});
			break;
		case 1:
			girlAnim.setElements(girlBitmaps.get(2), new Paint());
			float[] array = { 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f };
			girlAnim.transformMatrix.getValues(array);
			reconfigureMatrix.setValues(array);
			girlAnim.setStartMatrix(reconfigureMatrix);
			girlAnim.setTranslate(0, 0, 1000);
			girlAnim.setRepeatTimes(1);
			girlAnim.start(true);
			girlAnim.setCallback(new CanvasAnimation.Callback() {

				@Override
				public void onEnd() {
					float[] array = { 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f };
					girlAnim.transformMatrix.getValues(array);
					reconfigureMatrix.setValues(array);
					girlAnim.setStartMatrix(reconfigureMatrix);
					girlAnim.setTranslate(-100, 100, 500);
					girlAnim.setRepeatTimes(1);
					girlAnim.start(true);
					girlAnim.setCallback(new CanvasAnimation.Callback() {

						@Override
						public void onEnd() {
							float[] array = { 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f };
							girlAnim.transformMatrix.getValues(array);
							reconfigureMatrix.setValues(array);
							girlAnim.setStartMatrix(reconfigureMatrix);
							girlAnim.setTranslate(240, 240, 1200);
							girlAnim.setRepeatTimes(1);
							girlAnim.start(true);
							girlAnim.setCallback(new CanvasAnimation.Callback() {

								@Override
								public void onEnd() {
									float[] array = { 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f };
									girlAnim.transformMatrix.getValues(array);
									reconfigureMatrix.setValues(array);
									girlAnim.setStartMatrix(reconfigureMatrix);
									girlAnim.setTranslate(100, -100, 500);
									girlAnim.setRepeatTimes(1);
									girlAnim.start(true);
									girlAnim.setCallback(new CanvasAnimation.Callback() {

										@Override
										public void onEnd() {
											float[] array = { 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f };
											girlAnim.transformMatrix.getValues(array);
											reconfigureMatrix.setValues(array);
											girlAnim.setStartMatrix(reconfigureMatrix);
											girlAnim.setTranslate(-240, -240, 1200);
											girlAnim.setRepeatTimes(1);
											girlAnim.start(true);
											girlAnim.setCallback(new CanvasAnimation.Callback() {

												@Override
												public void onEnd() {
													gameEnded = true;
													girlAnim.setElements(girlBitmaps.get(0), new Paint());
													reconfigureMatrix.reset();
													girlAnim.setStartMatrix(reconfigureMatrix);
													girlAnim.setTranslate(0, 0, 1000);
													girlAnim.setRepeatTimes(1);
													girlAnim.start(true);
													girlAnim.setCallback(null);
												}
											});

										}
									});
								}
							});
						}
					});
				}
			});
			Paint paint1 = new Paint();
			paint1.setAlpha(0x00);
			heartAnim.setElements(heartBm, paint1);
			float[] array2 = { 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f };
			heartAnim.transformMatrix.getValues(array2);
			reconfigureMatrix.setValues(array2);
			heartAnim.setStartMatrix(reconfigureMatrix);
			heartAnim.setTranslate(0, 0, 6000);
			heartAnim.setRepeatTimes(1);
			heartAnim.start(true);
			heartAnim.setCallback(new CanvasAnimation.Callback() {

				@Override
				public void onEnd() {
					heartAnim.setElements(heartBm, new Paint());
					float[] array = { 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f };
					heartAnim.transformMatrix.getValues(array);
					reconfigureMatrix.setValues(array);
					lastTargetCenter[0] = targetCenter[0];
					lastTargetCenter[1] = targetCenter[1];
					targetCenter[0] = sceneState.x1;
					targetCenter[1] = sceneState.y1;
					reconfigureMatrix.setTranslate(targetCenter[0] - heartBm.getWidth() / 2, targetCenter[1] - heartBm.getHeight() / 2);
					heartAnim.setStartMatrix(reconfigureMatrix);
					heartAnim.setTranslate(0, 0, 20);
					heartAnim.setRepeatTimes(1);
					heartAnim.start(true);
					heartAnim.setCallback(null);
					targetLocationStateJudge();
				}
			});

			break;

		default:
			break;
		}
		achieved = false;
		return 0;
	}

	private void targetBorderConflictProbe(){
		if (targetIsMoving == false)
			return;
		float[] array = { 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f };
		girlAnim.traceMatrix.getValues(array);
		targetCenter[0] = array[2]; 
		targetCenter[1] = array[5]; 
		Log.v(TAG, "The target center is moving"+array[2]);
		
		if (targetIsInScreen == true){
			if (targetCenter[0] < heartAnim.mAnimBitmapWidth/2){
//				targetIsInScreen = false;
				if (insecuritySpaceLeft == false) {
					insecuritySpaceLeft = true;
					Log.v(TAG,
							"targetCenter[0] < heartAnim.mAnimBitmapWidth/2 "
									+ heartAnim.mAnimBitmapWidth / 2);
					remainTranslateDistance = targetTranslateDistance
							- (float) Math
									.sqrt((targetCenter[0] - targetNewStartPosition[0])
											* (targetCenter[0] - targetNewStartPosition[0])
											+ (targetCenter[1] - targetNewStartPosition[1])
											* (targetCenter[1] - targetNewStartPosition[1]));
					targetMoveDirection[0] = -targetMoveDirection[0];
					double vectorX = remainTranslateDistance
							* targetMoveDirection[0];
					double vectorY = remainTranslateDistance
							* targetMoveDirection[1];
					girlAnim.setTranslate((int) vectorX, (int) vectorY, 1000
							* remainTranslateDistance / targetTranslateDistance);
					targetNewStartPosition[0] = targetCenter[0];
					targetNewStartPosition[1] = targetCenter[1];
					// Log.v(TAG, "targetCenter[0] = "+targetCenter[0]);
					// Log.v(TAG, "targetCenter[1] = "+targetCenter[1]);
					// Log.v(TAG,
					// "remainTranslateDistance = "+remainTranslateDistance);
					// Log.v(TAG,
					// "targetMoveDirection[0] = "+targetMoveDirection[0]);
					// Log.v(TAG,
					// "targetMoveDirection[1] = "+targetMoveDirection[1]);
					// Log.v(TAG, "vectorX = "+vectorX);
					// Log.v(TAG, "vectorY = "+vectorY);
				}
				
			}else if (targetCenter[0] > mWidth-heartAnim.mAnimBitmapWidth/2){
//				targetIsInScreen = false;
				if (insecuritySpaceRight == false) {
					insecuritySpaceRight = true;
					Log.v(TAG,
							"targetCenter[0] > mWidth-heartAnim.mAnimBitmapWidth/2 "
									+ (mWidth - heartAnim.mAnimBitmapWidth / 2));
					remainTranslateDistance = targetTranslateDistance
							- (float) Math
									.sqrt((targetCenter[0] - targetNewStartPosition[0])
											* (targetCenter[0] - targetNewStartPosition[0])
											+ (targetCenter[1] - targetNewStartPosition[1])
											* (targetCenter[1] - targetNewStartPosition[1]));
					targetMoveDirection[0] = -targetMoveDirection[0];
					double vectorX = remainTranslateDistance
							* targetMoveDirection[0];
					double vectorY = remainTranslateDistance
							* targetMoveDirection[1];
					girlAnim.setTranslate((int) vectorX, (int) vectorY, 1000
							* remainTranslateDistance / targetTranslateDistance);
					targetNewStartPosition[0] = targetCenter[0];
					targetNewStartPosition[1] = targetCenter[1];
					// Log.v(TAG, "targetCenter[0] = "+targetCenter[0]);
					// Log.v(TAG, "targetCenter[1] = "+targetCenter[1]);
					// Log.v(TAG,
					// "remainTranslateDistance = "+remainTranslateDistance);
					// Log.v(TAG,
					// "targetMoveDirection[0] = "+targetMoveDirection[0]);
					// Log.v(TAG,
					// "targetMoveDirection[1] = "+targetMoveDirection[1]);
					// Log.v(TAG, "vectorX = "+vectorX);
					// Log.v(TAG, "vectorY = "+vectorY);
				}
			}else if (targetCenter[1] < heartAnim.mAnimBitmapHeight/2){
//				targetIsInScreen = false;
				if (insecuritySpaceUp == false) {
					insecuritySpaceUp = true;
					Log.v(TAG,
							"targetCenter[1] < heartAnim.mAnimBitmapHeight/2 "
									+ heartAnim.mAnimBitmapHeight / 2);
					remainTranslateDistance = targetTranslateDistance
							- (float) Math
									.sqrt((targetCenter[0] - targetNewStartPosition[0])
											* (targetCenter[0] - targetNewStartPosition[0])
											+ (targetCenter[1] - targetNewStartPosition[1])
											* (targetCenter[1] - targetNewStartPosition[1]));
					targetMoveDirection[1] = -targetMoveDirection[1];
					double vectorX = remainTranslateDistance
							* targetMoveDirection[0];
					double vectorY = remainTranslateDistance
							* targetMoveDirection[1];
					girlAnim.setTranslate((int) vectorX, (int) vectorY, 1000
							* remainTranslateDistance / targetTranslateDistance);
					targetNewStartPosition[0] = targetCenter[0];
					targetNewStartPosition[1] = targetCenter[1];
					// Log.v(TAG, "targetCenter[0] = "+targetCenter[0]);
					// Log.v(TAG, "targetCenter[1] = "+targetCenter[1]);
					// Log.v(TAG,
					// "remainTranslateDistance = "+remainTranslateDistance);
					// Log.v(TAG,
					// "targetMoveDirection[0] = "+targetMoveDirection[0]);
					// Log.v(TAG,
					// "targetMoveDirection[1] = "+targetMoveDirection[1]);
					// Log.v(TAG, "vectorX = "+vectorX);
					// Log.v(TAG, "vectorY = "+vectorY);
				}
			}else if (targetCenter[1] > mHeight-200){
				if (insecuritySpaceDown == false) {
					insecuritySpaceDown = true;
					Log.v(TAG, "targetCenter[1] > mHeight-200 "
							+ (mHeight - 200));
					remainTranslateDistance = targetTranslateDistance
							- (float) Math
									.sqrt((targetCenter[0] - targetNewStartPosition[0])
											* (targetCenter[0] - targetNewStartPosition[0])
											+ (targetCenter[1] - targetNewStartPosition[1])
											* (targetCenter[1] - targetNewStartPosition[1]));
					targetMoveDirection[1] = -targetMoveDirection[1];
					double vectorX = remainTranslateDistance
							* targetMoveDirection[0];
					double vectorY = remainTranslateDistance
							* targetMoveDirection[1];
					girlAnim.setTranslate((int) vectorX, (int) vectorY, 1000
							* remainTranslateDistance / targetTranslateDistance);
					targetNewStartPosition[0] = targetCenter[0];
					targetNewStartPosition[1] = targetCenter[1];
					// Log.v(TAG, "targetCenter[0] = "+targetCenter[0]);
					// Log.v(TAG, "targetCenter[1] = "+targetCenter[1]);
					// Log.v(TAG,
					// "remainTranslateDistance = "+remainTranslateDistance);
					// Log.v(TAG,
					// "targetMoveDirection[0] = "+targetMoveDirection[0]);
					// Log.v(TAG,
					// "targetMoveDirection[1] = "+targetMoveDirection[1]);
					// Log.v(TAG, "vectorX = "+vectorX);
					// Log.v(TAG, "vectorY = "+vectorY);
				}
			}
		}else{
			if ((targetCenter[0]>heartAnim.mAnimBitmapWidth/2)&&(targetCenter[0]<mWidth-heartAnim.mAnimBitmapWidth/2)
					&&(targetCenter[1]>heartAnim.mAnimBitmapHeight/2)&&(targetCenter[1]<mHeight-200))
				targetIsInScreen = true;	
		}
		
		if (targetCenter[0]>heartAnim.mAnimBitmapWidth/2+5)
			insecuritySpaceLeft = false;	
		if (targetCenter[0]<mWidth-heartAnim.mAnimBitmapWidth/2-5)
			insecuritySpaceRight = false;
		if (targetCenter[1]>heartAnim.mAnimBitmapHeight/2+5)
			insecuritySpaceUp = false;
		if (targetCenter[1]<mHeight-200-5)
			insecuritySpaceDown = false;
	}
	private void drawAnimationInstance() {
		mCanvas.drawBitmap(initBackgroundBm, 0, 0, new Paint());
		girlAnim.transformModel(mCanvas);
		heartAnim.transformModel(mCanvas);
		drawBackground();
		reconfigureAnimationInstance();
		targetBorderConflictProbe();
		if (hintAnim != null)
			hintAnim.transformModel(mCanvas);
		if (bulletAnim != null)
			bulletAnim.transformModel(mCanvas);

		batteryAnimOdd.transformModel(mCanvas);
		batteryAnimEven.transformModel(mCanvas);
		batteryAnimOdd.transformModel(mCanvas);

		artilleryAnimOdd.transformModel(mCanvas);
		artilleryAnimEven.transformModel(mCanvas);
		artilleryAnimOdd.transformModel(mCanvas);
		if (boomAnim != null)
			boomAnim.transformModel(mCanvas);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mWidth = this.getWidth();
		mHeight = this.getHeight();
		powerTubeBaseAdress[0] = 28;
		powerTubeBaseAdress[1] = mHeight - 30;
		rotateCenter[0] = mWidth / 2 + 3;
		rotateCenter[1] = mHeight + 3;
		memBm = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.RGB_565);
		mCanvas = new Canvas(memBm);
		backgroundStageWidth = backgroundStage.getWidth();
		backgroundStageHeight = backgroundStage.getHeight();
		backgroundStagePosition[0] = -(backgroundStageWidth - mWidth) / 2;
		backgroundStagePosition[1] = mHeight - backgroundStageHeight;
		bulletTrackBm = Bitmap.createBitmap(mWidth, mHeight, Config.ARGB_8888);

		
		initGirlBitmaps();
		initAnimationInstance();
		initSound();
		
		//Initialize some data about target moving.
		initConflictData();
		
		//Initialize bulletCounter
		initBulletCounter();
		
		targetLocationStateJudge();
		
		// Optimize mThread start
		isRunning = true;
		mThread = new Thread(this);// 创建一个绘图线程
		mThread.start();

	}

	// timer
	public long lastSystemTime = 0;
	public long timeCounter = 99000;
	public Bitmap[] numbersBm = { null, null, null, null, null, null, null, null, null, null };
	public Bitmap[] redNumbersBm = { null, null, null, null, null, null, null, null, null, null };
	public Bitmap[] timerBm = { numbersBm[9], numbersBm[9] };

	public void timer() {
//		Log.i(TAG, "Run into timer()");
		int counter = 0;
		int counterReminder = 0;
		long currentTime = 0;
		if (lastSystemTime == 0) {
			lastSystemTime = System.currentTimeMillis();
		} else {
			currentTime = System.currentTimeMillis();
			timeCounter = timeCounter - (currentTime - lastSystemTime);
			lastSystemTime = currentTime;
		}
		if (timeCounter >= 0) {
			counter = (int) (timeCounter / 1000);
			counterReminder = (int)(timeCounter % 1000);
			if(counter>10){
				timerBm[0] = numbersBm[counter / 10];
				timerBm[1] = numbersBm[counter % 10];	
			}else{
				//timer shining
				if (counterReminder>500){
					timerBm[0] = numbersBm[counter / 10];
					timerBm[1] = numbersBm[counter % 10];
				}else{
					timerBm[0] = redNumbersBm[counter / 10];
					timerBm[1] = redNumbersBm[counter % 10];
				}
			}
		} else {
			timerBm[0] = redNumbersBm[0];
			timerBm[1] = redNumbersBm[0];
		}
	}

	public long bulletCounter = 50;
	public Bitmap[] bulletCounterBm = {numbersBm[5], numbersBm[0] };
	
	public void againChallenge() {
		trigger = false;
		achievedCounter = -1;
		gameEnded = false;
		achieved = false;
		initBulletCounter();
		timeCounter = 99000;
		initGirlBitmaps();
		initAnimationInstance();
		initSound();
		initConflictData();		
		targetLocationStateJudge();
		timeCounter = 99000;
		MobclickAgent.onEvent(mContext, "cupidCannonStart");
	}
 
	private Bitmap createHintBitmap(Bitmap src, String str) {
		String tag = "createBitmap";
		Log.d(tag, "create a new bitmap");
		if (src == null) {
			return null;
		}

		int w = src.getWidth();
		int h = src.getHeight();

		// create the new blank bitmap
		Bitmap newb = Bitmap.createBitmap(w, h, Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图
		Canvas cv = new Canvas(newb);
		// draw src into
		cv.drawBitmap(src, 0, 0, null);// 在 0，0坐标开始画入src
		// draw str into
		Paint paint = new Paint();
		paint.setTextSize(30);
		paint.setColor(Color.GREEN);
		cv.drawText(str, w / 7, 4*h / 7, paint);		
		// save all clip
		cv.save(Canvas.ALL_SAVE_FLAG);// 保存
		// store
		cv.restore();// 存储
		return newb;
	}  

	private Bitmap createBulletTrackBitmap(Bitmap src, float x, float y) {
		String tag = "createBitmap";
		Log.d(tag, "create a new bitmap");
		if (src == null) {
			return null;
		}

		int w = src.getWidth();
		int h = src.getHeight();

		// create the new blank bitmap
		Bitmap newb = Bitmap.createBitmap(w, h, Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图
		Canvas cv = new Canvas(newb);
		// draw src into
		cv.drawBitmap(src, 0, 0, null);// 在 0，0坐标开始画入src
		// draw str into
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(Color.WHITE);
		cv.drawCircle(x, y, 10, paint);
		// save all clip
		cv.save(Canvas.ALL_SAVE_FLAG);// 保存
		// store
		cv.restore();// 存储
		return newb;
	} 
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		isRunning = false;

	}

	public final static int WIN = 0;
	public final static int TIME_OUT = 1;
	public final static int NO_BULLET_LEFT = 2;
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			// msg.what为handler接收到的消息编号
			switch (msg.what) {
			case WIN:
				Bundle bundle = msg.getData();
				cupidCannonActivity.gameTime = bundle.getString("gameTime");
				cupidCannonActivity.weibo = bundle.getString("weibo");
				cupidCannonActivity.gameState = bundle.getInt("gameState");
				cupidCannonActivity.showImage();
				Log.v(TAG, "msg.what is " + msg.what);
				break;
			case TIME_OUT:
				cupidCannonActivity.showImage();
				break;
			case NO_BULLET_LEFT:
				cupidCannonActivity.showImage();
				break;
			}
			super.handleMessage(msg);
		}

	};

	@Override
	public void run() {
		while (isRunning) {

			if (achievedCounter < 1)
				timer();
			if (!gameEnded && (timeCounter >= 0)&&(bulletCounter>=0)) {
				drawAnimationInstance();
			} else {				
				Message msg = new Message();
				if (bulletCounter < 0) {
					if (trigger == false) {
						msg.what = NO_BULLET_LEFT;
						Bundle bundle = new Bundle();
						bundle.putInt("gameState", NO_BULLET_LEFT);
						handler.sendMessage(msg);
						MobclickAgent.onEvent(mContext, "cupidGameFailed");
						//Mask
						Paint mPaint = new Paint();
						mPaint.setAlpha(0xaa);
						mCanvas.drawBitmap(popMaskBm, 0, 0, mPaint);
						trigger = true;
					}	
				}else if (timeCounter < 0) {
					if (trigger == false) {
						msg.what = TIME_OUT;
						Bundle bundle = new Bundle();
						bundle.putInt("gameState", TIME_OUT);
						handler.sendMessage(msg);
						MobclickAgent.onEvent(mContext, "cupidGameFailed");
						//Mask
						Paint mPaint = new Paint();
						mPaint.setAlpha(0xaa);
						mCanvas.drawBitmap(popMaskBm, 0, 0, mPaint);
						trigger = true;
					}
				} else if (gameEnded) {
					float achievedTime = (float) (99000 - timeCounter) / 1000.0f;
					Log.i(TAG, "achievedTime is" + achievedTime);
					if (trigger == false) {
						msg.what = WIN;
						Bundle bundle = new Bundle();
						bundle.putString("gameTime", String.valueOf(achievedTime));
						bundle.putString("weibo", sceneState.weibo);
						bundle.putInt("gameState", WIN);
						msg.setData(bundle);
						handler.sendMessage(msg);
						MobclickAgent.onEvent(mContext, "cupidGameWin", String.valueOf(achievedTime));
						//Mask
						Paint mPaint = new Paint();
						mPaint.setAlpha(0xaa);
						mCanvas.drawBitmap(popMaskBm, 0, 0, mPaint);
						trigger = true;
					}
				}
//				Log.i(TAG, "msg.what is" + msg.what);
			}

			try {
				Thread.sleep(3);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Canvas renderer = null;
			synchronized (mHolder) {
				try {
					renderer = mHolder.lockCanvas();
					if (renderer != null) {
						renderer.drawBitmap(memBm, 0, 0, null);
					}
				} finally {
					if (renderer != null)
						mHolder.unlockCanvasAndPost(renderer);
				}
			}

		}
	}

	public boolean trigger = false;
	public boolean gameEnded = false;
	public boolean powerTubeEnable = false;
	public boolean bulletEnable = false;
	public boolean achieved = false;
	public float startX = 0;
	public float startY = 0;
	public double moveLength = 0;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		/** 拿到触摸的状态 **/
		int action = event.getAction();
		float currentX = 0;
		float currentY = 0;
		switch (action) {
		// 触摸按下的事件
		case MotionEvent.ACTION_DOWN:
			Log.v("test", "ACTION_DOWN");
			powerTubeEnable = true;
			startX = event.getX();
			startY = event.getY();
			moveLength = 0.0f;
			break;
		// 触摸移动的事件
		case MotionEvent.ACTION_MOVE:
			Log.v("test", "ACTION_MOVE");
			currentX = event.getX();
			currentY = event.getY();
			if (currentY >= startY)
				moveLength = Math.sqrt((currentX - startX) * (currentX - startX) + (currentY - startY) * (currentY - startY));
			break;
		// 触摸抬起的事件
		case MotionEvent.ACTION_UP:
			Log.v("test", "ACTION_UP");
			currentX = event.getX();
			currentY = event.getY();
			if (currentY >= startY)
				moveLength = Math.sqrt((currentX - startX) * (currentX - startX) + (currentY - startY) * (currentY - startY));
			powerTubeEnable = false;
			bulletEnable = true;
			break;
		}

		if (bulletEnable == true) {
			bulletCounter--;
			if (bulletCounter >=0){
				bulletCounterBm[0] = numbersBm[(int)(bulletCounter/10)];
				bulletCounterBm[1] = numbersBm[(int)(bulletCounter%10)];	
			}else{
				bulletCounter = -1;
				bulletCounterBm[0] = numbersBm[0];
				bulletCounterBm[1] = numbersBm[0];
			}
			Matrix matrix = new Matrix();
			float[] vector = { 0.0f, 0.0f };
			float[] array1 = { 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f };
			float[] array2 = { 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f };

			if (artilleryAnimOdd.isStarted == true) {
				artilleryAnimOdd.traceMatrix.getValues(array1);
				batteryAnimOdd.traceMatrix.getValues(array2);
			} else {
				artilleryAnimEven.traceMatrix.getValues(array1);
				batteryAnimEven.traceMatrix.getValues(array2);
			}

			matrix.setValues(array1);
			vector[0] = array2[2] - rotateCenter[0];
			vector[1] = array2[5] - rotateCenter[1];
			bulletAnim = new CanvasAnimation();
			bulletAnim.setCallback(new CanvasAnimation.Callback() {

				@Override
				public void onEnd() {
					Matrix matrix = new Matrix();
					float[] array = { 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f };
					boomAnim = new CanvasAnimation();
					boomAnim.setCallback(new CanvasAnimation.Callback() {

						@Override
						public void onEnd() {

							hintAnim = new CanvasAnimation();
							double boomCenter2TargetCenter = Math.sqrt((boomCenter[0] - targetCenter[0]) * (boomCenter[0] - targetCenter[0]) + (boomCenter[1] - targetCenter[1]) * (boomCenter[1] - targetCenter[1]));
							double boomCenter2RotateCenter = Math.sqrt((boomCenter[0] - rotateCenter[0]) * (boomCenter[0] - rotateCenter[0]) + (boomCenter[1] - rotateCenter[1]) * (boomCenter[1] - rotateCenter[1]));
							double targetCenter2RotateCenter = Math.sqrt((targetCenter[0] - rotateCenter[0]) * (targetCenter[0] - rotateCenter[0]) + (targetCenter[1] - rotateCenter[1]) * (targetCenter[1] - rotateCenter[1]));
							if (boomCenter2TargetCenter < RADIUS) {
								achieved = true;
								Matrix matrix = new Matrix();
								switch(targetLocationState){
								case LEFTUP:
									hintAnim.setElements(createHintBitmap(hintLeftUpBm, "Hit!!!"), new Paint());
									matrix.setTranslate(targetCenter[0], targetCenter[1]);
									hintAnim.setStartMatrix(matrix);
									hintAnim.setTranslate(0, 0, 1000);
									break;
								case LEFTDOWN:
									hintAnim.setElements(createHintBitmap(hintLeftDownBm, "Hit!!!"), new Paint());
									matrix.setTranslate(targetCenter[0], targetCenter[1]-hintLeftDownBm.getHeight());
									hintAnim.setStartMatrix(matrix);
									hintAnim.setTranslate(0, 0, 1000);
									break;
								case RIGHTUP:
									hintAnim.setElements(createHintBitmap(hintRightUpBm, "Hit!!!"), new Paint());
									matrix.setTranslate(targetCenter[0]-hintRightUpBm.getWidth(), targetCenter[1]);
									hintAnim.setStartMatrix(matrix);
									hintAnim.setTranslate(0, 0, 1000);
									break;
								case RIGHTDOWN:
									hintAnim.setElements(createHintBitmap(hintRightDownBm, "Hit!!!"), new Paint());
									matrix.setTranslate(targetCenter[0]-hintRightDownBm.getWidth(), targetCenter[1]-hintRightDownBm.getHeight());
									hintAnim.setStartMatrix(matrix);
									hintAnim.setTranslate(0, 0, 1000);
									break;
								}
								
							} else if (boomCenter2RotateCenter < targetCenter2RotateCenter - RADIUS) {
								achieved = false;
								Matrix matrix = new Matrix();
								switch(targetLocationState){
								case LEFTUP:
									hintAnim.setElements(createHintBitmap(hintLeftUpBm, "浮云。。。"), new Paint());
									matrix.setTranslate(targetCenter[0], targetCenter[1]);
									hintAnim.setStartMatrix(matrix);
									hintAnim.setTranslate(0, 0, 1000);
									break;
								case LEFTDOWN:
									hintAnim.setElements(createHintBitmap(hintLeftDownBm, "浮云。。。"), new Paint());
									matrix.setTranslate(targetCenter[0], targetCenter[1]-hintLeftDownBm.getHeight());
									hintAnim.setStartMatrix(matrix);
									hintAnim.setTranslate(0, 0, 1000);
									break;
								case RIGHTUP:
									hintAnim.setElements(createHintBitmap(hintRightUpBm, "浮云。。。"), new Paint());
									matrix.setTranslate(targetCenter[0]-hintRightUpBm.getWidth(), targetCenter[1]);
									hintAnim.setStartMatrix(matrix);
									hintAnim.setTranslate(0, 0, 1000);
									break;
								case RIGHTDOWN:
									hintAnim.setElements(createHintBitmap(hintRightDownBm, "浮云。。。"), new Paint());
									matrix.setTranslate(targetCenter[0]-hintRightDownBm.getWidth(), targetCenter[1]-hintRightDownBm.getHeight());
									hintAnim.setStartMatrix(matrix);
									hintAnim.setTranslate(0, 0, 1000);
									break;
								}
							} else if ((targetCenter2RotateCenter - RADIUS <= boomCenter2RotateCenter) && (boomCenter2RotateCenter <= targetCenter2RotateCenter + RADIUS)) {
								achieved = false;
								Matrix matrix = new Matrix();
								switch(targetLocationState){
								case LEFTUP:
									hintAnim.setElements(createHintBitmap(hintLeftUpBm, "打偏了"), new Paint());
									matrix.setTranslate(targetCenter[0], targetCenter[1]);
									hintAnim.setStartMatrix(matrix);
									hintAnim.setTranslate(0, 0, 1000);
									break;
								case LEFTDOWN:
									hintAnim.setElements(createHintBitmap(hintLeftDownBm, "打偏了"), new Paint());
									matrix.setTranslate(targetCenter[0], targetCenter[1]-hintLeftDownBm.getHeight());
									hintAnim.setStartMatrix(matrix);
									hintAnim.setTranslate(0, 0, 1000);
									break;
								case RIGHTUP:
									hintAnim.setElements(createHintBitmap(hintRightUpBm, "打偏了"), new Paint());
									matrix.setTranslate(targetCenter[0]-hintRightUpBm.getWidth(), targetCenter[1]);
									hintAnim.setStartMatrix(matrix);
									hintAnim.setTranslate(0, 0, 1000);
									break;
								case RIGHTDOWN:
									hintAnim.setElements(createHintBitmap(hintRightDownBm, "打偏了"), new Paint());
									matrix.setTranslate(targetCenter[0]-hintRightDownBm.getWidth(), targetCenter[1]-hintRightDownBm.getHeight());
									hintAnim.setStartMatrix(matrix);
									hintAnim.setTranslate(0, 0, 1000);
									break;
								}
							} else if (boomCenter2RotateCenter > targetCenter2RotateCenter + RADIUS) {
								achieved = false;
								Matrix matrix = new Matrix();
								switch(targetLocationState){
								case LEFTUP:
									hintAnim.setElements(createHintBitmap(hintLeftUpBm, "太用力了"), new Paint());
									matrix.setTranslate(targetCenter[0], targetCenter[1]);
									hintAnim.setStartMatrix(matrix);
									hintAnim.setTranslate(0, 0, 1000);
									break;
								case LEFTDOWN:
									hintAnim.setElements(createHintBitmap(hintLeftDownBm, "太用力了"), new Paint());
									matrix.setTranslate(targetCenter[0], targetCenter[1]-hintLeftDownBm.getHeight());
									hintAnim.setStartMatrix(matrix);
									hintAnim.setTranslate(0, 0, 1000);
									break;
								case RIGHTUP:
									hintAnim.setElements(createHintBitmap(hintRightUpBm, "太用力了"), new Paint());
									matrix.setTranslate(targetCenter[0]-hintRightUpBm.getWidth(), targetCenter[1]);
									hintAnim.setStartMatrix(matrix);
									hintAnim.setTranslate(0, 0, 1000);
									break;
								case RIGHTDOWN:
									hintAnim.setElements(createHintBitmap(hintRightDownBm, "太用力了"), new Paint());
									matrix.setTranslate(targetCenter[0]-hintRightDownBm.getWidth(), targetCenter[1]-hintRightDownBm.getHeight());
									hintAnim.setStartMatrix(matrix);
									hintAnim.setTranslate(0, 0, 1000);
									break;
								}
							}
							Paint paint = new Paint();
							paint.setAlpha(0x00);
							bulletAnim.setElements(bulletBm, paint);
							boomAnim.setElements(boomBm, paint);
							boomAnim.start(false);

							hintAnim.setCallback(new CanvasAnimation.Callback() {

								@Override
								public void onEnd() {
									hintAnim.start(false);
								}
							});
							hintAnim.setRepeatTimes(1);
							hintAnim.start(true);

						}
					});
					boomAnim.setElements(boomBm, new Paint());
					bulletAnim.transformMatrix.getValues(array);
					matrix.setValues(array);
					boomAnim.setStartMatrix(matrix);
					bulletAnim.traceMatrix.getValues(array);
					boomCenter[0] = array[2];
					boomCenter[1] = array[5];
					boomAnim.setScale(1.8f, boomCenter[0], boomCenter[1], 200);
					boomAnim.setRepeatTimes(1);
					boomAnim.start(true);

					soundPool.play(bombSound, 1.0f, 1.0f, 1, 0, 1f);
					bulletAnim.start(false);

				}
			});
			bulletAnim.setElements(bulletBm, new Paint());
			bulletAnim.setStartMatrix(matrix);
			// trace the center of the bullet
			matrix.setValues(array2);
			bulletAnim.setTraceMatrix(matrix);
			float tubeLength = (float) moveLength / POWERSENSITY;
			if (tubeLength > 32)
				tubeLength = 32;
			if (tubeLength == 0)
				tubeLength = 1;
			bulletAnim.setAccelerate(vector[0], vector[1], -0.0064f, 50 * (float) Math.sqrt(tubeLength));
			bulletAnim.setRepeatTimes(1);
			bulletAnim.start(true);
			bulletEnable = false;
		}
		// return super.onTouchEvent(event);
		return true;
	}

}

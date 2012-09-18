package com.cube.attract.gameEntry;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.SoundPool;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.util.Log;

import com.cube.attract.R;
import com.cube.attract.gameEntry.SceneState.PictureViewGallary.PictureView;
import com.cube.common.LocalData;
import com.cube.common.LocalData.Game.ActiveGirl;
import com.cube.common.imageservice.BitmapPool;
import com.cube.opengl.common.AnimationManager;
import com.cube.opengl.common.AnimationManager.AnimationGl;
import com.cube.opengl.common.AnimationManager.Callback;
import com.cube.opengl.common.GLAnimation2;
import com.cube.opengl.common.Utils;

public class GlRenderer implements Renderer {

	private Context context;
	private static final String TAG = "GlRenderer";

	public GlRenderer(Context context) {
		this.context = context;

		this.sceneState = SceneState.getInstance();
		this.sceneState.render = this;
	}

	private final static float lightAmb[] = { 0.5f, 0.5f, 0.5f, 1.0f };
	private final static float lightDif[] = { 1.0f, 1.0f, 1.0f, 1.0f };
	private final static float lightPos[] = { 0.0f, 0.0f, 2.0f, 1.0f };

	private final static FloatBuffer lightAmbBfr;
	private final static FloatBuffer lightDifBfr;
	private final static FloatBuffer lightPosBfr;

	private static float[] girlTotalTextureCoords = new float[] { 0, 1, 0, 0, 1, 1, 1, 0 };

	private static float[] girlIndexVertexCoords = new float[] { 0.6f, 0.23f, 0, 0.6f, 0.11f, 0, 0.72f, 0.23f, 0, 0.72f, 0.11f, 0 };
	private static float[] girlTotalVertexCoords = new float[] { 0.72f, 0.23f, 0, 0.72f, 0.11f, 0, 0.9f, 0.23f, 0, 0.9f, 0.11f, 0 };

	private static float[] retnBtnVertxCoords = new float[] {-0.31f,0.23f,0, -0.31f,-0.13f,0, 0.3395f,0.23f,0, 0.3395f,-0.13f,0};
	
	final static float UNIT_SIZE = 0.35f;
	static float[] quadVertexLogo = new float[] { 0 * UNIT_SIZE, 1 * UNIT_SIZE, 0, 0.8f * UNIT_SIZE, 0.5f * UNIT_SIZE, 0, 0.8f * UNIT_SIZE, -0.5f * UNIT_SIZE, 0, 0 * UNIT_SIZE, -1 * UNIT_SIZE, 0, -0.8f * UNIT_SIZE, -0.5f * UNIT_SIZE, 0, -0.8f * UNIT_SIZE, 0.5f * UNIT_SIZE, 0 };

	private static ByteBuffer mIndexBuffer;

	private static float[] quadTextureLogo = new float[] { 0.5f, 0, 0, 0.25f, 0, 0.75f, 0.5f, 1f, 1f, 0.75f, 1f, 0.25f };

	private static FloatBuffer retnBtnVertxBuffer;
	
	private static FloatBuffer quadVertexBufferLogo;
	private static FloatBuffer quadTextureBufferLogo;

	private static FloatBuffer girlIndexVertexBuffer;
	private static FloatBuffer girlTotalTextureBuffer;

	private static FloatBuffer girlTotalVertexBuffer;
	// private static FloatBuffer girlTotalTextureBuffer1;

	private static float[] quadVertexGirl = new float[] { -9.0f, 16.0f, 0, -9.0f, -16.0f, 0, 9.0f, 16.0f, 0, 9.0f, -16.0f, 0 };

	private static float[] quadTextureGirl = new float[] { 0, 0, 0, 1, 1, 0, 1, 1 };

	private static FloatBuffer quadVertexBufferGirl;
	private static FloatBuffer quadTextureBufferGirl;

	SceneState sceneState = null;

	static {
		byte indices[] = new byte[] { 0, 5, 4, 3, 2, 1 };

		mIndexBuffer = ByteBuffer.allocateDirect(indices.length);
		mIndexBuffer.put(indices);
		mIndexBuffer.position(0);

		lightAmbBfr = BufferUtil.floatToBuffer(lightAmb);
		lightDifBfr = BufferUtil.floatToBuffer(lightDif);
		lightPosBfr = BufferUtil.floatToBuffer(lightPos);

		quadVertexBufferLogo = BufferUtil.floatToBuffer(quadVertexLogo);
		quadTextureBufferLogo = BufferUtil.floatToBuffer(quadTextureLogo);

		girlIndexVertexBuffer = BufferUtil.floatToBuffer(girlIndexVertexCoords);
		girlTotalTextureBuffer = BufferUtil.floatToBuffer(girlTotalTextureCoords);

		girlTotalVertexBuffer = BufferUtil.floatToBuffer(girlTotalVertexCoords);

		quadVertexBufferGirl = BufferUtil.floatToBuffer(quadVertexGirl);
		quadTextureBufferGirl = BufferUtil.floatToBuffer(quadTextureGirl);
		
		retnBtnVertxBuffer = BufferUtil.floatToBuffer(retnBtnVertxCoords);

	}

	public static class BufferUtil {
		public static FloatBuffer mBuffer;

		public static FloatBuffer floatToBuffer(float[] a) {
			ByteBuffer mbb = ByteBuffer.allocateDirect(a.length * 4);
			mbb.order(ByteOrder.nativeOrder());
			mBuffer = mbb.asFloatBuffer();
			mBuffer.put(a);
			mBuffer.position(0);
			return mBuffer;
		}
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glClearColor(0, 0, 0, 0);

		gl.glClearDepthf(1.0f);
		gl.glDisable(GL10.GL_DEPTH_TEST);
		gl.glDepthFunc(GL10.GL_LEQUAL);

		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);

		gl.glEnable(GL10.GL_CULL_FACE);
		gl.glCullFace(GL10.GL_BACK);

		// lighting
		gl.glEnable(GL10.GL_LIGHT0);
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, lightAmbBfr);
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, lightDifBfr);
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, lightPosBfr);

		// blending
		gl.glColor4f(1.0f, 1.0f, 1.0f, 0.8f);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE);
	}



	public void onSurfaceChanged(GL10 gl, int width, int height) {
		sceneState.screenHeight = height;
		sceneState.screenWidth = width;
		loadTexture(gl);
		loadGirlsTexture(gl);
		if (height == 0)
			height = 1;
		gl.glViewport(0, 0, width, height);
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		GLU.gluPerspective(gl, 45.0f, (float) width / (float) height, 1.0f, 100.0f);

		initAnimations();
		initSoundPool();
	}

	AnimationManager animationManager = new AnimationManager();
	AnimationGl girls = null;

	GLAnimation2 girlGoBack = null;
	GLAnimation2 girlGoFront = null;
	GLAnimation2 girlRotateBack = null;
	GLAnimation2 girlRotateFront = null;

	void initAnimations() {
		animationManager.animationGls.clear();
		girls = animationManager.addAnimationGl(new Callback() {
			@Override
			public void ondraw(GL10 gl) {
				drawGirls(gl);
			}
		});
		girls.matrix.translate(0, 0f, -62f);

		girlGoBack = new GLAnimation2();
		girlGoFront = new GLAnimation2();
		girlRotateBack = new GLAnimation2();
		girlRotateFront = new GLAnimation2();

		girlRotateFront.setCallback(new GLAnimation2.Callback() {
			@Override
			public void onEnd() {
				soundPool.play(effect_tick, 0.2f, 0.2f, 1, 0, 1f);
			}
		});

		girlGoBack.setTranslate(0, 4.5f, -12.5f, 200f);
		girlGoFront.setTranslate(0, -4.5f, 12.5f, 200f);
		girlRotateBack.setRotate(25, 1, 0, 0, 200f);
		girlRotateFront.setRotate(-25, 1, 0, 0, 200f);
	}

	SoundPool soundPool = null;
	int fireSound = 0;
	int explodeSound = 0;
	int passSound = 0;
	int startSound = 0;
	int flyawaySound = 0;
	int effect_tick = 0;

	void initSoundPool() {
		soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 100);
		fireSound = soundPool.load(context, R.raw.fire, 1);
		explodeSound = soundPool.load(context, R.raw.explode, 1);
		passSound = soundPool.load(context, R.raw.pass, 1);
		startSound = soundPool.load(context, R.raw.start, 1);
		flyawaySound = soundPool.load(context, R.raw.flyaway, 1);
		effect_tick = soundPool.load(context, R.raw.effect_tick, 1);
	}

	public void onDrawFrame(GL10 gl) {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glDisable(GL10.GL_CULL_FACE);
		if (sceneState.blending) {
			gl.glEnable(GL10.GL_BLEND);
			gl.glDisable(GL10.GL_CULL_FACE);
		} else {
			gl.glDisable(GL10.GL_BLEND);
			gl.glEnable(GL10.GL_CULL_FACE);
		}
		sceneState.isLocked[1] = false;
		sceneState.isLocked[2] = true;
		sceneState.isLocked[3] = true;
		sceneState.isLocked[4] = true;
		sceneState.isLocked[5] = true;
		drawPolygon(gl, 0, -0.316f, sceneState.isLocked[1], sceneState.isSelected[1]);// 2
		drawPolygon(gl, -1.6f * UNIT_SIZE, -0.31616f, sceneState.isLocked[0], sceneState.isSelected[0]);// 1
		drawPolygon(gl, 1.6f * UNIT_SIZE, -0.31616f, sceneState.isLocked[2], sceneState.isSelected[2]);// 3
		drawPolygon(gl, -0.8f * UNIT_SIZE, -0.316f - 1.5f * UNIT_SIZE, sceneState.isLocked[3], sceneState.isSelected[3]);// 4
		drawPolygon(gl, 0.8f * UNIT_SIZE, -0.316f - 1.5f * UNIT_SIZE, sceneState.isLocked[4], sceneState.isSelected[4]);// 5
		drawPolygon(gl, 0, -0.316f - 3 * UNIT_SIZE, sceneState.isLocked[5], sceneState.isSelected[5]);// 6
		animationManager.draw(gl);
		drawGirlNumber(gl);
		drawReturnButton(gl);

	}

	public void drawGirlNumber(GL10 gl) {

		gl.glBindTexture(GL10.GL_TEXTURE_2D, texturesBuffer.get(GIRLSINDEX + sceneState.pictureViewGallary.pictureView[sceneState.pictureViewGallary.frontViewIndex].girlNumber + 1));
		gl.glLoadIdentity();
		gl.glTranslatef(-0.06f, 1.13f, -4f);

		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, girlIndexVertexBuffer);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, girlTotalTextureBuffer);

		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);

		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glDisable(GL10.GL_TEXTURE_2D);

		gl.glBindTexture(GL10.GL_TEXTURE_2D, texturesBuffer.get(GIRLSINDEX_S + girlsSize));
		gl.glLoadIdentity();
		gl.glTranslatef(-0.06f, 1.13f, -4f);

		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, girlTotalVertexBuffer);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, girlTotalTextureBuffer);

		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);

		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glDisable(GL10.GL_TEXTURE_2D);

	}

	public void drawReturnButton(GL10 gl){
		if(sceneState.isReturn == true){
			gl.glBindTexture(GL10.GL_TEXTURE_2D, texturesBuffer.get(RETURNBUTTON + 1));
		}
		else{
			gl.glBindTexture(GL10.GL_TEXTURE_2D, texturesBuffer.get(RETURNBUTTON + 0));			
		}

		gl.glLoadIdentity();
		gl.glColor4f(1f, 1f, 1f, 1f);
		gl.glEnable(GL10.GL_BLEND);
		gl.glTranslatef(-1.6f * UNIT_SIZE, 1.43f, -4.5f);
		
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, retnBtnVertxBuffer);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, girlTotalTextureBuffer);

		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);

		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glDisable(GL10.GL_TEXTURE_2D);
		
	}
	public void drawPolygon(GL10 gl, float xaxis, float yaxis, boolean isLocked, boolean isSelected) {

		if (isLocked) {
			gl.glBindTexture(GL10.GL_TEXTURE_2D, texturesBuffer.get(POLYGON + 0));
		} else {
			gl.glBindTexture(GL10.GL_TEXTURE_2D, texturesBuffer.get(POLYGON + 1));
		}

		gl.glLoadIdentity();
		gl.glColor4f(0.6f, 0.6f, 0.6f, 1f);
		if (isSelected) {
			gl.glBindTexture(GL10.GL_TEXTURE_2D,texturesBuffer.get(POLYGON + 2));
//			gl.glColor4f(1f, 0.4f, 0.4f, 1f);
		}/* else {
			gl.glColor4f(0.6f, 0.6f, 0.6f, 1f);
		}*/

		// gl.glDisable(GL10.GL_BLEND);
		gl.glEnable(GL10.GL_BLEND);
		gl.glTranslatef(xaxis, yaxis, -4.5f);

		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, quadVertexBufferLogo);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, quadTextureBufferLogo);

		gl.glDrawElements(GL10.GL_TRIANGLE_FAN, 6, GL10.GL_UNSIGNED_BYTE, mIndexBuffer);

		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glDisable(GL10.GL_TEXTURE_2D);
	}

	public long lastMillis = 0;
	double PI = Math.PI;

	public void drawGirls(GL10 gl) {

		sceneState.pictureViewGallary.movement();
		gl.glColor4f(1.0f, 1.0f, 1.0f, 0.8f);

		int front = sceneState.pictureViewGallary.frontViewIndex;
		
		PictureView[] pictureView = sceneState.pictureViewGallary.pictureView;
		sceneState.girlNumber = (pictureView[front].girlNumber  + girlsSize) % girlsSize;
		for (int i = -2; i <= +2; i++) {
			int j = (front + i + 9) % 9;
			pictureView[j].girlNumber = (pictureView[front].girlNumber + i + girlsSize) % girlsSize;

		}

		for (int i = -2; i <= +2; i++) {
			int j = (front + i + 9) % 9;

			int girlNumber = sceneState.pictureViewGallary.pictureView[j].girlNumber;
			double x = sceneState.pictureViewGallary.pictureView[j].x;
			double y = sceneState.pictureViewGallary.pictureView[j].y;
			double z = sceneState.pictureViewGallary.pictureView[j].z;

			gl.glBindTexture(GL10.GL_TEXTURE_2D, girlsTexturesBuffer.get(girlNumber));

			gl.glPushMatrix();
			gl.glTranslatef((float) x, (float) y, (float) z);
			float angle = (float) (-sceneState.pictureViewGallary.pictureView[j].radian * 180 / PI) % 360;

			gl.glRotatef(angle, 0, -1, 0);
			gl.glEnable(GL10.GL_TEXTURE_2D);
			gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
			gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, quadVertexBufferGirl);
			gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, quadTextureBufferGirl);

			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);

			gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
			gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
			gl.glDisable(GL10.GL_TEXTURE_2D);
			gl.glPopMatrix();
		}
		long currentMillis = System.currentTimeMillis();

		if (lastMillis != 0) {
			long delta = currentMillis - lastMillis;
			sceneState.pictureViewGallary.dAngle += sceneState.pictureViewGallary.dxSpeed * delta;
			sceneState.pictureViewGallary.dampenSpeed(delta);
		}

		lastMillis = currentMillis;
		if (lastMillis != 0) {
			long delta = currentMillis - lastMillis;
			sceneState.pictureViewGallary.dx += sceneState.pictureViewGallary.dxSpeed * delta;
			sceneState.pictureViewGallary.dy += sceneState.pictureViewGallary.dySpeed * delta;
		}

	}

	private IntBuffer girlsTexturesBuffer;
	int girlsSize = 0;

	void loadGirlsTexture(GL10 gl) {
		girlsSize = localData.game.activeGirls.size();
		gl.glEnable(GL10.GL_TEXTURE_2D);
		girlsTexturesBuffer = IntBuffer.allocate(girlsSize);
		gl.glGenTextures(girlsSize, girlsTexturesBuffer);

		int i = 0;
		for (ActiveGirl activegirl : localData.game.activeGirls) {
			Bitmap texture = null;
			String url = activegirl.girl.pictures.get(1).url;
			String filename = url.substring(url.lastIndexOf("/") + 1);
			if (!localData.game.loadedPictures.contains(filename)) {
				texture = Utils.getTextureFromBitmapResource(context, R.drawable.heart_1_s);
			} else {
				texture = bitmapPool.get(filename);
				Log.v(TAG, "texture is loaded: " + filename);
			}
			if (texture == null) {
				texture = Utils.getTextureFromBitmapResource(context, R.drawable.heart_1_s);
			}

			gl.glBindTexture(GL10.GL_TEXTURE_2D, girlsTexturesBuffer.get(i));
			gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_NEAREST);
			gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
			gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
			gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, texture, 0);

			texture.recycle();
			i++;
		}

	}

	private IntBuffer texturesBuffer;
	LocalData localData = LocalData.getInstance();
	public int POLYGON = 0;
	public int BACKGROUND = POLYGON + 3;
	public int GIRLSINDEX = BACKGROUND + 1;
	public int GIRLSINDEX_S = GIRLSINDEX + 10;
	public int RETURNBUTTON = GIRLSINDEX_S+10;

	public int textureNum = RETURNBUTTON + 2;
	private BitmapPool bitmapPool;

	private void loadTexture(GL10 gl) {
		gl.glEnable(GL10.GL_TEXTURE_2D);
		texturesBuffer = IntBuffer.allocate(textureNum);
		gl.glGenTextures(textureNum, texturesBuffer);

		bitmapPool = BitmapPool.getInstance();

		Bitmap[] texture = new Bitmap[textureNum];

		texture[POLYGON + 0] = Utils.getTextureFromBitmapResource(context, R.drawable.polygon_locked);
		texture[POLYGON + 1] = Utils.getTextureFromBitmapResource(context, R.drawable.polygon_cupid);
		texture[POLYGON + 2] = Utils.getTextureFromBitmapResource(context, R.drawable.polygon_cupid_down);
		texture[BACKGROUND + 0] = Utils.getTextureFromBitmapResource(context, R.drawable.gameentry_background);
		texture[GIRLSINDEX + 0] = Utils.getTextureFromBitmapResource(context, R.drawable.number_0);
		texture[GIRLSINDEX + 1] = Utils.getTextureFromBitmapResource(context, R.drawable.number_1);
		texture[GIRLSINDEX + 2] = Utils.getTextureFromBitmapResource(context, R.drawable.number_2);
		texture[GIRLSINDEX + 3] = Utils.getTextureFromBitmapResource(context, R.drawable.number_3);
		texture[GIRLSINDEX + 4] = Utils.getTextureFromBitmapResource(context, R.drawable.number_4);
		texture[GIRLSINDEX + 5] = Utils.getTextureFromBitmapResource(context, R.drawable.number_5);
		texture[GIRLSINDEX + 6] = Utils.getTextureFromBitmapResource(context, R.drawable.number_6);
		texture[GIRLSINDEX + 7] = Utils.getTextureFromBitmapResource(context, R.drawable.number_7);
		texture[GIRLSINDEX + 8] = Utils.getTextureFromBitmapResource(context, R.drawable.number_8);
		texture[GIRLSINDEX + 9] = Utils.getTextureFromBitmapResource(context, R.drawable.number_9);

		texture[GIRLSINDEX_S + 0] = Utils.getTextureFromBitmapResource(context, R.drawable.number_s_0);
		texture[GIRLSINDEX_S + 1] = Utils.getTextureFromBitmapResource(context, R.drawable.number_s_1);
		texture[GIRLSINDEX_S + 2] = Utils.getTextureFromBitmapResource(context, R.drawable.number_s_2);
		texture[GIRLSINDEX_S + 3] = Utils.getTextureFromBitmapResource(context, R.drawable.number_s_3);
		texture[GIRLSINDEX_S + 4] = Utils.getTextureFromBitmapResource(context, R.drawable.number_s_4);
		texture[GIRLSINDEX_S + 5] = Utils.getTextureFromBitmapResource(context, R.drawable.number_s_5);
		texture[GIRLSINDEX_S + 6] = Utils.getTextureFromBitmapResource(context, R.drawable.number_s_6);
		texture[GIRLSINDEX_S + 7] = Utils.getTextureFromBitmapResource(context, R.drawable.number_s_7);
		texture[GIRLSINDEX_S + 8] = Utils.getTextureFromBitmapResource(context, R.drawable.number_s_8);
		texture[GIRLSINDEX_S + 9] = Utils.getTextureFromBitmapResource(context, R.drawable.number_s_9);
		texture[RETURNBUTTON + 0] = Utils.getTextureFromBitmapResource(context, R.drawable.returnbutton);
		texture[RETURNBUTTON + 1] = Utils.getTextureFromBitmapResource(context, R.drawable.returnbuttondown);

		
		for (int i = 0; i < textureNum; i++) {

			gl.glBindTexture(GL10.GL_TEXTURE_2D, texturesBuffer.get(i));
			gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_NEAREST);
			gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
			gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
			gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, texture[i], 0);

			texture[i].recycle();
		}

	}

}

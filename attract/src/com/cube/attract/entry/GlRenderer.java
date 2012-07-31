package com.cube.attract.entry;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.cube.attract.R;
import com.cube.opengl.common.GLAnimation;
import com.cube.opengl.common.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.opengl.GLSurfaceView.Renderer;

public class GlRenderer implements Renderer {

	private Context context;
	private Activity mActivity;

	public GlRenderer(Context context) {
		this.context = context;
		this.mActivity = (Activity) context;
	}

	private final static float[][] cubeVertexCoords = new float[][] { new float[] { // top
			1, 1, -1, -1, 1, -1, -1, 1, 1, 1, 1, 1 }, new float[] { // bottom
			1, -1, 1, -1, -1, 1, -1, -1, -1, 1, -1, -1 }, new float[] { // front
			1, 1, 1, -1, 1, 1, -1, -1, 1, 1, -1, 1 }, new float[] { // back
			1, -1, -1, -1, -1, -1, -1, 1, -1, 1, 1, -1 }, new float[] { // left
			-1, 1, 1, -1, 1, -1, -1, -1, -1, -1, -1, 1 }, new float[] { // right
			1, 1, -1, 1, 1, 1, 1, -1, 1, 1, -1, -1 }, };

	private final static float[][] cubeNormalCoords = new float[][] { new float[] { // top
			0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0 }, new float[] { // bottom
			0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0 }, new float[] { // front
			0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1 }, new float[] { // back
			0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1 }, new float[] { // left
			-1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0 }, new float[] { // right
			1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0 }, };

	private final static float[][] cubeTextureCoords = new float[][] { new float[] { // top
			1, 0, 1, 1, 0, 1, 0, 0 }, new float[] { // bottom
			0, 0, 1, 0, 1, 1, 0, 1 }, new float[] { // front
			1, 1, 0, 1, 0, 0, 1, 0 }, new float[] { // back
			0, 1, 0, 0, 1, 0, 1, 1 }, new float[] { // left
			1, 1, 0, 1, 0, 0, 1, 0 }, new float[] { // right
			0, 1, 0, 0, 1, 0, 1, 1 }, };

	private final static float lightAmb[] = { 0.5f, 0.5f, 0.5f, 1.0f };
	private final static float lightDif[] = { 1.0f, 1.0f, 1.0f, 1.0f };
	private final static float lightPos[] = { 0.0f, 0.0f, 2.0f, 1.0f };

	private final static FloatBuffer[] cubeVertexBfr;
	private final static FloatBuffer[] cubeNormalBfr;
	private final static FloatBuffer[] cubeTextureBfr;

	private final static FloatBuffer lightAmbBfr;
	private final static FloatBuffer lightDifBfr;
	private final static FloatBuffer lightPosBfr;

	private static float[] quadVertexLogo = new float[] { -1.0f, 0.2354f, 0, -1.0f, -0.2354f, 0, 1.0f, 0.2354f, 0, 1.0f, -0.2354f, 0 };

	private static float[] quadTextureLogo = new float[] { 0, 1, 0, 0, 1, 1, 1, 0 };

	private static FloatBuffer quadVertexBufferLogo;
	private static FloatBuffer quadTextureBufferLogo;

	private static float[] quadVertexBackground = new float[] { -8.0f, 8.0f, 0, -8.0f, -8.0f, 0, 8.0f, 8.0f, 0, 8.0f, -8.0f, 0 };

	private static float[] quadTextureBackground = new float[] { 0, 1, 0, 0, 1, 1, 1, 0 };

	private static FloatBuffer quadVertexBufferBackground;
	private static FloatBuffer quadTextureBufferBackground;

	static final SceneState sceneState;
	private long lastMillis;

	static {
		cubeVertexBfr = new FloatBuffer[6];
		cubeNormalBfr = new FloatBuffer[6];
		cubeTextureBfr = new FloatBuffer[6];
		for (int i = 0; i < 6; i++) {
			cubeVertexBfr[i] = FloatBuffer.wrap(cubeVertexCoords[i]);
			cubeNormalBfr[i] = FloatBuffer.wrap(cubeNormalCoords[i]);
			cubeTextureBfr[i] = FloatBuffer.wrap(cubeTextureCoords[i]);
		}

		lightAmbBfr = FloatBuffer.wrap(lightAmb);
		lightDifBfr = FloatBuffer.wrap(lightDif);
		lightPosBfr = FloatBuffer.wrap(lightPos);

		quadVertexBufferLogo = FloatBuffer.wrap(quadVertexLogo);
		quadTextureBufferLogo = FloatBuffer.wrap(quadTextureLogo);

		quadVertexBufferBackground = FloatBuffer.wrap(quadVertexBackground);
		quadTextureBufferBackground = FloatBuffer.wrap(quadTextureBackground);

		sceneState = new SceneState();
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
		gl.glColor4f(1.0f, 1.0f, 1.0f, 0.0f);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE);
	}

	GLAnimation testAnimation = new GLAnimation();
	GLAnimation test1Animation = new GLAnimation();
	GLAnimation test2Animation = new GLAnimation();
	GLAnimation test3Animation = new GLAnimation();
	GLAnimation test4Animation = new GLAnimation();
	GLAnimation test5Animation = new GLAnimation();
	GLAnimation cube1Animation = new GLAnimation();

	public void onSurfaceChanged(GL10 gl, int width, int height) {
		// reload textures
		loadTexture(gl);
		// avoid division by zero
		if (height == 0)
			height = 1;
		// draw on the entire screen
		gl.glViewport(0, 0, width, height);
		// setup projection matrix
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		GLU.gluPerspective(gl, 45.0f, (float) width / (float) height, 1.0f, 100.0f);

		testAnimation.setTranslate(0.2f, -1.2f, -0.3f, 1000.0f);

		cube1Animation.setTranslate(0.0f, -0.0f, 9.0f, 3000.0f);

		testAnimation.addNextAnimation(test1Animation);
		test1Animation.setTranslate(-1f, -1f, -0f, 1000.0f);
		test1Animation.addNextAnimation(test2Animation);
		test2Animation.setTranslate(1f, 1f, -0f, 1000.0f);
		test2Animation.addNextAnimation(test3Animation);
		test2Animation.addNextAnimation(test4Animation);
		test3Animation.setTranslate(0.0f, -1f, -2f, 1000.0f);
		test4Animation.setTranslate(-1f, 0f, -3f, 2000.0f);
		test4Animation.addNextAnimation(test5Animation);
		test5Animation.setTranslate(1f, 1f, 5f, 1000.0f);
		
		test5Animation.setCallback(new GLAnimation.Callback() {
		public void onEnd() {
			Intent about = new Intent(Intent.ACTION_MAIN);
			about.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			about.setClassName("com.cube.attract", "com.cube.attract.about.AboutActivity");
			context.startActivity(about);
			mActivity.finish();
		}
	});

	}

	public void onDrawFrame(GL10 gl) {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();

		// update lighting
		if (sceneState.lighting) {
			gl.glEnable(GL10.GL_LIGHTING);
		} else {
			gl.glDisable(GL10.GL_LIGHTING);
		}

		// update blending
		if (sceneState.blending) {
			gl.glEnable(GL10.GL_BLEND);
			gl.glDisable(GL10.GL_CULL_FACE);
		} else {
			gl.glDisable(GL10.GL_BLEND);
			gl.glEnable(GL10.GL_CULL_FACE);
		}

		// draw cube

		gl.glTranslatef(0, 0, -7);
		gl.glTranslatef(0, 0, -8);
		cube1Animation.transformModel(gl);

		sceneState.rotateModel(gl);

		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		for (int i = 0; i < 6; i++) // draw each face
		{
			gl.glBindTexture(GL10.GL_TEXTURE_2D, texturesBuffer.get(i));
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, cubeVertexBfr[i]);
			gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, cubeTextureBfr[i]);
			gl.glNormalPointer(GL10.GL_FLOAT, 0, cubeNormalBfr[i]);
			gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, 4);
		}
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glDisable(GL10.GL_TEXTURE_2D);

		drawLogo(gl);
		drawBackground(gl);
		// get current millis
		long currentMillis = System.currentTimeMillis();

		// update rotations
		if (lastMillis != 0) {
			long delta = currentMillis - lastMillis;
			sceneState.dx += sceneState.dxSpeed * delta;
			sceneState.dy += sceneState.dySpeed * delta;
			sceneState.dampenSpeed(delta);
		}

		// update millis
		lastMillis = currentMillis;
	}

	public void drawLogo(GL10 gl) {

		gl.glBindTexture(GL10.GL_TEXTURE_2D, texturesBuffer.get(LOGO + 0));
		gl.glLoadIdentity();

		// gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		gl.glEnable(GL10.GL_BLEND);

		gl.glTranslatef(0, 1.2f, -3.8f);

		testAnimation.transformModel(gl);

		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, quadVertexBufferLogo);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, quadTextureBufferLogo);

		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);

		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glDisable(GL10.GL_TEXTURE_2D);

	}

	public void drawBackground(GL10 gl) {

		gl.glBindTexture(GL10.GL_TEXTURE_2D, texturesBuffer.get(BACKGROUND + 0));
		gl.glLoadIdentity();
		gl.glTranslatef(0, 0, -8.5f);

		// test1Animation.transformModel(gl);

		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, quadVertexBufferBackground);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, quadTextureBufferBackground);

		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);

		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glDisable(GL10.GL_TEXTURE_2D);

	}

	private IntBuffer texturesBuffer;

	public int LOGO = 6;
	public int BACKGROUND = 8;

	public int textureNum = 9;

	private void loadTexture(GL10 gl) {
		// create textures
		gl.glEnable(GL10.GL_TEXTURE_2D);
		texturesBuffer = IntBuffer.allocate(textureNum);
		gl.glGenTextures(textureNum, texturesBuffer);

		// load bitmap
		Bitmap[] texture = new Bitmap[textureNum];
		texture[0] = Utils.getTextureFromBitmapResource(context, R.drawable.girl_entry_1);
		texture[1] = Utils.getTextureFromBitmapResource(context, R.drawable.girl_entry_2);
		texture[2] = Utils.getTextureFromBitmapResource(context, R.drawable.girl_entry_3);
		texture[3] = Utils.getTextureFromBitmapResource(context, R.drawable.girl_entry_4);
		texture[4] = Utils.getTextureFromBitmapResource(context, R.drawable.girl_entry_5);
		texture[5] = Utils.getTextureFromBitmapResource(context, R.drawable.girl_entry_6);

		texture[LOGO + 0] = Utils.getTextureFromBitmapResource(context, R.drawable.welcome_title1);
		texture[LOGO + 1] = Utils.getTextureFromBitmapResource(context, R.drawable.welcome_title2);
		texture[BACKGROUND + 0] = Utils.getTextureFromBitmapResource(context, R.drawable.entry_background);

		for (int i = 0; i < textureNum; i++) {

			// setup texture 0
			gl.glBindTexture(GL10.GL_TEXTURE_2D, texturesBuffer.get(i));
			gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
			gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
			gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
			gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, texture[i], 0);

			texture[i].recycle();
		}

	}

}

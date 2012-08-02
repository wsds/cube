package com.cube.attract.gameEntry;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
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

	private static float[] quadVertexLogo = new float[] { -1.1f, 1f, 0, -1.1f, -1f, 0, 1.1f, 1f, 0, 1.1f, -1f, 0 };

	private static float[] quadTextureLogo = new float[] { 0, 1, 0, 0, 1, 1, 1, 0 };

	private static FloatBuffer quadVertexBufferLogo;
	private static FloatBuffer quadTextureBufferLogo;

	private static float[] quadVertexBackground = new float[] { -6.0f, 9.0f, 0, -6.0f, -9.0f, 0, 6.0f, 9.0f, 0, 6.0f, -9.0f, 0 };

	private static float[] quadTextureBackground = new float[] { 0, 1, 0, 0, 1, 1, 1, 0 };

	private static FloatBuffer quadVertexBufferBackground;
	private static FloatBuffer quadTextureBufferBackground;

	SceneState sceneState = SceneState.getInstance();
	private long lastMillis;

	static {
		cubeVertexBfr = new FloatBuffer[6];
		cubeNormalBfr = new FloatBuffer[6];
		cubeTextureBfr = new FloatBuffer[6];
		for (int i = 0; i < 6; i++) {
			cubeVertexBfr[i] = BufferUtil.floatToBuffer(cubeVertexCoords[i]);
			cubeNormalBfr[i] = BufferUtil.floatToBuffer(cubeNormalCoords[i]);
			cubeTextureBfr[i] = BufferUtil.floatToBuffer(cubeTextureCoords[i]);
		}

		lightAmbBfr = BufferUtil.floatToBuffer(lightAmb);
		lightDifBfr = BufferUtil.floatToBuffer(lightDif);
		lightPosBfr = BufferUtil.floatToBuffer(lightPos);

		quadVertexBufferLogo = BufferUtil.floatToBuffer(quadVertexLogo);
		quadTextureBufferLogo = BufferUtil.floatToBuffer(quadTextureLogo);

		quadVertexBufferBackground = BufferUtil.floatToBuffer(quadVertexBackground);
		quadTextureBufferBackground = BufferUtil.floatToBuffer(quadTextureBackground);

	}
	
	 
	 
	public static class BufferUtil {
	    public static FloatBuffer mBuffer;
	    public static FloatBuffer floatToBuffer(float[] a){
	    //先初始化buffer，数组的长度*4，因为一个float占4个字节
	       ByteBuffer mbb = ByteBuffer.allocateDirect(a.length*4);
	    //数组排序用nativeOrder
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

	GLAnimation testAnimation = new GLAnimation();
	GLAnimation test1Animation = new GLAnimation();
	GLAnimation test2Animation = new GLAnimation();
	GLAnimation test3Animation = new GLAnimation();
	GLAnimation test4Animation = new GLAnimation();
	GLAnimation test5Animation = new GLAnimation();

	GLAnimation rotate1Animation = new GLAnimation();
	GLAnimation rotate2Animation = new GLAnimation();
	GLAnimation cube1Animation = new GLAnimation();
	GLAnimation cube2Animation = new GLAnimation();

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

		testAnimation.setTranslate(0.0f, 0f, -0.3f, 1000.0f);

		cube1Animation.setTranslate(0.0f, -0.0f, 9.0f, 3000.0f);
		cube1Animation.addNextAnimation(cube2Animation);
		cube2Animation.setTranslate(0.0f, -0.5f, -1.0f, 3000.0f);

		testAnimation.addNextAnimation(test1Animation);
		test1Animation.setRepeatTimes(5);

		test1Animation.setTranslate(-1f, -1f, -0f, 1000.0f);
		test1Animation.addNextAnimation(test2Animation);
		test2Animation.setTranslate(1f, 1f, -0f, 1000.0f);
		test2Animation.addNextAnimation(test3Animation);
		test2Animation.addNextAnimation(test4Animation);
		test3Animation.setTranslate(0.0f, -1f, -2f, 1000.0f);
		test4Animation.setTranslate(-1f, 0f, -3f, 2000.0f);
		test4Animation.addNextAnimation(test5Animation);
		test5Animation.setTranslate(1f, 1f, 5f, 1000.0f);
		//
		// test5Animation.setCallback(new GLAnimation.Callback() {
		// public void onEnd() {
		// Intent about = new Intent(Intent.ACTION_MAIN);
		// about.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		// about.setClassName("com.cube.attract",
		// "com.cube.attract.about.AboutActivity");
		// context.startActivity(about);
		// mActivity.finish();
		// }
		// });
		rotate1Animation.setRotate(360f, 0, 0f, -1f, 30000f);
		rotate1Animation.setRepeatTimes(GLAnimation.INFINITE);

		testAnimation.addNextAnimation(rotate2Animation);

		rotate2Animation.setRotate(1440f, 0, 1f, 0f, 5000f);

	}

	public void onDrawFrame(GL10 gl) {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		// // update lighting
		// if (sceneState.lighting) {
		// gl.glEnable(GL10.GL_LIGHTING);
		// } else {
		// gl.glDisable(GL10.GL_LIGHTING);
		// }

		// update blending
		if (sceneState.blending) {
			gl.glEnable(GL10.GL_BLEND);
			gl.glDisable(GL10.GL_CULL_FACE);
		} else {
			gl.glDisable(GL10.GL_BLEND);
			gl.glEnable(GL10.GL_CULL_FACE);
		}

		drawPolygon(gl);
//		drawBackground(gl);               
		drawGirl(gl);
	}

	public void drawPolygon(GL10 gl) {

		gl.glBindTexture(GL10.GL_TEXTURE_2D, texturesBuffer.get(POLYGON + 0));
		gl.glLoadIdentity();

		// gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		gl.glEnable(GL10.GL_BLEND);

		gl.glTranslatef(0, -0.7f, -4.5f);

		// testAnimation.transformModel(gl);

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

	public void drawGirl(GL10 gl) {

		gl.glBindTexture(GL10.GL_TEXTURE_2D, texturesBuffer.get(0));
		gl.glLoadIdentity();
		gl.glTranslatef(0, 0, -20.5f);

		// rotate1Animation.transformModel(gl);

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

	public void drawBackground(GL10 gl) {

		gl.glBindTexture(GL10.GL_TEXTURE_2D, texturesBuffer.get(BACKGROUND + 0));
		gl.glLoadIdentity();
		gl.glTranslatef(0, 0, -20.0f);

		// rotate1Animation.transformModel(gl);

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

	public int POLYGON = 1;
	public int BACKGROUND = 2;

	public int textureNum = 3;

	private void loadTexture(GL10 gl) {
		gl.glEnable(GL10.GL_TEXTURE_2D);
		texturesBuffer = IntBuffer.allocate(textureNum);
		gl.glGenTextures(textureNum, texturesBuffer);

		Bitmap[] texture = new Bitmap[textureNum];
		texture[0] = Utils.getTextureFromBitmapResource(context, R.drawable.girl4_2);
		texture[POLYGON + 0] = Utils.getTextureFromBitmapResource(context, R.drawable.polygon);
		texture[BACKGROUND + 0] = Utils.getTextureFromBitmapResource(context, R.drawable.gameentry_background);

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

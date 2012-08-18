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


import android.content.Context;

import android.graphics.Bitmap;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.opengl.GLSurfaceView.Renderer;
public class GlRenderer implements Renderer {

	private Context context;


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

	private static float[] girlTotalTextureCoords = new float[] { 0, 1, 0, 0,
			1, 1, 1, 0 };

	private static float[] girlIndexVertexCoords = new float[] { 0.6f, 0.23f,
			0, 0.6f, 0.11f, 0, 0.72f, 0.23f, 0, 0.72f, 0.11f, 0 };
	private static float[] girlTotalVertexCoords = new float[] { 0.72f, 0.23f,
			0, 0.72f, 0.11f, 0, 0.9f, 0.23f, 0, 0.9f, 0.11f, 0 };

	final static float UNIT_SIZE = 0.35f;
	static float[] quadVertexLogo = new float[] { 0 * UNIT_SIZE, 1 * UNIT_SIZE,
			0, 0.8f * UNIT_SIZE, 0.5f * UNIT_SIZE, 0, 0.8f * UNIT_SIZE,
			-0.5f * UNIT_SIZE, 0, 0 * UNIT_SIZE, -1 * UNIT_SIZE, 0,
			-0.8f * UNIT_SIZE, -0.5f * UNIT_SIZE, 0, -0.8f * UNIT_SIZE,
			0.5f * UNIT_SIZE, 0 };

	private static ByteBuffer mIndexBuffer;

	private static float[] quadTextureLogo = new float[] { 0.5f, 0, 0, 0.25f,
			0, 0.75f, 0.5f, 1f, 1f, 0.75f, 1f, 0.25f };

	private static FloatBuffer quadVertexBufferLogo;
	private static FloatBuffer quadTextureBufferLogo;

	private static FloatBuffer girlIndexVertexBuffer;
	private static FloatBuffer girlTotalTextureBuffer;

	private static FloatBuffer girlTotalVertexBuffer;
	// private static FloatBuffer girlTotalTextureBuffer1;

	private static float[] quadVertexGirl = new float[] { -9.0f, 16.0f, 0,
			-9.0f, -16.0f, 0, 9.0f, 16.0f, 0, 9.0f, -16.0f, 0 };

	private static float[] quadTextureGirl = new float[] { 0, 1, 0, 0, 1, 1, 1,
			0 };

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
		girlTotalTextureBuffer = BufferUtil
				.floatToBuffer(girlTotalTextureCoords);

		girlTotalVertexBuffer = BufferUtil.floatToBuffer(girlTotalVertexCoords);

		quadVertexBufferGirl = BufferUtil.floatToBuffer(quadVertexGirl);
		quadTextureBufferGirl = BufferUtil.floatToBuffer(quadTextureGirl);

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

	GLAnimation girlGoBack = new GLAnimation();
	GLAnimation girlGoFront = new GLAnimation();
	GLAnimation girlRotateBack = new GLAnimation();
	GLAnimation girlRotateFront = new GLAnimation();

	// GLAnimation ploygonColor = new GLAnimation();
	// GLAnimation girlRotatetoPos = new GLAnimation();

	public void initializeAnimations() {
		girlGoBack.setTranslate(0, -11.5f, -5.5f, 200f);
		girlGoBack.start(false);
		girlGoFront.setTranslate(0, 11.5f, 5.5f, 200f);
		girlGoFront.start(false);
		girlRotateBack.setRotate(15, 1, 0, 0, 200f);
		girlRotateBack.start(false);
		girlRotateFront.setRotate(-15, 1, 0, 0, 200f);
		girlRotateFront.start(false);
		// ploygonColor.setColor(1f, 0.4f, 0.4f, 1f, 800f);
		// ploygonColor.start(false);

		// girlRotatetoPos.setRotate(
		// (float) (sceneState.pictureViewGallary.diff * 180 / PI), 0, 0,
		// 1, 1800f);
		// girlRotatetoPos.setCallback(new GLAnimation.Callback() {
		//
		// @Override
		// public void onEnd() {
		// sceneState.pictureViewGallary.stop();
		// // TODO Auto-generated method stub
		//
		// }
		// });
		// girlRotatetoPos.start(false);
	}

	public void onSurfaceChanged(GL10 gl, int width, int height) {
		sceneState.screenHeight = height;
		sceneState.screenWidth = width;
		loadTexture(gl);
		if (height == 0)
			height = 1;
		gl.glViewport(0, 0, width, height);
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		GLU.gluPerspective(gl, 45.0f, (float) width / (float) height, 1.0f,
				100.0f);

		initializeAnimations();
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
		sceneState.isLocked[1] = true;
		sceneState.isLocked[2] = true;
		sceneState.isLocked[3] = true;
		sceneState.isLocked[4] = true;
		drawPolygon(gl, 0, -0.316f, sceneState.isLocked[1],
				sceneState.isSelected[1]);// 2
		drawPolygon(gl, -1.6f * UNIT_SIZE, -0.31616f, sceneState.isLocked[0],
				sceneState.isSelected[0]);// 1
		drawPolygon(gl, 1.6f * UNIT_SIZE, -0.31616f, sceneState.isLocked[2],
				sceneState.isSelected[2]);// 3
		drawPolygon(gl, -0.8f * UNIT_SIZE, -0.316f - 1.5f * UNIT_SIZE,
				sceneState.isLocked[3], sceneState.isSelected[3]);// 4
		drawPolygon(gl, 0.8f * UNIT_SIZE, -0.316f - 1.5f * UNIT_SIZE,
				sceneState.isLocked[4], sceneState.isSelected[4]);// 5
		drawPolygon(gl, 0, -0.316f - 3 * UNIT_SIZE, sceneState.isLocked[5],
				sceneState.isSelected[5]);// 6
		drawGirls(gl);
		drawGirlNumber(gl);

	}

	public void drawGirlNumber(GL10 gl) {

		gl.glBindTexture(
				GL10.GL_TEXTURE_2D,
				texturesBuffer
						.get(GIRLSINDEX
								+ sceneState.pictureViewGallary.pictureView[sceneState.pictureViewGallary.frontViewIndex].girlNumber
								+ 1));
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

		gl.glBindTexture(
				GL10.GL_TEXTURE_2D,
				texturesBuffer.get(GIRLSINDEX_S
						+ sceneState.pictureViewGallary.totalGirls));
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

	public void drawPolygon(GL10 gl, float xaxis, float yaxis,
			boolean isLocked, boolean isSelected) {

		if (isLocked) {
			gl.glBindTexture(GL10.GL_TEXTURE_2D,
					texturesBuffer.get(POLYGON + 0));
		} else {
			gl.glBindTexture(GL10.GL_TEXTURE_2D,
					texturesBuffer.get(POLYGON + 1));
		}

		gl.glLoadIdentity();
		if (isSelected) {
			gl.glColor4f(1f, 0.4f, 0.4f, 1f);
		} else {
			gl.glColor4f(0.6f, 0.6f, 0.6f, 1f);
		}

		// gl.glDisable(GL10.GL_BLEND);
		gl.glEnable(GL10.GL_BLEND);
		gl.glTranslatef(xaxis, yaxis, -4.5f);



		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, quadVertexBufferLogo);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, quadTextureBufferLogo);


		gl.glDrawElements(GL10.GL_TRIANGLE_FAN, 6, GL10.GL_UNSIGNED_BYTE,
				mIndexBuffer);

		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glDisable(GL10.GL_TEXTURE_2D);
	}

	public long lastMillis = 0;
	double PI = Math.PI;

	public void drawGirls(GL10 gl) {

		sceneState.pictureViewGallary.movement();
		gl.glColor4f(1.0f, 1.0f, 1.0f, 0.8f);

		for (int i = 0; i < sceneState.pictureViewGallary.viewsNum; i++) {
			int girlNumber = sceneState.pictureViewGallary.pictureView[i].girlNumber;
			double x = sceneState.pictureViewGallary.pictureView[i].x;
			double y = sceneState.pictureViewGallary.pictureView[i].y;
			double z = sceneState.pictureViewGallary.pictureView[i].z;

			gl.glBindTexture(GL10.GL_TEXTURE_2D, texturesBuffer.get(girlNumber));
			gl.glLoadIdentity();

			girlGoBack.transformModel(gl);
			girlGoFront.transformModel(gl);
			girlRotateBack.transformModel(gl);
			girlRotateFront.transformModel(gl);

			// gl.glEnable(GL10.GL_BLEND);
			gl.glTranslatef(0, 0f, -62f);

			gl.glTranslatef((float) x, (float) y, (float) z);
			float angle = (float) (-sceneState.pictureViewGallary.pictureView[i].radian * 180 / PI) % 360;

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

		}

		// get current millis
		long currentMillis = System.currentTimeMillis();

		// update rotations
		if (lastMillis != 0) {
			long delta = currentMillis - lastMillis;
			sceneState.pictureViewGallary.dAngle += sceneState.pictureViewGallary.dxSpeed
					* delta;
			sceneState.pictureViewGallary.dampenSpeed(delta);
		}

		// update millis
		lastMillis = currentMillis;
		if (lastMillis != 0) {
			long delta = currentMillis - lastMillis;
			sceneState.pictureViewGallary.dx += sceneState.pictureViewGallary.dxSpeed
					* delta;
			sceneState.pictureViewGallary.dy += sceneState.pictureViewGallary.dySpeed
					* delta;
			// sceneState.pictureViewGallary.dampenSpeed(delta);
		}

	}

	private IntBuffer texturesBuffer;

	public int POLYGON = 3;
	public int BACKGROUND = 5;
	public int GIRLSINDEX = 6;
	public int GIRLSINDEX_S = 16;

	public int textureNum = 26;

	private void loadTexture(GL10 gl) {
		gl.glEnable(GL10.GL_TEXTURE_2D);
		texturesBuffer = IntBuffer.allocate(textureNum);
		gl.glGenTextures(textureNum, texturesBuffer);

		Bitmap[] texture = new Bitmap[textureNum];
		texture[0] = Utils.getTextureFromBitmapResource(context,
				R.drawable.girl4_1);
		texture[1] = Utils.getTextureFromBitmapResource(context,
				R.drawable.girl4_2);
		texture[2] = Utils.getTextureFromBitmapResource(context,
				R.drawable.girl4_3);
		texture[POLYGON + 0] = Utils.getTextureFromBitmapResource(context,
				R.drawable.polygon_locked);
		texture[POLYGON + 1] = Utils.getTextureFromBitmapResource(context,
				R.drawable.polygon_cupid);
		texture[BACKGROUND + 0] = Utils.getTextureFromBitmapResource(context,
				R.drawable.gameentry_background);
		texture[GIRLSINDEX + 0] = Utils.getTextureFromBitmapResource(context,
				R.drawable.number_0);
		texture[GIRLSINDEX + 1] = Utils.getTextureFromBitmapResource(context,
				R.drawable.number_1);
		texture[GIRLSINDEX + 2] = Utils.getTextureFromBitmapResource(context,
				R.drawable.number_2);
		texture[GIRLSINDEX + 3] = Utils.getTextureFromBitmapResource(context,
				R.drawable.number_3);
		texture[GIRLSINDEX + 4] = Utils.getTextureFromBitmapResource(context,
				R.drawable.number_4);
		texture[GIRLSINDEX + 5] = Utils.getTextureFromBitmapResource(context,
				R.drawable.number_5);
		texture[GIRLSINDEX + 6] = Utils.getTextureFromBitmapResource(context,
				R.drawable.number_6);
		texture[GIRLSINDEX + 7] = Utils.getTextureFromBitmapResource(context,
				R.drawable.number_7);
		texture[GIRLSINDEX + 8] = Utils.getTextureFromBitmapResource(context,
				R.drawable.number_8);
		texture[GIRLSINDEX + 9] = Utils.getTextureFromBitmapResource(context,
				R.drawable.number_9);

		texture[GIRLSINDEX_S + 0] = Utils.getTextureFromBitmapResource(context,
				R.drawable.number_s_0);
		texture[GIRLSINDEX_S + 1] = Utils.getTextureFromBitmapResource(context,
				R.drawable.number_s_1);
		texture[GIRLSINDEX_S + 2] = Utils.getTextureFromBitmapResource(context,
				R.drawable.number_s_2);
		texture[GIRLSINDEX_S + 3] = Utils.getTextureFromBitmapResource(context,
				R.drawable.number_s_3);
		texture[GIRLSINDEX_S + 4] = Utils.getTextureFromBitmapResource(context,
				R.drawable.number_s_4);
		texture[GIRLSINDEX_S + 5] = Utils.getTextureFromBitmapResource(context,
				R.drawable.number_s_5);
		texture[GIRLSINDEX_S + 6] = Utils.getTextureFromBitmapResource(context,
				R.drawable.number_s_6);
		texture[GIRLSINDEX_S + 7] = Utils.getTextureFromBitmapResource(context,
				R.drawable.number_s_7);
		texture[GIRLSINDEX_S + 8] = Utils.getTextureFromBitmapResource(context,
				R.drawable.number_s_8);
		texture[GIRLSINDEX_S + 9] = Utils.getTextureFromBitmapResource(context,
				R.drawable.number_s_9);

		for (int i = 0; i < textureNum; i++) {

			gl.glBindTexture(GL10.GL_TEXTURE_2D, texturesBuffer.get(i));
			gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
					GL10.GL_NEAREST);
			gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
					GL10.GL_NEAREST);
			gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,
					GL10.GL_CLAMP_TO_EDGE);
			gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,
					GL10.GL_CLAMP_TO_EDGE);
			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, texture[i], 0);

			texture[i].recycle();
		}

	}

}

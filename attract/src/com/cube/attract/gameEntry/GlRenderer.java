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

	private final static float lightAmb[] = { 0.5f, 0.5f, 0.5f, 1.0f };
	private final static float lightDif[] = { 1.0f, 1.0f, 1.0f, 1.0f };
	private final static float lightPos[] = { 0.0f, 0.0f, 2.0f, 1.0f };

	private final static FloatBuffer lightAmbBfr;
	private final static FloatBuffer lightDifBfr;
	private final static FloatBuffer lightPosBfr;

	// private static float[] quadVertexLogo = new float[] { -1.1f, 1f, 0,
	// -1.1f,
	// -1f, 0, 1.1f, 1f, 0, 1.1f, -1f, 0 };
	final static float UNIT_SIZE = 0.35f;
	static float[] quadVertexLogo = new float[] { 
		0 * UNIT_SIZE, 0 * UNIT_SIZE,0, 
		0 * UNIT_SIZE, 1 * UNIT_SIZE, 0,
		0.8f * UNIT_SIZE,0.5f * UNIT_SIZE, 0,
		0.8f * UNIT_SIZE, -0.5f * UNIT_SIZE, 0,
		0 * UNIT_SIZE, -1 * UNIT_SIZE, 0,
		-0.8f * UNIT_SIZE,-0.5f * UNIT_SIZE, 0,
		-0.8f * UNIT_SIZE, 0.5f * UNIT_SIZE, 0 };

	private static ByteBuffer mIndexBuffer; // 顶点构建索引数据缓冲

	private static float[] quadTextureLogo = new float[] { 0.5f, 0.5f, 1f, 0.25f,0.5f, 0,
		0.5f, 0.5f,1f, 0.75f,1f, 0.25f,
		0.5f, 0.5f,0.5f, 1f, 1f, 0.75f,
		0.5f, 0.5f,0, 0.75f,0.5f, 1f,
		0.5f, 0.5f,0, 0.25f,0, 0.75f,
		0.5f, 0.5f,0.5f, 0,0, 0.25f,};
	
/*	private static float[] quadTextureLogo = new float[] { 0.5f, 0.5f, 0.5f, 0,
			1f, 0.25f, 1f, 0.75f,
			0.5f, 1f, 0, 0.75f,
			0, 0.25f };*/

	private static FloatBuffer quadVertexBufferLogo;
	private static FloatBuffer quadTextureBufferLogo;

	private static float[] quadVertexGirl = new float[] { -9.0f, 16.0f, 0,
			-9.0f, -16.0f, 0, 9.0f, 16.0f, 0, 9.0f, -16.0f, 0 };

	private static float[] quadTextureGirl = new float[] { 0, 1, 0, 0, 1, 1, 1,
			0 };

	private static FloatBuffer quadVertexBufferGirl;
	private static FloatBuffer quadTextureBufferGirl;

	SceneState sceneState = SceneState.getInstance();

	static {

        byte indices[]=new byte[]{
            	0,2,1,
            	0,3,2,
            	0,4,3,
            	0,5,4,
            	0,6,5,
            	0,1,6
            };
		// 创建三角形构造索引数据缓冲
		mIndexBuffer = ByteBuffer.allocateDirect(indices.length);
		mIndexBuffer.put(indices);// 向缓冲区中放入三角形构造索引数据
		mIndexBuffer.position(0);// 设置缓冲区起始位置

		lightAmbBfr = BufferUtil.floatToBuffer(lightAmb);
		lightDifBfr = BufferUtil.floatToBuffer(lightDif);
		lightPosBfr = BufferUtil.floatToBuffer(lightPos);

		quadVertexBufferLogo = BufferUtil.floatToBuffer(quadVertexLogo);
		quadTextureBufferLogo = BufferUtil.floatToBuffer(quadTextureLogo);

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

	public void initializeAnimations() {
		girlGoBack.setTranslate(0, -11.5f, -5.5f, 200f);
		girlGoBack.start(false);
		girlGoFront.setTranslate(0, 11.5f, 5.5f, 200f);
		girlGoFront.start(false);
		girlRotateBack.setRotate(15, 1, 0, 0, 200f);
		girlRotateBack.start(false);
		girlRotateFront.setRotate(-15, 1, 0, 0, 200f);
		girlRotateFront.start(false);
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

		drawPolygon(gl,0,-0.316f);
		drawPolygon(gl,-1.6f*UNIT_SIZE,-0.31616f);
		drawPolygon(gl,1.6f*UNIT_SIZE,-0.31616f);
		drawPolygon(gl,-0.8f*UNIT_SIZE,-0.316f-1.5f*UNIT_SIZE);
		drawPolygon(gl,0.8f*UNIT_SIZE,-0.316f-1.5f*UNIT_SIZE);
		drawPolygon(gl,0,-0.316f-3*UNIT_SIZE);
		drawGirls(gl);

	}

	public void drawPolygon(GL10 gl,float xaxis,float yaxis) {

		gl.glBindTexture(GL10.GL_TEXTURE_2D, texturesBuffer.get(POLYGON + 1));
		gl.glLoadIdentity();

		gl.glEnable(GL10.GL_BLEND);

		gl.glTranslatef(xaxis, yaxis, -4.5f);

		// testAnimation.transformModel(gl);

		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, quadVertexBufferLogo);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, quadTextureBufferLogo);

		gl.glDrawArrays(GL10.GL_TRIANGLES, 0, 4);
//		gl.glDrawElements(GL10.GL_TRIANGLES, 18, GL10.GL_UNSIGNED_BYTE, mIndexBuffer);

		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glDisable(GL10.GL_TEXTURE_2D);

	}

	public long lastMillis = 0;
	double PI = Math.PI;

	public void drawGirls(GL10 gl) {

		sceneState.pictureViewGallary.movement();
		gl.glColor4f(1.0f, 1.0f, 1.0f, 0.3f);
		for (int i = 0; i < sceneState.pictureViewGallary.viewsNum; i++) {
			int index = 0;
			index = (i) % sceneState.pictureViewGallary.viewsNum;
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
			gl.glTranslatef(0, 0f, -63f);

			gl.glTranslatef((float) x, (float) y, (float) z);
			float angle = (float) (-sceneState.pictureViewGallary.pictureView[i].radian * 180 / PI) % 360;
			// if (angle < 180 && angle > 0) {
			// continue;
			// }

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
			sceneState.pictureViewGallary.dampenSpeed(delta);
		}
		// sceneState.pictureViewGallary.isStopmoving();

	}

	private IntBuffer texturesBuffer;

	public int POLYGON = 3;
	public int BACKGROUND = 5;

	public int textureNum = 6;

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
		// texture[POLYGON + 0] = Utils.getTextureFromBitmapResource(context,
		// R.drawable.polygon);
//		texture[POLYGON + 0] = Utils.getTextureFromBitmapResource(context,
//				R.drawable.six);
		texture[POLYGON + 0] = Utils.getTextureFromBitmapResource(context,
				R.drawable.polygon_locked);
		texture[POLYGON + 1] = Utils.getTextureFromBitmapResource(context,
				R.drawable.polygon_cupid);
		texture[BACKGROUND + 0] = Utils.getTextureFromBitmapResource(context,
				R.drawable.gameentry_background);

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

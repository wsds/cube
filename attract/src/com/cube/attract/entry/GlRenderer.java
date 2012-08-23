package com.cube.attract.entry;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import ro.brite.android.opengl.common.GlMatrix;
import ro.brite.android.opengl.common.Utils;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLUtils;
import android.util.Log;

import com.cube.attract.R;
import com.cube.common.pickup.IBufferFactory;
import com.cube.common.pickup.Matrix4f;
import com.cube.common.pickup.Ray;
import com.cube.common.pickup.Vector3f;

public class GlRenderer implements Renderer {

	private Context context;
	private Activity mActivity;
	private Vector3f mvEye = new Vector3f(0, 0, 6f), mvCenter = new Vector3f(0, 0, 0), mvUp = new Vector3f(0, 1, 0);
	private Cube cube;
	public GlRenderer(Context context) {
		this.context = context;
		this.mActivity = (Activity) context;
		cube=new Cube();
	}

//	private final static float[][] cubeVertexCoords = new float[][] { new float[] { // top
//			1, 1, -1, -1, 1, -1, -1, 1, 1, 1, 1, 1 }, new float[] { // bottom
//			1, -1, 1, -1, -1, 1, -1, -1, -1, 1, -1, -1 }, new float[] { // front
//			1, 1, 1, -1, 1, 1, -1, -1, 1, 1, -1, 1 }, new float[] { // back
//			1, -1, -1, -1, -1, -1, -1, 1, -1, 1, 1, -1 }, new float[] { // left
//			-1, 1, 1, -1, 1, -1, -1, -1, -1, -1, -1, 1 }, new float[] { // right
//			1, 1, -1, 1, 1, 1, 1, -1, 1, 1, -1, -1 }, };
//
//	private final static float[][] cubeNormalCoords = new float[][] { new float[] { // top
//			0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0 }, new float[] { // bottom
//			0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0 }, new float[] { // front
//			0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1 }, new float[] { // back
//			0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1 }, new float[] { // left
//			-1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0 }, new float[] { // right
//			1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0 }, };
//
//	private final static float[][] cubeTextureCoords = new float[][] { new float[] { // top
//			1, 0, 1, 1, 0, 1, 0, 0 }, new float[] { // bottom
//			0, 0, 1, 0, 1, 1, 0, 1 }, new float[] { // front
//			1, 1, 0, 1, 0, 0, 1, 0 }, new float[] { // back
//			0, 1, 0, 0, 1, 0, 1, 1 }, new float[] { // left
//			1, 1, 0, 1, 0, 0, 1, 0 }, new float[] { // right
//			0, 1, 0, 0, 1, 0, 1, 1 }, };

	private final static float lightAmb[] = { 0.5f, 0.5f, 0.5f, 1.0f };
	private final static float lightDif[] = { 1.0f, 1.0f, 1.0f, 1.0f };
	private final static float lightPos[] = { 0.0f, 0.0f, 2.0f, 1.0f };

//	private final static FloatBuffer[] cubeVertexBfr;
//	private final static FloatBuffer[] cubeNormalBfr;
//	private final static FloatBuffer[] cubeTextureBfr;

//	private final static FloatBuffer lightAmbBfr;
//	private final static FloatBuffer lightDifBfr;
//	private final static FloatBuffer lightPosBfr;

	private static float[] quadVertexLogo = new float[] { -1.0f, 0.2354f, 0, -1.0f, -0.2354f, 0, 1.0f, 0.2354f, 0, 1.0f, -0.2354f, 0 };

	private static float[] quadTextureLogo = new float[] { 0, 1, 0, 0, 1, 1, 1, 0 };

	private static FloatBuffer quadVertexBufferLogo;
	private static FloatBuffer quadTextureBufferLogo;

	private static float[] quadVertexBackground = new float[] { -8.0f, 8.0f, 0, -8.0f, -8.0f, 0, 8.0f, 8.0f, 0, 8.0f, -8.0f, 0 };

	private static float[] quadTextureBackground = new float[] { 0, 1, 0, 0, 1, 1, 1, 0 };

	private static FloatBuffer quadVertexBufferBackground;
	private static FloatBuffer quadTextureBufferBackground;

	SceneState sceneState = SceneState.getInstance();
	private long lastMillis;

	static {
//		cubeVertexBfr = new FloatBuffer[6];
//		cubeNormalBfr = new FloatBuffer[6];
//		cubeTextureBfr = new FloatBuffer[6];
//		for (int i = 0; i < 6; i++) {
//			cubeVertexBfr[i] = BufferUtil.floatToBuffer(cubeVertexCoords[i]);
//			cubeNormalBfr[i] = BufferUtil.floatToBuffer(cubeNormalCoords[i]);
//			cubeTextureBfr[i] = BufferUtil.floatToBuffer(cubeTextureCoords[i]);
//		}

//		lightAmbBfr = BufferUtil.floatToBuffer(lightAmb);
//		lightDifBfr = BufferUtil.floatToBuffer(lightDif);
//		lightPosBfr = BufferUtil.floatToBuffer(lightPos);

		quadVertexBufferLogo = getDirectBuffer(quadVertexLogo);
		quadTextureBufferLogo = getDirectBuffer(quadTextureLogo);

		quadVertexBufferBackground = getDirectBuffer(quadVertexBackground);
		quadTextureBufferBackground = getDirectBuffer(quadTextureBackground);

	}
	
	 
	public static FloatBuffer getDirectBuffer(float[] buffer) {
		ByteBuffer bb = ByteBuffer.allocateDirect(buffer.length * 4);
		bb.order(ByteOrder.nativeOrder());
		FloatBuffer directBuffer = bb.asFloatBuffer();
		directBuffer.put(buffer);
		directBuffer.position(0);
		return directBuffer;
	} 
	public static class BufferUtil {
	    public static FloatBuffer mBuffer;
	    public static FloatBuffer floatToBuffer(float[] a){
	    //鍏堝垵濮嬪寲buffer锛屾暟缁勭殑闀垮害*4锛屽洜涓轰竴涓猣loat鍗�涓瓧鑺�
	       ByteBuffer mbb = ByteBuffer.allocateDirect(a.length*4);
	    //鏁扮粍鎺掑簭鐢╪ativeOrder
	       mbb.order(ByteOrder.nativeOrder());
	       mBuffer = mbb.asFloatBuffer();
	       mBuffer.put(a);
	       mBuffer.position(0);
	       return mBuffer;
	    }
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// 全局性设置
		gl.glEnable(GL10.GL_DITHER);

		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
		// 设置清屏背景颜色
		// gl.glClearColor(0, 0, 0, 0);
		gl.glClearColor(0, 0, 0, 0);
		// 设置着色模型为平滑着色
		gl.glShadeModel(GL10.GL_SMOOTH);

		// 启用背面剪裁
		gl.glEnable(GL10.GL_CULL_FACE);
		gl.glCullFace(GL10.GL_BACK);
		// 启用深度测试
		gl.glEnable(GL10.GL_DEPTH_TEST);
		// 禁用光照和混合
		gl.glDisable(GL10.GL_LIGHTING);
		gl.glDisable(GL10.GL_BLEND);
		
		
//		gl.glShadeModel(GL10.GL_SMOOTH);
//		gl.glClearColor(0, 0, 0, 0);
//
//		gl.glClearDepthf(1.0f);
//		gl.glDisable(GL10.GL_DEPTH_TEST);
//		gl.glDepthFunc(GL10.GL_LEQUAL);
//
//		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
//
//		gl.glEnable(GL10.GL_CULL_FACE);
//		gl.glCullFace(GL10.GL_BACK);
//
//		// lighting
//		gl.glEnable(GL10.GL_LIGHT0);
//		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, lightAmbBfr);
//		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, lightDifBfr);
//		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, lightPosBfr);
//
//		// blending
//		gl.glColor4f(1.0f, 1.0f, 1.0f, 0.8f);
//		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE);
		// reload textures
		loadTexture(gl);
		sceneState.gMatModel.setIdentity();
	}

//	public GLAnimation testAnimation = new GLAnimation();
//	public GLAnimation test1Animation = new GLAnimation();
//	public GLAnimation test2Animation = new GLAnimation();
//	public GLAnimation test3Animation = new GLAnimation();
//	public GLAnimation test4Animation = new GLAnimation();
//	public GLAnimation test5Animation = new GLAnimation();
//
//	public GLAnimation rotate1Animation = new GLAnimation();
//	public GLAnimation rotate2Animation = new GLAnimation();
//	public GLAnimation cube1Animation = new GLAnimation();
//	public GLAnimation cube2Animation = new GLAnimation();
//	
//	public GLAnimation logoDown = new GLAnimation();
//	public GLAnimation logoUp = new GLAnimation();
//	

	public void onSurfaceChanged(GL10 gl, int width, int height) {
		sceneState.screenHeight=height;
		sceneState.screenWidth=width;


		// avoid division by zero
		if (height == 0)
			height = 1;
		// draw on the entire screen
		gl.glViewport(0, 0, width, height);
		sceneState.gpViewport[0] = 0;
		sceneState.gpViewport[1] = 0;
		sceneState.gpViewport[2] = width;
		sceneState.gpViewport[3] = height;
		float ratio = (float) width / height;// 屏幕宽高比
		// setup projection matrix
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
//		GLU.gluPerspective(gl, 45.0f, (float) width / (float) height, 1.0f, 100.0f);
		Matrix4f.gluPersective(45.0f, ratio, 1.0f, 10.0f, sceneState.gMatProject);
		gl.glLoadMatrixf(sceneState.gMatProject.asFloatBuffer());
		sceneState.gMatProject.fillFloatArray(sceneState.gpMatrixProjectArray);
		// 每次修改完GL_PROJECTION后，最好将当前矩阵模型设置回GL_MODELVIEW
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		
//		testAnimation.setTranslate(0.0f, 0f, -0.3f, 1000.0f);
//
//		cube1Animation.setTranslate(0.0f, -0.0f, 9.0f, 3000.0f);
//		cube1Animation.addNextAnimation(cube2Animation);
//		cube2Animation.setTranslate(0.0f, -0.5f, -1.0f, 3000.0f);

//		testAnimation.addNextAnimation(test1Animation);
//		test1Animation.setRepeatTimes(5);
//		
//		test1Animation.setTranslate(-1f, -1f, -0f, 1000.0f);
//		test1Animation.addNextAnimation(test2Animation);
//		test2Animation.setTranslate(1f, 1f, -0f, 1000.0f);
//		test2Animation.addNextAnimation(test3Animation);
//		test2Animation.addNextAnimation(test4Animation);
//		test3Animation.setTranslate(0.0f, -1f, -2f, 1000.0f);
//		test4Animation.setTranslate(-1f, 0f, -3f, 2000.0f);
//		test4Animation.addNextAnimation(test5Animation);
//		test5Animation.setTranslate(1f, 1f, 5f, 1000.0f);
//
//		rotate1Animation.setRotate(360f, 0, 0f, -1f, 30000f);
//		rotate1Animation.setRepeatTimes(GLAnimation.INFINITE);
//		
//		testAnimation.addNextAnimation(rotate2Animation);
//
//		rotate2Animation.setRotate(1440f, 0, 1f, 0f, 5000f);
		
//		logoDown.setTranslate(0f, 0.1f, -0.5f, 100.0f);
//		logoDown.start(false);
//		logoUp.setTranslate(0f, -0.1f, 0.5f, 100.0f);
//		logoUp.start(false);
//		
//		
//		logoUp.setCallback(new GLAnimation.Callback() {
//			public void onEnd() {
//				Intent about = new Intent(Intent.ACTION_MAIN);
//				about.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//				about.setClassName("com.cube.attract", "com.cube.attract.gameEntry.GameEntryActivity");
//				context.startActivity(about);
//			}
//		});

	}

	public void onDrawFrame(GL10 gl) {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		setUpCamera(gl);
		// update lighting
//		if (sceneState.lighting) {
//			gl.glEnable(GL10.GL_LIGHTING);
//		} else {
			gl.glDisable(GL10.GL_LIGHTING);
//		}

		// update blending
		if (sceneState.blending) {
			gl.glEnable(GL10.GL_BLEND);
			gl.glDisable(GL10.GL_CULL_FACE);
		} else {
			gl.glDisable(GL10.GL_BLEND);
			gl.glEnable(GL10.GL_CULL_FACE);
		}
		gl.glPushMatrix();
		{
					// draw cube

		gl.glTranslatef(0,-1, 0);

//		cube1Animation.transformModel(gl);

		sceneState.rotateModel(gl);
		// 设置默认颜色
		gl.glColor4f(1.0f, 1.0f, 1.0f, 0.0f);


		
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, cube.getCoordinate(Cube.VERTEX_BUFFER));
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, cube.getCoordinate(Cube.TEXTURE_BUFFER));
		for (int i = 0; i < 6; i++) // draw each face
		{
			gl.glBindTexture(GL10.GL_TEXTURE_2D, texturesBuffer.get(i));
			gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, (i+1)*4, GL10.GL_UNSIGNED_BYTE, cube.getIndices());
		}
//		for (int i = 0; i < 6; i++) // draw each face
//		{
//			gl.glBindTexture(GL10.GL_TEXTURE_2D, texturesBuffer.get(i));
//			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, Cube.cubeVertexBfr[i]);
//			gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, Cube.cubeTextureBfr[i]);
//			gl.glNormalPointer(GL10.GL_FLOAT, 0, Cube.cubeNormalBfr[i]);
//			gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, 4);
//		}
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glDisable(GL10.GL_TEXTURE_2D);


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
		gl.glPopMatrix();
		drawLogo(gl);
		drawBackground(gl);
		updatePick();
	}

	private Vector3f transformedSphereCenter = new Vector3f();
	private Ray transformedRay = new Ray();
	private Matrix4f matInvertModel = new Matrix4f();
	private Vector3f[] mpTriangle = { new Vector3f(), new Vector3f(), new Vector3f(), new Vector3f() };
	private FloatBuffer mBufPickedTriangle = IBufferFactory.newFloatBuffer(4 * 3);

	/**
	 * 更新拾取事件
	 */
	private void updatePick() {
		if (!sceneState.gbNeedPick) {
			return;
		}
		sceneState.gbNeedPick = false;
		// 更新最新的拾取射线
		PickFactory.update(sceneState.x, sceneState.y);
		// 获得最新的拾取射线
		Ray ray = PickFactory.getPickRay();

		GlMatrix translation = new GlMatrix();
		translation.translate(0, -1, 0);
		translation.multiply(sceneState.baseMatrix);
		sceneState.gMatModel.fillMatrix(translation.data);
		// 首先把模型的绑定球通过模型矩阵，由模型局部空间变换到世界空间
		sceneState.gMatModel.transform(cube.getSphereCenter(), transformedSphereCenter);
	
			Log.i("cubSphereCenter and transformedSphereCenter", String.valueOf(cube.getSphereCenter().x)+
					String.valueOf(cube.getSphereCenter().y)+String.valueOf(cube.getSphereCenter().z)+"and"+
					transformedSphereCenter.x+transformedSphereCenter.y+transformedSphereCenter.z
					);
		
		

		// 触碰的立方体面的标记为无
		cube.surface = -1;

		// 首先检测拾取射线是否与模型绑定球发生相交
		// 这个检测很快，可以快速排除不必要的精确相交检测
		if (ray.intersectSphere(transformedSphereCenter, cube.getSphereRadius())) {
			// 如果射线与绑定球发生相交，那么就需要进行精确的三角面级别的相交检测
			// 由于我们的模型渲染数据，均是在模型局部坐标系中
			// 而拾取射线是在世界坐标系中
			// 因此需要把射线转换到模型坐标系中
			// 这里首先计算模型矩阵的逆矩阵
			matInvertModel.set(sceneState.gMatModel);
			matInvertModel.invert();
			// 把射线变换到模型坐标系中，把结果存储到transformedRay中
			ray.transform(matInvertModel, transformedRay);
			// 将射线与模型做精确相交检测
			if (cube.intersect(transformedRay, mpTriangle)) {
				// 如果找到了相交的最近的三角形
				sceneState.gbTrianglePicked = true;
				// 触碰了哪一个面
				Log.i("触碰的立方体面", "=标记=" + cube.surface);
				// 回调
				// if (null != onSurfacePickedListener) {
				// onSurfacePickedListener.onSurfacePicked(cube.surface);
				// }
				// 填充数据到被选取三角形的渲染缓存中
				mBufPickedTriangle.clear();
				for (int i = 0; i < 4; i++) {
					IBufferFactory.fillBuffer(mBufPickedTriangle, mpTriangle[i]);
					// Log.i("点" + i, mpTriangle[i].x + "\t" + mpTriangle[i].y
					// + "\t" + mpTriangle[i].z);
				}
				mBufPickedTriangle.position(0);
			}
		} else {
			sceneState.gbTrianglePicked = false;
		}
	}
	public void drawLogo(GL10 gl) {

		gl.glBindTexture(GL10.GL_TEXTURE_2D, texturesBuffer.get(LOGO + 0));
		gl.glLoadIdentity();

		// gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
//		gl.glEnable(GL10.GL_BLEND);

		gl.glTranslatef(0, 1.2f, -3.8f);

//		testAnimation.transformModel(gl);
//		logoDown.transformModel(gl);
//		logoUp.transformModel(gl);

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

//		rotate1Animation.transformModel(gl);

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

	/**
	 * 设置相机矩阵
	 * 
	 * @param gl
	 */
	private void setUpCamera(GL10 gl) {
		// 设置模型视图矩阵
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		// GLU.gluLookAt(gl, mfEyeX, mfEyeY, mfEyeZ, mfCenterX, mfCenterY,
		// mfCenterZ, 0, 1, 0);//系统提供
		Matrix4f.gluLookAt(mvEye, mvCenter, mvUp, sceneState.gMatView);
		gl.glLoadMatrixf(sceneState.gMatView.asFloatBuffer());
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
			gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_NEAREST);
			gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
			gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
			gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, texture[i], 0);

			texture[i].recycle();
		}

	}

}

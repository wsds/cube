package Neibu.main.beta;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import org.join.ogles.lib.IBufferFactory;
import org.join.ogles.lib.Matrix4f;
import org.join.ogles.lib.Ray;
import org.join.ogles.lib.Vector3f;

import ro.brite.android.opengl.common.GlMatrix;
import ro.brite.android.opengl.common.Utils;
import Neibu.main.beta.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLUtils;
import android.util.Log;

public class RayPickRenderer implements Renderer {

	private Context context;
	private Cube cube;

	int texture = -1;

	public float mfAngleX = 0.0f;
	public float mfAngleY = 0.0f;

	public float gesDistance = 0.0f;
	
	private static float[] quadVertexCoords = new float[] { -0.5f, 0.33f, 0, -0.5f, -0.33f, 0, 0.5f, 0.33f, 0, 0.5f, -0.33f, 0 };
	private static float[] quadVertexCoords1 = new float[] { -1.0f, 0.33f, 0, -1.0f, -0.33f, 0, 1.0f, 0.33f, 0, 1.0f, -0.33f, 0 };

	private static float[] quadTextureCoords = new float[] { 0, 1, 0, 0, 1, 1, 1, 0 };

	private static FloatBuffer quadVertexBuffer;
	private static FloatBuffer quadVertexBuffer1;
	private static FloatBuffer quadTextureBuffer;
	public SceneState sceneState=SceneState.getInstance();
	
	static {
		quadVertexBuffer = getDirectBuffer(quadVertexCoords);
		quadVertexBuffer1 = getDirectBuffer(quadVertexCoords1);
		quadTextureBuffer = getDirectBuffer(quadTextureCoords);
	}
	
	public static FloatBuffer getDirectBuffer(float[] buffer) {
		ByteBuffer bb = ByteBuffer.allocateDirect(buffer.length * 4);
		bb.order(ByteOrder.nativeOrder());
		FloatBuffer directBuffer = bb.asFloatBuffer();
		directBuffer.put(buffer);
		directBuffer.position(0);
		return directBuffer;
	}

	// 观察者、中心和上方
	private Vector3f mvEye = new Vector3f(0, 0, 6f), mvCenter = new Vector3f(0, 0, 0), mvUp = new Vector3f(0, 1, 0);


	public RayPickRenderer(Context context) {
		this.context = context;
		cube = new Cube();
	}

	/**
	 * 逐帧渲染
	 */
	@Override
	public void onDrawFrame(GL10 gl) {

		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT); // 清除屏幕和深度缓存
		gl.glLoadIdentity(); // 重置当前的模型观察矩阵

		// 紧接着设置模型视图矩阵
		setUpCamera(gl);

		gl.glPushMatrix();
		{
			// 渲染物体
			drawCub(gl);
		}
		gl.glPopMatrix();

		gl.glPushMatrix();
		{
			// 渲染射线
//			PickFactory.getPickRay().draw(gl);
		}
		gl.glPopMatrix();

		gl.glPushMatrix();
		{
			// 渲染选中的三角形
			drawPickedTriangle(gl);
		}
		gl.glPopMatrix();
//		
//		gl.glEnable(GL10.GL_BLEND);
//		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE);
		drawBrands(gl);
		drawImpression(gl);

		updatePick();
	}
	public long lastMillis=0;

	public void drawBrands(GL10 gl) {
		sceneState.movement();
		gl.glColor4f(1.0f, 1.0f, 1.0f, 0.0f);

		for (int i = 0; i < sceneState.viewsNum; i++) {
			int index = 0;
			index = (i + sceneState.index) % sceneState.viewsNum;
			int brand = sceneState.brandViews[index].brand;
			float x = sceneState.brandViews[index].x;
			float y = sceneState.brandViews[index].y;
			float z = sceneState.brandViews[index].z;

			gl.glBindTexture(GL10.GL_TEXTURE_2D, texturesBuffer.get(BRAND+brand));
			gl.glLoadIdentity();
			gl.glTranslatef(0, 1.25f, sceneState.zoom);

			gl.glTranslatef(x, y, z);
			gl.glRotatef(sceneState.brandViews[index].angle, 0, -1, 0);

			gl.glEnable(GL10.GL_TEXTURE_2D);
			gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
			gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, quadVertexBuffer);
			gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, quadTextureBuffer);

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
			sceneState.dx_BRAND += sceneState.dxSpeed_BRAND * delta;
			sceneState.dy_BRAND += sceneState.dySpeed_BRAND * delta;
			sceneState.dampenSpeed_BRAND(delta);
		}

		// update millis
		lastMillis = currentMillis;
		sceneState.isStopmoving();
	}

	public int impression = 0;
	public float px = 0;

	public void drawImpression(GL10 gl) {
		
		if (Math.abs(px - sceneState.dx_BRAND) > 40) {
			px = sceneState.dx_BRAND;
			impression = (impression + 1) % 5;
		}

		gl.glBindTexture(GL10.GL_TEXTURE_2D, texturesBuffer.get(IMPRESSIONINDEX + impression));
		gl.glLoadIdentity();
		gl.glTranslatef(0, 1.4f, sceneState.zoom + 2.5f);

		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, quadVertexBuffer1);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, quadTextureBuffer);

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

	// private Matrix4f matRotX = new Matrix4f();
	// private Matrix4f matRotY = new Matrix4f();

	private long lastMillis1 = 0;

	/**
	 * 渲染模型
	 */
	private void drawCub(GL10 gl) {

		gl.glTranslatef(0, -1, 0);
		sceneState.rotateModel(gl);

		// 设置默认颜色
		gl.glColor4f(1.0f, 1.0f, 1.0f, 0.0f);


		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, cube.getCoordinate(Cube.VERTEX_BUFFER));
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, cube.getCoordinate(Cube.TEXTURE_BUFFER));

		for (int i = 0; i < 6; i++) // draw each face
		{
			gl.glBindTexture(GL10.GL_TEXTURE_2D, texturesBuffer.get(i));
			gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, (i+1)*4, GL10.GL_UNSIGNED_BYTE, cube.getIndices());
		}
		gl.glDisable(GL10.GL_TEXTURE_2D);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);

		// get current millis
		long currentMillis = System.currentTimeMillis();

		// update rotations
		if (lastMillis1 != 0) {
			long delta = currentMillis - lastMillis1;
			sceneState.dx_CUB += sceneState.dxSpeed_CUB * delta;
			sceneState.dy_CUB += sceneState.dySpeed_CUB * delta;
			sceneState.dampenSpeed_CUB(delta);
		}

		// update millis
		lastMillis1 = currentMillis;

		// 渲染坐标系
//		drawCoordinateSystem(gl);
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

	/**
	 * 渲染选中的三角形
	 */
	private void drawPickedTriangle(GL10 gl) {
		if (!sceneState.gbTrianglePicked) {
			return;
		}
		// 由于返回的拾取三角形数据是出于模型坐标系中
		// 因此需要经过模型变换，将它们变换到世界坐标系中进行渲染
		// 设置模型变换矩阵
		gl.glTranslatef(0, -1, 0);
		sceneState.rotateModel(gl);
		// gl.glMultMatrixf(AppConfig.gMatModel.asFloatBuffer());
		// 设置三角形颜色，alpha为0.7
		gl.glColor4f(0.3f, 0.3f, 0.3f, 0.5f);
		// 开启Blend混合模式
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		// 禁用无关属性，仅仅使用纯色填充
		gl.glDisable(GL10.GL_DEPTH_TEST);
		gl.glDisable(GL10.GL_TEXTURE_2D);
		// 开始绑定渲染顶点数据
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mBufPickedTriangle);
		// 提交渲染
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		// 重置相关属性
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glDisable(GL10.GL_BLEND);
	}

	/**
	 * 渲染坐标系
	 */
	private void drawCoordinateSystem(GL10 gl) {
		// 暂时禁用深度测试
		gl.glDisable(GL10.GL_DEPTH_TEST);
		// 设置点和线的宽度
		gl.glLineWidth(2.0f);
		// 仅仅启用顶点数据
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

		FloatBuffer fb = IBufferFactory.newFloatBuffer(3 * 2);
		fb.put(new float[] { 0, 0, 0, 1.4f, 0, 0 });
		fb.position(0);

		// 渲染X轴
		gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);// 设置红色
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, fb);
		// 提交渲染
		gl.glDrawArrays(GL10.GL_LINES, 0, 2);

		fb.clear();
		fb.put(new float[] { 0, 0, 0, 0, 1.4f, 0 });
		fb.position(0);
		// 渲染Y轴
		gl.glColor4f(0.0f, 1.0f, 0.0f, 1.0f);// 设置绿色
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, fb);
		// 提交渲染
		gl.glDrawArrays(GL10.GL_LINES, 0, 2);

		fb.clear();
		fb.put(new float[] { 0, 0, 0, 0, 0, 1.4f });
		fb.position(0);
		// 渲染Z轴
		gl.glColor4f(0.0f, 0.0f, 1.0f, 1.0f);// 设置蓝色
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, fb);
		// 提交渲染
		gl.glDrawArrays(GL10.GL_LINES, 0, 2);

		// 重置
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glLineWidth(1.0f);
		gl.glEnable(GL10.GL_DEPTH_TEST);
	}

	/**
	 * 创建绘图表面时调用
	 */
	@Override
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

		
		
		loadTexture(gl);

		sceneState.gMatModel.setIdentity();
	}

	/**
	 * 当绘图表面尺寸发生改变时调用
	 */
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		
	
		// 设置视口
		gl.glViewport(0, 0, width, height);
		sceneState.gpViewport[0] = 0;
		sceneState.gpViewport[1] = 0;
		sceneState.gpViewport[2] = width;
		sceneState.gpViewport[3] = height;

		// 设置投影矩阵
		float ratio = (float) width / height;// 屏幕宽高比
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		// GLU.gluPerspective(gl, 45.0f, ratio, 1, 5000);系统提供
		Matrix4f.gluPersective(45.0f, ratio, 1, 10, sceneState.gMatProject);
		gl.glLoadMatrixf(sceneState.gMatProject.asFloatBuffer());
		sceneState.gMatProject.fillFloatArray(sceneState.gpMatrixProjectArray);
		// 每次修改完GL_PROJECTION后，最好将当前矩阵模型设置回GL_MODELVIEW
		gl.glMatrixMode(GL10.GL_MODELVIEW);
	}

	private IntBuffer texturesBuffer;
	
	public int BRAND=6;
	public int IMPRESSIONINDEX=12;
	
	public int textureNum=17;

	private void loadTexture(GL10 gl) {
		// create textures
		gl.glEnable(GL10.GL_TEXTURE_2D);
		texturesBuffer = IntBuffer.allocate(textureNum);
		gl.glGenTextures(textureNum, texturesBuffer);

		// load bitmap
		Bitmap[] texture = new Bitmap[textureNum];
		texture[0] = Utils.getTextureFromBitmapResource(context, R.drawable.nav_diandiandian);
		texture[1] = Utils.getTextureFromBitmapResource(context, R.drawable.nav_chuihuaban);
		texture[2] = Utils.getTextureFromBitmapResource(context, R.drawable.nav_mofan);
		texture[3] = Utils.getTextureFromBitmapResource(context, R.drawable.nav_kuanyi);
		texture[4] = Utils.getTextureFromBitmapResource(context, R.drawable.nav_mashaike);
		texture[5] = Utils.getTextureFromBitmapResource(context, R.drawable.nav_touneiku);
		
		texture[BRAND+0] = Utils.getTextureFromBitmapResource(context, R.drawable.brand1);
		texture[BRAND+1] = Utils.getTextureFromBitmapResource(context, R.drawable.brand2);
		texture[BRAND+2] = Utils.getTextureFromBitmapResource(context, R.drawable.brand3);
		texture[BRAND+3] = Utils.getTextureFromBitmapResource(context, R.drawable.brand4);
		texture[BRAND+4] = Utils.getTextureFromBitmapResource(context, R.drawable.brand5);
		texture[BRAND+5] = Utils.getTextureFromBitmapResource(context, R.drawable.brand6);
		texture[IMPRESSIONINDEX+0] = Utils.getTextureFromBitmapResource(context, R.drawable.impressionindex0);
		texture[IMPRESSIONINDEX+1] = Utils.getTextureFromBitmapResource(context, R.drawable.impressionindex1);
		texture[IMPRESSIONINDEX+2] = Utils.getTextureFromBitmapResource(context, R.drawable.impressionindex2);
		texture[IMPRESSIONINDEX+3] = Utils.getTextureFromBitmapResource(context, R.drawable.impressionindex3);
		texture[IMPRESSIONINDEX+4] = Utils.getTextureFromBitmapResource(context, R.drawable.impressionindex4);
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

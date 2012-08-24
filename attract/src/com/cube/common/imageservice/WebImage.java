package com.cube.common.imageservice;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import com.cube.common.LocalData;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

public class WebImage {
	private static final String TAG = "ImageService";

	public static Context mContext = null;
	public static String app = "attract";

	public static boolean getBitmap(String url, String fileName) {
		boolean isLoaded = false;

		try {
			InputStream cipherBitmapStream = null;
			InputStream bitmapStream = null;
			cipherBitmapStream = loadStreamFromSDCard(fileName);
			if (cipherBitmapStream != null) {
				bitmapStream = DecryptInputStream(cipherBitmapStream);
			}
			if (bitmapStream == null) {
				bitmapStream = getImageStream(url);
				if (bitmapStream != null) {
					Log.d(TAG, fileName + " is downloaded!");
					cipherBitmapStream = EncryptInputStream(bitmapStream);
					saveStreamToSDCard(cipherBitmapStream, fileName);
					bitmapStream.close();
					cipherBitmapStream.close();
					cipherBitmapStream = loadStreamFromSDCard(fileName);
					if (cipherBitmapStream != null) {
						bitmapStream = DecryptInputStream(cipherBitmapStream);
					}
				} else {
					Log.d(TAG, fileName + " cannot be  downloaded!");
				}
			}

			if (bitmapStream != null) {
				// bitmap = BitmapFactory.decodeStream(bitmapStream);
				isLoaded = true;
				Log.d(TAG, fileName + " is loaded!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (isLoaded == false) {
			Log.d(TAG, fileName + " cannot be loaded!");
		}
		return isLoaded;
	}

	public static InputStream getImageStream(String url) throws Exception {
		HttpGet conn = new HttpGet(url);
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse response = (HttpResponse) httpclient.execute(conn);
		HttpEntity entity = response.getEntity();
		BufferedHttpEntity bufferedHttpEntity = new BufferedHttpEntity(entity);
		return bufferedHttpEntity.getContent();

	}

	public static InputStream loadStreamFromSDCard(String fileName) {
		InputStream fin = null;
		try {
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

				String SDCardPath = Environment.getExternalStorageDirectory() + "/DataService/" + "/" + app + "/image/";
				File saveFile = new File(SDCardPath, fileName);
				if (!saveFile.exists() && mContext != null) {
					fin = mContext.getResources().getAssets().open(fileName);
				} else {
					fin = new FileInputStream(saveFile);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return fin;
	}

	public static Bitmap loadBitmapFromSDCard(String fileName) {
		Bitmap bitmap = null;
		LocalData localData = LocalData.getInstance();
		if (localData.game.loadedPictures.contains(fileName)) {
			InputStream cipherBitmapStream = null;
			InputStream bitmapStream = null;

			cipherBitmapStream = WebImage.loadStreamFromSDCard(fileName);
			if (cipherBitmapStream != null) {
				bitmapStream = DecryptInputStream(cipherBitmapStream);
			}

			if (bitmapStream != null) {
				bitmap = BitmapFactory.decodeStream(bitmapStream);
			}
		}
		return bitmap;
	}

	public static void saveStreamToSDCard(InputStream inputStream, String fileName) throws IOException {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			String SDCardPath = Environment.getExternalStorageDirectory() + "/DataService/" + "/" + app + "/image/";

			File dirFile = new File(SDCardPath);
			if (!dirFile.exists()) {
				dirFile.mkdir();
			}
			File myCaptureFile = new File(SDCardPath + fileName);
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));

			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = inputStream.read(buffer)) != -1) {
				bos.write(buffer, 0, len);
			}
			bos.flush();
			bos.close();
		}
	}

	private static String sKey = "abcdef123456";

	public static InputStream EncryptInputStream(InputStream inputStream) {

		CipherInputStream cipherInputStream = null;
		try {
			int mode = Cipher.ENCRYPT_MODE;
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			byte[] keyData = sKey.getBytes();
			DESKeySpec keySpec = new DESKeySpec(keyData);
			Key key = keyFactory.generateSecret(keySpec);
			Cipher cipher = Cipher.getInstance("DES");
			cipher.init(mode, key);
			cipherInputStream = new CipherInputStream(inputStream, cipher);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cipherInputStream;
	}

	public static InputStream DecryptInputStream(InputStream inputStream) {

		CipherInputStream cipherInputStream = null;
		try {
			int mode = Cipher.DECRYPT_MODE;
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			byte[] keyData = sKey.getBytes();
			DESKeySpec keySpec = new DESKeySpec(keyData);
			Key key = keyFactory.generateSecret(keySpec);
			Cipher cipher = Cipher.getInstance("DES");
			cipher.init(mode, key);
			cipherInputStream = new CipherInputStream(inputStream, cipher);

		} catch (Exception e) {
		}
		return cipherInputStream;
	}

	public static void initializeWebData(Context context) {
		// here,we get a reference to the instance of the activity or the
		// service that the thread is hold by,
		// for access the files stored in the <assets> folder of the
		// application.
		mContext = context;
	}

}

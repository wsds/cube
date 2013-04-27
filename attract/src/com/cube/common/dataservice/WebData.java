package com.cube.common.dataservice;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.http.util.EncodingUtils;
import org.json.JSONObject;

import android.content.Context;
import android.os.Environment;

public class WebData {

	public Context mContext = null;
	
	public JSONObject get(String url) {
		JSONObject result = null;
		if (url != "") {
			result = WebInterface.getJSON(url);
		}
		return result;
	}

	public JSONObject get(String username, String app, String key) {
		String url = "";
		JSONObject result = null;
		if (url != "") {
			result = WebInterface.getJSON(url);
		}
		return result;
	}

	public void set(String username, String app, String key, JSONObject value) {
	}

	public void set(String username, String app, String key, JSONObject value, String path) {
	}

	public void add(String username, String app, String key, JSONObject value, String path) {
	}
	
	public void diff(String username, String app, String key, JSONObject value, String path) {
	}

	public void initializeWebData(Context mContext) {
		//here,we get a reference to the instance of the activity or the service that the thread is hold by, 
		//for access the files stored in the <assets> folder of the application.
		this.mContext = mContext;
	}

	public JSONObject loadFromSDCard(String app, String key) {
		JSONObject result = null;
		try {
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				String str = "";

				String SDCardPath = Environment.getExternalStorageDirectory() + "/DataService/" + "/" + app + "/";
				File saveFile = new File(SDCardPath, key + ".json");
				if (!saveFile.exists() && mContext != null) {
					InputStream fin = mContext.getResources().getAssets().open(key + ".json");
					int length = fin.available();
					byte[] buffer = new byte[length];
					fin.read(buffer);
					str = EncodingUtils.getString(buffer, "UTF-8");
					fin.close();
				} else {
					FileInputStream fin = new FileInputStream(saveFile);
					int length = fin.available();
					byte[] buffer = new byte[length];
					fin.read(buffer);
					str = EncodingUtils.getString(buffer, "UTF-8");
					fin.close();
				}
				
				int start = str.indexOf(123);
				str = str.substring(start);//work around here to normalize the JSONStr
				result = new JSONObject(str);
				str = result.optString("data");
			}
			else{
				String str = "";
				InputStream fin = mContext.getResources().getAssets().open(key + ".json");
				int length = fin.available();
				byte[] buffer = new byte[length];
				fin.read(buffer);
				str = EncodingUtils.getString(buffer, "UTF-8");
				fin.close();
				
				int start = str.indexOf(123);
				str = str.substring(start);//work around here to normalize the JSONStr
				result = new JSONObject(str);
				str = result.optString("data");
			}
		} catch (Exception ex) {

		}
		return result;
	}

	public void saveToSDCard(String app, String key, JSONObject value) {
		try {
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				String SDCardPath = Environment.getExternalStorageDirectory() + "/DataService/" + "/" + app + "/";
				File folder = new File(SDCardPath);
				if (!folder.exists()) {
					folder.mkdirs();
				}
				File saveFile = new File(SDCardPath, key + ".json");
				if (!saveFile.exists()) {
					saveFile.createNewFile();
				}
				FileOutputStream outStream = new FileOutputStream(saveFile);
				outStream.write(value.toString().getBytes());
				outStream.close();
			}
		} catch (Exception ex) {

		}
	}

}

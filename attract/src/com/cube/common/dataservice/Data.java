package com.cube.common.dataservice;

import org.json.JSONObject;

import android.util.Log;

public abstract class Data {
	private static final String TAG = "DataService";

	public JSONObject JSON = null;
	public String username = "";
	public String app = "";
	public String key = "";
	public String url = "";

	public abstract void parseJSON();

	public void getWebData() {
		if (this.url != "") {
			this.JSON = WebInterface.getJSON(this.url);
			Log.d(TAG, "Data is: " + this.JSON);
			this.parseJSON();
		}
	}
	
	public void registService(){
		DataService.datas.add(this);
	}
}

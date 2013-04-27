package com.cube.common.dataservice;

import org.json.JSONObject;

import android.content.Context;

public abstract class Data {
	WebData webData = null;

	public JSONObject JSON = null;
	public String username = "";
	public String app = "";
	public String key = "";
	public String url = "";

	public abstract void parseJSON();

	public void initializeData(Context mContext) {
		webData = new WebData();
		webData.initializeWebData(mContext);
		JSON = webData.get(url);
		if (JSON == null) {
			JSON = webData.loadFromSDCard(app, key);
		}
		
		parseJSON();
		webData.saveToSDCard(app, key, JSON);
	}


	public void registService() {
		DataService.datas.add(this);
	}
}

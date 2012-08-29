package com.cube.common;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cube.common.dataservice.Data;

public class ServerData extends Data {
	ServerData() {
		super.url = "http://cubeservice.sinaapp.com/girls/attrat/serverdata.json";
		super.key = "ServerData";
		super.app = "attract";
		super.username = "";
		super.registService();
	}

	static ServerData instance = null;

	static public ServerData getInstance() {
		if (instance == null)
			instance = new ServerData();
		return instance;
	}

	public String APIVersion = "1.0";

	public String updateTime = "2012.09.01";

	public ArrayList<String> activeGames = new ArrayList<String>();

	public ArrayList<Girl> girls = new ArrayList<Girl>();

	public class Girl {

		public long id = 0;

		public String name = "小悦悦";

		public String weibo = "@小悦悦";

		public SNSIndex mSNSIndex = new SNSIndex();

		public class SNSIndex {
			public long liked = 1002;
			public long shared = 602;
		}

		public ArrayList<Picture> pictures = new ArrayList<Picture>();

		public class Picture {
			public long id = 0;

			public String url = "http://cubeservice.sinaapp.com/girls/girl_4_1.jpg";

			public String status = "";

			public ArrayList<Point> points = new ArrayList<Point>();

			public class Point {
				public long x = 0;
				public long y = 0;
				public String status = "";
			}
		}
	}

	@Override
	public void parseJSON() {
		try {
			APIVersion = this.JSON.getString("apiversion");
			updateTime = this.JSON.getString("updateTime");

			JSONArray activeGamesJSON = this.JSON.getJSONArray("activeGames");
			for (int i = 0; i < activeGamesJSON.length(); i++) {
				String activeGame = activeGamesJSON.getString(i);
				activeGames.add(activeGame);
			}

			JSONArray girlsJSON = this.JSON.getJSONArray("girls");
			for (int i = 0; i < girlsJSON.length(); i++) {
				JSONObject girlJSON = (JSONObject) girlsJSON.get(i);
				Girl girl = new Girl();
				girls.add(girl);
				girl.id = girlJSON.getLong("id");
				girl.name = girlJSON.getString("name");
				girl.weibo = girlJSON.getString("weibo");

				JSONObject mSNSIndexJSON = girlJSON.getJSONObject("mSNSIndex");
				{
					girl.mSNSIndex.liked = mSNSIndexJSON.getLong("liked");
					girl.mSNSIndex.shared = mSNSIndexJSON.getLong("shared");
				}

				JSONArray picturesJSON = girlJSON.getJSONArray("pictures");
				for (int j = 0; j < picturesJSON.length(); j++) {
					JSONObject pictureJSON = (JSONObject) picturesJSON.get(j);
					// Special syntax to resolve the Closure problem for JAVA
					// inner class.
					Girl.Picture picture = new Girl().new Picture();
					girl.pictures.add(picture);
					picture.id = pictureJSON.getLong("id");
					picture.url = pictureJSON.getString("url");
					picture.status = pictureJSON.getString("status");

					if (!pictureJSON.has("points")) {
						continue;
					}
					JSONArray pointsJSON = pictureJSON.getJSONArray("points");
					for (int k = 0; k < pointsJSON.length(); k++) {
						JSONObject pointJSON = (JSONObject) pointsJSON.get(k);
						Girl.Picture.Point point = new Girl().new Picture().new Point();
						picture.points.add(point);
						point.x = pointJSON.getLong("x");
						point.y = pointJSON.getLong("y");
						point.status = pointJSON.getString("status");
					}
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}

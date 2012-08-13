package com.cube.common;

import java.util.ArrayList;

import org.json.JSONObject;

public class ServerData {
	static ServerData instance = null;
	JSONObject JSON = null;

	static public ServerData getInstance() {
		if (instance == null)
			instance = new ServerData();
		return instance;
	}

	public String APIVersion = "1.0";

	public String UpdateTime = "2012.09.01";

	public ArrayList<String> activeGames = new ArrayList<String>();

	public ArrayList<Girl> girls = new ArrayList<Girl>();

	public class Girl {

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
}

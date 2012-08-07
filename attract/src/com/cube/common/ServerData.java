package com.cube.common;

import java.util.ArrayList;

public class ServerData {
	static ServerData instance = null;

	static public ServerData getInstance() {
		if (instance == null)
			instance = new ServerData();
		return instance;
	}

	public String APIVersion = "1.0";

	public String UpdataTime = "2012.09.01";
	
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

			public String url = "http://cubeservice.sinaapp.com/girls/4-1.jpg";
			
			public String status="";
			
			public ArrayList<Point> points = new ArrayList<Point>();
			public class Point {
				public long x;
				public long y;
				public String status="";
			}
		}
	}
}

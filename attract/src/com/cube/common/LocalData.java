package com.cube.common;

import java.util.ArrayList;

public class LocalData {
	static LocalData instance = null;

	static public LocalData getInstance() {
		if (instance == null)
			instance = new LocalData();
		return instance;
	}

	public String username = "null";

	public String weibo = "null";

	public String isLogin = "false";

	public String status = "null";

	public ArrayList<Game> games = new ArrayList<Game>();

	public class Game {

		public ArrayList<ActiveGirl> activeGirls = new ArrayList<ActiveGirl>();

		public class ActiveGirl {
			public long id = 0;

			public String url = "http://cubeservice.sinaapp.com/girls/4-1.jpg";

			public String status = "";

			public ArrayList<GameData> games = new ArrayList<GameData>();

			public class GameData {
				public String gameName = "CupidCannon";
				public String isCompleted = "false";
				public String isLocked = "false";
				public String isShared = "false";
				public long highScore = 0;
			}
		}

		public CupidCannon cupidCannon = new CupidCannon();

		public class CupidCannon {

			public String status = "";

			public ArrayList<Picture> pictures = new ArrayList<Picture>();

			public class Picture {
				public long id = 0;
				public String url = "http://cubeservice.sinaapp.com/girls/4-1.jpg";
				public ArrayList<Point> points = new ArrayList<Point>();

				public class Point {
					public long x;
					public long y;
					public String status = "";
				}
			}
		}
	}
}

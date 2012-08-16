package com.cube.common;

import java.util.ArrayList;

import com.cube.common.dataservice.Data;

public class LocalData extends Data {
	public LocalData() {
		super.url = "";
		super.key = "LocalData";
		super.app = "attract";
		super.username = "";
		super.registService();
	}

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

	public Game game = new Game();

	public class Game {
		public ArrayList<String> downloadedPictures = new ArrayList<String>();
		
		public ArrayList<ActiveGirl> cubeGirls = new ArrayList<ActiveGirl>();

		public ArrayList<ActiveGirl> activeGirls = new ArrayList<ActiveGirl>();

		public class ActiveGirl {

			public ServerData.Girl girl = null;
			public long id = 0;

			public String cubePicture = "girl_4_1.jpg";
			public String gameEntryPicture = "girl_4_1.jpg";

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
				public String url = "http://cubeservice.sinaapp.com/girls/girl_4_1.jpg";
				public String picture1 = "girl_4_1.jpg";
				public String picture2 = "girl_4_2.jpg";
				public String picture3 = "girl_4_3.jpg";
				public ArrayList<Point> points = new ArrayList<Point>();

				public class Point {
					public long x = 0;
					public long y = 0;
					public String status = "";
				}
			}
		}
	}

	@Override
	public void parseJSON() {
	}

}

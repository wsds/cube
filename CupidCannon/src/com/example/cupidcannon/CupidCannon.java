package com.example.cupidcannon;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

public class CupidCannon extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    //强制为竖屏   
    	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(new AnimView(this));
    }
    
}

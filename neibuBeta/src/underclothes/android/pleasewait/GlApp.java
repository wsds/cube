package underclothes.android.pleasewait;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import Neibu.main.beta.R;

public class GlApp extends Activity {
	Context mContext = null;

	RelativeLayout canvasContainer = null;
	public boolean initailized = false;
	
	
	
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		setContentView(R.layout.pleasewait);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    @Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
	}
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE); 
		long [] pattern = {5,100};   // 停止 开启 停止 开启  
		vibrator.vibrate(pattern,-1);
		return true;
	}
	
}
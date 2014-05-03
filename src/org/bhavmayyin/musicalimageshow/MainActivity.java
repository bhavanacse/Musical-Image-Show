package org.bhavmayyin.musicalimageshow;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import org.bhavmayyin.musicalimageshow.R.color;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

@SuppressLint("NewApi")
public class MainActivity extends Activity {
private ImageView imagev;
private Timer timer;
private int nindex;
private MyHandler handler;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);
		imagev = (ImageView) findViewById(R.id.imageView1);
	    ActionBar bar = getActionBar();
	    bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0099CC")));
		timer = new Timer();
		timer.schedule(new TickClass(), 1000,1000);
		handler = new MyHandler();
		
	}
	
	private class TickClass extends TimerTask
	{
		@Override
		public void run(){
			handler.sendEmptyMessage(nindex);
			nindex++;
		}
	}
	private class MyHandler extends Handler
	{
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			if (nindex > 3){
				getActivity();
				
			} else {
			
			try {

				String fname = new String();
				if (nindex%2 == 0){
					fname = "images.jpg";
				} else { fname="play_slideshow.jpg";}
				Bitmap bmp = BitmapFactory.decodeStream(MainActivity.this.getAssets().open(fname));
				imagev.setImageBitmap(bmp);
				Log.v("Loading image: ", "trying to switch");
			} catch (IOException e){
				Log.v("Exception :", e.getMessage());
			}
		}
	}
		public void getActivity(){
			timer.cancel();
			Intent menuchoice = new Intent(getApplicationContext(), ShowList.class);
			startActivity(menuchoice);
			
		}
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		//return super.onCreateOptionsMenu(menu);
		
		return true;
	}
}
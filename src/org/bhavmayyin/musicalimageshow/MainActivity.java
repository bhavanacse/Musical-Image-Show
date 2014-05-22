/*
 * First Screen that shows two images one after another 
 * with a bit of introductory music for the app
 */
package org.bhavmayyin.musicalimageshow;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

@SuppressLint("NewApi")
public class MainActivity extends Activity {
	private ImageView imagev;
	private Timer timer;
	private int nindex;
	private MyHandler handler;

	private static final String SPLASH_SCREEN_IMAGE1_JPG = "splash_screen_image1.jpg";
	private static final int BORDER_COLOR = Color.BLACK;
	final int BORDER_WIDTH = 20;

	MediaPlayer myPlayer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);
		
		// Set the color of the action bar
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0099CC")));

		imagev = new ImageView(MainActivity.this);
		
		// Defining properties of the image to be set in the activity
		imagev.setScaleType(ScaleType.FIT_CENTER);
		LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
		imagev.setLayoutParams(layoutParams);
		try {
			setBitmapImage(SPLASH_SCREEN_IMAGE1_JPG);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		RelativeLayout layout = new RelativeLayout(MainActivity.this);
		layout.addView(imagev); // Add the image to the layout
		setContentView(layout);

		// Play the music
		myPlayer = MediaPlayer.create(MainActivity.this, R.raw.music_bit);
		myPlayer.start();
		myPlayer.setVolume(100, 100);

		timer = new Timer();
		timer.schedule(new TickClass(), 1000, 1000);
		handler = new MyHandler();
	}
	
	// To show the image with a border
	private void setBitmapImage(String fname) throws IOException {
		Bitmap bmp = BitmapFactory.decodeStream(MainActivity.this.getAssets()
				.open(fname));

		Bitmap bmpWithBorder = Bitmap.createBitmap(bmp.getWidth() + 2
				* BORDER_WIDTH, bmp.getHeight() + 2 * BORDER_WIDTH,
				bmp.getConfig());
		Canvas c = new Canvas(bmpWithBorder);
		Paint p = new Paint();
		p.setColor(BORDER_COLOR);
		c.drawRect(0, 0, bmpWithBorder.getWidth(), bmpWithBorder.getHeight(), p);

		p = new Paint(Paint.FILTER_BITMAP_FLAG);
		c.drawBitmap(bmp, BORDER_WIDTH, BORDER_WIDTH, p);
		imagev.setImageBitmap(bmp);
	}

	private class TickClass extends TimerTask {
		@Override
		public void run() { //timer thread handler
			handler.sendEmptyMessage(nindex);
			nindex++;
		}
	}

	// Handler to rotate the images one after other
	private class MyHandler extends Handler {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (nindex > 2) {
				getActivity();

			} else {

				try {
					String fname = new String();
					if (nindex % 2 == 1) {
						fname = "splash_screen_image2.jpg";
					} else {
						fname = SPLASH_SCREEN_IMAGE1_JPG;
					}
					Log.v("Loading image: " + fname, "trying to switch");
					setBitmapImage(fname);
				} catch (IOException e) {
					StringBuilder excep = new StringBuilder();
					for (StackTraceElement st : e.getStackTrace()) {
						excep.append(st.toString() + "\n");
					}
					Log.v("Exception :", excep.toString());
				}
			}
		}

		public void getActivity() {
			timer.cancel();//stop the timer thread and go to next activity
			Intent menuchoice = new Intent(getApplicationContext(),
					ShowList.class);
			startActivity(menuchoice);

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	// Release the media player on pause of the activity
	protected void onPause() {
		super.onPause();
		myPlayer.release();
	}
}
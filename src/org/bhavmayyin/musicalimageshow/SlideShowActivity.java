/**
 * 
 */
package org.bhavmayyin.musicalimageshow;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author bhavana
 * 
 */
@SuppressLint("NewApi")
public class SlideShowActivity extends Activity {

	ArrayList<String> myImageUris;
	Random randomNumGenerator = new Random();
	ArrayList<String> musicUris;
	private int filePathIndex = 0;
	ImageView mySlidingImage;
	Timer timer = new Timer();
	final Handler mHandler = new Handler();
	// BackgroundSound mBackgroundSound = new BackgroundSound();
	static PlaySound mp;
	OrientationEventListener orientationListener;

	int showid;
	AdapterView.AdapterContextMenuInfo info;
	GridView imageGrid;
	int imageId;
	String selected;

	@SuppressWarnings("static-access")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_slideshow);
		Bundle b = this.getIntent().getExtras();
		myImageUris = b.getStringArrayList("ImageFilePaths");
		musicUris = b.getStringArrayList("MusicFilePaths");
		ActionBar bar = getActionBar();
		bar.hide();

		if (!myImageUris.isEmpty() && !musicUris.isEmpty()) {
			//ArrayList<String> musicpath = new ArrayList<String>();
			mp = new PlaySound(musicUris);
			if (!mp.isplaying()) {
				mp.stop(0);
				mp.play();
			}
			animateImage();
			
			orientationListener = new OrientationEventListener(
					getApplicationContext(), SensorManager.SENSOR_DELAY_UI) {
				public void onOrientationChanged(int orientation) {
					if (mp.isplaying()) {
						mp.pause();
					} else {
						mp.resume();
					}
				}
			};
		} else if (!myImageUris.isEmpty() && musicUris.isEmpty()) {

			alertbox("Playing Slideshow without music", "No music selected");
		}

	}

	@SuppressWarnings("static-access")
	@SuppressLint("NewApi")
	public void animateImage() {

		int imageCount = myImageUris.size();

		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;

		BitmapFactory.decodeFile(myImageUris.get(filePathIndex % imageCount),
				options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, 100, 100);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		Bitmap currentBitmap = BitmapFactory.decodeFile(
				myImageUris.get(filePathIndex % imageCount), options);

		mySlidingImage = (ImageView) findViewById(R.id.slide_show);
		mySlidingImage.setImageBitmap(currentBitmap);
		filePathIndex++;

		mySlidingImage.setScaleX(1.0f);
		mySlidingImage.setScaleY(1.0f);
		mySlidingImage.setAlpha(0f);
		mySlidingImage.setRotation(0);

		mySlidingImage.setVisibility(View.VISIBLE);
		final float nextScale = 1.0f + (randomNumGenerator.nextFloat() - 0.5f) * 2 / 5;
		final float nextRotation = (randomNumGenerator.nextFloat() - 0.5f) * 2 * 5;

		final AnimatorListenerAdapter nextImageAnimationListener = new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				animateImage();
			}
		};
		final AnimatorListenerAdapter thirdAnimationListener = new AnimatorListenerAdapter() {
			@SuppressLint("NewApi")
			@Override
			public void onAnimationEnd(Animator animation) {
				mySlidingImage.animate().alpha(0f).setDuration(2000)
						.setListener(nextImageAnimationListener).start();

			};
		};
		AnimatorListenerAdapter secondAnimationListener = new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {

				mySlidingImage.animate().scaleX(nextScale).scaleY(nextScale)
						.rotationBy(nextRotation).setDuration(3000)
						.setListener(thirdAnimationListener).start();

			}
		};
		mySlidingImage.animate().alpha(1.0f).setDuration(1000)
				.setListener(secondAnimationListener).start();


	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
		int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2
			// and keeps both height and width larger than the requested height
			// and width.

			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}

	@SuppressWarnings("static-access")
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			if (mp.isplaying()) {
				mp.pause();
				mp.stop(1);
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	public void onResume() {
		super.onResume();
		// // mBackgroundSound.execute();
		if (!mp.isplaying()) {
			mp.stop(0);
			mp.resume();
			// mp.play();
		}
	}

	public void onPause() {
		super.onPause();
		timer.cancel();
		// mp.stop(1);
		// mBackgroundSound.cancel(true);
		if (mp.isplaying()) {
			mp.pause();
			mp.stop(1);
		}
	}

	protected void alertbox(String title, String msg) {
		final String finaltitle = title;
		new AlertDialog.Builder(this)

				.setTitle(finaltitle)
				.setMessage(msg)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {

						animateImage();
					}
				})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// do nothing if it is delete
								finish();
							}
						}).show();
	}

}

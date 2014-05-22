/**
 * 
 */
package org.bhavmayyin.musicalimageshow;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

/**
 * @author bhavana
 * SlideShowActivity  - Displays the Slideshow of 
 * selected images & music files
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
	static PlaySound mp;
	OrientationEventListener orientationListener;

	int showID;
	AdapterView.AdapterContextMenuInfo info;
	GridView imageGrid;
	int imageId;
	String selected;

	@SuppressWarnings("static-access")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Hide the tool bar and make the activity to show full screen
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_slideshow);
		
		Bundle b = this.getIntent().getExtras();
		myImageUris = b.getStringArrayList("ImageFilePaths");
		musicUris = b.getStringArrayList("MusicFilePaths");
		
		// Hide the action bar
		ActionBar bar = getActionBar();
		bar.hide();

		// Play the music and start animating images if both images and music files exist
		if (!myImageUris.isEmpty() && !musicUris.isEmpty()) {
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
						mp.continuePlay();
//						mp.pause();
					} else {
						mp.resume();
					}
				}
			};
		// If music files do not exist unlike images	
		} else if (!myImageUris.isEmpty() && musicUris.isEmpty()) {

			alertbox("Playing Slideshow without music", "No music selected");
		}

	}

	@SuppressWarnings("static-access")
	@SuppressLint("NewApi")
	/*
	 * Start animating images - Ken Burns + more animation effects
	 */
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
		mySlidingImage.setImageBitmap(currentBitmap); // Set the current image to image view
		filePathIndex++;

		mySlidingImage.setScaleX(1.0f); // Initial scale of image in X direction
		mySlidingImage.setScaleY(1.0f); // Initial scale of image in Y direction
		mySlidingImage.setAlpha(0f); // Set brightness to 0 
		mySlidingImage.setRotation(0); // Set initial rotation to 0

		mySlidingImage.setVisibility(View.VISIBLE); // Make the image visible
		// Set the next scaling number using random number generation
		final float nextScale = 1.0f + (randomNumGenerator.nextFloat() - 0.5f) * 2 / 5;
		// Set the next rotation number using random number generation
		final float nextRotation = (randomNumGenerator.nextFloat() - 0.5f) * 2 * 5;

		final AnimatorListenerAdapter nextImageAnimationListener = new AnimatorListenerAdapter() {
			// Call the animateImage() method on end of animating each image
			@Override
			public void onAnimationEnd(Animator animation) {
				animateImage(); 
			}
		};
		final AnimatorListenerAdapter thirdAnimationListener = new AnimatorListenerAdapter() {
			@SuppressLint("NewApi")
			@Override
			public void onAnimationEnd(Animator animation) {
				// Fade out the image by setting the alpha value
				mySlidingImage.animate().alpha(0f).setDuration(2000)
						.setListener(nextImageAnimationListener).start();

			};
		};
		AnimatorListenerAdapter secondAnimationListener = new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				// Zoom in/Zoom out and rotate the image and then start the third animation effect  
				mySlidingImage.animate().scaleX(nextScale).scaleY(nextScale)
						.rotationBy(nextRotation).setDuration(3000)
						.setListener(thirdAnimationListener).start();

			}
		};
		
		// Give a FadeIn effect to image by setting the alpha value and then start applying 
		//second animation effects
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

	@SuppressWarnings("static-access")
	public void onResume() {
		super.onResume();
		if (!mp.isplaying()) {
			mp.stop(0);
			mp.resume();
		}
	}

	@SuppressWarnings("static-access")
	public void onPause() {
		super.onPause();
		timer.cancel();
		if (mp.isplaying()) {
			mp.pause();
			mp.stop(1);
		}
	}

	/*
	 * Alert box with Ok and Cancel buttons
	 */
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

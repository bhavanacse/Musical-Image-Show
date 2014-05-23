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
 * @author bhavana SlideShowActivity - Displays the Slideshow of selected images
 *         & music files
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
	int firstDuration = 1000, secDuration = 2000;

	@SuppressWarnings("static-access")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_slideshow);

		Bundle b = this.getIntent().getExtras();
		myImageUris = b.getStringArrayList("ImageFilePaths");
		musicUris = b.getStringArrayList("MusicFilePaths");

		// Play the music and start animating images if both images and music
		// files exist
		if (!myImageUris.isEmpty() && !musicUris.isEmpty()) {
			// if music and image lists are not empty
			mp = new PlaySound(musicUris);
			if (!mp.isplaying()) {
				mp.stop(0);// set the stop status, 0=really stop
				mp.play();
			}
			animateImage();
			// try to set orientation change not to stop
			orientationListener = new OrientationEventListener(
					getApplicationContext(), SensorManager.SENSOR_DELAY_UI) {
				public void onOrientationChanged(int orientation) {
					if (mp.isplaying()) {
						mp.continuePlay();
						//
					} else {
						mp.resume();// customized resume
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

		final int imageCount = myImageUris.size();

		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;

		Bitmap currentBitmap = BitmapFactory.decodeFile(myImageUris
				.get(filePathIndex % imageCount));

		mySlidingImage = (ImageView) findViewById(R.id.slide_show);
		mySlidingImage.setImageBitmap(currentBitmap); // Set the current image
														// to image view
		filePathIndex++;

		mySlidingImage.setScaleX(1.0f); // Initial scale of image in X direction
		mySlidingImage.setScaleY(1.0f); // Initial scale of image in Y direction
		mySlidingImage.setAlpha(0f); // Set brightness to 0
		mySlidingImage.setRotation(0); // Set initial rotation to 0
		mySlidingImage.setRotationX(0f); // Set initial rotation in X direction to 0
		mySlidingImage.setRotationY(0f); // Set initial rotation in Y direction to 0
		mySlidingImage.setTranslationX(0f); // Set initial translation in X direction to 0
		mySlidingImage.setTranslationY(0f); // Set initial translation in Y direction to 0

		mySlidingImage.setVisibility(View.VISIBLE); // Make the image visible
		// Set the next scaling number using random number generation
		final float nextScale = 1.0f + (randomNumGenerator.nextFloat() - 0.5f) * 2 / 5;
		// Set the next rotation number using random number generation
		final float nextRotation = (randomNumGenerator.nextFloat() - 0.5f) * 2 * 10;
		final float otherRotation = (randomNumGenerator.nextFloat() - 0.5f) * 800;

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
				mySlidingImage.animate().alpha(0f).setDuration(firstDuration)
						.setListener(nextImageAnimationListener).start();

			};
		};
		AnimatorListenerAdapter secondAnimationListener = new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				Random rN = new Random();
				int r = rN.nextInt(100);
				if (r <= 15) {
					// Zoom in/Zoom out and rotate the image and then start the
					// third animation effect
					mySlidingImage.animate().scaleX(nextScale)
							.scaleY(nextScale).rotationBy(nextRotation)
							.setDuration(secDuration)
							.setListener(thirdAnimationListener).start();
				// Translate in X direction and rotate in Y direction
				} else if (r > 15 && r <= 30) {
					mySlidingImage.animate().translationX(-800f)
							.rotationY(otherRotation).setDuration(secDuration)
							.setListener(thirdAnimationListener).start();
				// Translate in Y direction	
				} else if (r > 30 && r <= 45) {
					mySlidingImage.animate()
							.rotationY(otherRotation).setDuration(secDuration)
							.setListener(thirdAnimationListener).start();
				// Translate in opposite X & Y direction	
				} else if (r > 45 && r <= 60) {
					mySlidingImage.animate().translationX(-500f)
							.translationY(-500f).setDuration(secDuration)
							.setListener(thirdAnimationListener).start();
				// Translate in X & Y direction	
				} else if (r > 60 && r <= 75) {
					mySlidingImage.animate().translationX(500f)
							.translationY(500f).setDuration(secDuration)
							.setListener(thirdAnimationListener).start();
				// Translate and rotate in positive X direction
				} else if (r > 75 && r <= 90) {
					mySlidingImage.animate().translationX(800f)
							.rotationX(otherRotation).setDuration(secDuration)
							.setListener(thirdAnimationListener).start();
				// Zoom in image in X and Y scale
				} else {
					mySlidingImage.animate().scaleX(nextScale)
							.scaleY(nextScale)
							.setDuration(secDuration)
							.setListener(thirdAnimationListener).start();
				}
			}
		};

		// Give a FadeIn effect to image by setting the alpha value and then
		// start applying
		// second animation effects
		mySlidingImage.animate().alpha(1.0f).setDuration(firstDuration)
				.setListener(secondAnimationListener).start();
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

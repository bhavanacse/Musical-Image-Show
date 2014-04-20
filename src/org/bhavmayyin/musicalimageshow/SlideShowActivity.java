/**
 * 
 */
package org.bhavmayyin.musicalimageshow;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.ActionBar;
import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * @author bhavana
 * 
 */
public class SlideShowActivity extends Activity {

//	final static int[] IMAGE_IDS = {
//        // some ids
//        R.drawable.ic_launcher,
//        R.drawable.ic_action_new_light,
//        R.drawable.ic_action_play_light,
//    };
	
	ArrayList<String> myImageUris;
	
	private int filePathIndex = 0;
	ImageView mySlidingImage;
	Timer timer = new Timer();
	final Handler mHandler = new Handler();
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_slideshow);

		Bundle b = this.getIntent().getExtras();
		//myFilePaths = b.getStringArrayList("ImageFilePaths");
		myImageUris = b.getStringArrayList("ImageFilePaths");
	    ActionBar bar = getActionBar();
	    bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0066FF")));
		
		
		final Runnable mUpdateResults = new Runnable() {
	        public void run() {
	        	animateImage();
	        }
	    };
	    
	    int delay = 0;
	    int period = 2500;
	   // Timer timer = new Timer();
	    timer.scheduleAtFixedRate(new TimerTask() {

	        public void run() {
	            mHandler.post(mUpdateResults);
	        }
	    },delay, period);
	  
	}

	public void animateImage() {
		int imageCount = myImageUris.size();
	//	int imageCount = IMAGE_IDS.length;
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;

			BitmapFactory.decodeFile(myImageUris.get(filePathIndex%imageCount), options);

			// Calculate inSampleSize
			options.inSampleSize = calculateInSampleSize(options, 100, 100);

			// Decode bitmap with inSampleSize set
			options.inJustDecodeBounds = false;
			Bitmap currentBitmap = BitmapFactory.decodeFile(
					myImageUris.get(filePathIndex%imageCount), options);
			Bitmap scaledBitmap = Bitmap.createScaledBitmap(currentBitmap, 1000,
					1000, true);

//			Bitmap bitmap = BitmapFactory.decodeResource(getResources(), IMAGE_IDS[filePathIndex % IMAGE_IDS.length]);

//		    int imViewheight = 400;
//		    int imViewwidth = 600;
//		    Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, imViewwidth, imViewheight, true);
			
			mySlidingImage = (ImageView) findViewById(R.id.slide_show);
//			mySlidingImage.setImageResource(IMAGE_IDS[filePathIndex%imageCount]);
			mySlidingImage.setImageBitmap(scaledBitmap);
			filePathIndex++;

//			final AnimationSet animationSet = new AnimationSet(true);
//			final Animation fadeOutAnimation = new AlphaAnimation(1.0f, 0.0f);
//			final Animation fadeInAnimation = new AlphaAnimation(0.0f, 1.0f);

//			animationSet.addAnimation(fadeInAnimation);
//			animationSet.addAnimation(fadeOutAnimation);
//			fadeInAnimation.setDuration(2000);
//			fadeInAnimation.setStartOffset(0);
//			fadeOutAnimation.setDuration(2000);
//			fadeOutAnimation.setStartOffset(2000 + 1000);

//			animationSet.setAnimationListener(animationListener);
//			mySlidingImage.startAnimation(animationSet);
			
//			Animation slideInImage = AnimationUtils.makeInChildBottomAnimation(this);

//		    mySlidingImage.startAnimation(slideInImage);
			mySlidingImage.getAnimation();
		//}
	
	}

	public int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2
			// and
			// keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}
 public void OnPause(){
	 timer.cancel();
	 
 }
//	final Animation.AnimationListener animationListener = new Animation.AnimationListener() {
//		@Override
//		public void onAnimationStart(final Animation animation) {
//			// nothing to do here
//		}
//
//		@Override
//		public void onAnimationEnd(final Animation animation) {
//			// launch showing of next image on the end of the animation
//			animateImage();
//		}
//
//		@Override
//		public void onAnimationRepeat(final Animation animation) {
//			// nothing to do here
//		}
//	};
}

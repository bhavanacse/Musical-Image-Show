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
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.ContextMenu;
import android.view.MenuItem;
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

//	final static int[] IMAGE_IDS = {
//        // some ids
//        R.drawable.ic_launcher,
//        R.drawable.ic_action_new_light,
//        R.drawable.ic_action_play_light,
//    };
	
	ArrayList<String> myImageUris;
	Random randomNumGenerator = new Random();
	ArrayList<String> musicUris;
	private int filePathIndex = 0;
	ImageView mySlidingImage;
	Timer timer = new Timer();
	final Handler mHandler = new Handler();
//	BackgroundSound mBackgroundSound = new BackgroundSound();
	PlaySound mp ;

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
//	    bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0099CC")));
	    bar.hide();
	    if (musicUris.size() > 0){
	    	ArrayList<String> musicpath = new ArrayList<String>();
	    	for (String struri : musicUris){
	    		musicpath.add( getMusicPath(Uri.parse(struri)));
	    	}
	    	mp = new PlaySound( musicpath);
	    	animateImage();
	    	mp.play();
	    }
	    else {
	    	
	    	alertbox("Cannot Play Music With Image","No Music Selected");
	    }
		
//	    final Runnable mUpdateResults = new Runnable() {
//	        public void run() {
//	        	animateImage();
//	        }
//	    };
//	    
//	    int delay = 0;
//	    int period = 5500;
//	    timer.scheduleAtFixedRate(new TimerTask() {
//
//	        public void run() {
//	            mHandler.post(mUpdateResults);
//	        }	
//	    },delay, period);
	}
	public String getMusicPath(Uri uri){
		   Cursor cursor = getContentResolver().query(uri, null, null, null, null);
		   cursor.moveToFirst();
		   String document_id = cursor.getString(0);
		   document_id = document_id.substring(document_id.lastIndexOf(":")+1);
		   cursor.close();

		   cursor = getContentResolver().query( 
		   android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
		   null, MediaStore.Audio.Media._ID + " = ? ", new String[]{document_id}, null);
		   cursor.moveToFirst();
		   String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
		   cursor.close();

		   return path;
		}
	   @SuppressWarnings("static-access")
	@SuppressLint("NewApi")
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

			mySlidingImage = (ImageView) findViewById(R.id.slide_show);
			mySlidingImage.setImageBitmap(currentBitmap);
			filePathIndex++;

//			final AnimationSet animationSet = new AnimationSet(true);
//			final Animation fadeOutAnimation = new AlphaAnimation(1.0f, 0.0f);
//			final Animation fadeInAnimation = new AlphaAnimation(0.0f, 1.0f);
//
//			animationSet.addAnimation(fadeInAnimation);
//			animationSet.addAnimation(fadeOutAnimation);
//			fadeInAnimation.setDuration(2000);
//			fadeInAnimation.setStartOffset(0);
//			fadeOutAnimation.setDuration(2000);
//			fadeOutAnimation.setStartOffset(2000 + 1000);
			
//			animationSet.setAnimationListener(animationListener);
			
//			mySlidingImage.startAnimation(animationSet);
			
//			mySlidingImage.getAnimation();
			
			mySlidingImage.setScaleX(1.0f);
			mySlidingImage.setScaleY(1.0f);
			mySlidingImage.setAlpha(0f);
			mySlidingImage.setRotation(0);
			
			mySlidingImage.setVisibility(View.VISIBLE);
			final float nextScale = 1.0f + (randomNumGenerator.nextFloat()-0.5f) * 2 / 5;
			final float nextRotation = (randomNumGenerator.nextFloat()-0.5f) * 2 * 5;

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
								
//			mySlidingImage.animate()
//				.alpha(0f)
//				.setDuration(1000)
//				.setListener(new AnimatorListenerAdapter() {
//				@Override
//				public void onAnimationEnd(Animator animation) {
//					mySlidingImage.setVisibility(View.GONE);
//					mySlidingImage.setScaleX((float)1.0);
//					mySlidingImage.setScaleY((float)1.0);
//				}
//				}).start();
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
			// and keeps both height and width larger than the requested height and width.

			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}
	
//	public void onResume() {
//	    super.onResume();
////	    mBackgroundSound.execute();
//	}

	public void onPause(){
		super.onPause(); 
		timer.cancel();	
		mp.stop();
//		 mBackgroundSound.cancel(true);
	 }
	 protected void alertbox(String title, String msg){
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
		                    public void onClick(DialogInterface dialog, int which) {
		                       //do nothing if it is delete
		                    	finish();
	                      }
		        	})
		        	.show();
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
	
//	public class BackgroundSound extends AsyncTask<Void, Void, Void> {
//
//	    @Override
//	    protected Void doInBackground(Void... params) {
//	        MediaPlayer mediaPlayer = MediaPlayer.create(SlideShowActivity.this, R.raw.rojaflute); 
//	        mediaPlayer.setLooping(true); // Set looping 
//	        mediaPlayer.setVolume(100,100); 
//	        mediaPlayer.start(); 
//
//	        return null;
//	    }
//	}
}

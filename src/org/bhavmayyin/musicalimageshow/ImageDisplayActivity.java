/**
 * clone 4/12/2014
 */
package org.bhavmayyin.musicalimageshow;

import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData.Item;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author bhavana
 * ImageDisplayActivity - Screen that allows to select images from
 * Photo Gallery and show those images in GridView with "Add New Image" button
 * Slideshow button to play Slideshow
 * 
 */
@SuppressLint("NewApi")
public class ImageDisplayActivity extends Activity {

	private static final int SELECT_PICTURE = 1;

	List<Drawable> splittedBitmaps;
	List<String> filePaths;
	List<String> imgURI;
	List<String> musicURI;
	DatabaseHelper db;
	int showID;
	ImageAdapter imgAdapter;
	AdapterView.AdapterContextMenuInfo info;
	GridView imageGrid;
	ImageButton btnChooseGallery;
	ImageButton btnPlayShow;
	int imageId;
	String selected;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_imagedisplay);

		btnChooseGallery = (ImageButton) findViewById(R.id.addImageButton);
		btnPlayShow = (ImageButton) findViewById(R.id.playImageButton);

		btnChooseGallery.setOnClickListener(btnOpenGallery);
		btnPlayShow.setOnClickListener(btnSlideShow);

		db = new DatabaseHelper(this);
		showID = getIntent().getIntExtra("showID", 0);
		imgURI = db.getAllimageURI(showID); //Get all the image URIs associated to Slide show ID 

		imageGrid = (GridView) findViewById(R.id.grid);
		
		// List of image URIs to render by setting the adapter to GridView
		imgAdapter = new ImageAdapter(this, imgURI);
		imageGrid.setAdapter(imgAdapter);
		registerForContextMenu(imageGrid);
	}

	// Contextual menu for removing unwanted images from a Slideshow
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		info.targetView.setBackgroundColor(Color.rgb(204, 0, 0));// Red rim
		imageId = (int) info.position;
		selected = imgAdapter.getURI(imageId);
		
		// Alert with Cancel and Delete buttons for deletion of selected image file
		menu.setHeaderTitle("Delete Image file:");
		menu.add(0, v.getId(), 0, "Cancel");
		menu.add(0, v.getId(), 0, "Delete");
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		
		// If the selected button is "Delete" in Delete alert box
		if (item.getTitle() == "Delete") {
			Toast.makeText(getApplicationContext(),
					"Deleting image file-" + selected, Toast.LENGTH_LONG)
					.show();
			db.deleteImageShow(showID, selected);
			imgURI.remove(imgAdapter.getURI(imageId));
			imgAdapter.notifyDataSetChanged();
			
	    // If the selected button is "Delete" in Delete alert box	
		} else if (item.getTitle() == "Cancel") {
			Toast.makeText(getApplicationContext(), "Cancelling delete",
					Toast.LENGTH_LONG).show();
			info.targetView.setBackgroundColor(Color.rgb(255, 255, 255));
		} else {
			return false;
		}
		selected = "";
		return true;
	}

    // Open the Photo gallery on click of "Add New Picture" button
	public OnClickListener btnOpenGallery = new OnClickListener() {

		@SuppressLint("InlinedApi")
		public void onClick(View view) {
			if (getIntent().getCharSequenceExtra("TAB").toString()
					.contentEquals("Images")) {
				Intent intent = new Intent(Intent.ACTION_PICK);
				intent.setType("image/*"); // Set the type as image
				intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); // To allow selection of multiple images
				startActivityForResult(intent, SELECT_PICTURE);
			}
		}
	};

	// Play Slide show on click of "Play" button
	public OnClickListener btnSlideShow = new OnClickListener() {

		@SuppressLint("InlinedApi")
		public void onClick(View view) {
			// Check whether the tab is "Images"
			if (getIntent().getCharSequenceExtra("TAB").toString()
					.contentEquals("Images")) {
				playSlideShow();
			}
		}
	};

	public void playSlideShow() {

		Bundle b = new Bundle();
		String key = "ImageFilePaths";
		String musickey = "MusicFilePaths";
		musicURI = db.getShowMusicURI(showID);

		// If there are no images in Images tab but music files in Music tab
		if (imgURI.isEmpty() && !musicURI.isEmpty()) {
			
			Toast.makeText(
					getApplicationContext(),
					"Cannot play Slideshow without images.  Please select atleast one image.",
					Toast.LENGTH_LONG).show();
		// If both Images and Music tabs does not contain any files
		} else if (imgURI.isEmpty() && musicURI.isEmpty()) {

			Toast.makeText(getApplicationContext(),
					"Please select music files & images to play slideshow",
					Toast.LENGTH_LONG).show();
		} else { // Start SlideShowActivity by sending Image URIs and Music URIs

			b.putStringArrayList(key, (ArrayList<String>) imgURI);
			b.putStringArrayList(musickey, (ArrayList<String>) musicURI);
			Intent intent = new Intent(this, SlideShowActivity.class);
			intent.putExtras(b);
			startActivity(intent);
		}
	}

    // Once images are selected from the Image Gallery
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		int totalImages = 0;
		if (resultCode == RESULT_OK) {
			if (requestCode == SELECT_PICTURE) {
				filePaths = new ArrayList<String>();
				Uri selectedImageUri = null;
				String path; 
				// If multiple image selection is enabled, ClipData gives the number of selected images
				if (data.getClipData() != null) {
					totalImages = data.getClipData().getItemCount();
				} else if (data.getData() != null) {
					totalImages = 1; // Take selected images count to be 1 if multiple selection is not enabled
				}
				for (int currentImage = 0; currentImage < totalImages; currentImage++) {
					if (data.getClipData() != null) {
						Item currentClip = data.getClipData().getItemAt(
								currentImage);
                        // Get the URI   
						selectedImageUri = currentClip.getUri();

					} else if (data.getData() != null) {
						// Get the URI
						selectedImageUri = data.getData();
					}

					// Get the file path using the Image URI
					if (selectedImageUri != null){
						path = getFilePath(selectedImageUri);
						
						if (!path.isEmpty()) {
							filePaths.add(path);
							imgURI.add(path);
						}
					}
				}
				db.addImage(filePaths, showID); // Add filePaths list to database 
			}
			imageGrid.setAdapter(imgAdapter); // Set the adapter to the Grid View
		}
	}

	// Retrieve the file path using the URI
	public String getFilePath(Uri currentUri) {
		String[] filePathColumn = { MediaStore.Images.Media.DATA };

		Cursor cursor = getContentResolver().query(currentUri, filePathColumn,
				null, null, null);
		cursor.moveToFirst();

		int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
		String filePath = cursor.getString(columnIndex);
		cursor.close();

		return filePath;
	}

	// Close the database on pause of activity
	void OnPause() {
		db.closeDB();
	}

    // Reopen the database on resuming the activity 
	void OnResume() {
		db.reopen();
	}

	// Image Adapter
	public class ImageAdapter extends BaseAdapter {
		private Context mContext;
		private List<String> mThumbIds;

		public ImageAdapter(Context c, List<String> list) {
			mContext = c;
			mThumbIds = list;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mThumbIds.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return BitmapFactory.decodeFile(mThumbIds.get(position));
		}

		public String getURI(int position) {
			return mThumbIds.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imageView;
			imageView = new ImageView(mContext);
			try {
				// TODO Auto-generated method stub
				imageView.setLayoutParams(new GridView.LayoutParams(150, 150));
				imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
				imageView.setPadding(8, 8, 8, 8);

				final BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true;
                
				// Decode the file to avoid java.lang.OutOfMemory exception
				BitmapFactory.decodeFile(mThumbIds.get(position), options);

				// Calculate inSampleSize
				options.inSampleSize = calculateInSampleSize(options, 110, 110);

				// Decode bitmap with inSampleSize set
				options.inJustDecodeBounds = false;
				Bitmap tempBitmap = BitmapFactory.decodeFile(
						mThumbIds.get(position), options);
				imageView.setImageBitmap(tempBitmap);

			} catch (Error e) {
				e.printStackTrace();
			}
			return imageView;
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

	}

}

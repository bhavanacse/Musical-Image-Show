/**
 * clone 4/12/2014
 */
package org.bhavmayyin.musicalimageshow;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipData.Item;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
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
	int showid;
	TextView tv;
	ImageAdapter imgadapter;
	AdapterView.AdapterContextMenuInfo info;
	GridView imageGrid;
	int imageId;
	String selected;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_imagedisplay);

		ImageButton btnChooseGallery = (ImageButton) findViewById(R.id.addImageButton);
		ImageButton btnPlayShow = (ImageButton) findViewById(R.id.playImageButton);

		btnChooseGallery.setOnClickListener(btnOpenGallery);
		btnPlayShow.setOnClickListener(btnSlideShow);

		db = new DatabaseHelper(this);
		showid = getIntent().getIntExtra("showID", 0);
		imgURI = db.getAllimageURI(showid);
		
		tv = (TextView) findViewById(R.id.ssTitle);
		tv.setText(getIntent().getStringExtra("showTitle"));

		imageGrid = (GridView) findViewById(R.id.grid);
		imgadapter = new ImageAdapter(this, imgURI);
		imageGrid.setAdapter(imgadapter);
	     registerForContextMenu(imageGrid);
	}

    @Override 
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
    {
            super.onCreateContextMenu(menu, v, menuInfo);
            info = (AdapterView.AdapterContextMenuInfo) menuInfo;
           	info.targetView.setBackgroundColor(Color.rgb(255,204,0));// orange rim
            imageId = (int)info.position;
            selected = imgadapter.getURI(imageId);
            menu.setHeaderTitle("Delete Image file:"  );  
            menu.add(0, v.getId(), 0, "Cancel");//groupId, itemId, order, title 
            menu.add(0, v.getId(), 0, "Delete"); 

    } 
    
    @Override  
    public boolean onContextItemSelected(MenuItem item){  
            if(item.getTitle()=="Delete"){
            	Toast.makeText(getApplicationContext(),"deleting image file-" + selected ,Toast.LENGTH_LONG).show();
            	db.deleteImageShow(showid, selected);
            	imgURI.remove(imgadapter.getURI(imageId));
            	imgadapter.notifyDataSetChanged();
            }  
            else if(item.getTitle()=="Cancel"){
            	Toast.makeText(getApplicationContext(),"Cancelling delete",Toast.LENGTH_LONG).show();
            	info.targetView.setBackgroundColor(Color.rgb(255,255,255));
            }else{
               return false;
            } 
            selected = "";
          return true;  
                            
      }  
	public OnClickListener btnOpenGallery = new OnClickListener() {

		@SuppressLint("InlinedApi")
		public void onClick(View view) {
			if (getIntent().getCharSequenceExtra("TAB").toString()
					.contentEquals("Images")) {
				Intent intent = new Intent(Intent.ACTION_PICK);
				intent.setType("image/*");
				intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
				startActivityForResult(intent, SELECT_PICTURE);

			}
		}
	};

	public OnClickListener btnSlideShow = new OnClickListener() {

		@SuppressLint("InlinedApi")
		public void onClick(View view) {
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
		musicURI =db.getShowMusicURI(showid);
		b.putStringArrayList(key, (ArrayList<String>) imgURI);
		b.putStringArrayList(musickey, (ArrayList<String>) musicURI);
		Intent intent = new Intent(this, SlideShowActivity.class);
		intent.putExtras(b);
		startActivity(intent);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		int totalImages = 0;
		if (resultCode == RESULT_OK) {
			if (requestCode == SELECT_PICTURE) {
				filePaths = new ArrayList<String>();
				Uri selectedImageUri = null;
				String path;
				if (data.getClipData() != null){
					totalImages = data.getClipData().getItemCount(); 
				} else if (data.getData() != null){
					totalImages = 1;
				}
					for (int currentImage = 0; currentImage < totalImages; currentImage++) {
						if (data.getClipData() != null){
							Item currentClip = data.getClipData().getItemAt(
									currentImage);
		
							selectedImageUri = currentClip.getUri();
							
						} else if (data.getData() != null){
							selectedImageUri = data.getData();
						}
						
						path = getFilePath(selectedImageUri);
	
						if (!path.isEmpty()) {
							filePaths.add(path);
							imgURI.add(path);
						}
					}
					db.addImage(filePaths, showid);
					// imgadapter.notifyDataSetChanged();
				}
				imageGrid.setAdapter(imgadapter);
		}
	}
	
	public String getFilePath(Uri currentUri){
		String[] filePathColumn = { MediaStore.Images.Media.DATA };
		
		Cursor cursor = getContentResolver().query(
				currentUri, filePathColumn, null, null, null);
		cursor.moveToFirst();

		int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
		String filePath = cursor.getString(columnIndex);
		cursor.close();

		return filePath;
	}
	void OnPause(){
		db.closeDB();
	}
	void OnResume(){
		db.reopen();
	}
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
		public String getURI(int position){
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

	}

}

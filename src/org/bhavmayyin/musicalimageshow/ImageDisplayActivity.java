/**
 * clone 4/12/2014
 */
package org.bhavmayyin.musicalimageshow;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData.Item;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author bhavana
 * 
 */
@SuppressLint("NewApi")
public class ImageDisplayActivity extends Activity {

	private static final int SELECT_PICTURE = 1;

	List<Drawable> splittedBitmaps;
	List<String> filePaths;
	DatabaseHelper db;
	int showid ;
	String sTitle;
	String sDesc;
	TextView tv;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_imagedisplay);
		
		ImageButton btnChooseGallery = (ImageButton) findViewById(R.id.addImageButton);

		btnChooseGallery.setOnClickListener(btnOpenGallery);
		db = new DatabaseHelper(this);
		showid = getIntent().getIntExtra("showID", 0);
		SlideShow ss = new SlideShow();
		ss=(db.getSlideShow(showid));
		sTitle = ss.getshowName();
		sDesc = ss.getshowDescription();
		tv = (TextView)  findViewById(R.id.ssTitle);
		tv.setText(sTitle + (!sDesc.isEmpty()? "-" :"") + sDesc);
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


	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == SELECT_PICTURE) {
					//result array[0] mData mData path decode
				// data.getData()
				int totalImages = 1; //data.getClipData().getItemCount();
				filePaths = new ArrayList<String>();
				for (int currentImage = 0; currentImage < totalImages; currentImage++) {
					//Item currentClip = data.getClipData().getItemAt(
						//	currentImage);

					Uri selectedImageUri = data.getData(); //currentClip.getUri();
					String[] filePathColumn = { MediaStore.Images.Media.DATA };

					Cursor cursor = getContentResolver().query(
							selectedImageUri, filePathColumn, null, null, null);
					cursor.moveToFirst();

					int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
					String filePath = cursor.getString(columnIndex);
					cursor.close();

					filePaths.add(filePath);

				}
			}

			GridView imageGrid = (GridView) findViewById(R.id.grid);
			if (!filePaths.isEmpty()) {
				imageGrid.setAdapter(new ImageAdapter(this, filePaths));
			}
		}
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
				imageView.setLayoutParams(new GridView.LayoutParams(110, 110));
				imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
				imageView.setPadding(8, 8, 8, 8);

				final BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true;

				BitmapFactory.decodeFile(mThumbIds.get(position), options);

				// Calculate inSampleSize
				options.inSampleSize = calculateInSampleSize(options, 110, 110);

				// // Decode bitmap with inSampleSize set
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

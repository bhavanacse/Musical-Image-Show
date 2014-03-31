/**
 * 
 */
package org.bhavmayyin.musicalimageshow;

import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author bhavana
 * 
 */
public class DisplayActivity extends Activity {

	private static final int SELECT_PICTURE = 1;
	private static final int SELECT_MUSIC = 1;

	// private String selectedImagePath;
	// private ImageView img;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_display);

		ImageButton btnChooseGallery = (ImageButton) findViewById(R.id.addImageButton);
		btnChooseGallery.setOnClickListener(btnOpenGallery);
	}

	public OnClickListener btnOpenGallery = new OnClickListener() {

		@SuppressLint("InlinedApi")
		public void onClick(View view) {
			if (getIntent().getCharSequenceExtra("TAB").toString()
					.contentEquals("Images")) {
				// Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(
				// "content://media/internal/images/media"));
				// startActivityForResult(intent, SELECT_PICTURE);

				// Intent intent = new Intent();
				// intent.setType("image/*");
				// intent.setAction(Intent.ACTION_GET_CONTENT);
				// startActivityForResult(Intent.createChooser(intent,"Select Picture"),
				// SELECT_PICTURE);

//				Intent intent = new Intent(
//						Intent.ACTION_PICK,
//						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//				intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
				Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(intent, SELECT_PICTURE);

			} else {
				Intent intent = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);

				startActivityForResult(intent, SELECT_MUSIC);
			}
			// Toast.makeText(this, "This should open Music Gallery",
			// Toast.LENGTH_LONG).show();
		}
	};

	// public void onActivityResult(int requestCode, int resultCode, Intent
	// data) {
	// if (resultCode == RESULT_OK) {
	// if (requestCode == SELECT_PICTURE) {
	// Uri selectedImageUri = data.getData();
	// selectedImagePath = getPath(selectedImageUri);
	// System.out.println("Image Path : " + selectedImagePath);
	// img.setImageURI(selectedImageUri);
	// }
	// }
	// }
	//
	// public String getPath(Uri uri) {
	// String[] projection = { MediaStore.Images.Media.DATA };
	// Cursor cursor = managedQuery(uri, projection, null, null, null);
	// startManagingCursor(cursor);
	// int column_index =
	// cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	// cursor.moveToFirst();
	// return cursor.getString(column_index);
	// }

//	public void onActivityResult(int requestCode, int resultCode, Intent data) {
//		if (resultCode == RESULT_OK) {
//			if (requestCode == SELECT_PICTURE) {
//				if (Intent.ACTION_SEND_MULTIPLE.equals(getIntent().getAction())
//						&& getIntent().hasExtra(Intent.EXTRA_STREAM)) {
//					ArrayList<Parcelable> list = getIntent()
//							.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
//					for (Parcelable parcel : list) {
//						Uri selectedImageUri = (Uri) parcel;
//						// String sourcepath=getPath(uri);
//
//						// Uri selectedImageUri = data.getData();
//						String[] filePathColumn = { MediaStore.Images.Media.DATA };
//
//						Cursor cursor = getContentResolver().query(
//								selectedImageUri, filePathColumn, null, null,
//								null);
//						cursor.moveToFirst();
//
//						int columnIndex = cursor
//								.getColumnIndex(filePathColumn[0]);
//						String filePath = cursor.getString(columnIndex);
//						cursor.close();
//
//						ImageView imageView = (ImageView) findViewById(R.id.imageView1);
//
//						Bitmap yourSelectedImage = BitmapFactory
//								.decodeFile(filePath);
//						imageView.setImageBitmap(yourSelectedImage);
//					}
//				}
//			}
//		}
//	}
}

/**
 * 
 */
package org.bhavmayyin.musicalimageshow;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.R.color;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData.Item;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract.Data;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author bhavana
 * 
 */
public class MusicDisplayActivity extends Activity {

	private static final int SELECT_MUSIC = 1;

	File file;
	DatabaseHelper db;
	int showid ;
	TextView tv;
	ListView musicList;
	MusicAdapter musicadapter;
	List<ShowMusic> musicObj ;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_musicdisplay);

		ImageButton btnChooseGallery = (ImageButton) findViewById(R.id.addMusicButton);
		btnChooseGallery.setOnClickListener(btnOpenGallery);

		db = new DatabaseHelper(this);
		showid = getIntent().getIntExtra("showID", 0);
		tv = (TextView)  findViewById(R.id.ssMTitle);
		tv.setText(getIntent().getStringExtra("showTitle"));
		musicObj = db.getShowMusic(showid);
		//Toast.makeText(this, "add music added" , Toast.LENGTH_SHORT).show();
		musicList = (ListView) findViewById(R.id.musicListView);
		musicadapter = new MusicAdapter(this, musicObj);
		musicList.setAdapter(musicadapter);

	}

	public OnClickListener btnOpenGallery = new OnClickListener() {

		@SuppressLint("InlinedApi")
		public void onClick(View view) {
			if (getIntent().getCharSequenceExtra("TAB").toString()
					.contentEquals("Music")) {
				Intent intent = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);

				startActivityForResult(intent, SELECT_MUSIC);
			}
		}
	};

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == SELECT_MUSIC) {

				Uri selectedMusicUri = data.getData();
				String[] filePathColumn = { 
						MediaStore.Audio.Media.TITLE,
						MediaStore.Audio.Media.ARTIST
						};

				Cursor cursor = getContentResolver().query(selectedMusicUri,
						filePathColumn, null, null, null);
				cursor.moveToFirst();

				int columnOneIndex = cursor.getColumnIndex(filePathColumn[0]);
				String displayName = cursor.getString(columnOneIndex);

				int columnTwoIndex = cursor.getColumnIndex(filePathColumn[1]);
				String artistName = cursor.getString(columnTwoIndex);

				cursor.close();

				ShowMusic sm = new ShowMusic();
				sm.setArtist( artistName);
				sm.setMusic(displayName);
				musicObj.add(sm);
				db.addMusic( displayName, artistName,showid);
				musicadapter.notifyDataSetChanged();

			}
		}
	}

	public class MusicAdapter extends BaseAdapter {
		private Context myContext;
		private List<ShowMusic> showmusic ;
//		AssetManager assetManager = getAssets(); 

		public MusicAdapter(Context c, List<ShowMusic> musiclist) {
			myContext = c;
			showmusic = musiclist;
		}

		public int getCount() {
			return showmusic.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			TextView textView = new TextView(myContext);

			textView.setLayoutParams(new ListView.LayoutParams(400, 100));

			textView.setText(showmusic.get(position).getMusic() + " " 
					+ showmusic.get(position).getArtist());
//			textView.setText(fileDisplayName);
//			textView.setBackgroundColor(color.holo_red_dark);
//			final Typeface tvFont = Typeface.createFromAsset(assetManager, "OPTIMA.TTF");
//	        textView.setTypeface(tvFont);
//			textView.setTextColor(color.holo_blue_dark);

			return textView;
		}

	}

}




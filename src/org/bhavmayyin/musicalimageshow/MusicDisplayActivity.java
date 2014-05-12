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
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract.Data;
import android.provider.MediaStore;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
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
	int showid;
	TextView tv;
	ListView musicList;
	MusicAdapter musicadapter;
	List<ShowMusic> musicObj;
	String musicTitle;
	long musicId;
	String showT;
	AdapterView.AdapterContextMenuInfo info;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_musicdisplay);

		ImageButton btnChooseGallery = (ImageButton) findViewById(R.id.addMusicButton);
		btnChooseGallery.setOnClickListener(btnOpenGallery);
		ImageButton btnPlayMusic = (ImageButton) findViewById(R.id.playMusicButton);
		btnPlayMusic.setOnClickListener(btnPlay);

		db = new DatabaseHelper(this);
		showid = getIntent().getIntExtra("showID", 0);
		tv = (TextView) findViewById(R.id.ssMTitle);
		tv.setText(getIntent().getStringExtra("showTitle"));
		musicObj = db.getShowMusic(showid);

		musicList = (ListView) findViewById(R.id.musicListView);
		musicadapter = new MusicAdapter(this, musicObj);
		musicList.setAdapter(musicadapter);
		// Register the ListView for Context menu
		registerForContextMenu(musicList);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		musicTitle = ((TextView) info.targetView).getText().toString();
		info.targetView.setBackgroundColor(Color.rgb(153, 204, 255));
		musicId = info.id;
		menu.setHeaderTitle("Delete Music file:" + musicTitle);
		menu.add(0, v.getId(), 0, "Cancel");// groupId, itemId, order, title
		menu.add(0, v.getId(), 0, "Delete");
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getTitle() == "Delete") {
			Toast.makeText(getApplicationContext(),
					"Deleting music file-" + musicTitle, Toast.LENGTH_LONG)
					.show();
			ShowMusic sm = (ShowMusic) musicadapter.getItem((int) musicId);
			db.deleteShowMusic(sm.getId());
			musicObj.remove(sm);
			musicadapter.notifyDataSetChanged();
		} else if (item.getTitle() == "Cancel") {
			Toast.makeText(getApplicationContext(), "Cancelling delete",
					Toast.LENGTH_LONG).show();
			info.targetView.setBackgroundColor(Color.rgb(255, 255, 255));
		} else {
			return false;
		}
		return true;

	}

	public OnClickListener btnPlay = new OnClickListener() {

		@SuppressLint("InlinedApi")
		public void onClick(View view) {

			if (getIntent().getCharSequenceExtra("TAB").toString()
					.contentEquals("Music")) {
				playSlideShow();
			}
		}
	};

	public void playSlideShow() {
		// List<ShowMusic> playlist = db.getShowMusic(showid);
		ArrayList<String> musicUri = new ArrayList<String>();
		List<String> imgURI = new ArrayList<String>();
		imgURI = db.getAllimageURI(showid);
		musicUri = (ArrayList<String>) db.getShowMusicURI(showid);

		Bundle b = new Bundle();
		String key = "ImageFilePaths";
		String musickey = "MusicFilePaths";

		if (imgURI.isEmpty() && !musicUri.isEmpty()) {

			Toast.makeText(getApplicationContext(),
					"Cannot play Slideshow without images. Please select atleast one image", Toast.LENGTH_LONG)
					.show();
		} else if (imgURI.isEmpty() && musicUri.isEmpty()) {

			Toast.makeText(getApplicationContext(),
					"Please select music files & images to play slideshow",
					Toast.LENGTH_LONG).show();
		} else {

			b.putStringArrayList(key, (ArrayList<String>) imgURI);
			b.putStringArrayList(musickey, (ArrayList<String>) musicUri);
			Intent intent = new Intent(this, SlideShowActivity.class);
			intent.putExtras(b);
			startActivity(intent);
		}
	}

	public OnClickListener btnOpenGallery = new OnClickListener() {

	@SuppressLint("InlinedApi")
	public void onClick(View view) {
		if (getIntent().getCharSequenceExtra("TAB").toString()
				.contentEquals("Music")) {
			getmusic();

		}
	}
};
public void getmusic() {

	Bundle b = new Bundle();
	b.putInt("showID", showid);
	b.putString("showTitle",showT);
	Intent intent = new Intent(this, AudioMediaActivity.class);
	intent.putExtras(b);
	startActivity(intent); 
}
	@SuppressWarnings("static-access")
	protected void onStop() {
		super.onStop();

	}


	public class MusicAdapter extends BaseAdapter {
		private Context myContext;
		private List<ShowMusic> showmusic;

		// AssetManager assetManager = getAssets();

		public MusicAdapter(Context c, List<ShowMusic> musiclist) {
			myContext = c;
			showmusic = musiclist;
		}

		public int getCount() {
			return showmusic.size();
		}

		public Object getItem(int position) {
			return showmusic.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			TextView textView = new TextView(myContext);
			textView.setLayoutParams(new ListView.LayoutParams(
					LayoutParams.FILL_PARENT, 100));
			textView.setText("   " + showmusic.get(position).getMusic() + " "
					+ showmusic.get(position).getArtist());
			if (position % 2 == 0) {
				textView.setBackgroundColor(Color.rgb(204, 255, 255));
			} else {
				textView.setBackgroundColor(Color.rgb(255, 255, 255));
			}
			textView.setGravity(Gravity.CENTER_VERTICAL);
			return textView;
		}

		void OnPause() {
			db.close();
		}

	}

}

/**
 * 
 */
package org.bhavmayyin.musicalimageshow;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author bhavana
 * MusicDisplayActivity - Screen that allows to select music files with 
 * "Add New Music" button and show those files in List View.
 * Slideshow button to play Slideshow
 */
public class MusicDisplayActivity extends Activity {

	private static final int SELECT_MUSIC = 1;

	File file;
	DatabaseHelper db;
	int showID;
	ListView musicList;
	MusicAdapter musicAdapter;
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
		showID = getIntent().getIntExtra("showID", 0);
		musicObj = db.getShowMusic(showID);

		musicList = (ListView) findViewById(R.id.musicListView);
		musicAdapter = new MusicAdapter(this, musicObj);
		musicList.setAdapter(musicAdapter); // Set the adapter to the List View
		// Register the ListView for Context menu
		registerForContextMenu(musicList);
	}

	// Contextual menu to delete a selected music file
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		musicTitle = ((TextView) info.targetView).getText().toString();
		info.targetView.setBackgroundColor(Color.rgb(204, 0, 0)); // Red rim
		musicId = info.id;
		menu.setHeaderTitle("Delete Music file:" + musicTitle);
		menu.add(0, v.getId(), 0, "Cancel");
		menu.add(0, v.getId(), 0, "Delete");
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// If the selected option is "Delete", music file gets deleted
		if (item.getTitle() == "Delete") {
			Toast.makeText(getApplicationContext(),
					"Deleting music file-" + musicTitle, Toast.LENGTH_LONG)
					.show();
			ShowMusic sm = (ShowMusic) musicAdapter.getItem((int) musicId);
			db.deleteShowMusic(sm.getId());
			refreshList();
		// If the selected option is "Cancel", deletion of music file gets cancelled
		} else if (item.getTitle() == "Cancel") {
			Toast.makeText(getApplicationContext(), "Cancelling delete",
					Toast.LENGTH_LONG).show();
			info.targetView.setBackgroundColor(Color.rgb(255, 255, 255));
		} else {
			return false;
		}
		return true;

	}

	// Plays the Slide show on click of Slide show button 
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
		db.reopen();
		ArrayList<String> musicUri = new ArrayList<String>();
		List<String> imgUri = new ArrayList<String>();
		imgUri = db.getAllimageURI(showID); // Get Images' URIs for a slide show
		musicUri = (ArrayList<String>) db.getShowMusicURI(showID); // Get Music files' URIs for a slide show 
		
		Bundle b = new Bundle();
		String key = "ImageFilePaths";
		String musickey = "MusicFilePaths";

		// If there are no images in Images tab but music files in Music tab
		if (imgUri.isEmpty() && !musicUri.isEmpty()) {

			Toast.makeText(
					getApplicationContext(),
					"Cannot play Slideshow without images. Please select atleast one image",
					Toast.LENGTH_LONG).show();
		// If both Images and Music tabs does not contain any files			
		} else if (imgUri.isEmpty() && musicUri.isEmpty()) {

			Toast.makeText(getApplicationContext(),
					"Please select music files & images to play slideshow",
					Toast.LENGTH_LONG).show();
		} else { // Start the Slide show
     
			b.putStringArrayList(key, (ArrayList<String>) imgUri);
			b.putStringArrayList(musickey, (ArrayList<String>) musicUri);
			Intent intent = new Intent(this, SlideShowActivity.class);
			intent.putExtras(b);
			startActivity(intent);
		}
	}

	// Opens a custom Music library on click of "Add" button in Music tab
	public OnClickListener btnOpenGallery = new OnClickListener() {

		@SuppressLint("InlinedApi")
		public void onClick(View view) {
			if (getIntent().getCharSequenceExtra("TAB").toString()
					.contentEquals("Music")) {
				getmusic();
			}
		}
	};

	// Starts a new activity "AudioMediaActivity" for selecting music files
	public void getmusic() {

		Bundle b = new Bundle();
		b.putInt("showID", showID);
		b.putString("showTitle", showT);
		Intent intent = new Intent(this, AudioMediaActivity.class);
		intent.putExtras(b);
		startActivity(intent);
	}

	// Refresh the music files list by retrieving the music files using ShowID 
	// and then setting to adapter
	public void refreshList() {
		musicObj.clear();
		db.reopen();
		musicObj = db.getShowMusic(showID);
		musicAdapter.setMusicList(musicObj);
		musicAdapter.notifyDataSetChanged();
	}

	// Refresh the music files list on resuming the activity
	protected void onResume() {
		super.onResume();
		refreshList();
	}

	@SuppressWarnings("static-access")
	protected void onStop() {
		super.onStop();
	}

	// Adapter used to show the music files collection in list
	public class MusicAdapter extends BaseAdapter {
		private Context myContext;
		private List<ShowMusic> showmusic;

		public MusicAdapter(Context c, List<ShowMusic> musiclist) {
			myContext = c;
			showmusic = musiclist;
		}

		public void setMusicList(List<ShowMusic> newlist) {
			this.showmusic = newlist;
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

		// Create a dynamic text view to append the music file to the list view
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView textView = new TextView(myContext);
			textView.setLayoutParams(new ListView.LayoutParams(
					LayoutParams.FILL_PARENT, 60));
			StringBuffer result = new StringBuffer();// for using arrayList adapter
			String musicName = showmusic.get(position).getMusic();
			if (musicName.length() > 25){
				musicName = musicName.subSequence(0, 24) + ".....";
			}
			result.append("   " + musicName);
			
			// if (!showmusic.get(position).getArtist().isEmpty())
			// result.append(System.getProperty("line.separator") +
			// "   Artist: " + showmusic.get(position).getArtist());
			// + showmusic.get(position).getArtist());
			textView.setText(result);
			if (position % 2 == 0){
				textView.setBackgroundColor(Color.rgb(255, 255, 255));
			} else {
				textView.setBackgroundColor(Color.rgb(238, 238, 238));
			}
			textView.setGravity(Gravity.CENTER_VERTICAL);
			return textView;
		}

		// Close the database on pause of the current activity
		void OnPause() {
			db.close();
		}
	}
}
package org.bhavmayyin.musicalimageshow;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author bhavana
 * Custom Music library to select multiple music files available
 */
public class AudioMediaActivity extends ListActivity {
//activity to display a list of music on the device for user to pick
	File file;
	DatabaseHelper db;
	int showid;
	TextView tv;
	ListView musicList;
	InteractiveMediaAdapter musicadapter;
	List<ShowMusic> musicObj = new ArrayList<ShowMusic>();;
	String musicTitle;
	long musicId;
	MenuItem additem, acceptitem, edititem, deleteitem;
	AdapterView.AdapterContextMenuInfo info;
	int npos;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_audiomedia);//the xml layout

		db = new DatabaseHelper(this);
		showid = getIntent().getIntExtra("showID", 0);//to retrieve the parameter passed from previous activiy
		tv = (TextView) findViewById(R.id.ssATitle);
		tv.setText(getIntent().getStringExtra("showTitle"));
		initMediaList();//getting all music files and initializing musicObj list

		musicadapter = new InteractiveMediaAdapter(this, musicObj);//pass the arraylist to the adapter
		setListAdapter(musicadapter);
		ActionBar bar = getActionBar();
		bar.setTitle("Select Music");
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0099CC")));
	}

	public boolean onCreateOptionsMenu(Menu menu) {//use the default actionbar menu setting
		//instead of creating another menu to use
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.action_bar_item, menu);
		additem = menu.findItem(R.id.new_show);
		acceptitem = menu.findItem(R.id.accept_show);
		edititem = menu.findItem(R.id.edit_show);
		deleteitem = menu.findItem(R.id.delete_show);
		additem.setVisible(false);//hide this menu item
		edititem.setVisible(false);//hide this menu item
		deleteitem.setVisible(false);//hide this menu item

		return super.onCreateOptionsMenu(menu);
	}

	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		for (int i = 0; i < musicObj.size(); i++) {
			if (i == position) {//set the check box checked/unchecked
				musicObj.get(i).setisselected(!musicObj.get(i).getisSelected());
			}
		}
		((InteractiveMediaAdapter) musicadapter).notifyDataSetChanged();//update the view
	}

	@SuppressLint("NewApi")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {//only want 1 item on the action bar

		case R.id.accept_show:
			if (musicObj.size() > 0) {
				for (ShowMusic sm : musicObj) {
					if (sm.getisSelected()) {//add the selected music to the database
						//adding to db will return the music id
						sm.setID((int) db.addMusicGetID(sm.getName(),
								sm.getMusic(), sm.getArtist(), showid));

					}
				}
			}

			finish();//done with this activity and return to previous one
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@SuppressWarnings("static-access")
	protected void onStop() {
		super.onStop();
	}

	public void initMediaList() {
		//initialize and populate the music arraylist
		String selection = MediaStore.Audio.Media.IS_MUSIC + "!=0";
		ContentResolver cr = this.getContentResolver();
		//query a list of existing music files on the device
		Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
		Cursor cursor = cr.query(uri, null, selection, null, sortOrder);
		int count = 0;
		if (cursor != null) {//some music files are available
			count = cursor.getCount();
			if (count > 0) {//getting meta data of the music file
				while (cursor.moveToNext()) {
					ShowMusic sm = new ShowMusic();
					sm.setMusic(cursor.getString(cursor
							.getColumnIndex(MediaStore.Audio.Media.TITLE)));// DISPLAY_NAME
					sm.setArtist(cursor.getString(cursor
							.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
					sm.setlibraryid(cursor.getString(cursor
							.getColumnIndex(MediaStore.Audio.Media._ID)));
					sm.setname(cursor.getString(cursor
							.getColumnIndex(MediaStore.Audio.Media.DATA)));
					sm.setduration(cursor.getString(cursor
							.getColumnIndex(MediaStore.Audio.Media.DURATION)));
					musicObj.add(sm);//add the file to the adapter music arraylist
				}
			}
		}

		cursor.close();
	}

	public class InteractiveMediaAdapter extends ArrayAdapter<ShowMusic> {

		private final List<ShowMusic> list;
		private final Activity context;
//constructor
		public InteractiveMediaAdapter(Activity context, List<ShowMusic> list) {
			super(context, R.layout.audiogroup, list);
			this.context = context;
			this.list = list;
		}

		class ViewHolder {
			protected CheckBox musiccheck;
			protected TextView text;

		}

		@SuppressLint("NewApi")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = null;
			if (convertView == null) {
				LayoutInflater inflator = context.getLayoutInflater();
				view = inflator.inflate(R.layout.audiogroup, null);//individual layout from the list
				final ViewHolder viewHolder = new ViewHolder();
				viewHolder.musiccheck = (CheckBox) view
						.findViewById(R.id.musiccheck);
				viewHolder.musiccheck.setTag(list.get(position));
				viewHolder.text = (TextView) view.findViewById(R.id.mediatitle);

				view.setTag(viewHolder);
				view.setPadding(3, 0, 3, 15);
			} else {
				view = convertView;
				((ViewHolder) view.getTag()).musiccheck.setTag(list
						.get(position));//tag the check box with selected item
			}
			ViewHolder holder = (ViewHolder) view.getTag();//tag the view holder
			holder.musiccheck.setChecked(list.get(position).getisSelected());
			if (position % 2 == 0) {//to toggle color every other row
				view.setBackgroundColor(Color.rgb(255, 255, 255));
			} else {
				view.setBackgroundColor(Color.rgb(238, 238, 238));
			}
			StringBuffer result = new StringBuffer();// for using arrayList
														// adapter
			result.append(list.get(position).getMusic());
			if (!list.get(position).getArtist().isEmpty())
				result.append(System.getProperty("line.separator") + "Artist: "
						+ list.get(position).getArtist());
			holder.text.setText(result.toString());

			return view;
		}
	}

}

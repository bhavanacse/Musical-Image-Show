/**
 * 
 */
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
 * 
 */
public class AudioMediaActivity extends ListActivity {

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
		setContentView(R.layout.activity_audiomedia);

		db = new DatabaseHelper(this);
		showid = getIntent().getIntExtra("showID", 0);
		tv = (TextView) findViewById(R.id.ssATitle);
		tv.setText(getIntent().getStringExtra("showTitle"));
		initMediaList();

		musicadapter = new InteractiveMediaAdapter(this, musicObj);
		setListAdapter(musicadapter);
		ActionBar bar = getActionBar();
		bar.setTitle("Select Music");
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0099CC")));
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.action_bar_item, menu);
		additem = menu.findItem(R.id.new_show);
		acceptitem = menu.findItem(R.id.accept_show);
		edititem = menu.findItem(R.id.edit_show);
		deleteitem = menu.findItem(R.id.delete_show);
		additem.setVisible(false);
		edititem.setVisible(false);
		deleteitem.setVisible(false);

		return super.onCreateOptionsMenu(menu);
	}

	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		for (int i = 0; i < musicObj.size(); i++) {
			if (i == position) {
				musicObj.get(i).setisselected(!musicObj.get(i).getisSelected());
			}
		}
		((InteractiveMediaAdapter) musicadapter).notifyDataSetChanged();
	}

	@SuppressLint("NewApi")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.accept_show:
			if (musicObj.size() > 0) {
				for (ShowMusic sm : musicObj) {

					if (sm.getisSelected()) {

						sm.setID((int) db.addMusicGetID(sm.getName(),
								sm.getMusic(), sm.getArtist(), showid));

					}
				}
			}

			finish();

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@SuppressWarnings("static-access")
	protected void onStop() {
		super.onStop();
	}

	public void initMediaList() {
		// http://android-er.blogspot.com/2012/03/retrieve-playable-uri-from-mediastore.html
		String selection = MediaStore.Audio.Media.IS_MUSIC + "!=0";
		ContentResolver cr = this.getContentResolver();
		Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
		Cursor cursor = cr.query(uri, null, selection, null, sortOrder);
		int count = 0;
		if (cursor != null) {
			count = cursor.getCount();
			if (count > 0) {
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
					musicObj.add(sm);
				}
			}
		}

		cursor.close();
	}

	public class InteractiveMediaAdapter extends ArrayAdapter<ShowMusic> {

		private final List<ShowMusic> list;
		private final Activity context;

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
				view = inflator.inflate(R.layout.audiogroup, null);
				final ViewHolder viewHolder = new ViewHolder();
				viewHolder.musiccheck = (CheckBox) view
						.findViewById(R.id.musiccheck);
				viewHolder.musiccheck.setTag(list.get(position));
				viewHolder.text = (TextView) view.findViewById(R.id.mediatitle);

				view.setTag(viewHolder);
			} else {
				view = convertView;
				((ViewHolder) view.getTag()).musiccheck.setTag(list
						.get(position));
			}
			ViewHolder holder = (ViewHolder) view.getTag();
			holder.musiccheck.setChecked(list.get(position).getisSelected());
			if (position % 2 == 0) {
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

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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
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
	int showid ;
	TextView tv;
	ListView musicList;
	MusicAdapter musicadapter;
	List<ShowMusic> musicObj ;
	String musicTitle;
	long musicId;
	PlaySound mp;
	AdapterView.AdapterContextMenuInfo info;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_musicdisplay);

		ImageButton btnChooseGallery = (ImageButton) findViewById(R.id.addMusicButton);
		btnChooseGallery.setOnClickListener(btnOpenGallery);
		ImageButton btnPlayMusic = (ImageButton) findViewById(R.id.playMusicButton);

		db = new DatabaseHelper(this);
		showid = getIntent().getIntExtra("showID", 0);
		tv = (TextView)  findViewById(R.id.ssMTitle);
		tv.setText(getIntent().getStringExtra("showTitle"));
		musicObj = db.getShowMusic(showid);

		musicList = (ListView) findViewById(R.id.musicListView);
		musicadapter = new MusicAdapter(this, musicObj);
		musicList.setAdapter(musicadapter);
	    // Register the ListView  for Context menu
        registerForContextMenu(musicList);
	}
	

    @Override 
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
    {
            super.onCreateContextMenu(menu, v, menuInfo);
           info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            musicTitle = ((TextView) info.targetView).getText().toString();
            info.targetView.setBackgroundColor(Color.rgb(153,204,255));
   
            musicId = info.id;
            menu.setHeaderTitle("Delete Music file:" + musicTitle );  
            menu.add(0, v.getId(), 0, "Cancel");//groupId, itemId, order, title 
            menu.add(0, v.getId(), 0, "Delete"); 

    } 
    
    @Override  
    public boolean onContextItemSelected(MenuItem item){  
            if(item.getTitle()=="Delete"){
            	Toast.makeText(getApplicationContext(),"deleting music file-" + musicTitle,Toast.LENGTH_LONG).show();
            	ShowMusic sm = (ShowMusic) musicadapter.getItem((int)musicId);
            	db.deleteShowMusic(sm.getId());
            	musicObj.remove(sm);
            	musicadapter.notifyDataSetChanged();
            }  
            else if(item.getTitle()=="Cancel"){
            	Toast.makeText(getApplicationContext(),"Cancelling delete",Toast.LENGTH_LONG).show();
            	info.targetView.setBackgroundColor(Color.rgb(255,255,255));
            }else{
               return false;
            }  
          return true;  
                            
      }  
	public void btnPlayMusic (View  view) {

		@SuppressLint("InlinedApi")
		
				ArrayList<String> musicUri = new ArrayList<String>();
				for (int i = 0; i < musicObj.size();i++){
					musicUri.add(musicObj.get(i).getName());
				}
				mp = new PlaySound(getApplication(),musicUri);
			    PlaySound.play();
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
	@SuppressWarnings("static-access")
	protected void onStop(){
		super.onStop();
		mp.stop_mediaPlayer();
	}

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
				sm.setname(selectedMusicUri.toString());
				sm.setArtist( artistName);
				sm.setMusic(displayName);
				sm.setshowID(showid);
				sm.setID((int) db.addMusicGetID(selectedMusicUri.toString(),displayName,artistName,showid));
		
				musicObj.add(sm);
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
			return showmusic.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			TextView textView = new TextView(myContext);

			//textView.setLayoutParams(new ListView.LayoutParams(400, 100));
			textView.setLayoutParams(new ListView.LayoutParams(LayoutParams.FILL_PARENT,100 ));
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




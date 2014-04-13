/**
 * 
 */
package org.bhavmayyin.musicalimageshow;

import java.util.ArrayList;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

/**
 * @author bhavana
 *
 */
public class SelectionActivity extends TabActivity{
	ArrayList <Integer> selshowID = new ArrayList<Integer>();
	DatabaseHelper datasource;
	ArrayList<SlideShow> selshow = new ArrayList<SlideShow>();
 	ArrayList<ImageShow> selshowimg = new ArrayList<ImageShow>();
	Intent intent;
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_selection);
		intent = getIntent();
		selshowID = intent.getIntegerArrayListExtra("Selected Slide Show");
		datasource = new DatabaseHelper(this);
		for (int i = 0; i < selshowID.size(); i ++) {
			selshowimg = (ArrayList<ImageShow>) datasource.getAllimages(selshowID.get(i));
		}
			
		
		TabHost host = getTabHost();
		Resources r = getResources();
		
		TabSpec imageTab = host.newTabSpec("Images");
		//host.addTab(host.newTabSpec("Images")
		imageTab.setIndicator("Images", r.getDrawable(android.R.drawable.picture_frame));
			Intent intent = new Intent(this,  ImageDisplayActivity.class);
		    intent.putExtra("TAB", "Images");
		    intent.putExtra("showID", selshowID.get(0));
		imageTab.setContent(intent);
		host.addTab(imageTab);
			//.setContent(new Intent(this, ImageDisplayActivity.class).putExtra("TAB", "Images-No show")));
			//host.addTab(host.newTabSpec("Music")
		TabSpec musicTab = host.newTabSpec("Music");
		musicTab.setIndicator("Music", r.getDrawable(android.R.drawable.picture_frame));
		Intent intentm = new Intent(this,  MusicDisplayActivity.class);
	    intentm.putExtra("TAB", "Music");
	    intentm.putExtra("showID", selshowID.get(0)); //for testing for now
		musicTab.setContent(intentm);
		host.addTab(musicTab);

	
	
	}
	
}
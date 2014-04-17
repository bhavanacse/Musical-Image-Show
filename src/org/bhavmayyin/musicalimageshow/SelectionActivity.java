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
import android.widget.TextView;
import android.widget.TabHost.TabSpec;

/**
 * @author bhavana
 *
 */
public class SelectionActivity extends TabActivity{
	int selshowID ;
	DatabaseHelper datasource;
	SlideShow selshow = new SlideShow();
 	ArrayList<ImageShow> selshowimg = new ArrayList<ImageShow>();
 	ArrayList<String> musicURI = new ArrayList<String>();
 	String sTitle;
 	String sDesc;
 	String sTitleDesc;
	Intent intent;
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_selection);
		intent = getIntent();
		selshowID = intent.getIntExtra("Selected Slide Show",0);
		datasource = new DatabaseHelper(this);

		selshow=datasource.getSlideShow(selshowID);
		sTitle = selshow.getshowName();
		sDesc = selshow.getshowDescription();
		sTitleDesc = (sTitle + (!sDesc.isEmpty()? "-" :"") + sDesc);
	
		
		TabHost host = getTabHost();
		Resources r = getResources();
		
		TabSpec imageTab = host.newTabSpec("Images");
		imageTab.setIndicator("Images", r.getDrawable(android.R.drawable.picture_frame));
			Intent intent = new Intent(this,  ImageDisplayActivity.class);
		    intent.putExtra("TAB", "Images");
		    intent.putExtra("showID", selshowID);
		   intent.putExtra("showTitle", sTitleDesc);
		imageTab.setContent(intent);
		host.addTab(imageTab);
		
		TabSpec musicTab = host.newTabSpec("Music");
		musicTab.setIndicator("Music", r.getDrawable(android.R.drawable.picture_frame));
		Intent intentm = new Intent(this,  MusicDisplayActivity.class);
	    intentm.putExtra("TAB", "Music");
	    intentm.putExtra("showID", selshowID);
	    intentm.putExtra("showTitle", sTitleDesc);
	    musicTab.setContent(intentm);
		host.addTab(musicTab);

	}

}
/**
 * 
 */
package org.bhavmayyin.musicalimageshow;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

/**
 * @author bhavana
 * 
 */
@SuppressWarnings("deprecation")
public class SelectionActivity extends TabActivity {
	int selshowID;
	DatabaseHelper db;
	SlideShow selshow = new SlideShow();
	ArrayList<ImageShow> selshowimg = new ArrayList<ImageShow>();
	ArrayList<String> musicURI = new ArrayList<String>();
	String sTitle;
	String sDesc;
	String sTitleDesc;
	Intent intent;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		intent = getIntent();
		selshowID = intent.getIntExtra("Selected Slide Show", 0);
		db = new DatabaseHelper(this);

		selshow = db.getSlideShow(selshowID);
		db.closeDB();
		sTitle = selshow.getshowName();
		sDesc = selshow.getshowDescription();
		sTitleDesc = (sTitle + (!sDesc.isEmpty() ? "-" : "") + sDesc);
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0099CC")));

		TabHost host = getTabHost();
		Resources r = getResources();

		TabSpec imageTab = host.newTabSpec("Images");
		imageTab.setIndicator("Images",
				r.getDrawable(R.drawable.ic_action_picture));
		Intent intent = new Intent(this, ImageDisplayActivity.class);
		intent.putExtra("TAB", "Images");
		intent.putExtra("showID", selshowID);
		intent.putExtra("showTitle", sTitleDesc);
		imageTab.setContent(intent);
		host.addTab(imageTab);

		TabSpec musicTab = host.newTabSpec("Music");
		musicTab.setIndicator("Music", r.getDrawable(android.R.drawable.ic_media_play));
		Intent intentm = new Intent(this, MusicDisplayActivity.class);
		intentm.putExtra("TAB", "Music");
		intentm.putExtra("showID", selshowID);
		intentm.putExtra("showTitle", sTitleDesc);
		musicTab.setContent(intentm);
		host.addTab(musicTab);
	}
}
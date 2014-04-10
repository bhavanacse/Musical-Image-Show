/**
 * 
 */
package org.bhavmayyin.musicalimageshow;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

/**
 * @author bhavana
 *
 */
public class SelectionActivity extends TabActivity{
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_selection);
		
		TabHost host = getTabHost();
		Resources r = getResources();
		
		host.addTab(host.newTabSpec("Images")
			.setIndicator("Images", r.getDrawable(android.R.drawable.picture_frame))
			.setContent(new Intent(this, ImageDisplayActivity.class).putExtra("TAB", "Images")));
		host.addTab(host.newTabSpec("Music")
			.setIndicator("Music", r.getDrawable(android.R.drawable.picture_frame))
			.setContent(new Intent(this, MusicDisplayActivity.class).putExtra("TAB", "Music")));
	}
}
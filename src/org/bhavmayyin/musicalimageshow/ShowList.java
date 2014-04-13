package org.bhavmayyin.musicalimageshow;

import java.util.ArrayList;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class ShowList extends ListActivity {
	ListView listview;
	private ArrayList<SlideShow> slideshows;
	ArrayAdapter<SlideShow> adapter;
	DatabaseHelper datasource;
	EditText edTitle;
	EditText edDesc;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_list);
		setRequestedOrientation(ActivityInfo   
				  .SCREEN_ORIENTATION_PORTRAIT);
	    datasource = new DatabaseHelper(this);
	    slideshows = (ArrayList<SlideShow>) datasource.getAllSlideShows();
	   
	    if (slideshows.size() < 1){
	    	SlideShow ss = new SlideShow();
	    	ss.setshowName("Create Slide Show");
	    	ss.setshowDescription("Press + button on Action Bar to add First Slide show");
	    	ss.setId(-1);
	        slideshows.add(ss);
	    }
	    adapter = new InteractiveArrayAdapter(this,
	            slideshows);
	    setListAdapter(adapter);
	    edTitle = (EditText) findViewById(R.id.showname);
	    edDesc = (EditText) findViewById(R.id.showdesc);
	    edTitle.setVisibility(View.GONE);
	    edDesc.setVisibility(View.GONE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.action_bar_item, menu);
		
		return super.onCreateOptionsMenu(menu);

	}
	 @SuppressLint("NewApi")
	@Override
		public boolean onOptionsItemSelected(MenuItem item) {
		 	 switch (item.getItemId()) {
		 	 	case R.id.play_show:
		 	 		playShow();
	 	            return true;
		 	 	case R.id.delete_show:
		 	            deleteShow();
		 	            return true;
		 	 
		 	        case R.id.new_show:
		 	            addNewShow();
		 	            return true;
		 	 
		 	        case R.id.accept_show:
		 	        	String stitle = edTitle.getText().toString();
		 	        	String sdesc = edDesc.getText().toString();
		 	        	if(!stitle.isEmpty()) {
		 	        		SlideShow ss = new SlideShow(stitle,sdesc);
		 	        		
		 	        		if (slideshows.size() > 0 && slideshows.get(0).getId() == -1) {
		 	        			slideshows.remove(0); // a dummy first element
		 	        		}
		 	        		slideshows.add(0,ss);
		 	        		datasource.createSlideShow(ss);
		 	        		ss.setId(datasource.getShowID(stitle, sdesc));
		 	        		edTitle.setText("");
		 	        		edDesc.setText("");
		 	        		// hide the keyboard when input is done
		 	        		InputMethodManager inputmgr = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
		 	        			inputmgr.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
	 	        		    edTitle.setVisibility(View.GONE);
	 	        		    edDesc.setVisibility(View.GONE);
	 	        			adapter.notifyDataSetChanged();

		 	        	}
		 	        default:
		 	            return super.onOptionsItemSelected(item);
		 	        }
		  	    }
		 	 
		 	    public void deleteShow() {
		 	       // cancel the new input if inputbox is visible and X is pressed
		 	    	if (!edTitle.isShown()) {
			 	       	for (int i = slideshows.size() -1; i >=0 ;i--) {
							if (slideshows.get(i).isSelected()) {
								if (slideshows.get(i).getId() != -1) {
									datasource.deleteSlideShow((long) slideshows.get(i).getId());
									slideshows.remove(i);
								}
							}
			 	       	}
			 	       	} else {
			 	       		edTitle.setText("");
			 	       		edDesc.setText("");
			 	       		edTitle.setVisibility(View.GONE);
			 	       		edDesc.setVisibility(View.GONE);
			 	       	
			 	       	}
		 	    		adapter.notifyDataSetChanged();
			 	   } 

		 	    public void playShow() {
		 	        Toast.makeText(this, "Play Option Selected", Toast.LENGTH_SHORT).show();

		 	    }
		 	    public void addNewShow() {
		 	    	boolean checked = false;
		 	    	ArrayList<Integer> selectedlist = new ArrayList<Integer>();
		 	        Toast.makeText(this, "add Option Selected", Toast.LENGTH_SHORT).show();
			 	   	for (int i = 0;i < slideshows.size();i++) {
						if (slideshows.get(i).isSelected()) {
							checked = true;
							selectedlist.add(slideshows.get(i).getId());
						}
			 	   	}
			 	   	if (checked){
			 	    	Intent intent = new Intent(this, SelectionActivity.class);
			 	    	intent.putExtra("Selected Slide Show", selectedlist);
			 	    	startActivity(intent);
			 	   	} else {

			 	   		edTitle.setVisibility(View.VISIBLE);
			 	   		edDesc.setVisibility(View.VISIBLE);
			 	   		edTitle.requestFocus();
			 	 
			 	   	}
		 	    }

 	   public void setActivityBackgroundcolor( int color){
 	    	View view = this.getWindow().getDecorView();
 	    	view.setBackgroundColor(color);
 	   }
 	   void OnStop(){
 		   datasource.closeDB();
 	   }
 	   
}

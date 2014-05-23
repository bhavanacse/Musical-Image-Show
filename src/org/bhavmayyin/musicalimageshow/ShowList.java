/*
 * ShowList screen which displays the list of Slideshows
 * Allows to create, delete and edit a Slide show 
 */

package org.bhavmayyin.musicalimageshow;

import java.util.ArrayList;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

public class ShowList extends ListActivity {
	ListView listview;
	private ArrayList<SlideShow> slideshows;
	ArrayAdapter<SlideShow> adapter;
	DatabaseHelper datasource;
	EditText edTitle;
	EditText edDesc;
	int npos = -1;
	Menu actionmenu;
	int actionBarState = 0;// for display action bar menu item
	MenuItem additem, acceptitem, edititem, deleteitem;// menu items

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_show_list);
		datasource = new DatabaseHelper(this);// open database instance
		slideshows = (ArrayList<SlideShow>) datasource.getAllSlideShows();

		setDummyList();// add a dummy node if the list is empty
		adapter = new InteractiveArrayAdapter(this, slideshows);
		setListAdapter(adapter);
		edTitle = (EditText) findViewById(R.id.showname);
		edDesc = (EditText) findViewById(R.id.showdesc);
		edTitle.setVisibility(View.GONE);// hide the editbox from the view
		edDesc.setVisibility(View.GONE);
		ActionBar bar = getActionBar();// implement action bar
		bar.setTitle("Imageshows");// set bar title and color
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0099CC")));

	}

	public void setDummyList() {// initially the list is empty-display a message
		if (slideshows.size() < 1) {// new list no music
			SlideShow ss = new SlideShow();// slide show object
			ss.setshowName("Press + button");
			ss.setshowDescription("to add Imageshow");
			ss.setId(-1);// so it is dummy item
			slideshows.add(ss);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.action_bar_item, menu);
		additem = menu.findItem(R.id.new_show);
		acceptitem = menu.findItem(R.id.accept_show);
		edititem = menu.findItem(R.id.edit_show);
		deleteitem = menu.findItem(R.id.delete_show);
		if (menu != null) {
			setActionBar();// function to set menu item display
		}
		return super.onCreateOptionsMenu(menu);

	}

	protected void setActionBar() {
		// different state setting different actionBar icons
		switch (actionBarState) {
		case 0: // initial state
			deleteitem.setVisible(false);
			acceptitem.setVisible(false);
			additem.setVisible(true);
			edititem.setVisible(false);
			break;
		case 1: // add item
			deleteitem.setVisible(true);
			acceptitem.setVisible(true);
			additem.setVisible(false);
			edititem.setVisible(false);
			break;
		case 2: // item selected
			deleteitem.setVisible(true);
			acceptitem.setVisible(false);
			additem.setVisible(false);
			edititem.setVisible(true);
			break;
		default: // every menu item is displayed
			deleteitem.setVisible(true);
			acceptitem.setVisible(true);
			additem.setVisible(true);
			edititem.setVisible(true);
		}
	}

	@Override
	// click on anywhere on the list the check box will be set
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		//only if it is not in add new slide show mode
		if (!edTitle.isShown()) {
			npos = position;
			for (int i = 0; i < slideshows.size(); i++) {
				if (i != position) {
					slideshows.get(i).setselected(false);// unselect other
				} else {
					if (slideshows.get(i).getId() != -1) {// not a dummy list
						slideshows.get(i).setselected(
								!slideshows.get(i).isSelected());
						if (!slideshows.get(i).isSelected()) {
							npos = -1;
							actionBarState = 0; // none selected then original
												// action bar with + only
						} else {
							actionBarState = 2; // action bar has edit, delete
												// only
						}
						invalidateOptionsMenu();// reset the menu item on the
												// action bar
					}
				}
			}
		}
		((InteractiveArrayAdapter) adapter).notifyDataSetChanged();
	}

	@SuppressLint("NewApi")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {// on the action bar
		switch (item.getItemId()) {
		case R.id.edit_show:
			editShow();// will move to next activity
			return true; // edit
		case R.id.delete_show:
			deleteShow();
			return true;
		case R.id.new_show:
			actionBarState = 1;
			invalidateOptionsMenu();
			addNewShow();// setting up new input screen
			return true;
		case R.id.accept_show:
			acceptChoice();// verify the new input and move on

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void deleteShow() {
		// cancel the new input if inputbox is visible and X is pressed
		if (edTitle.isShown()) {
			resetInputScreen();
		} else {
			// no selection
			if (npos != -1 && slideshows.get(npos).getId() != -1) {
				// the first entry, not the dummy entry
				alertbox("Delete Imageshow",
						"Are you sure you want to delete Imageshow "
								+ slideshows.get(npos).getshowName() + " ?");
			}
		}
		adapter.notifyDataSetChanged();

	}

	public void editShow() {
		if (slideshows.get(0).getId() > -1) {
			boolean checked = false;
			if (npos > -1) {// not a dummy one
				checked = slideshows.get(npos).isSelected();
			}
			if (checked) { //
				Intent intent = new Intent(this, SelectionActivity.class);
				intent.putExtra("Selected Slide Show", slideshows.get(npos)
						.getId());
				startActivity(intent);// select the image and music activity
			}
		}
	}

	public void addNewShow() {
		if (slideshows.get(0).getId() > -1) {
			boolean checked = false;
			if (npos > -1) {
				checked = slideshows.get(npos).isSelected();
			}

			if (checked) {
				Intent intent = new Intent(this, SelectionActivity.class);
				intent.putExtra("Selected Slide Show", slideshows.get(npos)
						.getId());
				startActivity(intent);
			} else {
				// display the input box
				edTitle.setVisibility(View.VISIBLE);
				edDesc.setVisibility(View.VISIBLE);
				edTitle.requestFocus();

			}
		} else {
			edTitle.setVisibility(View.VISIBLE);
			edDesc.setVisibility(View.VISIBLE);
			edTitle.requestFocus();
		}
	}

	public void acceptChoice() {// for adding new show after input
		String stitle = edTitle.getText().toString();
		String sdesc = edDesc.getText().toString();
		if (!stitle.isEmpty()) {
			SlideShow ss = new SlideShow(stitle, sdesc);

			if (slideshows.size() > 0 && slideshows.get(0).getId() == -1) {
				slideshows.remove(0); // a dummy first element
			}
			slideshows.add(0, ss);// add to the top of the list
			ss.setId((int) datasource.createSlideShow(ss));
			resetInputScreen();// verify valid input entries

		} else {
			if (edTitle.isShown()) {
				alertbox("Cannot add Imageshow",
						"Imageshow name cannot be blank");
			}
		}

	}

	public void setActivityBackgroundcolor(int color) {
		View view = this.getWindow().getDecorView();
		view.setBackgroundColor(color);
	}

	protected void resetInputScreen() {
		edTitle.setText("");
		edDesc.setText("");
		// hide the keyboard when input is done
		InputMethodManager inputmgr = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
		inputmgr.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);// toggling
																			// keyboard
																			// shown/not
																			// shown
		edTitle.setVisibility(View.GONE);
		edDesc.setVisibility(View.GONE);
		adapter.notifyDataSetChanged();
		actionBarState = 0; // reset the action bar to display menu item
		invalidateOptionsMenu();// refresh the menu bar
	}

	protected void alertbox(String title, String msg) {// to display alert msg
		final String finaltitle = title;
		new AlertDialog.Builder(this)

				.setTitle(finaltitle)
				.setMessage(msg)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						if (finaltitle.toLowerCase().contains("delete")) {
							datasource.deleteSlideShow((long) slideshows.get(
									npos).getId());
							slideshows.remove(npos);
							npos = -1;
							setDummyList();// nothing on the list
							adapter.notifyDataSetChanged();
							actionBarState = 0;
							invalidateOptionsMenu();// reset menu bar items
						}
						if(finaltitle.toLowerCase().contains("cannot add")){
							edTitle.requestFocus();
						}
					}
				})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// do nothing if it is delete

								if (!finaltitle.toLowerCase()
										.contains("delete")) {
									resetInputScreen();// hide/unhide editboxes
								}

							}
						}).show();
	}

	void OnStop() {
		datasource.closeDB();// make sure database is closed to avoid memory
								// leak
	}

}

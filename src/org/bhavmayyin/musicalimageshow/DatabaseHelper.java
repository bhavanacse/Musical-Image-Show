package org.bhavmayyin.musicalimageshow;


//import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

	private static final String LOG = "DatabaseHelper";

	// Database Version
	private static final int DATABASE_VERSION = 2;

		// Database Name
		
	private static final String DATABASE_NAME = "imageslideshow";

	// Table Names

		private static final String TABLE_SLIDESHOW = "slideshows";
		private static final String TABLE_IMAGESHOW = "imageshows";
		private static final String TABLE_SHOWMUSIC = "showmusic";

// Common column names

		private static final String KEY_ID = "id";
		private static final String KEY_SHOWNAME = "showname";
		private static final String KEY_SHOWID = "showId";
		
		// slideshow Table - column names

		private static final String KEY_DESCRIPTION = "description";

		// imageshow Table - column names

		private static final String KEY_IMAGE = "showimage";

		// showmusic Table - column names

		private static final String KEY_MUSIC = "showmusic";

// Table Create Statements

// slideshow table create statement

		private static final String CREATE_TABLE_SLIDESHOW = "CREATE TABLE "
				+ TABLE_SLIDESHOW + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_SHOWNAME
				+ " TEXT," + KEY_DESCRIPTION + " TEXT" + ")";

// imageshow table create statement

		private static final String CREATE_TABLE_IMAGESHOW = "CREATE TABLE " + TABLE_IMAGESHOW
				+ "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_SHOWID + " INTEGER, " 
				+ KEY_IMAGE + " TEXT" + ")";

//  showmusic table create statement

		private static final String CREATE_TABLE_SHOWMUSIC = "CREATE TABLE "
				+ TABLE_SHOWMUSIC + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ KEY_SHOWID + " INTEGER, " +  KEY_MUSIC + " TEXT"
			+ ")";

public DatabaseHelper(Context context) {
	super(context, DATABASE_NAME, null, DATABASE_VERSION);

}

@Override

public void onCreate(SQLiteDatabase db) {

// creating required tables
	db.execSQL(CREATE_TABLE_SLIDESHOW);
	db.execSQL(CREATE_TABLE_IMAGESHOW);
	db.execSQL(CREATE_TABLE_SHOWMUSIC);

}

@Override

public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

// on upgrade drop older tables
	db.execSQL("DROP TABLE IF EXISTS " + TABLE_SLIDESHOW);
	db.execSQL("DROP TABLE IF EXISTS " + TABLE_IMAGESHOW);
	db.execSQL("DROP TABLE IF EXISTS " + TABLE_SHOWMUSIC);

// create new tables

	onCreate(db);

}

// ------------------------ "slideshow" table methods ----------------//

/*

* Creating a slideshow

*/

public void createSlideShow(SlideShow ss) {

	SQLiteDatabase db = this.getWritableDatabase();
	ContentValues values = new ContentValues();
	values.put(KEY_SHOWNAME, ss.getshowName());
	values.put(KEY_DESCRIPTION, ss.getshowDescription());
	// insert row
	//long show_id = 
	db.insert(TABLE_SLIDESHOW, null, values);
	// insert image_ids
	//for (long show_id : shows_ids) {
	//	createImageShow(show_id, tag_id);
	//}
	
}
public void createSlideShow(String title, String Desc, ArrayList<ImageShow> img) {

	SQLiteDatabase db = this.getWritableDatabase();
	ContentValues values = new ContentValues();
	values.put(KEY_SHOWNAME, title);
	values.put(KEY_DESCRIPTION, Desc);
	// insert row
	long show_id = 	db.insert(TABLE_SLIDESHOW, null, values);
	// insert image_ids
	for (int i = 0; i < img.size(); i++) {
		
		createImageShow(img.get(i).getShowImage(), show_id);
	}
	
}
/*

* get single todo

*/

public SlideShow getSlideShow(long showid) {

	SQLiteDatabase db = this.getReadableDatabase();
	String selectQuery = "SELECT * FROM " + TABLE_SLIDESHOW + " WHERE "
			+ KEY_ID + " = " + showid;
	
		//Log.e(LOG, selectQuery);
		Cursor c = db.rawQuery(selectQuery, null);
		if (c != null)
			c.moveToFirst();
		SlideShow td = new SlideShow();
		td.setId(c.getInt(c.getColumnIndex(KEY_ID)));
		td.setshowName(c.getString(c.getColumnIndex(KEY_SHOWNAME)));
		td.setshowDescription(c.getString(c.getColumnIndex(KEY_DESCRIPTION)));

		return td;
}

/**

* getting all SLIDESHOWS

* */

public List<SlideShow> getAllSlideShows() {
	List<SlideShow> slideshows = new ArrayList<SlideShow>();
	String selectQuery = "SELECT * FROM " + TABLE_SLIDESHOW +
			" ORDER BY " + KEY_ID + " DESC" ;
//Log.e(LOG, selectQuery);
	SQLiteDatabase db = this.getReadableDatabase();
	Cursor c = db.rawQuery(selectQuery, null);

// looping through all rows and adding to list

	if (c.moveToFirst()) {
		do {
			SlideShow td = new SlideShow();
			td.setId(c.getInt((c.getColumnIndex(KEY_ID))));
			td.setshowName(c.getString(c.getColumnIndex(KEY_SHOWNAME)));
			td.setshowDescription(c.getString(c.getColumnIndex(KEY_DESCRIPTION)));

			// adding to slideshow list
			slideshows.add(td);

		} while (c.moveToNext());

	}
	return slideshows;

}

/**


/*

* getting slideshow count

*/

public int getSlideShowCount() {

	String countQuery = "SELECT * FROM " + TABLE_SLIDESHOW;
	SQLiteDatabase db = this.getReadableDatabase();
	Cursor cursor = db.rawQuery(countQuery, null);
	int count = cursor.getCount();
	cursor.close();
	// return count
	return count;

}

/*

* Updating a SlideShow

*/

public int updateSlideShow(SlideShow ss) {

	SQLiteDatabase db = this.getWritableDatabase();
	ContentValues values = new ContentValues();
	values.put(KEY_SHOWNAME, ss.getshowName());
	values.put(KEY_DESCRIPTION, ss.getshowDescription());
	// updating row
	return db.update(TABLE_SLIDESHOW, values, KEY_ID + " = ?",
	new String[] { String.valueOf(ss.getId()) });
}

//get slideshow id (for new added show)

public int getShowID(String name, String desc){
	int showid;
	String idQquery = "SELECT * FROM " + TABLE_SLIDESHOW
		+ " WHERE " + KEY_SHOWNAME + " = '" + name + "'";
				 	        		
	//if (!desc.equals("")){ 
		idQquery = idQquery +  " AND " + KEY_DESCRIPTION + "= '" + desc + "'";
	//}
	SQLiteDatabase db = this.getReadableDatabase();
	Cursor cursor = db.rawQuery(idQquery, null);
	if (cursor != null) {
		cursor.moveToFirst();
		showid =  cursor.getInt(cursor.getColumnIndex(KEY_ID));
	} else {
		showid = -1;
	}
	cursor.close();
	return showid;
}

/*

* Deleting a todo

*/

public void deleteSlideShow(long ss_id) {
	SQLiteDatabase db = this.getWritableDatabase();
	// before delete the slideshow, all images and music should be delete too
	List<ImageShow> imageshows = new ArrayList<ImageShow>(getAllimages(ss_id));
	
		db.delete(TABLE_SLIDESHOW, KEY_ID + " = ?",
		new String[] { String.valueOf(ss_id) });

}

// ------------------------ "image" table methods ----------------//

/*

* Creating imageshows 1 by 1

*/

public void createImageShow(String imgs, long showid) {
	SQLiteDatabase db = this.getWritableDatabase();
	ContentValues values = new ContentValues();
	values.put(KEY_SHOWID, showid);
	// need to find the file location and store Uri as string
	values.put(KEY_IMAGE, imgs);

// insert row
	 db.insert(TABLE_IMAGESHOW, null, values);

//return image_id;

}

/**

* getting all imageshow

* */

public List<ImageShow> getAllimages(long showid) {
	List<ImageShow> imageshows = new ArrayList<ImageShow>();
	String selectQuery = "SELECT * FROM " + TABLE_IMAGESHOW  
			+ " WHERE " + KEY_SHOWID 
			+ " = " + showid;
	
	//Log.e(LOG, selectQuery);
	SQLiteDatabase db = this.getReadableDatabase();
	Cursor c = db.rawQuery(selectQuery, null);
	// looping through all rows and adding to list
	if (c.moveToFirst()) {
		do {
			ImageShow t = new ImageShow();
			t.setId(c.getInt((c.getColumnIndex(KEY_ID))));
			t.setShowID (c.getColumnIndex(KEY_SHOWID));
			t.setShowImage(c.getString(c.getColumnIndex(KEY_IMAGE)));
	// adding to imageshow list
			imageshows.add(t);
		} while (c.moveToNext());
	
	}
	
	return imageshows;

}

/*

/*

* Deleting an image

*/

public void deleteImageShow(ImageShow is) {

	SQLiteDatabase db = this.getWritableDatabase();
	// before deleting an image
	db.delete(TABLE_IMAGESHOW, KEY_ID + " = ?",
			new String[] { String.valueOf(is) });

}

// ------------------------ "SHOWMUSIC" table methods ----------------//

/*

* Creating SHOWMUSIC

*/

public void createShowMusic(long slideshow_id, String music) {
	SQLiteDatabase db = this.getWritableDatabase();
	ContentValues values = new ContentValues();
	values.put(KEY_SHOWID, slideshow_id);
	values.put(KEY_MUSIC, music);

	 db.insert(TABLE_SHOWMUSIC, null, values);

}
//delete a music

public void deleteShowMusic(long id) {

	SQLiteDatabase db = this.getWritableDatabase();
	// 
	db.delete(TABLE_IMAGESHOW, KEY_ID + " = ?",
			new String[] { String.valueOf(id) });

}

// closing database
public void closeDB() {

	SQLiteDatabase db = this.getReadableDatabase();

	if (db != null && db.isOpen())
		db.close();
		
}
}
	


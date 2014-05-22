package org.bhavmayyin.musicalimageshow;

public class ShowMusic { // define the ShowMusic object
	int id;
	String showname;// uri
	int showid;
	String music;
	String artist;
	boolean isSelected;
	String libraryid;
	String duration;

	public ShowMusic() {
		// TODO Auto-generated constructor stub
	}

	public ShowMusic(String name) {
		this.showname = name;
	}

	public ShowMusic(String name, String music) {
		this.showname = name;
		this.music = music;
	}

	public void setID(int i) {
		this.id = i;
	}

	public void setshowID(int i) {
		this.showid = i;
	}

	public void setArtist(String s) {
		this.artist = s;
	}

	public void setname(String s) {
		this.showname = s;
	}

	public void setMusic(String music) {
		this.music = music;
	}

	public void setisselected(boolean b) {
		this.isSelected = b;
	}

	public void setlibraryid(String s) {
		this.libraryid = s;
	}

	public void setduration(String s) {
		this.duration = s;
	}

	public int getId() {
		return this.id;
	}

	public int getshowId() {
		return this.showid;
	}

	public String getName() {
		return this.showname;
	}

	public String getMusic() {
		return this.music;
	}

	public String getArtist() {
		return this.artist;
	}

	public boolean getisSelected() {
		// TODO Auto-generated method stub
		return isSelected;
	}

	public String getlibraryid() {
		return this.libraryid;
	}

	public String getduration() {
		return this.duration;
	}
}

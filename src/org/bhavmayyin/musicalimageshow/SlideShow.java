package org.bhavmayyin.musicalimageshow;

import java.util.ArrayList;

public class SlideShow { // define the SlideShow object
	private int id;
	private ArrayList<ImageShow> imglist;
	private String showName;
	private String showDescription;
	private boolean selected;

	// empty constructor
	public SlideShow() {
	}

	public SlideShow(String name) {
		this.showName = name;
	}

	public SlideShow(String name, String desc) {
		this.showName = name;
		this.showDescription = desc;
	}

	public SlideShow(String name, int id) {
		this.showName = name;
		this.id = id;
	}

	public SlideShow(String name, String desc, int id) {
		this.showName = name;
		this.showDescription = desc;
		this.id = id;
	}

	public void setId(int i) {
		this.id = i;
	}

	public void setshowName(String s) {
		this.showName = s;
	}

	public void setshowDescription(String s) {
		this.showDescription = s;
	}

	public void setselected(boolean b) {
		this.selected = b;
	}

	public int getId() {
		return this.id;
	}

	public String getshowName() {
		return this.showName;
	}

	public String getshowDescription() {
		return this.showDescription;
	}

	public boolean isSelected() {
		return this.selected;
	}

	public void setshowimage(ArrayList<ImageShow> imglist) {
		this.imglist = imglist;
	}

	public ArrayList<ImageShow> getimglist() {
		return imglist;
	}
}

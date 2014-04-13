package org.bhavmayyin.musicalimageshow;

public class ShowMusic {
	int id;
	String showname;
	String music;
	
	public ShowMusic() {
		// TODO Auto-generated constructor stub
	}
	public ShowMusic(String name){
		this.showname = name;
	}
	public ShowMusic(String name, String music){
		this.showname = name;
		this.music = music;
	}
	public void setID(int i){
		this.id = i;
	}
	public void setname(String s){
		this.showname = s;
	}
	public void setMusic(String music){
		this.music = music;
	}
	public int getId(){
		return this.id;
	}
	public String getName(){
		return this.showname ;
	}
	public String getMusic(){
		return this.music ;
	}
}

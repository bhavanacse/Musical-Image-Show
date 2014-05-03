package org.bhavmayyin.musicalimageshow;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import android.content.Context;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.widget.Toast;

public class PlaySound {
	private static HashSet<MediaPlayer> mpSet = new HashSet<MediaPlayer>();
	static int musiccnt = 0;
	private static ArrayList<String> musicList = new ArrayList<String>();
	//private static Context context;
	
	
	@SuppressWarnings("static-access")
	public PlaySound( ArrayList<String> musicFile){
		this.musicList = musicFile;
		
	}
	
	static void play() {
	
			FileDescriptor fd = null;
		      
			try {
				FileInputStream fis = new FileInputStream(musicList.get(musiccnt));
		        fd = fis.getFD();
		        MediaPlayer mp = new MediaPlayer();      
		        mp.setDataSource(fd);
		        mp.setAudioStreamType(AudioManager.STREAM_RING);
		        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
					@Override
				public void onCompletion(MediaPlayer mp) {
					// TODO Auto-generated method stub
						
					musiccnt++;
					if (musiccnt >= musicList.size()){
						musiccnt = 0;
					}
					mpSet.remove(mp);
					mp.stop();
					mp.release();
					continuePlay();
					
				}
			});
		    mpSet.add(mp);
	        mp.prepare();
	        
	      //  Toast.makeText(context," playing -"  + musicList.get(musiccnt),Toast.LENGTH_LONG).show();
	        mp.start();
			//mediaPlayer.setLooping(true);
		} catch(Exception e) {
			e.printStackTrace();
		}	
	}
	protected static void continuePlay(){
		play();
	}
	
    protected static void stop_mediaPlayer() {
    	try {
		for (MediaPlayer mp : mpSet) {
			if(mp!= null){
				mp.stop();
				mp.release();
			}
		}
    	}catch (Exception e) {
    		e.printStackTrace();
    	}
		mpSet.clear();
    }
	static void stop(){
		stop_mediaPlayer();
	}
}

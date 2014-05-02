package org.bhavmayyin.musicalimageshow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.widget.Toast;

public class PlaySound {
	private static HashSet<MediaPlayer> mpSet = new HashSet<MediaPlayer>();
	static int musiccnt = 0;
	private static ArrayList<String> musicList = new ArrayList<String>();
	private static Context context;
	
	
	@SuppressWarnings("static-access")
	public PlaySound( Context context, ArrayList<String> musicFile){
		this.musicList = musicFile;
		this.context = context;
	}
	static void play(String uri) {
		try {
	        MediaPlayer mp = new MediaPlayer();
	        mp.setDataSource(context, Uri.parse(uri));
	        
	        mp.setAudioStreamType(AudioManager.STREAM_RING);
	        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
					@Override
				public void onCompletion(MediaPlayer mp) {
					// TODO Auto-generated method stub
					musiccnt++;
					mpSet.remove(mp);
					mp.stop();
					mp.release();
				}
			});
	        mp.prepare();
	        
	        mpSet.add(mp);
	       // Toast.makeText(context," playing -"  + uri,Toast.LENGTH_LONG).show();
	        mp.start();
			//mediaPlayer.setLooping(true);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	static void play() {
		try {
	        MediaPlayer mp = new MediaPlayer();
	        mp.setDataSource(context, Uri.parse(musicList.get(musiccnt)));
	        
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
	        mp.prepare();
	        mpSet.add(mp);
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

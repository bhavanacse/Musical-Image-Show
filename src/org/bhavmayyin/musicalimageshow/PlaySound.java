/*
 * Media Player functions defined to perform the operations 
 * of the music played along with Slide show 
 */

package org.bhavmayyin.musicalimageshow;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashSet;
import android.media.AudioManager;
import android.media.MediaPlayer;

public class PlaySound {
	private static HashSet<MediaPlayer> mpSet = new HashSet<MediaPlayer>();
	static int musiccnt = 0;
	private static ArrayList<String> musicList = new ArrayList<String>();
	private static int stop = 0;//the status state setting
	private static int length;

	@SuppressWarnings("static-access")
	public PlaySound(ArrayList<String> musicFile) {
		this.musicList = musicFile;
	}

	static void play() {//check the stop variable setting state
		if (stop == 0) {//if stop != 0, will not play
			FileDescriptor fd = null;

			try {
				FileInputStream fis = new FileInputStream(
						musicList.get(musiccnt));//the arraylist index
				fd = fis.getFD();
				MediaPlayer mp = new MediaPlayer();
				mp.setDataSource(fd);
				mp.setAudioStreamType(AudioManager.STREAM_RING);
				mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer mp) {
						// once player stop

						musiccnt++;//increment the index
						if (musiccnt >= musicList.size()) {
							musiccnt = 0;//reset index to beginning
						}
						mpSet.remove(mp);
						mp.stop();
						mp.release();
						continuePlay();

					}
				});
				mpSet.add(mp); //reset the player
				mp.prepare();
				mp.start();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	protected static void continuePlay() {
		play();
	}

	protected static boolean isplaying() {
		try { // tell whether the player is playing
			if (!mpSet.isEmpty()) {
				return mpSet.iterator().next().isPlaying();
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	protected static void pause() {
		try {

			if (!mpSet.isEmpty()) {
				MediaPlayer mpplayer = mpSet.iterator().next();
				mpplayer.pause();//want to track the current stop position 
				length = mpplayer.getCurrentPosition();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected static void resume() {
		try {

			if (!mpSet.isEmpty()) {
				MediaPlayer mpplayer = mpSet.iterator().next();
				mpplayer.seekTo(length);//want to restart at the last stop position
				mpplayer.start();
			}
		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	protected static void stop_mediaPlayer() {
		try {//stop all the music playing
			for (MediaPlayer mp : mpSet) {
				if (mp != null) {
					mp.stop();
					mp.release();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		mpSet.clear();
	}

	static void stop() {
		stop_mediaPlayer();
	}

	static void stop(int real) {
		stop = real; //setting the stop state
		stop_mediaPlayer();
	}
}

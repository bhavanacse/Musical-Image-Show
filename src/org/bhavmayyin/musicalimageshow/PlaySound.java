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
	private static int stop = 0;
	private static int length;

	@SuppressWarnings("static-access")
	public PlaySound(ArrayList<String> musicFile) {
		this.musicList = musicFile;
	}

	static void play() {
		if (stop == 0) {
			FileDescriptor fd = null;

			try {
				FileInputStream fis = new FileInputStream(
						musicList.get(musiccnt));
				fd = fis.getFD();
				MediaPlayer mp = new MediaPlayer();
				mp.setDataSource(fd);
				mp.setAudioStreamType(AudioManager.STREAM_RING);
				mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer mp) {
						// TODO Auto-generated method stub

						musiccnt++;
						if (musiccnt >= musicList.size()) {
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
		try {
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
				mpplayer.pause();
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
				mpplayer.seekTo(length);
				mpplayer.start();
			}
		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	protected static void stop_mediaPlayer() {
		try {
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
		stop = real;
		stop_mediaPlayer();
	}
}

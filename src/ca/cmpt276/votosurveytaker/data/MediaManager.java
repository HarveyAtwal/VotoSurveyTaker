package ca.cmpt276.votosurveytaker.data;

import java.io.IOException;

import ca.cmpt276.votosurveytaker.R;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

/**
 * 
 * Manages audio content
 *
 */

public class MediaManager {

	MediaPlayer player;
	private String songURL;
	private Activity activity;
	private SeekBar seekBar;
	private ImageButton mediaButton;
    private Handler mHandler = new Handler();
	protected boolean completed = false;
	
	public MediaManager(Activity activity) {
		this.activity = activity;
		seekBar = (SeekBar) activity.findViewById(R.id.songProgressBar);
		mediaButton = (ImageButton) activity.findViewById(R.id.imgButtonMedia);
	}
	
	public String getSongURL() {
		return songURL;
	}

	public void setSongURL(String songURL) {
		this.songURL = songURL;
	}

	public void startMusic() {
		// Not yet created:
		completed = false;
		if (player == null) {
			ProgressBar progress = (ProgressBar) activity.findViewById(R.id.mediaLoading);
			progress.setVisibility(View.VISIBLE);
			player = new MediaPlayer();

			// Handle media player errors (such as file not found...)
			player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
				@Override
				public boolean onError(MediaPlayer mp, int what, int extra) {
					stopMusic();
					switch(extra) {
					case MediaPlayer.MEDIA_ERROR_IO:
						Log.e("MediaDemo", "Media IO error.");
						break;
					case MediaPlayer.MEDIA_ERROR_MALFORMED:
						Log.e("MediaDemo", "Media malformed error.");
						break;
					default:
						Log.e("MediaDemo", "Media error: " + extra);
					}
					return true;
				}
			});
			
			// Load music in background so it does not hold up UI thread.
			try {
				player.setDataSource(songURL);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			player.prepareAsync();
			
			// Play music once it is prepared.
			player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
				@Override
				public void onPrepared(MediaPlayer mp) {
					// Change Play Button and add Listener
					initMediaButton();
					
					//Hide Progress bar
					ProgressBar progress = (ProgressBar) activity.findViewById(R.id.mediaLoading);
					progress.setVisibility(View.INVISIBLE);
					
					// Enable Seek Bar
					seekBar.setEnabled(true);
					mediaButton.setEnabled(true);
					// Set SeekBar Listener
					initSeekBarListener();
					
		            // set Progress bar values
		            seekBar.setProgress(0);
		            seekBar.setMax(100);
		            // Updating progress bar
		            updateProgressBar();
				}

			});
			
			// When done, free resources.
			player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					mediaButton.setImageResource(R.drawable.ic_play);
					completed = true;
					pauseMusic();
				}
			});
		} else {
			// Already have a player, so it must be paused. Just play.
			player.start();
		}
	}

	protected void initMediaButton() {
		mediaButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
	       		if (player != null) {
					if(completed) {
						player.seekTo(0);
						startMusic();
						mediaButton.setImageResource(R.drawable.ic_pause);
					} else if (player.isPlaying()) {
						mediaButton.setImageResource(R.drawable.ic_play);
						pauseMusic();
					} else {
						mediaButton.setImageResource(R.drawable.ic_pause);
						startMusic();
					}
	       		}
			}
		});
		
	}

	private void initSeekBarListener() {
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				
				
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				// remove message Handler from updating progress bar
				mHandler.removeCallbacks(mUpdateTimeTask);
				
			}

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				if (player != null) {
					mHandler.removeCallbacks(mUpdateTimeTask);
					int totalDuration = player.getDuration();
					int currentPosition = progressToTimer(seekBar.getProgress(), totalDuration);
					
					// forward or backward to certain seconds
					completed = false;
					player.seekTo(currentPosition);
					
					// update timer progress again
					updateProgressBar();
				}
			}
			
		});
		
	}
	
    /**
     * Update timer on seekbar
     * */
    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 50);
    }   
    
    /**
     * Background Runnable thread
     * */
    private Runnable mUpdateTimeTask = new Runnable() {
    	public void run() {
       		if (player != null) {
				long totalDuration = player.getDuration();
               	long currentDuration = player.getCurrentPosition();
				// Display information about the song; more information is available 
				// but this is just a sample.
               	int progress = (int)getProgressPercentage(currentDuration, totalDuration);
               	seekBar.setProgress(progress);
    		}
            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 50);
        }
    };

	public void stopMusic() {
		if (player != null) {
			if (player.isPlaying()) {
				player.stop();
				mediaButton.setImageResource(R.drawable.ic_play);
			}
			player.release();
			player = null;
			seekBar.setProgress(0);
	        mHandler.removeCallbacks(mUpdateTimeTask);
		}
	}
	
	public void pauseMusic() {
		if (player != null && player.isPlaying()) {
			player.pause();
		}
	}
	
    private int getProgressPercentage(long currentDuration, long totalDuration){
        Double percentage = (double) 0;
 
        long currentSeconds = (int) (currentDuration / 1000);
        long totalSeconds = (int) (totalDuration / 1000);
 
        // calculating percentage
        percentage =(((double)currentSeconds)/totalSeconds)*100;
 
        // return percentage
        return percentage.intValue();
    }

    private int progressToTimer(int progress, int totalDuration) {
		int currentDuration = 0;
		totalDuration = (int) (totalDuration / 1000);
		currentDuration = (int) ((((double)progress) / 100) * totalDuration);
		
		// return current duration in milliseconds
		return currentDuration * 1000;
	}
}

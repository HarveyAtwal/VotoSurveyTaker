package ca.cmpt276.votosurveytaker;

import org.votomobile.datamodel.taker.AbstractManager;
import org.votomobile.datamodel.taker.DataChangeListener;

import ca.cmpt276.votosurveytaker.data.ApplicationManager;

import com.android.volley.VolleyError;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * 
 * Diplays VOTO animated logo when the app starts up
 *
 */

public class SplashActivity extends Activity implements DataChangeListener {

	protected ApplicationManager app;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		initialize();
	}

	private void initialize() {
	    // Get the application instance
	    app = (ApplicationManager)getApplication();
		
		startAnimation();
	}

	private String getPhoneNumberPref() {
		SharedPreferences prefs = getSharedPreferences(Globals.PREF_SETTINGS, MODE_PRIVATE);
		String extractedText = prefs.getString(Globals.PREF_PHONE_NUMBER, "");
		return extractedText;
	}
	
	private int getRememberMePref() {
		SharedPreferences prefs = getSharedPreferences(Globals.PREF_SETTINGS, MODE_PRIVATE);
		int rememberMe = (int) prefs.getInt(Globals.PREF_REMEMBER, 0);
		return rememberMe;
	}
	
	private void signInActivity() {
		startActivity(new Intent(SplashActivity.this, RegistrationActivity.class));
		this.finish();
	}
	
	private void startAnimation() {
		TextView sloganOne = (TextView) findViewById(R.id.txtSloganOne);
		TextView sloganTwo = (TextView) findViewById(R.id.txtSloganTwo);
		
		Animation sloganOneFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
		Animation sloganTwoFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
		
		sloganOne.startAnimation(sloganOneFadeIn);
		
		sloganTwoFadeIn.setStartOffset(1000);
		sloganTwo.startAnimation(sloganTwoFadeIn);
		
		sloganTwoFadeIn.setAnimationListener(new AnimationListener() {

			public void onAnimationEnd(Animation arg0) {
				int isRememberMeChecked = getRememberMePref();
				if(isRememberMeChecked == 1) {
					login();
					
				} else {
					signInActivity();
				}
			}

			@Override
			public void onAnimationRepeat(Animation arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onAnimationStart(Animation arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	private void login() {
		setProgressBarVisible(R.id.mediaLoading, true);
		String phoneNumber = getPhoneNumberPref();
		
		app.getRegistrationManager().getRegistrationId(phoneNumber);
	}
	
	public void setProgressBarVisible(int id, boolean visibility) {
		ProgressBar progressBar = (ProgressBar) findViewById(id);
		if(visibility) {
			progressBar.setVisibility(progressBar.VISIBLE);
		} else {
			progressBar.setVisibility(progressBar.INVISIBLE);
		}		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.splash, menu);
		return true;
	}

	@Override
	public void dataChanged() {
		setProgressBarVisible(R.id.mediaLoading, false);
		Intent intent = new Intent(SplashActivity.this, MainActivity.class);
		startActivity(intent);
		finish();
	}


	@Override
	protected void onPause() {
		super.onPause();
		app.getRegistrationManager().removeListener(this);
	}


	@Override
	protected void onResume() {
		super.onResume();
		app.getRegistrationManager().addListener(this);
	}


	@Override
	public void networkError(VolleyError volleyError) {
		setProgressBarVisible(R.id.mediaLoading, false);
		AbstractManager.displayDefaultErrorBox(this, volleyError);	
		signInActivity();
	}

}

package ca.cmpt276.votosurveytaker;


import org.votomobile.datamodel.taker.DataChangeListener;


import ca.cmpt276.votosurveytaker.data.ApplicationManager;
import ca.cmpt276.votosurveytaker.data.ErrorManager;
import ca.cmpt276.votosurveytaker.helper.Validate;

import com.android.volley.VolleyError;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Activity to find Registration ID based on user's phone number.
 */
public class RegistrationActivity extends Activity implements DataChangeListener {
	
	protected ApplicationManager app;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	    // Get the application instance
	    app = (ApplicationManager)getApplication();
		setContentView(R.layout.activity_registration);
		
		//checking if user logged out and came back to registration
		boolean dontRememberMe = getIntent().getBooleanExtra(Globals.DONT_REMEMBER, false);
		setRememberStatus(dontRememberMe);


		TextView txtSignUp = (TextView) findViewById(R.id.txtSignUp);
		int signUpText = R.string.sign_up;
		addHtmlSupport(txtSignUp, signUpText);
		
		updateUI();
		initSignUpButton();
		
	}
	
	private void setRememberStatus(boolean dontRememberMe){
		if(dontRememberMe){
			setRememberMePref(0);
		}
	}

	private String getPhoneNumberPref() {
		SharedPreferences prefs = getSharedPreferences(Globals.PREF_SETTINGS, MODE_PRIVATE);
		String extractedText = prefs.getString(Globals.PREF_PHONE_NUMBER, "");
		return extractedText;
	}
	
	public static int getRememberMePref(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(Globals.PREF_SETTINGS, MODE_PRIVATE);
		int rememberMe = (int) prefs.getInt(Globals.PREF_REMEMBER, 0);
		return rememberMe;
	}

	private void updateUI() {
		int isRememberMeChecked = getRememberMePref(this);
		if(isRememberMeChecked == 1) {
			CheckBox chkBox = (CheckBox) findViewById(R.id.notificationsCheckBox);
			TextView txtPhoneNumber = (TextView) findViewById(R.id.txtPhoneNumber);
			
			chkBox.setChecked(true);
			txtPhoneNumber.setText(getPhoneNumberPref());
			
		}
	}

	private void setRememberMePref(int i) {
		SharedPreferences settings = getSharedPreferences(Globals.PREF_SETTINGS, MODE_PRIVATE);
		SharedPreferences.Editor prefEditor = settings.edit();
		prefEditor.putInt(Globals.PREF_REMEMBER, i);
		prefEditor.commit();
	}

	private void setPhoneNumberPref() {
		TextView txtView = (TextView) findViewById(R.id.txtPhoneNumber);
		
		SharedPreferences settings = getSharedPreferences(Globals.PREF_SETTINGS, MODE_PRIVATE);
		SharedPreferences.Editor prefEditor = settings.edit();
		prefEditor.putString(Globals.PREF_PHONE_NUMBER, txtView.getText().toString());
		prefEditor.commit();
	}

	private void saveRememberMe() {
		CheckBox chkBox = (CheckBox) findViewById(R.id.notificationsCheckBox);
		if(chkBox.isChecked()) {
			setRememberMePref(1);
			setPhoneNumberPref();
		} else {
			setRememberMePref(0);
		}
	}

	private void initSignUpButton() {
		Button btn = (Button) findViewById(R.id.btnLogin);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (validated()) {
					// Get Phone Number
					TextView txtPhoneNumber = (TextView) findViewById(R.id.txtPhoneNumber);
					String phoneNumber = txtPhoneNumber.getText().toString();
					
					saveRememberMe();
					
					setProgressBarVisible(R.id.mediaLoading, true);
					app.getRegistrationManager().getRegistrationId(phoneNumber);
				}
			}

			private boolean validated() {
				// Get Phone Number
				TextView txtPhoneNumber = (TextView) findViewById(R.id.txtPhoneNumber);
				String phoneNumber = txtPhoneNumber.getText().toString();
				
				if(!Validate.isPhone(phoneNumber)) {
					String errorMessage = RegistrationActivity.this.getResources()
							.getString(R.string.invalidPhoneNumber);
					txtPhoneNumber.setError(errorMessage);
					return false;
				}
				return true;
			}

		});
		
	}

	public void setProgressBarVisible(int id, boolean visibility) {
		ProgressBar progressBar = (ProgressBar) findViewById(id);
		if(visibility) {
			progressBar.setVisibility(progressBar.VISIBLE);
		} else {
			progressBar.setVisibility(progressBar.INVISIBLE);
		}
			
	}
	
	public void addHtmlSupport(TextView v, int id) {
		v.setText(Html.fromHtml(getString(id)));
		v.setMovementMethod(LinkMovementMethod.getInstance());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	@Override
	public void dataChanged() {
		setProgressBarVisible(R.id.mediaLoading, false);
		Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
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
		ErrorManager.displayErrorBox(this, volleyError);		
	}
}

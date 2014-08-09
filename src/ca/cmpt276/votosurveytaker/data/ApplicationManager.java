package ca.cmpt276.votosurveytaker.data;

import org.votomobile.datamodel.taker.LanguageManager;
import org.votomobile.datamodel.taker.RegistrationManager;

import ca.cmpt276.votosurveytaker.exception.GooglePlayServiceUnavailableException;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

/**
 * 
 * Contains and provides access to a variety of specialized managers
 *
 */

public class ApplicationManager extends Application {

	private RegistrationManager registrationManager = null;
	private MediaManager mediaManager = null;
	private SurveyManager surveyManager = null;
	private QuestionManager questionManager = null;
	private NotificationManager notificationManager = null;
	private LanguageManager languageManager = null;
	
	@Override
    public void onCreate() {
        super.onCreate();
		registrationManager = new RegistrationManager(this);
    }
	
	public MediaManager getMediaManager() {
		return mediaManager;
	}

	public RegistrationManager getRegistrationManager() {
		return registrationManager;
	}

	public void setRegistrationManager(RegistrationManager registrationManager) {
		this.registrationManager = registrationManager;
	}

	public SurveyManager getSurveyManager() {
		return surveyManager;
	}
	
	public void initNotificationManager(String apiKey, Context context) throws GooglePlayServiceUnavailableException{
		notificationManager = new NotificationManager(this, apiKey, context);
	}
	
	public NotificationManager getNotificationManager(){
		return notificationManager;
	}

	public void initSurveyManager(String regId) {
		this.surveyManager = new SurveyManager(regId, this);
		surveyManager.fetchAllInvitations();
	}
	
	public void initQuestionManager(String regId, Context context) {
		this.questionManager = new QuestionManager(regId, context);
	}
	
	public void initLanguageManager(String apiKey, Context context){
		this.languageManager = new LanguageManager(apiKey, context);
	}
	public LanguageManager getLanguageManager(){
		return languageManager;
	}

	public void initMediaManager(Activity activity) {
		this.mediaManager = new MediaManager(activity);
	}

	public QuestionManager getQuestionManager() {
		return questionManager;
	}
}

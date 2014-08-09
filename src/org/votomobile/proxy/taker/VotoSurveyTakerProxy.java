package org.votomobile.proxy.taker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Response;

/**
 * Interface to VOTO Mobile server. Requires an API key (see VotoLoginProxy)
 */
public class VotoSurveyTakerProxy extends VotoProxyBase{
	// Sender ID's for registering with GCM server.
	public static final String GCM_SENDER_ID_VOTO = "771219191686";         // VOTO's project
	public static final String GCM_SENDER_ID_TESTPROJECT = "763356822062";  // Test Project
	
	private static final String SERVER_PATH_INVITATIONS = "data-app/outgoing-invitations";
	private static final String SERVER_PATH_RESPONSES = "data-app/responses";
	private static final String SERVER_PATH_LANGUAGES = "data-app/subscribers/languages";
	private static final String SERVER_PATH_PREFERRED_LANGUAGE = "data-app/subscribers/preferred-language";
	private static final String SERVER_PATH_GCMID = "data-app/subscribers/gcm-id";
	private static final String QUERY_REGISTRATION_ID = "registration_id";

	private String registrationId;

	/**
	 * Create a proxy class for communicating with the VOTO Mobile server.
	 * @param registrationId Registration ID issued by VOTO Mobile to the user. Found user VotoRegistrationProxy.
	 * @param context Context currently being displayed. Used for default error handler.
	 */
	public VotoSurveyTakerProxy(String registrationId, Context context) {
		super(context);
		this.registrationId = registrationId;
	}
	/**
	 * Create a proxy class for communicating with the VOTO Mobile server.
	 * @param registrationId Registration ID issued by VOTO Mobile to the user. Found user VotoRegistrationProxy.
	 * @param context Context currently being displayed.
	 * @param errorListener Error listener function used for call-backs when an error is detected.
	 */
	public VotoSurveyTakerProxy(String registrationId, Context context, Response.ErrorListener errorListener) {
		super(context, errorListener);
		this.registrationId = registrationId;
	}

	public String getRegistrationId() {
		return registrationId;
	}

	
	
	/*
	 * Invitations
	 */
	/**
	 * List all outgoing invitations available to the current user.
	 * @param completionListener Listener for when result received; accepts an array of Invitation objects.
	 */
	public void getOutgoingInvitations(Response.Listener<Invitation[]> completionListener) {
		String url = buildUrl(SERVER_PATH_INVITATIONS);

		sendJsonObjectRequest(
				Method.GET,
				url,
				null,
				ResponseConverter.makeInvitationListener(completionListener, errorListener),
				errorListener);
	}


	/*
	 * Questions
	 */
	/**
	 * Get array of Questions for a given invitation ID.
	 * @param invitationId ID of the invitation to get questions for.
	 * @param completionListener Listener called when operation completes; accepts array of Questions.
	 */
	public void getQuestions(int invitationId, Response.Listener<Question[]> completionListener) {
		String url = buildUrl(SERVER_PATH_INVITATIONS + "/" + invitationId + "/questions");

		sendJsonObjectRequest(
				Method.GET,
				url,
				null,
				ResponseConverter.makeQuestionListener(completionListener, errorListener),
				errorListener);
	}
	/**
	 * Get details of one question.
	 * @param surveyId ID of the survey the question is part of.
	 * @param questionId ID of the question.
	 * @param completionListener Listener called when operation completes; accepts an array of 1 Question object.
	 */
	public void getQuestionDetails(int surveyId, int questionId, Response.Listener<Question[]> completionListener) {
		String url = buildUrl(SERVER_PATH_INVITATIONS + "/" + surveyId + "/questions/" + questionId);

		sendJsonObjectRequest(
				Method.GET,
				url,
				null,
				ResponseConverter.makeSingleQuestionListener(completionListener, errorListener),
				errorListener);
	}
	

	public void respondToQuestion(int invitationId, int questionId, QuestionResponse response, Response.Listener<Integer> completionListener) {
		String url = buildUrl(SERVER_PATH_INVITATIONS + "/" + invitationId + "/responses");
		String body = buildQuestionResponseBody(response);
		body += "&question_id=" + questionId;
		
		sendJsonObjectRequest(
				Method.POST, 
				url, 
				body,
				ResponseConverter.makePostResponseListener(completionListener, errorListener),
				errorListener);
	}
	public void reRespondToQuestion(QuestionResponse response, Response.Listener<Integer> completionListener) {
		String url = buildUrl(SERVER_PATH_RESPONSES + "/" + response.getId());
		String body = buildQuestionResponseBody(response);
		
		sendJsonObjectRequest(
				Method.PUT, 
				url, 
				body,
				ResponseConverter.makePostResponseListener(completionListener, errorListener),
				errorListener);
	}

	private String buildQuestionResponseBody(QuestionResponse response) {
		String body = "";
		if (response.getOpenText() != null) {
			body += "&open_text=" + encodeString(response.getOpenText());			
		} else if (response.getOptionChosenIndex() >= 0) {
			body += "&option_chosen=" + (response.getOptionChosenIndex() + 1);			
		} else {
			body += "&numeric_response=" + response.getNumericResponse();			
		}
		return body;
	}

	
	/*
	 * Preferred Language Support
	 */
	public void fetchLanguages(Response.Listener<Language[]> completionListener) {
		String url = buildUrl(SERVER_PATH_LANGUAGES);
		sendJsonObjectRequest(
				Method.GET,
				url,
				null,
				ResponseConverter.makeAvailableLanguagesListener(completionListener, errorListener),
				errorListener);
		
	}
	
	public void fetchPreferredLanguage(Response.Listener<Language> completionListener) {		
		String url = buildUrl(SERVER_PATH_PREFERRED_LANGUAGE);
		sendJsonObjectRequest(
				Method.GET,
				url,
				null,
				ResponseConverter.makePreferredLanguageListener(completionListener, errorListener),
				errorListener);
		
	}
	
	public void setPreferredLanguage(int languageId, Response.Listener<Language> completionListener) {
		String url = buildUrl(SERVER_PATH_PREFERRED_LANGUAGE);
		String body = "language_id=" + languageId;
		sendJsonObjectRequest(
				Method.POST,
				url,
				body,
				ResponseConverter.makePreferredLanguageListener(completionListener, errorListener),
				errorListener);
	}
	

	/*
	 * GCM Calls
	 */
	/**
	 * Set the GCM ID
	 * @param gcmId ID to set (from the Google Cloud Messenger server)
	 * @param completionListener Listener which accepts the GCM ID that was set.
	 */
	public void setGcmId(String gcmId, Response.Listener<String> completionListener) {		
		String url = buildUrl(SERVER_PATH_GCMID);
		String body = "gcm_id=" + gcmId;
		sendJsonObjectRequest(
				Method.POST,
				url,
				body,
				ResponseConverter.makeGcmIdListener(completionListener, errorListener),
				errorListener);
		
	}
	
	/**
	 * Get the GCM ID from VOTO's server (i.e., whatever ID they are storing for us)
	 * @param completionListener Listener which accepts the GCM ID that was read from VOTO server.
	 */
	public void getGcmId(Response.Listener<String> completionListener) {
		String url = buildUrl(SERVER_PATH_GCMID);
		sendJsonObjectRequest(
				Method.GET,
				url,
				null,
				ResponseConverter.makeGcmIdListener(completionListener, errorListener),
				errorListener);
	}
	

	/**
	 * Send a GCM notification message to the test GCM project (not VOTO!).
	 * Used for testing in not wanting to use VOTO's push notifications.
	 * 
	 * Process to use this code:
	 * 	1. In your GCM registration, use the sender id GCM_SENDER_ID_TESTPROJECT to 
	 *     register with Google Cloud Messaging for the test project (rather than VOTO).
	 *  2. Using the GCM registration ID for the test project, call this method. It will
	 *     have the GCM server send out a push notification for that ID (to us).
	 *     
	 * Note that if you send the GCM registration id for the test project to VOTO, they will
	 * not be able to send you notifications because it is for the wrong project. If you later
	 * change to a GCM registration ID for the VOTO GCM project, it should then work through
	 * VOTO as normal. 
	 * 
	 * @param gcmId The GCM id returned by using the Sender ID: GCM_SENDER_ID_TESTPROJECT
	 * @param invitationId
	 */
	public void triggerGcmNotificationForTestProject(final String gcmId, final int invitationId) {
		// Run the code on a background thread because UI thread cannot support any network activity.
		class FakeGcmTask extends AsyncTask<Void, Integer, String> {
			protected String doInBackground(Void... nothings) {
				try {
					// GCM Constants
					String TEST_SERVER_AUTHORIZATION_KEY = "AIzaSyCDqU1jOAwk_4J706PAsYBbeo6jLe1hmFg";
					String SERVER_PATH_GOOGLE_CLOUD_MESSAGING_SERVER_TEST_ONLY = "https://android.googleapis.com/gcm/send";
					String GCM_REGISTRATION_ID = "registration_id";
					String GCM_MESSAGE = "data.message";
					String GCM_INVITATION_ID = "data.invitation_id";
			
			
					// Create HTTP client and post request
					HttpClient client = new DefaultHttpClient();
					HttpPost post = new HttpPost(SERVER_PATH_GOOGLE_CLOUD_MESSAGING_SERVER_TEST_ONLY);
					
					// Setup authentication header:
					post.addHeader("Content-Type", "application/x-www-form-urlencoded");
					post.addHeader("Authorization", "key=" + TEST_SERVER_AUTHORIZATION_KEY);
					
					// Setup the parameters.
					List<NameValuePair> pairs = new ArrayList<NameValuePair>();
					pairs.add(new BasicNameValuePair(GCM_REGISTRATION_ID, gcmId));
					pairs.add(new BasicNameValuePair(GCM_MESSAGE, "New Survey Invitation - TEST"));
					pairs.add(new BasicNameValuePair(GCM_INVITATION_ID, "" + invitationId));
					post.setEntity(new UrlEncodedFormEntity(pairs));
					
					// Execute
					HttpResponse response = client.execute(post);
					
					// Print out response:
					Log.i("VOTO Proxy", "Done sending test GCM message.");
					Log.i("VOTO Proxy", "Response: " + response.getStatusLine());
					
					return response.toString();
				} catch (IOException e) {
					Log.i("VOTO Proxy", "Exception thrown while trying to send test GCM notification.", e);
					return "EXCEPTION CAUGHT: " + e.getMessage();
				}
			}

			protected void onProgressUpdate(Integer... progress) {
			}

			protected void onPostExecute(String result) {
			}
		}
		
		new FakeGcmTask().execute();
	}
	
	
	/*
	 * URL Building Routines
	 */
	@Override
	protected String buildUrl(String path) {
		return buildUrl(path, new HashMap<String, String>());
	}

	@Override
	protected String buildUrl(String path, Map<String, String> parameters) {
		parameters.put(QUERY_REGISTRATION_ID, registrationId);
		return super.buildUrl(path,  parameters);
	}

}

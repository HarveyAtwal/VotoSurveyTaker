package org.votomobile.proxy.taker;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

/**
 * Utility class for converting to JSON Object listeners from more meaningful listeners.
 */
class ResponseConverter {
	
	public static Listener<JSONObject> makeRegistrationListener(final Listener<String> clientListener, final ErrorListener errorListener) {
		return new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					JSONObject data = response.getJSONObject("data");
					JSONObject subscriber = data.getJSONObject("subscriber");
					String registrationId = subscriber.getString("registration_id");
					VotoProxyBase.logMessage("Registration ID response: " + registrationId);
					clientListener.onResponse(registrationId);
				} catch (JSONException e) {
					logFailedConversion(e, response, errorListener);
				}
			}
		};
	}
	
	public static Listener<JSONObject> makeVoidListener(final Listener<Void> clientListener, final ErrorListener errorListener) {
		return new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				VotoProxyBase.logMessage("Void response received");
				clientListener.onResponse(null);
			}
		};
	}


	public static Listener<JSONObject> makeIntegerListener(final Listener<Integer> clientListener, final ErrorListener errorListener) { 
		return new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					Integer answer = response.getInt("data");
					VotoProxyBase.logMessage("Integer response: " + answer);
					clientListener.onResponse(answer);
				} catch (JSONException e) {
					logFailedConversion(e, response, errorListener);
				}
			}
		};
	}

	public static Listener<JSONObject> makePostResponseListener(final Listener<Integer> clientListener, final ErrorListener errorListener) { 
		return new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					JSONObject data = response.getJSONObject("data");
					Integer answer = data.getInt("response_id");
					
					VotoProxyBase.logMessage("Integer response: " + answer);
					clientListener.onResponse(answer);
				} catch (JSONException e) {
					logFailedConversion(e, response, errorListener);
				}
			}
		};
	}


	public static Listener<JSONObject> makeInvitationListener(final Listener<Invitation[]> clientListener, final ErrorListener errorListener) { 
		return new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					JSONObject data = response.getJSONObject("data");
					JSONArray surveyArray = data.getJSONArray("outgoing_invitations");
					String json = surveyArray.toString();

					Gson gson = new Gson();
					Invitation surveyDataArray[] = gson.fromJson(json, Invitation[].class);
					VotoProxyBase.logMessage("Surveys Rx: " + surveyDataArray.toString());

					clientListener.onResponse(surveyDataArray);
				} catch (JSONException e) {
					logFailedConversion(e, response, errorListener);
				}

			}
		};
	}
	
	public static Listener<JSONObject> makeQuestionListener(final Listener<Question[]> clientListener, final ErrorListener errorListener) { 
		return new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					List<Question> list = new ArrayList<Question>();
					JSONObject data = response.getJSONObject("data");
					
					Gson gson = new Gson();
					// Put the introduction in as a question:
					if (!data.isNull("introduction")) {
						JSONObject introJsonObj = data.getJSONObject("introduction");
						String introJson = introJsonObj.toString();
						Question intro = gson.fromJson(introJson, Question.class);
						if (intro != null) {
							intro.makeIntroductionOrConclusion(Question.INTERNAL_ID_INTRODUCTION);
							list.add(intro);
						}
					}
					
					// Handle the *real* questions
					JSONArray jsonArray = data.getJSONArray("questions");
					String json = jsonArray.toString();
					Question questionArray[] = gson.fromJson(json, Question[].class);
					for (Question question : questionArray) {
						list.add(question);
					}
					
					// Put the conclusion in as a question:
					if (!data.isNull("conclusion")) {
						JSONObject conclusionJsonObject = data.getJSONObject("conclusion");
						String conclusionJson = conclusionJsonObject.toString();
						Question conclusion = gson.fromJson(conclusionJson, Question.class);
						if (conclusion != null) {
							conclusion.makeIntroductionOrConclusion(Question.INTERNAL_ID_CONCLUSION);
							list.add(conclusion);
						}
					}

					Question[] returnData = list.toArray(new Question[0]);
					VotoProxyBase.logMessage("Questions Rx: " + returnData.toString());
					clientListener.onResponse(returnData);
				} catch (JSONException e) {
					logFailedConversion(e, response, errorListener);
				}
			}
		};
	}

	public static Listener<JSONObject> makeSingleQuestionListener(final Listener<Question[]> clientListener, final ErrorListener errorListener) { 
		return new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					JSONObject data = response.getJSONObject("data");
					JSONObject questionJson = data.getJSONObject("question");
					String json = questionJson.toString();
					
					Gson gson = new Gson();
					Question question = gson.fromJson(json, Question.class);
					VotoProxyBase.logMessage("Question Rx: " + question.toString());
					
					Question[] dataArray = new Question[1];
					dataArray[0] = question;
					
					clientListener.onResponse(dataArray);
				} catch (JSONException e) {
					logFailedConversion(e, response, errorListener);
				}
			}
		};
	}
	
	
	public static Listener<JSONObject> makeAvailableLanguagesListener(final Listener<Language[]> clientListener, final ErrorListener errorListener) {
		return new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					Gson gson = new Gson();

					JSONObject data = response.getJSONObject("data");
					JSONArray jsonArray = data.getJSONArray("languages");
					String json = jsonArray.toString();

					Language languageArray[] = gson.fromJson(json, Language[].class);
					
					clientListener.onResponse(languageArray);
				} catch (JSONException e) {
					logFailedConversion(e, response, errorListener);
				}
			}
		};
	}
	public static Listener<JSONObject> makePreferredLanguageListener(final Listener<Language> clientListener, final ErrorListener errorListener) {
		return new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					Gson gson = new Gson();
					
					JSONObject data = response.getJSONObject("data");

					// NOTE: Skipping the registration_id, and subscriber_id in the data.
					
					JSONObject jsonObject = data.getJSONObject("preferred_language");
					String json = jsonObject.toString();
					
					Language language = gson.fromJson(json, Language.class);
					
					clientListener.onResponse(language);
				} catch (JSONException e) {
					logFailedConversion(e, response, errorListener);
				}
			}
		};
	}

	public static Listener<JSONObject> makeGcmIdListener(final Listener<String> clientListener, final ErrorListener errorListener) {
		return new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					JSONObject data = response.getJSONObject("data");
					String gcmId = data.getString("gcm_id");
					// NOTE: Skipping the registration_id, and subscriber_id in the data.
					
					clientListener.onResponse(gcmId);
				} catch (JSONException e) {
					logFailedConversion(e, response, errorListener);
				}
			}
		};
	}
	
	
	/**
	 * Handle a conversion error by putting a message in the LogCat, as well as calling the 
	 * application's registered error handler.
	 */
	private static void logFailedConversion(JSONException e, JSONObject response, ErrorListener errorListener) {
		Log.e("ResponseConverter", "Unable to convert server's JSON response to Plain Old Java Objects.", e);
		Log.e("ResponseConverter", "--> JSON data: " + response.toString());
		
		// Pass the error onto the application's error handler.
		errorListener.onErrorResponse(new VolleyError(e));
	}




}

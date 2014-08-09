package org.votomobile.datamodel.taker;

import org.votomobile.proxy.taker.VotoProxyBase;
import org.votomobile.proxy.taker.VotoSurveyTakerProxy;

import android.content.Context;

import com.android.volley.Response;
import com.android.volley.VolleyError;

/**
 * Manage the Google Cloud Messaging registration with VOTO's server
 */
public class GcmManager extends AbstractManager{
	// Sender IDs to use when accessing the GCM registration.
	public static final String GCM_SENDER_ID_VOTO = VotoSurveyTakerProxy.GCM_SENDER_ID_VOTO;
	public static final String GCM_SENDER_ID_TESTPROJECT = VotoSurveyTakerProxy.GCM_SENDER_ID_TESTPROJECT;

	private VotoSurveyTakerProxy proxy;
	private String gcmId;

	/**
	 * Create the GCM Manager
	 * @param apiKey API Key to use with server
	 * @param context The application's context; used for setting networking queue.
	 */
	public GcmManager(String apiKey, Context context) {
		proxy = new VotoSurveyTakerProxy(
				apiKey, 
				context, 
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						notifyErrorListeners(error);
					}
				});
	}

	// Give base class access to our proxy.
	@Override
	protected VotoProxyBase getProxy() {
		return proxy;
	}
	

	/**
	 * Get the currently configured GCM id from the server.
	 * Triggers notification when done.
	 */
	public void fetchGcmId() {
		// First wipe out the current data (if any).
		gcmId = null;

		// Update from server.
		proxy.getGcmId(
				new Response.Listener<String>() {
					// When data received, process it.
					@Override
					public void onResponse(String response) {
						gcmId = response;
						notifyDataChangeListeners();
					}
				});
	}

	/**
	 * Return the GCM ID if it has already been fetched.
	 * @return null if not yet fetched.
	 */
	public String getGcmId() {
		return gcmId;
	}

	/**
	 * Set the GCM ID; triggers notification when done.
	 */
	public void setGcmId(String newGcmId) {
		proxy.setGcmId(
				newGcmId,
				new Response.Listener<String>() {
					// When data received, process it.
					@Override
					public void onResponse(String response) {
						gcmId = response;
						notifyDataChangeListeners();
					}
				});
	}
	

	/**
	 * Send a GCM notification for the test project. 
	 * WARNING: Must use the GCM_SENDER_ID_TESTPROJECT instead of GCM_SENDER_ID_VOTO
	 * @param invitationId The ID number you want to send in the notification. Can be any integer.
	 */
	public void triggerGcmNotificationForTestProject(int invitationId) {
		proxy.triggerGcmNotificationForTestProject(gcmId, invitationId);
	}
}

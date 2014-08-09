package org.votomobile.proxy.taker;


import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.android.volley.Response;
import com.android.volley.Request.Method;

/**
 * Interface to the VOTO server for retrieving a customer Registration ID.
 */
public class VotoRegistrationProxy extends VotoProxyBase {
	private static final String SERVER_PATH_APIKEY= "data-app/subscribers/registration-id";

	/**
	 * Create a proxy class for getting the registration id from the VOTO Mobile server.
	 * @param parentActivity Context currently being displayed. Used for default error handler.
	 */
	public VotoRegistrationProxy(Context parentActivity) {
		super(parentActivity);
	}
	/**
	 * Create a proxy class for getting the API key from the VOTO Mobile server.
	 * @param parentContext Context currently being displayed.
	 * @param errorListener Error listener function used for call-backs when an error is detected.
	 */
	public VotoRegistrationProxy(Context parentContext, Response.ErrorListener errorListener) {
		super(parentContext, errorListener);
	}

	
	/**
	 * Get the customer's registration ID based on their phone number.
	 * @param phoneNumber Phone number to use for lookup.
	 * @param completionListener Listener for when result received from server; accepts the registration ID (string)
	 */
	public void getRegistrationId(String phoneNumber, Response.Listener<String> completionListener) {
		Map<String, String> parameters = new HashMap<String,String>();
		parameters.put("phone", encodeString(phoneNumber));
		String url = buildUrl(SERVER_PATH_APIKEY, parameters);

		sendJsonObjectRequest(
				Method.GET,
				url,
				null,
				ResponseConverter.makeRegistrationListener(completionListener, errorListener),
				errorListener);
	}
}

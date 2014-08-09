package org.votomobile.datamodel.taker;

import org.votomobile.proxy.taker.VotoProxyBase;
import org.votomobile.proxy.taker.VotoRegistrationProxy;

import android.content.Context;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Response.Listener;

/**
 * Data model for managing getting a registration ID from a phone number from remote server.
 * UI can be notified of any change by subscribing for updates; code in base class.
 */
public class RegistrationManager extends AbstractManager {
	private VotoRegistrationProxy proxy;
	private String regId;
	
	public RegistrationManager(Context context) {
		proxy = new VotoRegistrationProxy(
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
	 * Log into the server. Client notified of completion via the listener mechanism in base class.
	 * When notified of change, call getApiKey() to get the new API key.
	 * @param userName
	 * @param password
	 */
	public void getRegistrationId(String phoneNumber) {
		proxy.getRegistrationId(phoneNumber, new Listener<String>(){
			@Override
			public void onResponse(String regId) {
				RegistrationManager.this.regId = regId;
				notifyDataChangeListeners();
			}
		});	
	}

	/**
	 * Return the API key once available. Returns null if not yet available.
	 * @return API key; null if not yet retreived from server.
	 */
	public String getApiKey() {
		return regId;
	}
}

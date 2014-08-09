package org.votomobile.proxy.taker;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.votomobile.proxy.helpers.JsonObjectRequestStringBody;
import org.votomobile.proxy.helpers.VotoRequestQueue;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;

/**
 * Abstract base class for the VOTO Mobile proxy classes. 
 */
abstract public class VotoProxyBase {
	
	protected static final String TAG = "VotoProxy";
	protected static final String SERVER_SCHEME = "https";
	protected static final String SERVER_ADDRESS = "go.votomobile.org";
	
	protected Response.ErrorListener errorListener;
	private Context context;

	// Count the number of packets outstanding; used to track network activity.
	private int networkPacketsOutstanding = 0;
	
	List<NetworkActivityListener> networkActivityListeners = new ArrayList<NetworkActivityListener>();


	
	/*
	 * Constructors
	 */
	public VotoProxyBase(Context context) {
		this.context = context;
		this.errorListener = getErrorListener(context);
	}
	public VotoProxyBase(Context context, Response.ErrorListener errorListener) {
		this.context = context;
		this.errorListener = errorListener;
	}
	
	
	/*
	 * Support for determining if there is network activity.
	 */
	/**
	 * Check if there is current network activity (i.e., contacting the server).
	 * @return true if there is current network activity.
	 */
	public boolean hasNetworkActivity() {
		return (networkPacketsOutstanding > 0);
	}
	public void addNetworkActivityListener(NetworkActivityListener listener) {
		if (!networkActivityListeners.contains(listener)) {
			networkActivityListeners.add(listener);
		}
	}
	public void removeNetworkActivityListener(NetworkActivityListener listener) {
		networkActivityListeners.remove(listener);
	}
	
	/**
	 * Private method for reference counting. Make it one method so it's easy to synchronize between threads.
	 */
	private void incrementNetworkRefCount() {
		if (updateNetworkRefCount(true)) {
			for (NetworkActivityListener listener : networkActivityListeners) {
				listener.networkActivityStarted();
			}
		}
	}
	private void decrementNetworkRefCount() {
		if (updateNetworkRefCount(false)) {
			for (NetworkActivityListener listener : networkActivityListeners) {
				listener.networkActivityEnded();
			}
		}
	}
	/**
	 * Synchronizes access to reference counter.
	 * @param increment Count up (true) or down (false)
	 * @return true if state changed (i.e., first packet being sent, or last packet done).
	 */
	synchronized private boolean updateNetworkRefCount(boolean increment) {
		if (increment) {
			networkPacketsOutstanding ++;
			return networkPacketsOutstanding == 1;
		} else {
			networkPacketsOutstanding--;
			return networkPacketsOutstanding == 0;
		}
	}

	
	/*
	 * URL Building Routines
	 */
	protected String buildUrl(String path) {
		return buildUrl(path, new HashMap<String, String>());
	}

	protected String buildUrl(String path, Map<String, String> parameters) {
		Uri.Builder builder = new Uri.Builder();
		builder.scheme(SERVER_SCHEME);
		builder.authority(SERVER_ADDRESS);
		builder.appendEncodedPath(path);

		// Add all parameters:
		for (Map.Entry<String, String> entry : parameters.entrySet()) {
			builder.appendQueryParameter(entry.getKey(), entry.getValue());
		}


		String url = builder.build().toString();
		logMessage("Built URL: " + url);
		return url;
	}
	
	protected String encodeString(String input) {
		// Guard against null string.
		if (input == null) {
			return null;
		}
		
		try {
			return URLEncoder.encode(input, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logMessage("Unsupported Encoding Exception!");
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}


	/*
	 * Send to queue
	 */
	protected void sendJsonObjectRequest(int method, String url, String body, 
			Response.Listener<JSONObject> completionListener, 
			Response.ErrorListener errorListener) {
		
		JsonObjectRequestStringBody req = new JsonObjectRequestStringBody(
				method, 
				url, 
				body,
				wrapCompletionListenerForReferenceCounting(completionListener),
				wrapErrorListenerForReferenceCounting(errorListener));

		logMessage("Sending Request " + req.toString());
		incrementNetworkRefCount();
		VotoRequestQueue.addToRequestQueue(context.getApplicationContext(), req);
	}
	
	
	private Listener<JSONObject> wrapCompletionListenerForReferenceCounting(
			final Listener<JSONObject> completionListener) 
	{
		// Decement the reference count, and pass on call to the client's listener.
		return new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				completionListener.onResponse(response);
				decrementNetworkRefCount();
			}
		};
	}
	private ErrorListener wrapErrorListenerForReferenceCounting(
			final ErrorListener errorListener) 
	{
		return new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				// Decement the reference count, and pass on the error to the client's listener.
				errorListener.onErrorResponse(error);
				decrementNetworkRefCount();
			}
		};
	}
	
	/*
	 * Default error handler: put up a message box.
	 */
	public static Response.ErrorListener getErrorListener(final Context context) {
		return new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				// If this cast fails, it's because the context is likely an application
				// context, not an Activity context. If using this code, must have
				// passed in an Activity to the constructor!
				Activity activity = (Activity) context;
				
				displayDefaultErrorBox(activity, error);
			}
       };
	}
	
	public static void displayDefaultErrorBox(Activity context, VolleyError error) {
		String errorMessage = getErrorMessage(error);
		
		AlertDialog.Builder dlgBuilder = new AlertDialog.Builder(context);  
		dlgBuilder.setTitle("Network Error");
		dlgBuilder.setMessage(errorMessage);
		dlgBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {  
		    @Override  
		    public void onClick(DialogInterface dialog, int which) {  
		        dialog.dismiss();                      
		    }  
		});  
		dlgBuilder.setIcon(android.R.drawable.ic_dialog_alert);
		dlgBuilder.setCancelable(true); // This allows the 'BACK' button
		dlgBuilder.create().show();		
	}
	
	private static String getErrorMessage(VolleyError error) {
		
		Log.e(TAG, "Got error: " , error);
		
		String errorMessage = "";
		if (error == null) {
			errorMessage = "Something went wrong accessing the server, but no details available.";  					
		} else if (error.getMessage() != null) {					
			errorMessage = "Error message: " + error.getMessage(); 
		} else if (error.networkResponse == null) {
			errorMessage = "Error of type " + error.getClass().toString() + ".";
		} else {
			// Try finding message in response data (JSON from VOTO server)
			String msg = "";
			try {
				String contents = new String(error.networkResponse.data, "UTF-8");
				JSONObject response = new JSONObject(contents);
				msg = response.getString("message");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			errorMessage = "Server says: \n" + msg + "\n\nError class: " + error;
		}
		return errorMessage;
	}

	

	/*
	 * Logging
	 */
	public static void logMessage(String message) {
		Log.i(TAG, message);		
	}
}
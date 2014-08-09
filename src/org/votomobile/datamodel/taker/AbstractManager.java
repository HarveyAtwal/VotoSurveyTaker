package org.votomobile.datamodel.taker;

import java.util.ArrayList;
import java.util.List;

import org.votomobile.proxy.taker.NetworkActivityListener;
import org.votomobile.proxy.taker.VotoProxyBase;

import android.app.Activity;

import com.android.volley.Response;
import com.android.volley.VolleyError;

/**
 * Abstract base class for other data managers to factor out the code for being observable.
 * To use with an Android activity:
 * 1. Have the activity implement the DataChangeListener interface.
 * 2. Override the networkError() and dataChanged() methods
 * 3. Override the onResume() method, use it to register itself as a listener.
 * 4. Override the onPause() method, use it to unregister itself as a listener.
 * 
 * sample code for client usage:
  	// Listener support for data managers
	@Override
	protected void onPause() {
		super.onPause();
		App.getSurveyManager().removeListener(this);
	}
	@Override
	protected void onResume() {
		super.onResume();
		App.getSurveyManager().addListener(this);
	}

	@Override
	public void networkError(VolleyError volleyError) {
		AbstractManager.displayDefaultErrorBox(this, volleyError);			
	}

	@Override
	public void dataChanged() {
		// Do something! Get the list of items of interest by doing something like:
		for (Survey survey : App.getSurveyManager().surveys()) {
			... process that survey...
		}
	}
 */
abstract public class AbstractManager {
	private List<DataChangeListener> listeners = new ArrayList<DataChangeListener>();
	
	
	/**
	 * Add a DataChangeListener for callbacks when new data is available, or an error occurs.
	 * @param listener
	 */
	public void addListener(DataChangeListener listener) {
		listeners.add(listener);
	}
	/**
	 * Remove a DataChangeListener.
	 * @param listener
	 */
	public void removeListener(DataChangeListener listener) {
		listeners.remove(listener);
	}
	
	/*
	 * Protected method called by derived manager classes to notify listeners when data changes.
	 */
	protected void notifyDataChangeListeners(){
		for (DataChangeListener listener : listeners) {
			listener.dataChanged();
		}
	}
	/*
	 * Protected method called by derived manager classes to notify listeners when there is an error.
	 */
	protected void notifyErrorListeners(VolleyError volleyError){
		for (DataChangeListener listener : listeners) {
			listener.networkError(volleyError);
		}
	}
	
	
	// Give base class access to the proxy.
	abstract protected VotoProxyBase getProxy();
	
	/*
	 * Network Activity Listeners/accessor
	 */
	/**
	 * Add a listener for network activity.
	 * @param listener Client's listener to add.
	 */
	public void addNetworkActivityListener(NetworkActivityListener listener) {
		getProxy().addNetworkActivityListener(listener);
	}
	/**
	 * Remove a listener for network activity.
	 * @param listener Client's listener to remove.
	 */
	public void removeNetworkActivityListener(NetworkActivityListener listener) {
		getProxy().removeNetworkActivityListener(listener);
	}
	/**
	 * Determine if there is active network traffic (i.e., waiting for data from server).
	 * @return true if there is one or more active network requests.
	 */
	public boolean hasNetworkActivity() {
		return getProxy().hasNetworkActivity();
	}
	


	/**
	 * Simple error handling routine to display a message box on error.
	 * @param context Activity context.
	 * @return
	 */
	public static Response.ErrorListener getErrorListener(Activity context) {
		return VotoProxyBase.getErrorListener(context);
	}
	public static void displayDefaultErrorBox(Activity context, VolleyError error) { 
		getErrorListener(context).onErrorResponse(error);
	}}

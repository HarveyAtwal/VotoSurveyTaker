package org.votomobile.datamodel.taker;

import com.android.volley.VolleyError;

/**
 * Simple listener interface for notifying the view when the model changes,
 * or when there is an error. See AbstractManager for code on use.
 */
public interface DataChangeListener {
	void dataChanged();
	void networkError(VolleyError volleyError);
}

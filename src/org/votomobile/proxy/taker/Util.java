package org.votomobile.proxy.taker;

import android.util.Log;

public class Util {
	private static final String TAG = "VOTO Lib";

	/**
	 * Convert an int to a string, catching errors and returning 0 if a failure.
	 * @param source String to convert (possibly null, possibly text)
	 * @return Value of string (if OK); 0 otherwise.
	 */
	static public int toInt(String source) {
		if (source == null) {
			return 0;
		}

		try {
			return Integer.parseInt(source);
		} catch (NumberFormatException e) {
			Log.i(TAG, "Invalid number format in string " + source);
			return 0;
		}
		
	}
}

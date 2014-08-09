package ca.cmpt276.votosurveytaker.helper;

import java.util.regex.Pattern;



import android.util.Log;

/**
 * 
 * Contains a variety of static data validation methods
 *
 */
public class Validate {
	private final static String CLASS_NAME = "VALIDATE";

	private Validate() {}
	
	
	public static boolean isPhone(String text) {
		if(isEmpty(text))
			return false;
		String regex = "^(?:(?:\\+?1\\s*(?:[.-]\\s*)?)?(?:\\(\\s*([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9])\\s*\\)|([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9]))\\s*(?:[.-]\\s*)?)?([2-9]1[02-9]|[2-9][02-9]1|[2-9][02-9]{2})\\s*(?:[.-]\\s*)?([0-9]{4})(?:\\s*(?:#|x\\.?|ext\\.?|extension)\\s*(\\d+))?$";
		if(Pattern.matches(regex, text))
			return true;
		Log.i(CLASS_NAME, "Phone is not valid!");
		return false;
	}
	
	public static boolean isInteger(String text) {
		if(isEmpty(text))
			return false;
		long num = Long.parseLong(text);
		if (num >= Integer.MIN_VALUE && num <= Integer.MAX_VALUE) {
			return true;
		}
		return false;
	}
	
	public static boolean isEmpty(String text) {
		if(text.length() == 0) {
			return true;
		}
		return false;
	}
}

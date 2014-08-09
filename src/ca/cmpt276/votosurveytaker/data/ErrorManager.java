package ca.cmpt276.votosurveytaker.data;

import java.io.UnsupportedEncodingException;

import org.json.JSONException;
import org.json.JSONObject;
import org.votomobile.datamodel.taker.AbstractManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import ca.cmpt276.votosurveytaker.R;

import com.android.volley.VolleyError;

/**
 * 
 * Manages error messages
 * 
 */

public class ErrorManager {

	private static final String TAG = "ErrorManager";

	public static void displayErrorBox(Activity context, VolleyError error) {

		Log.e(TAG, "Got error: ", error);
		String errorStr = "";
		String errorMsg = "";
		if (error == null) {
			errorStr = context.getResources().getString(R.string.errorTitle);
			errorMsg = context.getResources().getString(R.string.errorMessageA);
			drawDefaultDialogBox(context, errorStr, errorMsg);
		} else if (error.getMessage() != null) {
			errorMsg = context.getResources().getString(R.string.errorMessageB);
			drawDetailedDialogBox(context, errorMsg, error.getMessage());
		} else {
			// Try finding message in response data (JSON from VOTO server)
			try {
				String contents = new String(error.networkResponse.data,
						"UTF-8");
				JSONObject response = new JSONObject(contents);
				drawDetailedDialogBox(context, response);
				Log.i(TAG, response.toString());
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public static void displayErrorBox(Activity context, String msg,
			String moreInfo, boolean displayMoreInfo, OnClickListener listener) {
		AlertDialog.Builder dlgBuilder = new AlertDialog.Builder(context);
		dlgBuilder.setTitle("Error");
		dlgBuilder.setMessage(msg);
		if (displayMoreInfo) {
			dlgBuilder.setView(createShowDetailsView(context, moreInfo));
		}

		if (listener == null) {
			dlgBuilder.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
		}else{
			dlgBuilder.setPositiveButton("OK", listener);
		}

		dlgBuilder.setIcon(android.R.drawable.ic_dialog_alert);
		dlgBuilder.setCancelable(true); // This allows the 'BACK' button
		dlgBuilder.create().show();
	}

	private static View createShowDetailsView(Activity context, String moreInfo) {
		LayoutInflater inflater = context.getLayoutInflater();
		View view = inflater.inflate(R.layout.dialog_more_details, null);
		final ImageButton button = (ImageButton) view
				.findViewById(R.id.imgBtnCollpase);
		button.setImageResource(R.drawable.ic_expand);
		final TextView txtViewMoreInfo = (TextView) view
				.findViewById(R.id.txtMoreInfo);
		final TextView txtViewDetails = (TextView) view
				.findViewById(R.id.txtDetails);
		final String intializedMoreInfo = moreInfo;
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (txtViewMoreInfo.getVisibility() == View.GONE) {
					button.setImageResource(R.drawable.ic_collapse);
					txtViewMoreInfo.setText(intializedMoreInfo + '\n');
					txtViewDetails.setText(R.string.hide_details);
					txtViewMoreInfo.setVisibility(v.VISIBLE);
				} else {
					button.setImageResource(R.drawable.ic_expand);
					txtViewMoreInfo.setVisibility(v.GONE);
					;
					txtViewDetails.setText(R.string.show_details);
				}
			}
		});
		return view;
	}

	private static void drawDetailedDialogBox(Activity context,
			JSONObject response) {
		String msg = "", moreInfo = "";
		try {
			msg = response.getString("message");
			moreInfo = response.getString("more_info");

			AlertDialog.Builder dlgBuilder = new AlertDialog.Builder(context);
			dlgBuilder.setTitle(context.getResources().getString(
					R.string.errorTitle));
			dlgBuilder.setMessage(msg);

			dlgBuilder.setView(createShowDetailsView(context, moreInfo));

			dlgBuilder.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			dlgBuilder.setIcon(android.R.drawable.ic_dialog_alert);
			dlgBuilder.setCancelable(true); // This allows the 'BACK' button
			dlgBuilder.create().show();

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private static void drawDetailedDialogBox(Activity context, String msg,
			String moreInfo) {
		AlertDialog.Builder dlgBuilder = new AlertDialog.Builder(context);
		dlgBuilder.setTitle("Error");
		dlgBuilder.setMessage(msg);
		dlgBuilder.setView(createShowDetailsView(context, moreInfo));
		dlgBuilder.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		dlgBuilder.setIcon(android.R.drawable.ic_dialog_alert);
		dlgBuilder.setCancelable(true); // This allows the 'BACK' button
		dlgBuilder.create().show();
	}

	public static void displayDefaultErrorBox(Activity context,
			VolleyError error) {
		AbstractManager.displayDefaultErrorBox(context, error);
	}

	private static void drawDefaultDialogBox(Activity context, String title,
			String message) {
		AlertDialog.Builder dlgBuilder = new AlertDialog.Builder(context);
		dlgBuilder.setTitle(title);
		dlgBuilder.setMessage(message);
		dlgBuilder.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		dlgBuilder.setIcon(android.R.drawable.ic_dialog_alert);
		dlgBuilder.setCancelable(true); // This allows the 'BACK' button
		dlgBuilder.create().show();
	}

}

package ca.cmpt276.votosurveytaker.data;

import java.io.IOException;

import org.votomobile.datamodel.taker.DataChangeListener;
import org.votomobile.datamodel.taker.GcmManager;

import ca.cmpt276.votosurveytaker.MainActivity;
import ca.cmpt276.votosurveytaker.R;
import ca.cmpt276.votosurveytaker.exception.GooglePlayServiceUnavailableException;

import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class NotificationManager implements DataChangeListener {

	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

	private Object syncObject = null;
	private GcmManager gcmManager = null;

	private GoogleCloudMessaging gcm = null;
	private Context context = null;
	private String apiKey = "";
	private volatile String regId = "";

	private boolean isInitialized = false;

	public NotificationManager(ApplicationManager app, String apiKey,
			Context context) throws GooglePlayServiceUnavailableException {
		Log.i("NotificationManager", "Attempting to initialize");
		if (!isPlayServiceAvailable(context)) {
			throw new GooglePlayServiceUnavailableException();
		}
		this.apiKey = apiKey;
		this.context = context;

		syncObject = new Object();
		initialize();
	}

	private void initialize() {
		new AsyncTask<NotificationManager, Void, String>() {

			@Override
			protected String doInBackground(NotificationManager... params) {
				NotificationManager notificationManager = params[0];
				gcmManager = new GcmManager(apiKey, context);
				gcmManager.addListener(notificationManager);

				try {
					notificationManager.unregister();
					if (!notificationManager.getGcmRegId()) {
						Log.i("Notification Manager",
								"No prefetched regId available, fetching...");

						notificationManager.doWait();
						notificationManager.getGcmRegId();
						Log.i("Notification Manager",
								"RegId fetched, Continuing operation...");
						Log.i("Notification Manager", regId);
						if (!validateRegId()) {

							if (!registerGCM()) {
								return "Failed to register with GCM.";
							}
							Log.i("Notification Manager",
									"GCM registration successful, Continuing operation...");
						}

					}
				} catch (InterruptedException e) {
					e.printStackTrace();
					return "Notification Manager initialization interrupted.";
				}

				return null;
			}

			@Override
			protected void onPostExecute(String msg) {
				if (msg == null) {
					isInitialized = true;
					Log.i("Notification Manager", "Initialization successful");
				} else {
					isInitialized = false;
					MainActivity activity = (MainActivity) context;
					if (activity == null) {
						throw new RuntimeException("Unsupported activity type.");
					}

					activity.setNotificationPref(false);
					ErrorManager.displayErrorBox((Activity) context, msg, null,
							false, null);
				}
			}

		}.execute(this);
	}

	private boolean registerGCM() {
		try {
			if (gcm == null) {
				gcm = GoogleCloudMessaging.getInstance(context);
			}

			regId = gcm.register(GcmManager.GCM_SENDER_ID_VOTO);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public void registerForNotifications() {
		setGcmRegId(regId);
		Log.i("Notification Manager", "registration complete!");
	}

	private boolean getGcmRegId() {
		regId = gcmManager.getGcmId();
		if (!validateRegId()) {
			regId = "";
			gcmManager.fetchGcmId();
			return false;
		}
		return true;
	}

	private boolean validateRegId() {
		if (regId == null || regId.isEmpty() || regId.equals("null")) {
			return false;
		}
		return true;
	}

	public void unregister() {
		if (isInitialized) {
			Log.i("Notification Manager", "Logging out, unregistering...");
			setGcmRegId(null);
		}
	}

	private void setGcmRegId(String id) {
		gcmManager.setGcmId(id);
	}

	public static boolean isPlayServiceAvailable(Context context) {
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(context);
		if (resultCode != ConnectionResult.SUCCESS) {
			return false;
		}
		return true;
	}

	public static boolean checkPlayServices(Activity activity) {
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(activity);

		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, activity,
						PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				ErrorManager.displayErrorBox(activity, activity.getResources()
						.getString(R.string.unsupported_device), null, false,
						null);
			}
			return false;
		}
		return true;
	}

	public GcmManager getGcmManager() {
		return gcmManager;
	}

	@Override
	public void dataChanged() {
		Log.i("Notification Manager", "Data is being changed");
		doNotify();
	}

	@Override
	public void networkError(VolleyError volleyError) {
		ErrorManager.displayErrorBox((Activity) context, volleyError);
	}

	public void doWait() throws InterruptedException {
		synchronized (syncObject) {
			syncObject.wait();
		}
	}

	public void doNotify() {
		synchronized (syncObject) {
			syncObject.notify();
		}
	}
}

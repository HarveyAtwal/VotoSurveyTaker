package ca.cmpt276.votosurveytaker.notification;


import com.google.android.gms.gcm.GoogleCloudMessaging;

import ca.cmpt276.votosurveytaker.R;
import ca.cmpt276.votosurveytaker.SplashActivity;
import ca.cmpt276.votosurveytaker.fragment.InvitationFragment;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class GcmIntentService extends IntentService {

	private final int DEFAULT_NOTIFICATION_ID = 1;
	private final String MESSAGE_KEY = "message";
	private final String NOTIFICATION_KEY = "android.support.content.wakelockid";
	private final String INVITATION_KEY = "invitation_id";

	private NotificationManager mNotificationManager;
	NotificationCompat.Builder builder;

	public GcmIntentService() {
		super("GcmIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		String action = intent.getAction();

		if (action.equals("com.google.android.c2dm.intent.REGISTRATION")) {
			Log.i("GcmIntentService", "Registration Successful");
		} else if (action.equals("com.google.android.c2dm.intent.RECEIVE")) {
			GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

			String messageType = gcm.getMessageType(intent);
			String msg = extras.getString(MESSAGE_KEY);

			int notificationId = DEFAULT_NOTIFICATION_ID;
			
			try {
				notificationId = Integer.parseInt(extras
						.getString(NOTIFICATION_KEY));
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}

			if (!extras.isEmpty()) {
				if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
						.equals(messageType)) {
					sendNotification("Send Error", msg, notificationId);
				} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
						.equals(messageType)) {
					sendNotification("Deleted Messages on Server", msg,
							notificationId);
				} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
						.equals(messageType)) {
					try {
						int invitationId = Integer.parseInt(extras
								.getString(INVITATION_KEY));
						InvitationFragment.setStartInvitationId(this, invitationId);
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}

					sendNotification(msg, null, notificationId);
				}
			}
		}

		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

	private void sendNotification(String title, String msg, int notificationId) {
		Log.i("GcmIntentService", title + ", " + msg);
		mNotificationManager = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);

		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, SplashActivity.class), 0);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this).setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle(title)
				.setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
				.setAutoCancel(true);
		if (msg != null) {
			mBuilder.setContentText(msg);
		}

		mBuilder.setContentIntent(contentIntent);
		mNotificationManager.notify(notificationId, mBuilder.build());
	}
}
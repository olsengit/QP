package com.hiof.quizphun;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class ReminderService extends IntentService {

	Thread reminderThread;

	public ReminderService() {
		super("ReminderService");
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		RemindUser();
	}

	/*
	 * Remind user to play if 1440000 minutes has passed since the user played A
	 * notification message will be sent
	 */
	private void RemindUser() {
		reminderThread = new Thread(new Runnable() {
			@Override
			public void run() {
				// Intent invoked when user press the notification
				Intent intent = new Intent(getApplicationContext(),
						MainActivity.class);
				PendingIntent pIntent = PendingIntent.getActivity(
						getApplicationContext(), 0, intent, 0);

				// Makes a notification
				Notification n = new NotificationCompat.Builder(
						getApplicationContext())
						.setContentTitle("QuizPhun")
						.setContentText(
								"You haven't played for 24 hours, come play")
						.setSmallIcon(R.drawable.qp).setContentIntent(pIntent)
						.build();

				n.flags = Notification.FLAG_AUTO_CANCEL;
				NotificationManager nManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
				nManager.notify(0, n);
			}
		});
		try {
			// Sleep thread for 24 hours
			// reminderThread.sleep(1440000);
			reminderThread.sleep(30000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		reminderThread.start();
	}
}

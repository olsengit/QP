package com.hiof.quizphun;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class ReminderService extends IntentService {
	
	public ReminderService() {
		super("ReminderService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		//Do work here, might be incoming data from the intent
		System.out.println("Service started");
		RemindUser(true);
	}
	
	/*
	 * Recursive method - Remind user to play if x minutes has passed since the user played
	 * A notification message will be sent
	 */
	private void RemindUser(final boolean firstCall) {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				if(!firstCall) {
					Intent intent = new Intent();
					PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
					
					Notification n = new NotificationCompat.Builder(getApplicationContext()) 
					.setContentTitle("QuizPhun")
					.setContentText("You haven't played for a while, come play")
					.setSmallIcon(R.drawable.qp)
					.setContentIntent(pIntent).build(); 
					
					n.flags=Notification.FLAG_AUTO_CANCEL; 
					NotificationManager nManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
					nManager.notify(0, n);
				}
			}
		});
		t.start();
		try {
			t.sleep(10000);
			RemindUser(false);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

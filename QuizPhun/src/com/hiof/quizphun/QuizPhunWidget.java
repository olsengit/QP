package com.hiof.quizphun;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.Toast;

public class QuizPhunWidget extends AppWidgetProvider {
//http://forum.xda-developers.com/showthread.php?t=1732939	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds){
		//http://stackoverflow.com/questions/3589741/how-to-open-a-application-from-widget-in-android
		for (int i = 0; i < appWidgetIds.length; i++) {
			try {
				int appWidgetId = appWidgetIds[i];
				Intent intent = new Intent(context, MainActivity.class);
				PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
				
				// Get the layout for the App Widget and attach an on-click listener to the button
				RemoteViews views = new RemoteViews(context.getPackageName(),R.layout.widgetlayout);
				views.setOnClickPendingIntent(R.id.widget_button, pendingIntent);
				appWidgetManager.updateAppWidget(appWidgetId, views);
			 } catch (ActivityNotFoundException e) {
		            Toast.makeText(context.getApplicationContext(),
		                    "There was a problem loading QuizPhun",
		                    Toast.LENGTH_SHORT).show();
		    }
		}
	}
}

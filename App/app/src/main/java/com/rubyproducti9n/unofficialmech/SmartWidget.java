package com.rubyproducti9n.unofficialmech;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SmartWidget extends AppWidgetProvider {

    private static final Handler handler = new Handler();
    private static Runnable runnable;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId);
        }
        startUpdating(context, appWidgetManager);
    }

    private void startUpdating(Context context, AppWidgetManager appWidgetManager) {
        if (runnable == null) {
            runnable = new Runnable() {
                @Override
                public void run() {
                    ComponentName componentName = new ComponentName(context, SmartWidget.class);
                    int[] widgetIds = appWidgetManager.getAppWidgetIds(componentName);
                    for (int appWidgetId : widgetIds) {
                        updateWidget(context, appWidgetManager, appWidgetId);
                    }
                    handler.postDelayed(this, 60000); // Update every minute
                }
            };
        }
        handler.post(runnable);
    }

    private static void updateWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

        // Update countdown timer (example: event at a fixed time)
        long eventTime =  System.currentTimeMillis() + 3600000; // 1 hour from now
        long currentTime = System.currentTimeMillis();
        long timeLeft = eventTime - currentTime;

        if (timeLeft > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            String formattedTime = sdf.format(new Date(timeLeft));
            views.setTextViewText(R.id.widget_timer, formattedTime);
        } else {
            views.setTextViewText(R.id.widget_timer, "00:00:00");
        }

        // Refresh on click
        Intent intent = new Intent(context, SmartWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.widget_timer, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}

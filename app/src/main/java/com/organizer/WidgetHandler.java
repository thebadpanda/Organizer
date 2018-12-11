package com.organizer;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

public class WidgetHandler extends AppWidgetProvider {
    public static String EXTRA_WORD = "com.commonsware.android.appwidget.lorem.WORD";

    Context mContext = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        mContext = context;
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int appWidgetIds[] = appWidgetManager.getAppWidgetIds(new ComponentName(context, WidgetHandler.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widgetListView);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        mContext = context;
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        for (int i = 0; i < appWidgetIds.length; i++) {
            Log.d("WidgetHandler", "loop executing");

            Intent svcIntent = new Intent(context, WidgetService.class);
            svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));

            RemoteViews widget = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            widget.setRemoteAdapter(R.id.widgetListView, svcIntent);

            Intent clickIntent = new Intent(context, MainActivity.class); //MainActivity
            PendingIntent clickPI = PendingIntent
                    .getActivity(context, 0,
                            clickIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);

            widget.setPendingIntentTemplate(R.id.widgetListView, clickPI);
            appWidgetManager.updateAppWidget(appWidgetIds[i], widget);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }
}

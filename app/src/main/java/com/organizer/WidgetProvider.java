package com.organizer;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class WidgetProvider extends AppWidgetProvider {

    public static final String ACTION_CHECK = "com.organizer.ACTION_CHECK";
    public static final String EXTRA_STRING = "com.organizer.EXTRA_STRING";

    List<String> mCollections = new ArrayList<>();

    Context mContext = null;
    private SharedPreferences prefs;
    RemoteViews mView;

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        mContext = context;
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int appWidgetIds[] = appWidgetManager.getAppWidgetIds(new ComponentName(context, WidgetProvider.class));

        if (intent.getAction().equals(ACTION_CHECK)) {
            String item = intent.getExtras().getString(EXTRA_STRING);
            Toast.makeText(context, item, Toast.LENGTH_LONG).show();
            for (int id : appWidgetIds) {
                mView = initViews(context, appWidgetManager, id);
                mView.setInt(R.id.checkBtn, "setBackgroundResource", R.drawable.checked);
                mView.setBoolean(R.id.checkBtn, "setSelected", true);
            }
        }
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widgetCollectionList);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        mContext = context;
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        for (int widgetId : appWidgetIds) {
            mCollections.clear();
            mView = initViews(context, appWidgetManager, widgetId);
            mView.setBoolean(R.id.checkBtn, "setSelected", true);
            mView.setInt(R.id.checkBtn, "setBackgroundResource", R.drawable.checked);
            final Intent onItemClick = new Intent(context, WidgetProvider.class);
            onItemClick.setAction(ACTION_CHECK);
            onItemClick.setData(Uri.parse(onItemClick.toUri(Intent.URI_INTENT_SCHEME)));
            final PendingIntent onClickPendingIntent = PendingIntent
                    .getBroadcast(context, 0, onItemClick,
                            PendingIntent.FLAG_UPDATE_CURRENT);
            mView.setPendingIntentTemplate(R.id.widgetCollectionList, onClickPendingIntent);
            appWidgetManager.updateAppWidget(widgetId, mView);

        }
    }

    private RemoteViews initViews(Context context, AppWidgetManager widgetManager, int widgetId) {
        RemoteViews mView = new RemoteViews(context.getPackageName(), R.layout.widget_provider_layout);
        Intent intent = new Intent(context, WidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        mView.setRemoteAdapter(R.id.widgetCollectionList, intent);
        return mView;
    }
}

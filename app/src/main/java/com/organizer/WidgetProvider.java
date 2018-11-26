package com.organizer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


import static com.organizer.MainActivity.ACTIVITY_CHECK;
import static com.organizer.MainActivity.POSITION;

public class WidgetProvider extends AppWidgetProvider {

    public static String EXTRA_INT = "com.organizer.WidgetProvider.EXTRA_INT";
    public static String ACTION_CHECK = "com.organizer.WidgetProvider.ACTION_CHECK";
    public static String EXTRA_STRING = "com.organizer.WidgetProvider.EXTRA_STRING";

    List<String> mCollections = new ArrayList<>();
    private PendingIntent pendingIntent;

    private SharedPreferences.Editor editor;
    private SharedPreferences prefs;
    private ArrayList<String> arrayCheckedItems;

    Context mContext = null;
    RemoteViews mView;

//    @Override
//    public void onEnabled(Context context) {
//        super.onEnabled(context);
//        Intent alarm = new Intent(context, MainActivity.class);
//        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
//        AlarmManager alarmManager = (AlarmManager) context
//                .getSystemService(Context.ALARM_SERVICE);
//        int appWidgetIds[] = appWidgetManager.getAppWidgetIds(new ComponentName(context, WidgetProvider.class));
//        alarm.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
//        alarm.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
//        pendingIntent = PendingIntent.getBroadcast(context, 0, alarm,
//                PendingIntent.FLAG_CANCEL_CURRENT);
//        alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(),
//                1000, pendingIntent);
//        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widgetCollectionList);
//    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        mContext = context;
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int appWidgetIds[] = appWidgetManager.getAppWidgetIds(new ComponentName(context, WidgetProvider.class));
        arrayCheckedItems = new ArrayList<>();

        if (intent.getAction().equals(ACTION_CHECK)) {
            String item = intent.getExtras().getString(EXTRA_STRING);
            Toast.makeText(context, item, Toast.LENGTH_SHORT).show();

            //TODO: send name or position to Activity
            Intent toActivityIntent = new Intent(context, MainActivity.MyBroadcast.class);
            toActivityIntent.setAction(ACTIVITY_CHECK);
            toActivityIntent.putExtra(POSITION, intent.getExtras().getInt(EXTRA_INT));
//            context.sendBroadcast(toActivityIntent); NOT WORK with non-static receiver in MainActivity

            SharedPreferences prefs = context.getSharedPreferences("checkedItems", context.MODE_PRIVATE);
            Log.i("PREFS:", prefs.getAll().toString());

            if (prefs.getInt("counter", 0) > 0) {
                for (int i = 0; i < prefs.getInt("counter", 0); i++) {
                    String myValue = prefs.getString("value_" + i, "");
                    arrayCheckedItems.add(myValue);
                }
            } else {
                System.out.println("Не збережені чекнуті айтемси");
            }


            editor = prefs.edit();
            editor.putInt("counter", arrayCheckedItems.size());
            if (arrayCheckedItems.size() > 0) {
                for (int i = 0; i < arrayCheckedItems.size(); i++) {
                    editor.putString("value_" + i, arrayCheckedItems.get(i));
                }
            }
            editor.apply();

            for (int id : appWidgetIds) {
                mView = initViews(context, appWidgetManager, id);
                mView.setInt(R.id.checkBtn, "setBackgroundResource", R.drawable.checked);
                mView.setInt(R.id.checkBtn, "setBackgroundColor", Color.RED);
                mView.setBoolean(R.id.checkBtn, "setItemChecked", true);
                mView.setBoolean(R.id.widgetCollectionList, "setItemChecked", true);
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

//            Intent onItemClick = new Intent(context, WidgetProvider.class);
//            onItemClick.setAction(ACTION_CHECK);
//            onItemClick.setData(Uri.parse(onItemClick.toUri(Intent.URI_INTENT_SCHEME)));
//            PendingIntent onClickPendingIntent = PendingIntent
//                    .getBroadcast(context, 0, onItemClick,
//                            PendingIntent.FLAG_UPDATE_CURRENT);
//            mView.setPendingIntentTemplate(R.id.checkBtn, onClickPendingIntent);

            Intent intent = new Intent(context, WidgetProvider.class);
            intent.setAction(ACTION_CHECK);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            mView.setOnClickPendingIntent(R.id.checkBtn, pendingIntent);

            setListClick(mView, context, widgetId);

            appWidgetManager.updateAppWidget(widgetId, mView);
        }
    }

    void setListClick(RemoteViews rv, Context context, int appWidgetId) {
        Intent listClickIntent = new Intent(context, WidgetProvider.class);
        listClickIntent.setAction(ACTION_CHECK);
        PendingIntent listClickPIntent = PendingIntent.getBroadcast(context, 0,
                listClickIntent, 0);
        rv.setPendingIntentTemplate(R.id.widgetCollectionList, listClickPIntent);

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

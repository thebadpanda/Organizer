package com.organizer;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<TaskItem> mainList = new ArrayList<>();
    private ListView mainListView = null;
    private ArrayAdapter ad = null;
    private EditText addTaskEditText = null;
    private FloatingActionButton addTaskButton = null;
    private SQLiteDatabase sqlDB = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sqlDB = DBHandle.createDBTables(this);
        Cursor existingTasks = sqlDB.rawQuery("SELECT * FROM ToDoList", null);
        if (existingTasks != null) {
            if (existingTasks.moveToFirst()) {
                do {
                    if (existingTasks.isNull(existingTasks.getColumnIndex("DeadlineDate"))) {
                        mainList.add(new TaskItem(existingTasks.getString(existingTasks.getColumnIndex("Task")), Boolean.valueOf(existingTasks.getString(existingTasks.getColumnIndex("Status"))), null));

                    } else {
                        mainList.add(new TaskItem(existingTasks.getString(existingTasks.getColumnIndex("Task")), Boolean.valueOf(existingTasks.getString(existingTasks.getColumnIndex("Status"))), existingTasks.getString(existingTasks.getColumnIndex("DeadlineDate"))));
                    }

                } while (existingTasks.moveToNext());
            }
        }
        mainListView = findViewById(R.id.mainListView);
        ad = new TaskAdapter((ArrayList) mainList, getApplicationContext(), sqlDB, getSupportFragmentManager());

        if (mainListView != null) {
            mainListView.setAdapter(ad);
        }

        addTaskEditText = findViewById(R.id.addTaskEditText);
        addTaskButton = findViewById(R.id.addTaskButton);

        addTaskButton.setOnClickListener(v -> {
            String enterField = addTaskEditText.getText().toString();
            if (!enterField.equals("")) {
                if (enterField.contains("'")){
//                    enterField = enterField.replaceAll("'", "\''");
                    enterField = DatabaseUtils.sqlEscapeString(enterField);
                }
                try {
                    sqlDB.execSQL("INSERT INTO ToDoList VALUES('" + enterField.trim() + "','false',NULL)");
                    mainList.add(new TaskItem(enterField.trim(), false, null));
                    addTaskEditText.setText("");
                    ad.notifyDataSetChanged();

                    int[] ids = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), WidgetHandler.class));
                    Intent intent = new Intent(getApplicationContext(), WidgetHandler.class);
                    intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
                    sendBroadcast(intent);

                } catch (SQLiteException e) {
                    Toast.makeText(getApplicationContext(), e + "", Toast.LENGTH_SHORT).show();
                    Log.e("SQLLite Error: ", e + "");
                }
            }
        });
    }
}

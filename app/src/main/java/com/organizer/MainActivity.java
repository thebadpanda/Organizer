package com.organizer;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.organizer.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends Activity {
    private ArrayList<String> notes;
    private ArrayAdapter<String> adapter;
    private SharedPreferences.Editor editor;
    private SharedPreferences prefs;
    private ActivityMainBinding binding;
    private BroadcastReceiver broadcastReceiver;
    public static String ACTIVITY_CHECK = "com.organizer.MainActivity.ACTIVITY_CHECK";
    public static final String POSITION = "POSITION";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.organizer.R.layout.activity_main);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        notes = new ArrayList<>();

//        broadcastReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                int position = intent.getIntExtra(POSITION, 0);
//                binding.lvMain.performItemClick(binding.lvMain, position, binding.lvMain.getChildAt(position).getId());
//            }
//        };
//        IntentFilter intentFilter = new IntentFilter(BROADCAST_ACTION);
//        registerReceiver(broadcastReceiver, intentFilter);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_checked, notes);//android.R.layout.simple_list_item_checked
        prefs = getSharedPreferences("sPref", MODE_PRIVATE);

        if (prefs.getInt("counter", 0) > 0) {
            for (int i = 0; i < prefs.getInt("counter", 0); i++) {
                String myValue = prefs.getString("value_" + i, "");
                notes.add(myValue);
            }
        } else {
            System.out.println("Список пустий");
        }
        adapter.notifyDataSetChanged();

        binding.addBtn.setOnClickListener(v -> addNote());

        binding.et.setOnKeyListener((view, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN)
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    addNote();
                }
            return false;
        });



        binding.lvMain.setAdapter(adapter);

        binding.lvMain.setOnItemClickListener((adapterView, itemClicked, position, id) ->
                Toast.makeText(getApplicationContext(), "Done:  " + ((TextView) itemClicked).getText(), Toast.LENGTH_SHORT).show());

        binding.lvMain.setOnItemLongClickListener((adapterLongView, itemLongClicked, position, id) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(itemLongClicked.getContext());
            builder.setMessage("Delete this note?")
                    .setPositiveButton("Yes", (dialog, id1) -> {
                        notes.remove(position);
                        adapter.notifyDataSetChanged();
                    }).setNegativeButton("No", null);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            saveNotes();
            return false;
        });
    }

    private boolean addNote() {
        if (binding.et.getText().toString().matches(".*[a-zA-Zа-яА-Я0-9].*")) {
            notes.add(binding.et.getText().toString());
            adapter.notifyDataSetChanged();
            binding.et.setText("");
            saveNotes();
        }
        return true;
    }

    void saveNotes() {
        SharedPreferences prefs = getSharedPreferences("sPref", MODE_PRIVATE);
        editor = prefs.edit();
        editor.putInt("counter", notes.size());
        if (notes.size() > 0) {
            for (int i = 0; i < notes.size(); i++) {
                editor.putString("value_" + i, notes.get(i));
            }
        }
        editor.apply();
        int[] ids = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), WidgetProvider.class));

        Intent intent = new Intent(getApplicationContext(), WidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        sendBroadcast(intent);
    }

    public class MyBroadcast extends BroadcastReceiver {
        public MyBroadcast() { }

        @Override
        public void onReceive(Context context, Intent intent) {
            int position = intent.getIntExtra(POSITION, 0);
            binding.lvMain.performItemClick(binding.lvMain, position, binding.lvMain.getChildAt(position).getId());
        }
    }


    @Override
    protected void onStart() {
//        registerReceiver(broadcastReceiver, intentFilter);
        super.onStart();
    }

    @Override
    public void onPause() {
        adapter.notifyDataSetChanged();
        super.onPause();
    }

    @Override
    protected void onStop() {
        adapter.notifyDataSetChanged();
//        unregisterReceiver(broadcastReceiver);
        super.onStop();
    }
}
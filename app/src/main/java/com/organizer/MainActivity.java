package com.organizer;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.organizer.R.layout.activity_main);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        notes = new ArrayList<>();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_checked, notes);
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

        binding.et.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                    if (keyCode == KeyEvent.KEYCODE_ENTER) {
                        if (binding.et.getText().toString().matches(".*[a-zA-Zа-яА-Я0-9].*")) {
                            notes.add(binding.et.getText().toString());
                            adapter.notifyDataSetChanged();
                            //adapter.add(et.getText().toString());
                            binding.et.setText("");
                            saveNotes();
                            return true;
                        }
                    }
                return false;
            }
        });

        binding.lvMain.setAdapter(adapter);

        binding.lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View itemClicked, int position, long id) {
                Toast.makeText(getApplicationContext(), "Done:  " + ((TextView) itemClicked).getText(), Toast.LENGTH_SHORT).show();
            }
        });

        binding.lvMain.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterLongView, View itemLongClicked, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(itemLongClicked.getContext());
                builder.setMessage("Delete this note?")
                        .setPositiveButton("Yes", new AlertDialog.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // adapter.remove(notes.get(id));
                                notes.remove(position);
                                adapter.notifyDataSetChanged();
                            }
                        }).setNegativeButton("No", null);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                saveNotes();
                return false;
            }
        });
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

    @Override
    public void onPause() {
        adapter.notifyDataSetChanged();
        super.onPause();
    }

    @Override
    protected void onStop() {
        adapter.notifyDataSetChanged();
        super.onStop();
    }
}
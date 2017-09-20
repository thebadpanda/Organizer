package com.example.arsenko.organizertest;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class MainActivity extends Activity {

    final ArrayList<String> notes = new ArrayList<>();

    Button saveBtn = (Button) findViewById(R.id.saveBtn);

    ListView lvMain = (ListView) findViewById(R.id.lvMain);

    final EditText et = (EditText) findViewById(R.id.et);


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_checked, notes);

        et.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                    if (keyCode == KeyEvent.KEYCODE_ENTER) {
                        if (et.getText().toString().matches(".*[a-zA-Zа-яА-Я0-9].*")) {
                            notes.add(et.getText().toString());
                            adapter.notifyDataSetChanged();
                            //adapter.add(et.getText().toString());
                            et.setText("");
                            return true;
                        }

                    }
                return false;

            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences prefs = getSharedPreferences("sPref", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("counter", notes.size());
                if (notes.size() > 0) {
                    for (int i = 0; i < notes.size(); i++) {
                        editor.putString("value_" + i, notes.get(i));
                    }
                    editor.commit();
                }

            }

        });

        lvMain.setAdapter(adapter);

        lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View itemClicked, int position, long id) {
                Toast.makeText(getApplicationContext(), "кароч, давай бистра:  " + ((TextView) itemClicked).getText(), Toast.LENGTH_SHORT).show();
            }
        });

        lvMain.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
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


                Toast.makeText(getApplicationContext(), "Видалено: " + ((TextView) itemLongClicked).getText(), Toast.LENGTH_SHORT).show();

                return false;
            }

        });
    }
    void saveNotes() {
        SharedPreferences prefs = getSharedPreferences("sPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("counter", notes.size());
        if (notes.size() > 0) {
            for (int i = 0; i < notes.size(); i++) {
                editor.putString("value_" + i, notes.get(i));
            }
            editor.commit();
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        saveNotes();

    }
}
package com.example.arsenko.organizertest;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView lvMain = (ListView)findViewById(R.id.lvMain);
        final EditText et = (EditText)findViewById(R.id.et);

        final ArrayList<String> notes = new ArrayList<>();

        final ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_checked, notes );

        lvMain.setAdapter(adapter);

        et.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if(event.getAction()==KeyEvent.ACTION_DOWN)
                    if(keyCode == KeyEvent.KEYCODE_ENTER){
                        notes.add(0, et.getText().toString());
                        adapter.notifyDataSetChanged();
                        et.setText("");
                        return true;
                    }
                return false;
            }
        });
        lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View itemClicked, int position, long id) {
                Toast.makeText(getApplicationContext(), "кароч, давай бистра:  " + ((TextView) itemClicked).getText(), Toast.LENGTH_SHORT).show();
//                TextView textView = (TextView)itemClicked;
//                String insideText = textView.getText().toString(); //отримуємо текст натиснутого елемента
//
//                if(insideText.equalsIgnoreCase(getResources().getString(R.string.))){
//                    startActivity(new Intent(this.MainActivity.java)); // викликаємо  актівіті
//                }


            }
        });

        lvMain.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterLongView, View itemLongClicked, int position, long id) {
                notes.remove(position);
                adapter.notifyDataSetChanged();

                Toast.makeText(getApplicationContext(), "Видалено: " + ((TextView)itemLongClicked).getText(), Toast.LENGTH_SHORT).show();

                return false;
            }
        });


    }
}
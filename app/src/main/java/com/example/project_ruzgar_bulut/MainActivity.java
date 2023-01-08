package com.example.project_ruzgar_bulut;

import static android.os.Environment.getExternalStoragePublicDirectory;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.SharedPreferencesCompat;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    //buttons and texts
    private ListView listView;
    private EditText editText1;
    private EditText editText2;
    private Button btnAdd;
    private Button btnSave;
    private Button btnInvert;
    private Button btnClear;
    private textAdapter adapter;

    //to save file
    private File file;


    //switch
    private Switch aSwitch;
    boolean nightMode;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    private ArrayList<texts> arrayList;

    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        listView = (ListView) findViewById(R.id.liste);
        editText1 = (EditText) findViewById(R.id.editText1);
        editText2 = (EditText) findViewById(R.id.editText2);
        btnAdd = (Button) findViewById(R.id.btnEntry);
        btnSave = findViewById(R.id.btnSave);


        ArrayList<texts> arrayList = new ArrayList<>();
        HashMap<String, String> map = new HashMap<>();
        Set<String> set = new HashSet<>();


        aSwitch = findViewById(R.id.darkModeSwitch);
        // i used shared preferences to save the mode if you exit and open again
        sharedPreferences = getSharedPreferences("MODE", Context.MODE_PRIVATE);

        nightMode = sharedPreferences.getBoolean("night", false); //light mode is default
        adapter = new textAdapter(this, R.layout.list_row, arrayList);

        //receiving saved list
        Set<String> setRetrieve = sharedPreferences.getStringSet("map", new HashSet<String>());
        for (String s : setRetrieve) {
            String[] parts = s.split(":");
            map.put(parts[0], parts[1]);
        }

        for (Map.Entry<String, String> entry : map.entrySet()) {
            arrayList.add(new texts(entry.getKey(), entry.getValue()));
        }
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        if (nightMode) {
            aSwitch.setChecked(true);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        }


        //night mode - light mode
        aSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // list view was reseting itself when i turn on - off darkmode. I wrote this
                //to fix this problem
                Set<String> setRetrieve = sharedPreferences.getStringSet("map", new HashSet<String>());
                for (String s : setRetrieve) {
                    String[] parts = s.split(":");
                    map.put(parts[0], parts[1]);
                }

                for (Map.Entry<String, String> entry : map.entrySet()) {
                    arrayList.add(new texts(entry.getKey(), entry.getValue()));
                }
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                //darkmode control
                if (nightMode) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    editor = sharedPreferences.edit();
                    editor.putBoolean("night", false);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    editor = sharedPreferences.edit();
                    editor.putBoolean("night", true);
                }

                editor.apply();

            }

        });


        //add entries
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = editText1.getText().toString();
                String subtext = editText2.getText().toString();

                arrayList.add(new texts(text, subtext));
                map.put(text, subtext);

                for (Map.Entry<String, String> entry : map.entrySet()) {
                    set.add(entry.getKey() + ":" + entry.getValue());
                }
                editor = sharedPreferences.edit();
                editor.putStringSet("map", set);

                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                editor.apply();

            }

        });


        //save Data
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Assume that the set of strings is called "stringSet"

                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "strings.txt");
                try (FileOutputStream outputStream = new FileOutputStream(file)) {
                    for (String s : set) {
                        outputStream.write(s.getBytes());
                        outputStream.write("\n".getBytes());  // Write a newline character after each string
                    }
                    Toast.makeText(getApplicationContext(), "File saved successfully!",
                            Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    // Handle the exception
                }
            }
        });


        //Toast
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                texts o = (texts) parent.getItemAtPosition(position);
                Toast.makeText(MainActivity.this, o.getText1().toString(), Toast.LENGTH_SHORT).show();
            }
        });


    }
}
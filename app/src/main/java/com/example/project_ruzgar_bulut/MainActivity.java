package com.example.project_ruzgar_bulut;

import static android.os.Environment.getExternalStoragePublicDirectory;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.SharedPreferencesCompat;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    //buttons and texts
    private ListView listView;
    private ListView invertedListView;
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



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();


        listView = (ListView) findViewById(R.id.liste);
        invertedListView = findViewById(R.id.invertedList);
        editText1 = (EditText) findViewById(R.id.editText1);
        editText2 = (EditText) findViewById(R.id.editText2);
        btnAdd = (Button) findViewById(R.id.btnEntry);
        btnSave = findViewById(R.id.btnSave);
        btnInvert = findViewById(R.id.btnInvert);
        btnClear = findViewById(R.id.btnClear);


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

                editText1.setText("");
                editText2.setText("");

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
                Set<String> setRetrieve = sharedPreferences.getStringSet("map", new HashSet<String>());
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "strings.txt");
                try (FileOutputStream outputStream = new FileOutputStream(file)) {
                    for (String s : setRetrieve) {
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

        //Invert Strings
        btnInvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Assume that the set of strings is called "stringSet"
                Set<String> setRetrieve = sharedPreferences.getStringSet("map", new HashSet<String>());

                Set<String> invertedSet = new HashSet<>();
                for (String s : setRetrieve) {
                    String inverted = new StringBuilder(s).reverse().toString();
                    invertedSet.add(inverted);
                }

                List<String> stringList = new ArrayList<>(invertedSet);  // Convert the set to a list

                Intent intent = new Intent(getApplicationContext(), secondActivity.class);
                intent.putExtra("string_list", (Serializable) stringList);

                startActivity(intent);

                // Put the inverted set into an intent and start the second activity

            }
        });

        //Clear List
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRetrieve.clear();
                arrayList.clear();
                map.clear();
                set.clear();


                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

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
package com.example.project_ruzgar_bulut;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
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
import java.util.Set;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;



import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
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
    public static final String CHANNEL_ID = "exampleChannel";

    private NotificationCompat notificationManager;

    //to save file
    private File file;






    //switch
    private Switch aSwitch;
    boolean nightMode;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    private ArrayList<texts> arrayList;

    private int count = 0;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }


    private void sendNotification(int set){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent
                ,PendingIntent.FLAG_ONE_SHOT);

        String ChannelId = "My channel ID";
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,ChannelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Notification")
                .setContentText("You have saved this much entry so far: " + set)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(ChannelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0,notificationBuilder.build());

    }



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();



        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);

        mBuilder.setSmallIcon(R.drawable.ic_launcher_foreground);
        mBuilder.setContentTitle("Notification Alert, Click Me!");
        mBuilder.setContentText("Hi, This is Android Notification Detail!");


        listView = (ListView) findViewById(R.id.liste);
        invertedListView = findViewById(R.id.invertedList);
        editText1 = (EditText) findViewById(R.id.editText1);
        editText2 = (EditText) findViewById(R.id.editText2);
        btnAdd = (Button) findViewById(R.id.btnEntry);
        btnSave = findViewById(R.id.btnSave);
        btnInvert = findViewById(R.id.btnInvert);
        btnClear = findViewById(R.id.btnClear);

        //LISTS
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
                verifyStoragePermissions(MainActivity.this);
                try  {
                    File file = new File(Environment.getExternalStoragePublicDirectory
                            (Environment.DIRECTORY_DOWNLOADS), "strings.txt");
                    FileOutputStream outputStream = new FileOutputStream(file);
                    for (String s : setRetrieve) {
                        outputStream.write(s.getBytes());
                        outputStream.write("\n".getBytes());  // Write a newline character after each string
                    }
                    outputStream.close();
                    sendNotification(setRetrieve.size());
                    Toast.makeText(getApplicationContext(), "File saved successfully!",
                            Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
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
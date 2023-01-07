package com.example.project_ruzgar_bulut;

import static android.os.Environment.getExternalStoragePublicDirectory;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.LauncherActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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

        file = new File(getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),"project.pdf");


        listView = (ListView) findViewById(R.id.liste);
        editText1 = (EditText) findViewById(R.id.editText1);
        editText2 = (EditText) findViewById(R.id.editText2);
        btnAdd = (Button) findViewById(R.id.btnEntry);
        btnSave = findViewById(R.id.btnSave);

        ArrayList<texts> arrayList = new ArrayList<>();



        aSwitch = findViewById(R.id.darkModeSwitch);
        // i used shared preferences to save the mode if you exit and open again
        sharedPreferences = getSharedPreferences("MODE",Context.MODE_PRIVATE);

        nightMode = sharedPreferences.getBoolean("night",false); //light mode is default

        if(nightMode){
            aSwitch.setChecked(true);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        aSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nightMode){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    editor = sharedPreferences.edit();
                    editor.putBoolean("night",false);
                }else{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    editor = sharedPreferences.edit();
                    editor.putBoolean("night",true);
                }
            editor.apply();
            }

        });



        adapter = new textAdapter(this,R.layout.list_row, arrayList);

        //add entries
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = editText1.getText().toString();
                String subtext = editText2.getText().toString();

                arrayList.add(new texts(text, subtext));


                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();


            }

        });

        //save Data
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    FileOutputStream fos = new FileOutputStream(file);
                    ObjectOutputStream oos = new ObjectOutputStream(fos);
                    oos.writeObject(arrayList);
                    oos.close();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Toast.makeText(MainActivity.this, "Saved Succesfully: Downloads", Toast.LENGTH_SHORT).show();
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
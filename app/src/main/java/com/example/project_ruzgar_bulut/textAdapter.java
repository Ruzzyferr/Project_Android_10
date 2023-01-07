package com.example.project_ruzgar_bulut;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class textAdapter extends ArrayAdapter<texts> {

    private int mResource;
    private Context mContext;


    public textAdapter(@NonNull Context context, int resource, @NonNull ArrayList<texts> objects) {
        super(context, resource, objects);
        this.mContext=context;
        this.mResource=resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);

        convertView = layoutInflater.inflate(mResource,parent,false);

        TextView text = convertView.findViewById(R.id.text1);
        TextView sub = convertView.findViewById(R.id.text2);

        text.setText(getItem(position).getText1());
        sub.setText(getItem(position).getText2());


        return convertView;
    }
}

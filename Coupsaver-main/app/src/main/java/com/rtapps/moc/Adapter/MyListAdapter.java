package com.rtapps.moc.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.rtapps.moc.R;

public class MyListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] text;

    public MyListAdapter(Activity context, String[] maintitle) {
        super(context, R.layout.mylist, maintitle);
        // TODO Auto-generated constructor stub

        this.context = context;
        this.text = maintitle;

    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.mylist, null, true);
        TextView titleText = rowView.findViewById(R.id.categorytext);
        titleText.setText(text[position]);


        return rowView;

    }

}

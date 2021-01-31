package com.example.kelimebulmaca;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Bu bolumdeki kodlar asagida verilen siteden alinarak uzerinde degisiklik yapilmistir
 * http://stephenpengilley.blogspot.com/2013/01/android-custom-spinner-tutorial.html
 */

public class CustomAdapter extends ArrayAdapter<CompleteTick> {

    private Activity context;
    ArrayList<CompleteTick> tick;

    public CustomAdapter(Activity context, int resource, ArrayList<CompleteTick> tick) {
        super(context, resource, tick);
        this.context = context;
        this.tick = tick;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        if (row == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            row = inflater.inflate(R.layout.spinner_row, parent, false);
        }

        CompleteTick current = tick.get(position);

        ImageView profile = (ImageView) row.findViewById(R.id.profile);
        profile.setBackgroundResource(current.getResourceId());

        TextView name = (TextView) row.findViewById(R.id.spinnerText);
        name.setText(current.getName());
        name.setTextColor(context.getResources().getColor(R.color.mor));
        name.setTextSize(18);

        return row;
    }
}

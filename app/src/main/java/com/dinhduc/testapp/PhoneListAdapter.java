package com.dinhduc.testapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nguyen Dinh Duc on 8/5/2015.
 */
public class PhoneListAdapter extends ArrayAdapter<Phone> {
    Context context;
    int layoutId;
    ArrayList<Phone> phones;

    public PhoneListAdapter(Context context, int resource, ArrayList<Phone> objects) {
        super(context, resource, objects);
        this.context = context;
        this.layoutId = resource;
        this.phones = objects;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layoutId, null);
        }
        TextView number = (TextView) convertView.findViewById(R.id.phoneNumber);
        TextView type = (TextView) convertView.findViewById(R.id.phoneType);
        ImageButton call = (ImageButton) convertView.findViewById(R.id.call);
        ImageButton sms = (ImageButton) convertView.findViewById(R.id.sms);

        final Phone phone = phones.get(position);
        number.setText(phone.getNumber());
        type.setText(phone.getType());

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent call = new Intent(Intent.ACTION_CALL);
                call.setData(Uri.parse("tel:" + phone.getNumber()));
                context.startActivity(call);
            }
        });
        sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SmsActivity.class);
                intent.putExtra("address", phone.getNumber());
                intent.putExtra("position", DetailActivity.position);
                context.startActivity(intent);
            }
        });
        return convertView;
    }
}

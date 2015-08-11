package com.dinhduc.testapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pkmmte.view.CircularImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import github.ankushsachdeva.emojicon.EmojiconTextView;

/**
 * Created by Nguyen Dinh Duc on 8/5/2015.
 */
public class SmsListAdapter extends ArrayAdapter<Sms> {
    Context context;
    int layoutId;
    ArrayList<Sms> smses;

    public SmsListAdapter(Context context, int resource, ArrayList<Sms> objects) {
        super(context, resource, objects);
        this.context = context;
        this.layoutId = resource;
        this.smses = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(layoutId, null);
        CircularImageView iconOther = (CircularImageView) convertView.findViewById(R.id.icon_other);
        EmojiconTextView smsOther = (EmojiconTextView) convertView.findViewById(R.id.sms_other);
        EmojiconTextView smsMe = (EmojiconTextView) convertView.findViewById(R.id.sms_me);
        LinearLayout layoutOther = (LinearLayout) convertView.findViewById(R.id.layout_other);
        LinearLayout layoutMe = (LinearLayout) convertView.findViewById(R.id.layout_me);
        Sms sms = smses.get(position);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, HH:mm", Locale.getDefault());
        String dateTime = dateFormat.format(new Date(sms.getDate()));
        if (sms.getType() == Sms.SMS_INBOX) {
            layoutMe.setVisibility(View.GONE);
            smsOther.setText(sms.getBody() + "\n" + dateTime);
            if (SmsActivity.other != null)
                iconOther.setImageBitmap(SmsActivity.other);
        } else {
            layoutOther.setVisibility(View.GONE);
            smsMe.setText(sms.getBody() + "\n" + dateTime);
        }
        return convertView;
    }
}

package com.dinhduc.testapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pkmmte.view.CircularImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nguyen Dinh Duc on 8/3/2015.
 */
public class ContactListAdapter extends ArrayAdapter<Contact> {
    Context context;
    int layoutId;
    ArrayList<Contact> contacts;
    ArrayList<Contact> oriContacts = new ArrayList<>();
    CircularImageView icon;
    TextView header, name;
    LinearLayout llcontact;

    public ContactListAdapter(Context context, int resource, ArrayList<Contact> objects) {
        super(context, resource, objects);
        this.context = context;
        layoutId = resource;
        contacts = objects;
        oriContacts.addAll(objects);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.contact_item_lv, null);
        header = (TextView) v.findViewById(R.id.header);
        icon = (CircularImageView) v.findViewById(R.id.icon_contact);
        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("position", position);
                context.startActivity(intent);
            }
        });
        name = (TextView) v.findViewById(R.id.name_contact);
        llcontact = (LinearLayout) v.findViewById(R.id.llcontact);
        if (position < contacts.size()) {
            Contact contact = contacts.get(position);
            String name_contact = contact.getName();
            Bitmap icon_contact = contact.getIcon();
            if ((name_contact.length() == 1) && (contact.getId() == 0)) {
                header.setText(name_contact);
                header.setTextSize(16);
                llcontact.setVisibility(View.GONE);
            } else {
                header.setVisibility(View.GONE);
                name.setText(name_contact);
                if (icon_contact != null) {
                    icon.setImageBitmap(icon_contact);
                }
            }
        }
        return v;
    }

    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                contacts.clear();
                if (constraint.length() == 0) {
                    contacts.addAll(oriContacts);
                } else {
                    for (Contact contact : oriContacts) {
                        if (contact.getName().toLowerCase().contains(constraint.toString().toLowerCase()) && !isNumeric(constraint.toString())) {
                            contacts.add(contact);
                        } else if (isNumeric(constraint.toString()) && contact.getPhones() != null) {

                            for (int i = 0; i < contact.getPhones().size(); i++) {
                                if (contact.getPhones().get(i).getNumber().contains(constraint))
                                    contacts.add(contact);
                            }
                        }
                    }
                }
                return null;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                notifyDataSetChanged();
            }
        };
    }
}

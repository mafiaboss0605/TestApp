package com.dinhduc.testapp;

import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {
    ListView contactLv;
    public static ArrayList<Contact> contacts;
    ContactListAdapter adapter;
    EditText search;
    boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new LoadDataTask().execute();
    }

    /**
     * Initialize Views in Activity
     */
    private void initView() {
        search = (EditText) findViewById(R.id.search);
        contactLv = (ListView) findViewById(R.id.listContact);

        Collections.sort(contacts, new Comparator<Contact>() {
            @Override
            public int compare(Contact lhs, Contact rhs) {
                return lhs.compareIgnoreCase(rhs);
            }
        });
        indexedContacts();
        adapter = new ContactListAdapter(this, R.layout.contact_item_lv, contacts);
        contactLv.setAdapter(adapter);

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        TextView loadingText = (TextView) findViewById(R.id.text_loading);
        progressBar.setVisibility(View.GONE);
        loadingText.setVisibility(View.GONE);
    }

    /**
     * Get all contacts in device
     *
     * @return an ArrayList
     */
    private ArrayList<Contact> getAllContacts() {
        ArrayList<Contact> contacts = new ArrayList<>();
        Cursor c = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
                new String[]{ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts._ID}, null, null, null);
        if (c.moveToFirst()) {
            do {
                String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                int id = c.getInt(c.getColumnIndex(ContactsContract.Contacts._ID));
                Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id);
                InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(getContentResolver(), contactUri);
                Bitmap icon = BitmapFactory.decodeStream(inputStream);
                ArrayList<Phone> phones = getAllPhoneNumber(id);
                Contact contact = new Contact(id, name, icon, phones);
                contacts.add(contact);
            } while (c.moveToNext());
        }
        c.close();
        return contacts;
    }

    private void selectNumber(Contact contact, final int contact_position) {

        final ArrayList<Phone> phones = contact.getPhones();
        String[] listPhoneString = new String[phones.size()];
        for (int i = 0; i < listPhoneString.length; i++) {
            listPhoneString[i] = phones.get(i).getNumber();
        }

        if (phones.size() > 1) {
            final Dialog dialog = new Dialog(this);
            dialog.setTitle("Select Number");
            dialog.setContentView(R.layout.select_number_dialog);
            ListView listPhone = (ListView) dialog.findViewById(R.id.select_number);
            listPhone.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listPhoneString));
            listPhone.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    startTheActivity(phones.get(position).getNumber(), contact_position);
                    dialog.cancel();
                }
            });
            dialog.show();
        } else if (phones.size() == 1) {
            startTheActivity(phones.get(0).getNumber(), contact_position);
        }
    }

    private void startTheActivity(String address, int position) {
        Intent intent = new Intent(getBaseContext(), SmsActivity.class);
        intent.putExtra("address", address);
        intent.putExtra("position", position);
        startActivity(intent);
    }

    private ArrayList<Phone> getAllPhoneNumber(int id) {
        ArrayList<Phone> phones = new ArrayList<>();
        Cursor cursorPhone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.TYPE},
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, null, null);
        if (cursorPhone.moveToFirst()) {
            do {
                String number = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                int type = cursorPhone.getInt(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                String typePhone = "";
                switch (type) {
                    case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                        typePhone = "HOME";
                        break;
                    case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                        typePhone = "MOBILE";
                        break;
                    case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                        typePhone = "WORK";
                        break;
                    case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER:
                        typePhone = "OTHER";
                        break;
                    case ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK:
                        typePhone = "FAX WORK";
                        break;
                    case ContactsContract.CommonDataKinds.Phone.TYPE_FAX_HOME:
                        typePhone = "FAX HOME";
                        break;
                    case ContactsContract.CommonDataKinds.Phone.TYPE_PAGER:
                        typePhone = "PAGER";
                        break;
                    case ContactsContract.CommonDataKinds.Phone.TYPE_CALLBACK:
                        typePhone = "CALL BACK";
                        break;
                    case ContactsContract.CommonDataKinds.Phone.TYPE_CAR:
                        typePhone = "CAR";
                        break;
                    case ContactsContract.CommonDataKinds.Phone.TYPE_MMS:
                        typePhone = "MMS";
                        break;
                    case ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM:
                        typePhone = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LABEL));
                        break;
                }
                phones.add(new Phone(number, typePhone));
            } while (cursorPhone.moveToNext());
        }
        cursorPhone.close();
        return phones;
    }

    /**
     * Add header to contact
     */
    private void indexedContacts() {
        String char1;
        String char2 = "";
        for (int i = 0; i < contacts.size(); i++) {
            char1 = contacts.get(i).getName().substring(0, 1);
            if (char1.compareToIgnoreCase(char2) != 0) {
                char2 = char1;
                Contact contact = new Contact(char1.toUpperCase(), null);
                contacts.add(i, contact);
            }
        }
    }


    private void getControl() {


        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        contactLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Contact contact = contacts.get(position);
                if ((contact.getName().length() != 1) || (contact.getId() != 0)) {
                    selectNumber(contact, position);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (flag)
            super.onBackPressed();
        else
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                flag = true;
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                flag = false;
            }
        }).start();
    }

    private class LoadDataTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            contacts = getAllContacts();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            initView();
            getControl();
        }
    }
}

package com.dinhduc.testapp;

import android.content.ContentUris;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Nguyen Dinh Duc on 8/4/2015.
 */
public class DetailActivity extends AppCompatActivity {
    Toolbar toolbar;
    static int position;
    int id;
    String name;

    ArrayList<Phone> phones = new ArrayList<>();
    PhoneListAdapter adapter;
    ListView phoneList;
    ImageView contactPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        initView();
        getControl();
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.detailActionBar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        position = intent.getIntExtra("position", 0);
        Contact contact = MainActivity.contacts.get(position);
        id = contact.getId();
        phones = contact.getPhones();
        name = contact.getName();
        actionBar.setTitle(name);
        adapter = new PhoneListAdapter(this, R.layout.phone_item_lv, phones);
        phoneList = (ListView) findViewById(R.id.phoneList);
        phoneList.setAdapter(adapter);

        contactPhoto = (ImageView) findViewById(R.id.contact_photo);
        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id);
        InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(getContentResolver(), contactUri, true);
        Bitmap photo = BitmapFactory.decodeStream(inputStream);
        if (photo != null)
            contactPhoto.setImageBitmap(photo);
    }


    private void getControl() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return true;
    }
}

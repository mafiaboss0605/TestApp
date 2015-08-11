package com.dinhduc.testapp;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import github.ankushsachdeva.emojicon.EmojiconEditText;
import github.ankushsachdeva.emojicon.EmojiconGridView;
import github.ankushsachdeva.emojicon.EmojiconsPopup;
import github.ankushsachdeva.emojicon.emoji.Emojicon;

/**
 * Created by Nguyen Dinh Duc on 8/4/2015.
 */
public class SmsActivity extends AppCompatActivity {
    public static final Uri INBOX_CONTENT_URI = Uri.parse("content://sms/inbox");
    public static final Uri SENT_CONTENT_URI = Uri.parse("content://sms/sent");
    public static final String DATE = "date";
    public static final String BODY = "body";
    public static final String ADDRESS = "address";
    public static final String SENT = "SMS_SENT";

    PendingIntent sentPI;
    BroadcastReceiver sentReceiver, smsReceiver;
    String address;
    ArrayList<Sms> smses = new ArrayList<>();
    SmsListAdapter adapter;
    ListView smsLv;
    ImageView sendBtn, emotionBtn;
    EmojiconEditText writeMessage;
    public static Bitmap other, me;
    private EmojiconsPopup popup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);
        initView();
        getControl();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS sent",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), "Generic failure",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), "No service",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(), "Null PDU",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "Radio off",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
        smsReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getStringExtra("address").contains(address.substring(1))) {
                    String msm = intent.getStringExtra("message");
                    Sms sms = new Sms(msm, System.currentTimeMillis(), Sms.SMS_INBOX);
                    adapter.add(sms);
                    adapter.notifyDataSetChanged();
                }
            }
        };
        registerReceiver(sentReceiver, new IntentFilter(SENT));
        registerReceiver(smsReceiver, new IntentFilter(SmsBroadcastReceiver.SMS_RECEIVE_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(sentReceiver);
        unregisterReceiver(smsReceiver);
    }

    private void initView() {
        sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
        sendBtn = (ImageView) findViewById(R.id.sendSMS);
        emotionBtn = (ImageView) findViewById(R.id.emotion);
        writeMessage = (EmojiconEditText) findViewById(R.id.writeMessage);

        View rootView = findViewById(R.id.rootView);
        popup = new EmojiconsPopup(rootView, this);
        popup.setSizeForSoftKeyboard();


        Toolbar toolbar = (Toolbar) findViewById(R.id.smsActionBar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        int position = intent.getIntExtra("position", 0);
        address = intent.getStringExtra("address").replace(" ", "").replace("-", "");
        Contact contact = MainActivity.contacts.get(position);
        other = contact.getIcon();
        actionBar.setTitle(contact.getName());
        smsLv = (ListView) findViewById(R.id.smsList);
        smses.addAll(getAllSmsInbox(address));
        smses.addAll(getAllSmsSent(address));
        Collections.sort(smses, new Comparator<Sms>() {
            @Override
            public int compare(Sms lhs, Sms rhs) {
                if (lhs.getDate() < rhs.getDate())
                    return -1;
                else if (lhs.getDate() > rhs.getDate())
                    return 1;
                return 0;
            }
        });
        adapter = new SmsListAdapter(this, R.layout.sms_item_lv, smses);
        smsLv.setAdapter(adapter);
    }

    private void getControl() {
        popup.setOnSoftKeyboardOpenCloseListener(new EmojiconsPopup.OnSoftKeyboardOpenCloseListener() {
            @Override
            public void onKeyboardOpen(int keyBoardHeight) {

            }

            @Override
            public void onKeyboardClose() {
                if (popup.isShowing())
                    popup.dismiss();
            }
        });

        popup.setOnEmojiconClickedListener(new EmojiconGridView.OnEmojiconClickedListener() {

            @Override
            public void onEmojiconClicked(Emojicon emojicon) {
                if (writeMessage == null || emojicon == null) {
                    return;
                }

                int start = writeMessage.getSelectionStart();
                int end = writeMessage.getSelectionEnd();
                if (start < 0) {
                    writeMessage.append(emojicon.getEmoji());
                } else {
                    writeMessage.getText().replace(Math.min(start, end),
                            Math.max(start, end), emojicon.getEmoji(), 0,
                            emojicon.getEmoji().length());
                }
                popup.dismiss();
            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = String.valueOf(writeMessage.getText());
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(address, null, message, sentPI, null);
                Sms sms = new Sms(message, System.currentTimeMillis(), Sms.SMS_SENT);
                adapter.add(sms);
                adapter.notifyDataSetChanged();
                writeMessage.setText("");
            }
        });

        emotionBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (!popup.isShowing()) {

                    if (popup.isKeyBoardOpen()) {
                        popup.showAtBottom();
                    } else {
                        writeMessage.setFocusableInTouchMode(true);
                        writeMessage.requestFocus();
                        popup.showAtBottomPending();
                        final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.showSoftInput(writeMessage, InputMethodManager.SHOW_IMPLICIT);
                    }
                } else {
                    popup.dismiss();
                }
            }
        });
    }

    private ArrayList<Sms> getAllSmsInbox(String address) {
        ArrayList<Sms> smsInboxes = new ArrayList<>();
        String projection[] = new String[]{ADDRESS, DATE, BODY};
        Cursor cursorSms = getContentResolver().query(INBOX_CONTENT_URI, projection, ADDRESS + " like '%" + address.substring(1) + "'", null, null);
        if (cursorSms.moveToFirst()) {
            do {
                String body = cursorSms.getString(cursorSms.getColumnIndex(BODY));
                long date = cursorSms.getLong(cursorSms.getColumnIndex(DATE));
                smsInboxes.add(new Sms(body, date, Sms.SMS_INBOX));
            } while (cursorSms.moveToNext());
        }
        cursorSms.close();
        return smsInboxes;
    }

    private ArrayList<Sms> getAllSmsSent(String address) {
        ArrayList<Sms> smsSents = new ArrayList<>();
        String projection[] = new String[]{DATE, BODY};
        Cursor cursorSms = getContentResolver().query(SENT_CONTENT_URI, projection, ADDRESS + " like '%" + address + "'", null, null);
        if (cursorSms.moveToFirst()) {
            do {
                String body = cursorSms.getString(cursorSms.getColumnIndex(BODY));
                long date = cursorSms.getLong(cursorSms.getColumnIndex(DATE));
                smsSents.add(new Sms(body, date, Sms.SMS_SENT));
            } while (cursorSms.moveToNext());
        }
        cursorSms.close();
        return smsSents;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }
}

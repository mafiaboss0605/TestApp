package com.dinhduc.testapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

/**
 * Created by Nguyen Dinh Duc on 8/10/2015.
 */
public class SmsBroadcastReceiver extends BroadcastReceiver {
    public static final String SMS_RECEIVE_ACTION = "sms_receive";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        SmsMessage[] messages = null;
        String str = "";
        String address = "";
        if (bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");
            messages = new SmsMessage[pdus.length];
            for (int i = 0; i < messages.length; i++) {
                messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                if (i == 0) {
                    address = messages[i].getOriginatingAddress();
                }

                str += messages[i].getMessageBody();
            }
        }
        this.abortBroadcast();
        Intent intentBroadcast = new Intent(SMS_RECEIVE_ACTION);
        intentBroadcast.putExtra("message", str);
        intentBroadcast.putExtra("address",address);
        context.sendBroadcast(intentBroadcast);
    }
}

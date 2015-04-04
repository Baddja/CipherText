package com.baddja.ciphertext;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.widget.Toast;

/**
 * Created by Jin on 3/24/2015.
 */
public class SMSSentReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        switch (getResultCode()) {
            case Activity.RESULT_OK:
                String number = intent.getStringExtra(HomeActivity.SMSHEADER);
                String message = intent.getStringExtra(HomeActivity.SMSBODY);
                ContentValues values = new ContentValues();
                values.put(Telephony.Sms.Sent.ADDRESS, number);// txtPhoneNo.getText().toString());
                values.put(Telephony.Sms.Sent.BODY, message);
                context.getContentResolver().insert(Telephony.Sms.Sent.CONTENT_URI, values);
                Toast.makeText(context, "SMS sent",
                        Toast.LENGTH_SHORT).show();
                break;
            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                Toast.makeText(context, "Generic failure",
                        Toast.LENGTH_SHORT).show();
                break;
            case SmsManager.RESULT_ERROR_NO_SERVICE:
                Toast.makeText(context, "No service",
                        Toast.LENGTH_SHORT).show();
                break;
            case SmsManager.RESULT_ERROR_NULL_PDU:
                Toast.makeText(context, "Null PDU",
                        Toast.LENGTH_SHORT).show();
                break;
            case SmsManager.RESULT_ERROR_RADIO_OFF:
                Toast.makeText(context, "Radio off",
                        Toast.LENGTH_SHORT).show();
                break;
        }
    }
}

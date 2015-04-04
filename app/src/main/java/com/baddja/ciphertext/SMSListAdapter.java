package com.baddja.ciphertext;

import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.content.Context;
import android.database.Cursor;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Jin on 2/27/2015.
 */
public class SMSListAdapter extends BaseAdapter{
    private Context mContext;
    Cursor cursor;
    Cursor contacts;
    public SMSListAdapter(Context context,Cursor cur)
    {
        super();
        mContext=context;
        cursor=cur;

    }

    public int getCount()
    {
        // return the number of records in cursor
        if(cursor != null)
            return cursor.getCount();
        else{
            return 0;
        }
    }

    public void changeCursor(Cursor c){
        this.cursor = c;
    }

    // getView method is called for each item of ListView
    public View getView(int position,  View view, ViewGroup parent)
    {
        // inflate the layout for each item of listView
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.sms_item, null);

        if(cursor != null) {
            // move the cursor to required position
            cursor.moveToPosition(position);

            // fetch the sender number and sms body from cursor
            String senderNumber = cursor.getString(cursor.getColumnIndex(Telephony.TextBasedSmsColumns.ADDRESS));
            String smsBody = cursor.getString(cursor.getColumnIndex(Telephony.TextBasedSmsColumns.BODY));
            Date date = new Date(cursor.getLong(cursor.getColumnIndex(Telephony.TextBasedSmsColumns.DATE)));
            String smsTimestamp = new SimpleDateFormat("EEE MMM dd HH:mm:ss").format(date);

            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(senderNumber));
            contacts = mContext.getContentResolver().query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null,null,null);

            if(contacts.getCount() >= 1){
                contacts.moveToFirst();
                senderNumber = contacts.getString(contacts.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
            }

            int cutPoint = smsBody.indexOf( "\n" +
                    "\n" +
                    "Decipher this message with CipherText");
            smsBody = smsBody.substring(0,cutPoint);

            // get the reference of textViews
            TextView contact = (TextView) view.findViewById(R.id.sms_number);
            TextView body = (TextView) view.findViewById(R.id.sms_body);
            TextView timestamp = (TextView) view.findViewById(R.id.sms_timestamp);

            // Set the Sender number and smsBody to respective TextViews
            contact.setText(senderNumber);
            body.setText(smsBody);
            timestamp.setText(smsTimestamp);
        }


        return view;
    }

    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }
}

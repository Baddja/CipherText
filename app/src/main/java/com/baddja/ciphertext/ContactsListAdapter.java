package com.baddja.ciphertext;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by Jin on 3/11/2015.
 */
public class ContactsListAdapter extends BaseAdapter {

    private Context mContext;
    Cursor cursor;
    public ContactsListAdapter(Context context,Cursor cur)
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
        view = inflater.inflate(R.layout.contacts_item, null);

        if(cursor != null) {
            // move the cursor to required position
            cursor.moveToPosition(position);

            // fetch the sender number and sms body from cursor
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            // get the reference of textViews
            TextView contactName = (TextView) view.findViewById(R.id.contact_name);
            TextView contactNumber = (TextView) view.findViewById(R.id.contact_number);

            // Set the Sender number and smsBody to respective TextViews
            contactName.setText(name);
            contactNumber.setText(number);
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

package com.baddja.ciphertext;

import android.app.LoaderManager;
import android.content.ContentProvider;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.provider.Telephony;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.gc.materialdesign.views.ButtonFloat;


public class HomeActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    public static final int SMSLOADER = 0;

    public static final String SMSHEADER = "com.baddja.ciphertext.sms_header";
    public static final String SMSBODY = "com.baddja.ciphertext.sms_body";
    public static final String SMSTIME = "com.baddja.ciphertext.sms_timestamp";

    private Context context;

    private ListView smslist;
    private ContentProvider smsContent;
    private Cursor cursor;
    private TextView txtEmptyList;

    private SMSListAdapter smsListAdapter;

    private ButtonFloat btnNewSms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        context = this;

        smslist = (ListView)findViewById(R.id.sms_list);

        //cursor = getContentResolver().query(Telephony.Sms.Inbox.CONTENT_URI,null,null,null,null);

        getLoaderManager().initLoader(SMSLOADER,null,this);

        smsListAdapter = new SMSListAdapter(this,null);

        smslist.setAdapter(smsListAdapter);

        smslist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView sms_header = (TextView)view.findViewById(R.id.sms_number);
                TextView sms_body = (TextView)view.findViewById(R.id.sms_body);
                TextView sms_timestamp = (TextView)view.findViewById(R.id.sms_timestamp);

                Intent smsView_intent = new Intent(context, DecipherActivity.class);

                smsView_intent.putExtra(SMSHEADER, sms_header.getText());
                smsView_intent.putExtra(SMSBODY, sms_body.getText());
                smsView_intent.putExtra(SMSTIME, sms_timestamp.getText());

                startActivity(smsView_intent);
            }
        });


        btnNewSms = (ButtonFloat)findViewById(R.id.new_sms_btn);
        btnNewSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context,CipherActivity.class));
            }
        });

        txtEmptyList = (TextView)findViewById(R.id.empty_list);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_smslist, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch(id){
            case R.id.action_refresh:
                getLoaderManager().restartLoader(SMSLOADER,null,this);
                Log.d("SMSLIST", "Refreshing List");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderID, Bundle bundle)
    {
    /*
     * Takes action based on the ID of the Loader that's being created
     */
        switch (loaderID) {
            case SMSLOADER:
                // Returns a new CursorLoader
                return new CursorLoader(
                        context,   // Parent activity context
                        Telephony.Sms.Inbox.CONTENT_URI,        // Table to query
                        null,               // Projection to return
                        Telephony.Sms.Inbox.BODY + " LIKE '%Decipher this message with CipherText%'",            // No selection clause
                        null,            // No selection arguments
                        null             // Default sort order
                );
            default:
                // An invalid id was passed in
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.d("SMSLIST", "Loader "+loader.getId()+" finished");
        if(cursor.getCount()>0) {
            txtEmptyList.setVisibility(View.GONE);
            smslist.setVisibility(View.VISIBLE);
            smsListAdapter.changeCursor(cursor);
            smsListAdapter.notifyDataSetChanged();
        }else{
            smslist.setVisibility(View.GONE);
            txtEmptyList.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader){
        smsListAdapter.changeCursor(null);
    }
}

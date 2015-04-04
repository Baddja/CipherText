package com.baddja.ciphertext;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonRectangle;


public class DecipherActivity extends ActionBarActivity {
    private Context context;

    private TextView sms_header, sms_body, sms_timestamp;
    private EditText cipher_key;

    private ButtonRectangle btn_decipher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decipher);

        context = this;

        Intent intent = getIntent();

        String sms_header_text = intent.getStringExtra(HomeActivity.SMSHEADER);
        String sms_body_text = intent.getStringExtra(HomeActivity.SMSBODY);
        String sms_timestamp_text = intent.getStringExtra(HomeActivity.SMSTIME);

        sms_header = (TextView) findViewById(R.id.decipher_number);
        sms_body = (TextView) findViewById(R.id.decipher_message);
        sms_timestamp = (TextView)findViewById(R.id.decipher_timestamp);

        sms_header.setText(sms_header_text);
        sms_body.setText(sms_body_text);
        sms_timestamp.setText(sms_timestamp_text);

        cipher_key = (EditText) findViewById(R.id.decipher_key);

        btn_decipher = (ButtonRectangle)findViewById(R.id.decipher_btn);
        btn_decipher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decipherSMS(v);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_smsview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        return super.onOptionsItemSelected(item);
    }

    public void decipherSMS(View view){
        Toast toast;
        String toast_text;

        String message = sms_body.getText().toString();
        String key = cipher_key.getText().toString();

        if(key.isEmpty()){
            toast_text = "You have not entered a cipher keyword yet.";
            Toast.makeText(context, toast_text, Toast.LENGTH_SHORT).show();
        }else {
            if (!key.matches("[a-zA-Z]+") && !key.isEmpty()) {
                toast_text = "Cipher key must be a single word containing only letters.";
                toast = Toast.makeText(context, toast_text, Toast.LENGTH_SHORT);
                toast.show();
            }else{
                key = key.toUpperCase();
                char[] key_array = key.toCharArray();
                int[] key_array_shifts = new int[key_array.length];

                for(int i=0; i<key_array.length; i++){
                    key_array_shifts[i] = key_array[i] - 65;
                }

                char[] message_array = message.toCharArray();
                int shift_counter = 0;
                String accepted=" \n.";
                for(int i=0; i<message_array.length; i++){
                    if(!accepted.contains(""+message_array[i])){
                        Log.d("SMSVIEW","Deciphering '"+message_array[i]+"' which is ascii "+ (int) message_array[i]);
                        int result = ((int) message_array[i] - key_array_shifts[shift_counter]);
                        if(result < 33){
                            int diff = 33-result;
                            result = 127 - diff;
                        }
                        message_array[i] = (char) (result);
                        shift_counter++;
                        if (shift_counter == key_array_shifts.length) {
                            shift_counter = 0;
                        }
                    }
                }

                sms_body.setText(new String(message_array));
            }
        }

    }
}

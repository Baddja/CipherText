package com.baddja.ciphertext;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.gc.materialdesign.views.ButtonRectangle;

import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * This view allows users to create new SMS messages and apply the Vigenere cipher before sending.
 *
 * @author Jin Dhaliwal
 * @version 4/4/2015
 */
public class CipherActivity extends ActionBarActivity implements ContactsFragment.OnFragmentInteractionListener{
    //Intent Constants
    public static final String SENT = "com.baddja.ciphertext.smsMan.sentIntent";
    public static final String DELIVERED = "com.baddja.ciphertext.smsMan.deliveredIntent";

    private Context context; //activity context takes place of 'this' keyword

    //Views
    private FrameLayout contactsFrame;
    private EditText phoneNumber, message, cipherKey;
    private ButtonRectangle sendButton, cipherButton;

    //Objects used in SMS logic
    private SmsManager smsMan;
    private Intent sentIntent, deliveredIntent;
    private PendingIntent sendPI, deliveredPI;

    //BroadcastReceivers
    private SMSSentReceiver sentReceiver = new SMSSentReceiver();
    private SMSDeliveredReceiver deliveredReceiver = new SMSDeliveredReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cipher);

        context = this;

        //Avoid recreation of objects if there is a saved instance state
        if(savedInstanceState != null){
            return;
        }

        contactsFrame = (FrameLayout)findViewById(R.id.contacts_frame);

        ContactsFragment contactsFragment = ContactsFragment.newInstance(null, null);

        getFragmentManager().beginTransaction().add(R.id.contacts_frame,contactsFragment).commit();

        smsMan = SmsManager.getDefault();

        phoneNumber = (EditText)findViewById(R.id.cipher_number);

        message = (EditText)findViewById(R.id.cipher_message);
        message.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    contactsFrame.setVisibility(View.GONE); //Hide contactsFrame when message has focus
                }
            }
        });

        cipherKey = (EditText)findViewById(R.id.cipher_key);

        sendButton = (ButtonRectangle)findViewById(R.id.cipher_send_btn);
        sendButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCipher(v);
            }
        });

        //cipherButton = (ButtonRectangle)findViewById(R.id.btn_apply_cipher);
        /*cipherButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyCipher(v);
            }
        });*/
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_smsnew, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    public void onFragmentInteraction(Uri uri){
        //This handles interaction between activity and hosted fragments.
    }

    /**
     * Applys Vigenere cipher to message, then sends the message.
     *
     * @param view the calling view
     */
    public void sendCipher(View view){
        String toast_text;

        String key = cipherKey.getText().toString();
        String number = phoneNumber.getText().toString();
        String msg = message.getText().toString();

        if(number.isEmpty()){
            toast_text="You must enter a phone number before sending.";
            Toast.makeText(this, toast_text, Toast.LENGTH_SHORT).show();
        }else{
            if(msg.isEmpty()){
                toast_text = "You have not entered a message yet.";
                Toast.makeText(this, toast_text, Toast.LENGTH_SHORT).show();
            }else {
                if (!key.matches("[a-zA-Z]+") || key.isEmpty()) {
                    toast_text = "Cipher key must be a single word containing only letters.";
                    Toast.makeText(this, toast_text, Toast.LENGTH_SHORT).show();
                }else{
                    key = key.toUpperCase();
                    char[] key_array = key.toCharArray();
                    int[] key_array_shifts = new int[key_array.length];

                    for(int i=0; i<key_array.length; i++){
                        key_array_shifts[i] = key_array[i] - 65;
                    }

                    char[] message_array = msg.toCharArray();
                    int shift_counter = 0;
                    String accepted=" \n.";
                    for(int i=0; i<message_array.length; i++){
                        if(!accepted.contains(""+message_array[i])){
                            int remainder = ((int) message_array[i] + key_array_shifts[shift_counter]) % 127;
                            if(remainder < 33){
                                remainder += 33;
                            }
                            message_array[i] = (char)remainder;
                            shift_counter++;
                            if (shift_counter == key_array_shifts.length) {
                                shift_counter = 0;
                            }
                        }
                    }

                    msg = new String(message_array);
                    message.setText(msg);

                    msg += "\n\nDecipher this message with CipherText\nhttps://play.google.com/store/apps/details?id=com.baddja.ciphertext";

                    sentIntent = new Intent(SENT);
                    sentIntent.putExtra(HomeActivity.SMSHEADER, number);
                    sentIntent.putExtra(HomeActivity.SMSBODY, msg);

                    deliveredIntent = new Intent(DELIVERED);

                    sendPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), PendingIntent.FLAG_ONE_SHOT);

                    deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), PendingIntent.FLAG_ONE_SHOT);

                    if(message.length() > 180){
                        ArrayList<String> msgArray = smsMan.divideMessage(msg);
                        ArrayList<PendingIntent> piSentArray = new ArrayList<PendingIntent>();
                        for(int i=0; i<msgArray.size()-1; i++){
                            piSentArray.add(null);
                        }
                        ArrayList<PendingIntent> piDeliveredArray = new ArrayList<PendingIntent>();
                        for(int i=0; i<msgArray.size()-1; i++){
                            piSentArray.add(null);
                        }
                        piSentArray.add(sendPI);
                        piDeliveredArray.add(deliveredPI);
                        smsMan.sendMultipartTextMessage(number, null, msgArray, piSentArray, piDeliveredArray);
                    }else{
                        smsMan.sendTextMessage(number, null, msg, sendPI, deliveredPI);
                    }
                }
            }

            Log.e("NEWSMS","Sending to: "+number +"\n"+msg);
        }
    }
}

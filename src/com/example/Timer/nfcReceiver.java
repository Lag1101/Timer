package com.example.Timer;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by vasiliy.lomanov on 08.05.2014.
 */
public class nfcReceiver extends Activity {


    private String nfcText;
    private NfcAdapter mNfcAdapter;
    private IntentFilter[] intentFiltersArray;
    private PendingIntent pendingIntent;
    private String[][] techListsArray;

    protected Tag tag;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.timer);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        pendingIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        // Setup an intent filter for all MIME based dispatches
        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndef.addDataType("*/*");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("fail", e);
        }
        IntentFilter td = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        intentFiltersArray = new IntentFilter[] {
                ndef, td
        };

        // Setup a tech list for all NfcF tags
        techListsArray = new String[][] { new String[] {
                NfcV.class.getName(),
                NfcF.class.getName(),
                NfcA.class.getName(),
                NfcB.class.getName()
        } };
    }

    private TextView getTimedTextView(String msg) {
        TextView text = new TextView(this);
        text.setText(msg + " " + Calendar.getInstance().getTime().toString());
        return text;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        mNfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techListsArray);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        mNfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    public void onNewIntent(Intent intent){
        // fetch the tag from the intent
        tag = (Tag)intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
    }
}
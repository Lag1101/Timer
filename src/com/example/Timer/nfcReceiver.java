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

import java.util.Arrays;
import java.util.Calendar;

/**
 * Created by vasiliy.lomanov on 08.05.2014.
 */
public class nfcReceiver {
    private NfcAdapter mNfcAdapter;
    private IntentFilter[] intentFiltersArray;
    private PendingIntent pendingIntent;
    private String[][] techListsArray;

    public Tag tag;

    Activity parentActivity = null;

    public nfcReceiver(Activity activity) {

        parentActivity = activity;

        mNfcAdapter = NfcAdapter.getDefaultAdapter(activity);
        if (mNfcAdapter == null) {
            Toast.makeText(activity, "NFC is not available", Toast.LENGTH_LONG).show();
            return;
        }

        pendingIntent = PendingIntent.getActivity(
                activity, 0, new Intent(activity, activity.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

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

    public void onResume()
    {
        mNfcAdapter.enableForegroundDispatch(parentActivity, pendingIntent, intentFiltersArray, techListsArray);
    }

    public void onPause()
    {
        mNfcAdapter.disableForegroundDispatch(parentActivity);
    }

    public void onNewIntent(Intent intent){
        // fetch the tag from the intent
        tag = (Tag)intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        Toast.makeText(parentActivity, Arrays.toString(tag.getId()), Toast.LENGTH_LONG).show();
    }
}
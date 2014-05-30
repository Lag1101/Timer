package com.example.Timer;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by vasiliy.lomanov on 13.05.2014.
 */
public class PrActivity extends PreferenceActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Make sure default values are applied.  In a real app, you would
        // want this in a shared function that is used to retrieve the
        // SharedPreferences wherever they are needed
        addPreferencesFromResource(R.xml.preference);
    }
}

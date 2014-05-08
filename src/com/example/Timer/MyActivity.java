package com.example.Timer;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.*;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.SystemClock;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;


public class MyActivity extends nfcReceiver {

    private Chronometer chronometer;
    private LinearLayout timeList;
    private List<Event> dates;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.timer);

        chronometer = (Chronometer)findViewById(R.id.chronometer);
        timeList = (LinearLayout)findViewById(R.id.timeList);
        dates = new ArrayList<Event>();
        loadDates();
    }

    public void onClear(View view) {
        dates.clear();
        timeList.removeAllViews();
    }
    public void onToggleClicked(View view) {

        addEventAndView("click");

        if (((ToggleButton) view).isChecked()) {
            chronometer.setBase(SystemClock.elapsedRealtime());
            chronometer.start();
        } else {
            chronometer.stop();
        }
    }


    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        saveDates();
    }
    @Override
    public void onDestroy()
    {
        saveDates();
        super.onDestroy();
    }

    void addEventAndView(String reason) {
        Event ev = new Event(Calendar.getInstance().getTime(), reason);
        dates.add( ev );
        TextView text = new TextView(this);
        text.setText(Event.encode(ev));
        timeList.addView(text);
    }

    @Override
    public void onNewIntent(Intent intent){
        // fetch the tag from the intent
        super.onNewIntent(intent);
        addEventAndView(Arrays.toString(tag.getId()));
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void saveDates() {
        SharedPreferences sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();

        String datesStr = new String();
        for(Event event : dates) {
            datesStr += Event.encode(event) + ";";
        }
        ed.putString( "dates", datesStr);
        ed.commit();
    }

    private void loadDates() {
        SharedPreferences sPref = getPreferences(MODE_PRIVATE);
        String savedText = sPref.getString("dates", "");
        String[] dateStrs = savedText.split(";");
        for(String dateStr : dateStrs) {
            try {
                Event ev = Event.decode(dateStr);
                dates.add(ev);
            } catch (Exception x){}
        }
        for(Event event : dates) {
            TextView text = new TextView(this);
            text.setText(Event.encode(event));
            timeList.addView(text);
        }
    }
}

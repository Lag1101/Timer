package com.example.Timer;

import android.app.DialogFragment;
import android.content.*;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import java.text.DateFormat;
import java.util.*;



public class MyActivity extends nfcReceiver {

    private Chronometer chronometer;
    private LinearLayout timeList;

    static final String BASE = "base";
    static final String WORK_ID = "[52, -63, 9, -63, 62, -28, 54]";
    private List<Event> dates = new ArrayList<Event>();
    private Alarm alarm = new Alarm();
    public long msForAlarm = 0;

    private SharedPreferences sp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.timer);

        chronometer = (Chronometer)findViewById(R.id.chronometer);
        timeList = (LinearLayout)findViewById(R.id.timeList);
        loadDates();

        sp = PreferenceManager.getDefaultSharedPreferences(this);
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem mi = menu.add(0, 1, 0, "Preferences");
        mi.setIntent(new Intent(this, PrActivity.class));
        return super.onCreateOptionsMenu(menu);
    }
    public void onToggleClicked(View view) {

        addEventAndView("click");

        alarm.SetAlarm(this, msForAlarm);

        if (((ToggleButton) view).isChecked()) {
            chronometer.setBase(SystemClock.elapsedRealtime());
            chronometer.start();
        } else {
            chronometer.stop();
        }
    }

    static public long dateToMs(Date date) {
        return (date.getHours()*60+date.getMinutes())*60*1000;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        try {
            String time = sp.getString("time", "");
            msForAlarm = dateToMs(DateFormat.getTimeInstance().parse(time));
        } catch (Exception x){}
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
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putLong(BASE, chronometer.getBase());

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

        // Restore state members from saved instance
        chronometer.setBase(savedInstanceState.getLong(BASE));
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
        String id = Arrays.toString(tag.getId());
        addEventAndView(id);

        if( id.equalsIgnoreCase(WORK_ID) )
            alarm.SetAlarm(this, msForAlarm);
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

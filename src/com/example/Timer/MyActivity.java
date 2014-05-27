package com.example.Timer;

import android.app.*;
import android.content.*;
import android.content.res.Configuration;
import android.os.*;
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
    public long msForAlarm = 0;
    private SharedPreferences sp;

    Messenger mService = null;
    boolean mIsBound;
    final Messenger mMessenger = new Messenger(new IncomingHandler());
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MyService.MSG_SET_INT_VALUE:
                    //textIntValue.setText("Int Message: " + msg.arg1);
                    break;
                case MyService.MSG_SET_STRING_VALUE:
                    String str1 = msg.getData().getString("str1");
                    //textStrValue.setText("Str Message: " + str1);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = new Messenger(service);
            //textStatus.setText("Attached.");
            try {
                Message msg = Message.obtain(null, MyService.MSG_REGISTER_CLIENT);
                msg.replyTo = mMessenger;
                mService.send(msg);
            } catch (RemoteException e) {
                // In this case the service has crashed before we could even do anything with it
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been unexpectedly disconnected - process crashed.
            mService = null;
            //textStatus.setText("Disconnected.");
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.timer);

        chronometer = (Chronometer)findViewById(R.id.chronometer);
        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            boolean alarmed = false;
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                long elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();

               // mNotification.setContentText(DateFormat.getTimeInstance().format(MyActivity.msToDate((int)elapsedMillis)));
              //  mNotificationManager.notify(0, mNotification.build());

                if(elapsedMillis>msForAlarm && !alarmed){
                    alarmed = true;

                    Intent newIntent = new Intent(getBaseContext(), AlarmActivity.class);
                    newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(newIntent);

                    {
                        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        vibrator.vibrate(2000);
                    }
                    //chronometer.stop();
                }
            }
        });
        timeList = (LinearLayout)findViewById(R.id.timeList);
        loadDates();

        sp = PreferenceManager.getDefaultSharedPreferences(this);
        if (!MyService.isRunning()) {
            startService( new Intent(this, MyService.class));
        }

        doBindService();

    }

    void doBindService() {
        bindService(new Intent(this, MyService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }
    void doUnbindService() {
        if (mIsBound) {
            // If we have received the service, and hence registered with it, then now is the time to unregister.
            if (mService != null) {
                try {
                    Message msg = Message.obtain(null, MyService.MSG_UNREGISTER_CLIENT);
                    msg.replyTo = mMessenger;
                    mService.send(msg);
                } catch (RemoteException e) {
                    // There is nothing special we need to do if the service has crashed.
                }
            }
            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
            //textStatus.setText("Unbinding.");
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem mi = menu.add(0, 1, 0, "Preferences");
        mi.setIntent(new Intent(this, PrActivity.class));
        return super.onCreateOptionsMenu(menu);
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

    static public long dateToMs(Date date) {
        return (date.getHours()*60+date.getMinutes())*60*1000;
    }
    static public Date msToDate(int ms) {
        Date time = new Date(ms);
        Date timeFromZero = new Date(0);
        time.setYear(time.getYear()-timeFromZero.getYear());
        time.setMonth(time.getMonth()-timeFromZero.getMonth());
        time.setDate(time.getDate()-timeFromZero.getDate());
        time.setHours(time.getHours()-timeFromZero.getHours());
        time.setMinutes(time.getMinutes()-timeFromZero.getMinutes());
        time.setSeconds(time.getSeconds()-timeFromZero.getSeconds());

        return time;
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
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
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
        try {
            doUnbindService();
        } catch (Throwable t) {
            //Log.e("MainActivity", "Failed to unbind from the service", t);
        }
        super.onDestroy();
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        //savedInstanceState.putLong(BASE, chronometer.getBase());

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

        // Restore state members from saved instance
        //chronometer.setBase(savedInstanceState.getLong(BASE));
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
        /*String id = Arrays.toString(tag.getId());
        addEventAndView(id);

        if( id.equalsIgnoreCase(WORK_ID) )
            alarm.SetAlarm(this, msForAlarm);*/
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

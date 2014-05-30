package com.example.Timer;

import android.app.Activity;
import android.content.*;
import android.content.res.Configuration;
import android.os.*;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.text.DateFormat;
import java.util.Date;
import java.util.Map;


public class MyActivity extends Activity {
    private TextView ticker;
    private TextView dateOfAlarm;
    SharedPreferences preferenceManager;

    //private nfcReceiver nfcreceiver;
    //static final String WORK_ID = "[52, -63, 9, -63, 62, -28, 54]";

    Messenger mService = null;
    boolean mIsBound;
    final Messenger mMessenger = new Messenger(new IncomingHandler());
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MyService.MSG_TICK:
                    ticker.setText((String) msg.obj);
                    break;
                case MyService.MSG_ALARM_TIME:
                    String strDateToAlarm = DateFormat.getDateTimeInstance().format((Date) msg.obj);
                    dateOfAlarm.setText( strDateToAlarm );

                    ((ToggleButton)findViewById(R.id.toggleButton)).setChecked(true);
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
            mService = null;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timer);

        ticker = (TextView)findViewById(R.id.ticker);
        dateOfAlarm = (TextView)findViewById(R.id.dateOfAlarm);
        preferenceManager = PreferenceManager.getDefaultSharedPreferences(this);

        startService( new Intent(this, MyService.class));
        doBindService();

        //nfcreceiver = new nfcReceiver(this);
    }

    private void sendMessageToService(int command, String text) {
        if (mIsBound) {
            if (mService != null) {
                try {
                    Message msg = Message.obtain(null, command, text);
                    msg.replyTo = mMessenger;
                    mService.send(msg);
                } catch (RemoteException e) {
                }
            }
        }
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
                }
            }
            unbindService(mConnection);
            mIsBound = false;
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem mi = menu.add(0, 1, 0, "Preferences");
        mi.setIntent(new Intent(this, PrActivity.class));
        return super.onCreateOptionsMenu(menu);
    }
    public void onToggleClicked(View view) {
        if (((ToggleButton) view).isChecked()) {

            Map<String, ?> all = preferenceManager.getAll();

            //all.get("timePick")
            long msForAlarm = 3*60*60*1000+preferenceManager.getLong("timePick", 0);
            String strDateToAlarm = DateFormat.getDateTimeInstance().format(new Date().getTime() + msForAlarm);
            dateOfAlarm.setText( strDateToAlarm );

            sendMessageToService(MyService.MSG_START, strDateToAlarm);
        } else {
            sendMessageToService(MyService.MSG_STOP, "");
        }
    }

    @Override
    public void onResume()
    {
        //nfcreceiver.onResume();
        super.onResume();
    }
    @Override
    public void onPause()
    {
        //nfcreceiver.onPause();
        super.onPause();
    }
    @Override
    public void onDestroy()
    {
        try {
            doUnbindService();
        } catch (Throwable t) {
            //Log.e("MainActivity", "Failed to unbind from the service", t);
        }


        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}

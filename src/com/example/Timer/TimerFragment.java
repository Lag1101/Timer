package com.example.Timer;

import android.app.Activity;
import android.content.*;
import android.content.res.Configuration;
import android.os.*;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SlidingPaneLayout;
import android.util.Log;
import android.view.*;
import android.widget.*;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


public class TimerFragment extends Fragment {

    final String LOG_TAG = "myLogs";

    private TextView ticker;
    private TextView dateOfAlarm;
    private ToggleButton toggle;
    private SharedPreferences preferenceManager;

    private LinearLayout datumLayout;

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

                    ((ToggleButton)getActivity().findViewById(R.id.toggleButton)).setChecked(true);
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

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d(LOG_TAG, "Fragment1 onAttach");


    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "Fragment1 onCreate");

        getActivity().startService( new Intent(getActivity(), MyService.class));
        doBindService();
    }

    void doBindService() {
        getActivity().bindService(new Intent(getActivity(), MyService.class), mConnection, Context.BIND_AUTO_CREATE);
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
            getActivity().unbindService(mConnection);
            mIsBound = false;
        }
    }

    public void onToggleClicked(View view) {
        if (((ToggleButton) view).isChecked()) {

            Map<String, ?> all = preferenceManager.getAll();

            //all.get("timePick")
            long msForAlarm = 3*60*60*1000+preferenceManager.getLong("timePick", 0);
            Date now = new Date();
            {
                TextView textView = new TextView(getActivity());
                textView.setText( DateFormat.getDateTimeInstance().format(now) );

                datumLayout =  (LinearLayout)getActivity().findViewById(R.id.datumLayout);
                datumLayout.addView(textView);
            }
            String strDateToAlarm = DateFormat.getDateTimeInstance().format(now.getTime() + msForAlarm);
            dateOfAlarm.setText( strDateToAlarm );

            sendMessageToService(MyService.MSG_START, strDateToAlarm);
        } else {
            sendMessageToService(MyService.MSG_STOP, "");
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Fragment1 onCreateView");
        return inflater.inflate(R.layout.timer_fragment, null);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(LOG_TAG, "Fragment1 onActivityCreated");

        ticker = (TextView)getActivity().findViewById(R.id.ticker);
        dateOfAlarm = (TextView)getActivity().findViewById(R.id.dateOfAlarm);
        toggle = (ToggleButton)getActivity().findViewById((R.id.toggleButton));
        toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onToggleClicked(view);
            }
        });

        preferenceManager = PreferenceManager.getDefaultSharedPreferences(getActivity());


    }

    public void onStart() {
        super.onStart();
        Log.d(LOG_TAG, "Fragment1 onStart");
    }

    public void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "Fragment1 onResume");
    }

    public void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "Fragment1 onPause");
    }

    public void onStop() {
        super.onStop();
        Log.d(LOG_TAG, "Fragment1 onStop");
    }

    public void onDestroyView() {
        super.onDestroyView();
        Log.d(LOG_TAG, "Fragment1 onDestroyView");
    }

    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "Fragment1 onDestroy");

        try {
            doUnbindService();
        } catch (Throwable t) {
            //Log.e("MainActivity", "Failed to unbind from the service", t);
        }
    }

    public void onDetach() {
        super.onDetach();
        Log.d(LOG_TAG, "Fragment1 onDetach");
    }
    /*private TextView ticker;
    private TextView dateOfAlarm;
    private LinearLayout datumLayout;

    //static final String WORK_ID = "[52, -63, 9, -63, 62, -28, 54]";



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setContentView(R.layout.timer);

        ticker = (TextView)getActivity().findViewById(R.id.ticker);
        dateOfAlarm = (TextView)getActivity().findViewById(R.id.dateOfAlarm);

        getActivity().startService( new Intent(getActivity(), MyService.class));
        doBindService();

        History history = new History(getActivity());

        //datumLayout = (ScrollView)findViewById(R.id.datum);
        *//*for ( Date date : history.events ) {
            TextView textView = new TextView(this);
            textView.setText( DateFormat.getDateTimeInstance().format(date) );
            datumLayout.addView(textView);
        }*//*
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }*/
}

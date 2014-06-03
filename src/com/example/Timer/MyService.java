package com.example.Timer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.*;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MyService extends Service {
    private Date alarmTime = null;
    private mAlarm alarm = null;
    private static boolean isRunning = false;

    private NotificationManager mNotificationManager;
    private Notification.Builder mNotifyBuilder;

    ArrayList<Messenger> mClients = new ArrayList<Messenger>(); // Keeps track of all current registered clients.

    public History.Writer history;

    static final int MSG_REGISTER_CLIENT = 1;
    static final int MSG_UNREGISTER_CLIENT = 2;
    static final int MSG_START = 3;
    static final int MSG_STOP = 4;
    static final int MSG_TICK = 5;
    static final int MSG_ALARM_TIME = 6;
    final Messenger mMessenger = new Messenger(new IncomingHandler()); // Target we publish for clients to send messages to IncomingHandler.

    class mAlarm extends Alarm {
        @Override
        public void onTick(long elapsed, Date now) {
            String time = DateFormat.getTimeInstance().format(elapsed - 3 * 60 * 60 * 1000);
            mNotifyBuilder.setContentText(time);
            mNotificationManager.notify(0, mNotifyBuilder.build());
            for (int i = mClients.size() - 1; i >= 0; i--) {
                try {
                    // Send data as an Integer
                    mClients.get(i).send(Message.obtain(null, MSG_TICK, time));
                } catch (RemoteException e) {
                    // The client is dead. Remove it from the list; we are going through the list from back to front so this is safe to do inside the loop.
                    mClients.remove(i);
                }
            }

            if (alarmTime != null && now.after(alarmTime)) {
                alarmTime = null;

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
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }
    class IncomingHandler extends Handler { // Handler of incoming messages from clients.
        @Override
        public void handleMessage(Message msg) {
            try {
                switch (msg.what) {
                    case MSG_REGISTER_CLIENT:
                        mClients.add(msg.replyTo);
                        if(alarmTime != null)
                            msg.replyTo.send(Message.obtain(null, MSG_ALARM_TIME, alarmTime));
                        break;
                    case MSG_UNREGISTER_CLIENT:
                        mClients.remove(msg.replyTo);
                        history.writeAll(MyService.this);
                        break;
                    case MSG_START:
                        alarmTime = DateFormat.getDateTimeInstance().parse((String) msg.obj);
                        Toast.makeText(MyService.this, (String) msg.obj, Toast.LENGTH_SHORT).show();

                        if( alarm == null ) alarm = new mAlarm();
                        alarm.Start();
                        msg.replyTo.send(Message.obtain(null, MSG_ALARM_TIME, alarmTime));

                        history.addNow();

                        break;
                    case MSG_STOP:
                        alarm.Stop();
                        alarm = null;

                        history.addNow();
                        break;
                    default:
                        super.handleMessage(msg);
                }
            } catch (Exception ex){
                Toast.makeText(MyService.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, "Service started", Toast.LENGTH_SHORT).show();
        //showNotification();
        isRunning = true;

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, MyActivity.class);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        mNotifyBuilder = new Notification.Builder(this)
                .setContentTitle("Worked")
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentIntent(resultPendingIntent);

        history = new History.Writer(this); // TODO load from internal storage
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Received start id " + startId + ": " + intent, Toast.LENGTH_SHORT).show();
        return START_STICKY; // run until explicitly stopped.
    }

    public static boolean isRunning()
    {
        return isRunning;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        history.writeAll(this);
        mNotifyBuilder.setContentText("Destroyed");
        mNotificationManager.notify(0, mNotifyBuilder.build());
        mNotificationManager.cancel(R.string.service_started); // Cancel the persistent notification.
        Toast.makeText(this, "Service stopped", Toast.LENGTH_SHORT).show();
        isRunning = false;
    }
}
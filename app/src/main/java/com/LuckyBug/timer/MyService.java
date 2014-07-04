package com.LuckyBug.timer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.widget.Toast;

import java.text.DateFormat;

public class MyService extends Service {

    final Messenger mMessenger = new Messenger(new IncomingHandler());
    private static boolean isRunning = false;

    static final int MSG_REGISTER_CLIENT = 1;
    static final int MSG_UNREGISTER_CLIENT = 2;

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, "Service started", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isRunning = true;
        Toast.makeText(this, "Received start id " + startId + ": " + intent, Toast.LENGTH_SHORT).show();
        return START_STICKY; // run until explicitly stopped.
    }


    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service stopped", Toast.LENGTH_SHORT).show();
        isRunning = false;
        super.onDestroy();
    }

    class IncomingHandler extends Handler { // Handler of incoming messages from clients.
        @Override
        public void handleMessage(Message msg) {
            try {
                switch (msg.what) {
                    case MSG_REGISTER_CLIENT:
                        //mClients.add(msg.replyTo);
                        break;
                    case MSG_UNREGISTER_CLIENT:
                        //mClients.remove(msg.replyTo);
                        break;
                    default:
                        super.handleMessage(msg);
                }
            } catch (Exception ex){
                Toast.makeText(MyService.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}

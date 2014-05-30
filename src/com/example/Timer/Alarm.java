package com.example.Timer;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by vasiliy.lomanov on 27.05.2014.
 */
abstract public class Alarm {

    private Timer timer = null;
    private long startTime = 0;

    public Alarm() {

    }

    public abstract void onTick(long elapsedMs, Date now);

    public void Start() {
        startTime = (new Date()).getTime();
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask(){ public void run() {
            Date now = new Date();
            long elapsed = now.getTime() - startTime;
            onTick(elapsed, now);
        }}, 0, 1000);
    }

    public void Stop() {
        timer.purge();
        timer.cancel();
    }
}


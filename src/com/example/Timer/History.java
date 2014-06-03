package com.example.Timer;

import android.content.Context;
import android.util.ArrayMap;
import android.widget.Toast;

import java.io.*;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by vasiliy.lomanov on 02.06.2014.
 */
public class History {
    public static String historyFile = "history";

    //public static enum EV implements java.io.Serializable {START, STOP}

    public ArrayList<Date> events;

    History(Context context) {
        events = new ArrayList<Date>();
        readAll(context);
    }

    public void addNow() {
        events.add(new Date());
    }

    public void readAll(Context context) {
        try {
            FileInputStream fis = context.openFileInput(historyFile);
            ObjectInputStream in = new ObjectInputStream(fis);
            events = ( ArrayList<Date> )in.readObject();
            in.close();
            fis.close();
        } catch(Exception i) {
            Toast.makeText(context, i.getMessage(), Toast.LENGTH_SHORT).show();
            i.printStackTrace();
        }
    }

    public final static class Writer extends History {
        Writer(Context context) {
            super(context);
        }
        public void writeAll(Context context) {
            try {
                FileOutputStream fos = context.openFileOutput(historyFile, Context.MODE_MULTI_PROCESS );
                ObjectOutputStream out = new ObjectOutputStream(fos);
                out.writeObject(events);
                out.close();
                fos.close();
            } catch(Exception i) {
                Toast.makeText(context, i.getMessage(), Toast.LENGTH_SHORT).show();
                i.printStackTrace();
            }
        }
    }
}

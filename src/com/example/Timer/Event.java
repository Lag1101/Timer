package com.example.Timer;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by vasiliy.lomanov on 08.05.2014.
 */
public class Event {
    private Date date;
    private String reason;
    public Event(Date date, String reason){
        this.date = date;
        this.reason = reason;
    }
    static public String encode(Event e) {
        return DateFormat.getDateTimeInstance().format(e.date) + "--" + e.reason;
    }
    static public Event decode(String s) throws java.text.ParseException {
        String[] strs = s.split("--");
        Date d = DateFormat.getDateTimeInstance().parse(strs[0]);
        return new Event(d, strs[1]);
    }
}

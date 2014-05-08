package com.example.Timer;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by vasiliy.lomanov on 08.05.2014.
 */
public class Event {
    static private String separator = "--";
    private Date date;
    private String reason;
    public Event(Date date, String reason){
        this.date = date;
        this.reason = reason;
    }
    static public String encode(Event e) {
        return DateFormat.getDateTimeInstance().format(e.date) + Event.separator + e.reason;
    }
    static public Event decode(String s) throws java.text.ParseException {
        String[] eventStrings = s.split(Event.separator);
        Date d = DateFormat.getDateTimeInstance().parse(eventStrings[0]);
        return new Event(d, eventStrings[1]);
    }
}

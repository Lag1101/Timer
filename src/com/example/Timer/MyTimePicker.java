package com.example.Timer;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by vasiliy.lomanov on 12.05.2014.
 */
public class MyTimePicker extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Create a new instance of DatePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, 0, 0, true);
    }

    public void onTimeSet(TimePicker view, int hour, int minute) {
        MyActivity act = (MyActivity)getActivity();

        act.msForAlarm = (hour*60 + minute) * 60 * 1000;
    }
}
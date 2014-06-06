package com.example.Timer;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by vasiliy.lomanov on 05.06.2014.
 */
public class DatumFragment extends Fragment{
    final String LOG_TAG = "myLogs";
    private LinearLayout datumLayout;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d(LOG_TAG, "Fragment2 onAttach");
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "Fragment2 onCreate");
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Fragment2 onCreateView");
        return inflater.inflate(R.layout.datum_fragment, null) ;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(LOG_TAG, "Fragment2 onActivityCreated");


        datumLayout = (LinearLayout)getActivity().findViewById(R.id.datumLayout);

        History history = new History(getActivity());
        for ( Date date : history.events ) {
            TextView textView = new TextView(getActivity());
            textView.setText( DateFormat.getDateTimeInstance().format(date) );
            datumLayout.addView(textView);
        }
    }

    public void onStart() {
        super.onStart();
        Log.d(LOG_TAG, "Fragment2 onStart");
    }

    public void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "Fragment2 onResume");
    }

    public void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "Fragment2 onPause");
    }

    public void onStop() {
        super.onStop();
        Log.d(LOG_TAG, "Fragment2 onStop");
    }

    public void onDestroyView() {
        super.onDestroyView();
        Log.d(LOG_TAG, "Fragment2 onDestroyView");
    }

    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "Fragment2 onDestroy");
    }

    public void onDetach() {
        super.onDetach();
        Log.d(LOG_TAG, "Fragment2 onDetach");
    }
}

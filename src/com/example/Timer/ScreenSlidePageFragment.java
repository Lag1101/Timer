package com.example.Timer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by vasiliy.lomanov on 05.06.2014.
 */
public class ScreenSlidePageFragment extends Fragment {

    private int xml;

    public ScreenSlidePageFragment(int xml) {
        this.xml = xml;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(
                xml, container, false);

        return rootView;
    }
}
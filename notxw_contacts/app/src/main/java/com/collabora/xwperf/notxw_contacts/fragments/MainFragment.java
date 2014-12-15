package com.collabora.xwperf.notxw_contacts.fragments;

/*
 * Copyright 2014 Intel Corporation. All rights reserved.
 * License: BSD-3-clause-Intel, see LICENSE.txt
 */

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;

import com.collabora.xwperf.notxw_contacts.R;

import org.androidannotations.annotations.EFragment;

@EFragment(R.layout.fragment_main)
public class MainFragment extends Fragment {
    //Holder for our tabs
    public static Fragment newInstance() {
        return MainFragment_.builder().build();
    }

    private TabHost tabHost;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_main, container, false);
        tabHost = (TabHost) rootview.findViewById(android.R.id.tabhost);
        tabHost.setup();

        TabHost.TabSpec spec = tabHost.newTabSpec("tag_fav");
        spec.setContent(R.id.tab_fav);
        spec.setIndicator(getString(R.string.favorites), getResources().getDrawable(R.drawable.ic_favorite));
        tabHost.addTab(spec);


        spec = tabHost.newTabSpec("tag_contact");
        spec.setContent(R.id.tab_all);
        spec.setIndicator(getString(R.string.all), getResources().getDrawable(R.drawable.ic_contact));
        tabHost.addTab(spec);

        spec = tabHost.newTabSpec("tag_groups");
        spec.setContent(R.id.tab_groups);
        spec.setIndicator(getString(R.string.groups), getResources().getDrawable(R.drawable.ic_groups));
        tabHost.addTab(spec);

//        tabHost.setCurrentTab(0);


        return rootview;
    }


}

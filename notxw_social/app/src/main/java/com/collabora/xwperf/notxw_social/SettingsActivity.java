package com.collabora.xwperf.notxw_social;

/*
 * Copyright 2014 Intel Corporation. All rights reserved.
 * License: BSD-3-clause-Intel, see LICENSE.txt
 */

import android.os.Bundle;

import org.androidannotations.annotations.EActivity;

@EActivity
public class SettingsActivity extends BaseActivity {

    @Override
    public void pushFragment(Bundle savedInstanceState) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, SettingsFragment.newInstance())
                    .commit();
        }
    }
}

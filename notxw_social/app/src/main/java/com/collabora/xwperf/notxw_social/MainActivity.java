package com.collabora.xwperf.notxw_social;

/*
 * Copyright 2014 Intel Corporation. All rights reserved.
 * License: BSD-3-clause-Intel, see LICENSE.txt
 */

import android.os.Bundle;
import android.view.View;

import org.androidannotations.annotations.EActivity;

@EActivity
public class MainActivity extends BaseActivity implements View.OnClickListener {

    @Override
    public void pushFragment(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, MainFragment.newInstance())
                    .commit();
        }
    }
}

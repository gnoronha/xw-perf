package com.collabora.xwperf.notxw_contacts.fragments;

/*
 * Copyright 2014 Intel Corporation. All rights reserved.
 * License: BSD-3-clause-Intel, see LICENSE.txt
 */

import android.support.v4.app.Fragment;

import com.collabora.xwperf.notxw_contacts.R;

import org.androidannotations.annotations.EFragment;

@EFragment(R.layout.fragment_groups)
public class GroupsFragment extends BaseTabFragment {

    //nothing to do here
    public static Fragment newInstance() {
        return GroupsFragment_.builder().build();
    }

    @Override
    public void setSearchTerm(String searchTerm) {
        //nothing
    }
}

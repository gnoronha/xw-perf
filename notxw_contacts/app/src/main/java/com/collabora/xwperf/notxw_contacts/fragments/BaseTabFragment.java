package com.collabora.xwperf.notxw_contacts.fragments;
/*
 * Copyright 2014 Intel Corporation. All rights reserved.
 * License: BSD-3-clause-Intel, see LICENSE.txt
 */

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;

import com.collabora.xwperf.notxw_contacts.adapters.ContactsAdapter;

public abstract class BaseTabFragment extends Fragment implements ITabScrollHider {

    protected ContactsAdapter.OnItemClickListener contactClickListener;
    protected RecyclerView.OnScrollListener scrollListener;

    @Override
    public void setScrollListener(RecyclerView.OnScrollListener scrollListener) {
        this.scrollListener = scrollListener;
    }

    @Override
    public void setContactClickListener(ContactsAdapter.OnItemClickListener listener) {
        contactClickListener = listener;
    }
}
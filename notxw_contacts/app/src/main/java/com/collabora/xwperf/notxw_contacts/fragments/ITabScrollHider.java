package com.collabora.xwperf.notxw_contacts.fragments;

/*
 * Copyright 2014 Intel Corporation. All rights reserved.
 * License: BSD-3-clause-Intel, see LICENSE.txt
 */

import android.support.v7.widget.RecyclerView;

import com.collabora.xwperf.notxw_contacts.adapters.ContactsAdapter;

public interface ITabScrollHider {
    static final String SEARCH_TERM = "searchTerm";

    public void setScrollListener(RecyclerView.OnScrollListener scrollListener);

    public void setSearchTerm(String searchTerm);

    public void setContactClickListener(ContactsAdapter.OnItemClickListener listener);
}

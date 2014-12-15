package com.collabora.xwperf.notxw_contacts.fragments;

import android.support.v7.widget.RecyclerView;

public interface ITabScrollHider {
    static final String SEARCH_TERM = "searchTerm";

    public void setScrollListener(RecyclerView.OnScrollListener scrollListener);

    public void setSearchTerm(String searchTerm);
}

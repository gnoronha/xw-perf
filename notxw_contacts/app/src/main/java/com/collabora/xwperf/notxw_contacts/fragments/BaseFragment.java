package com.collabora.xwperf.notxw_contacts.fragments;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;

import com.collabora.xwperf.notxw_contacts.adapters.ContactsAdapter;

public abstract class BaseFragment extends Fragment implements ITabScrollHider {

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
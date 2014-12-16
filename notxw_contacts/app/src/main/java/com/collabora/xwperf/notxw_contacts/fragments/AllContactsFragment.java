package com.collabora.xwperf.notxw_contacts.fragments;

/*
 * Copyright 2014 Intel Corporation. All rights reserved.
 * License: BSD-3-clause-Intel, see LICENSE.txt
 */

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.collabora.xwperf.notxw_contacts.R;
import com.collabora.xwperf.notxw_contacts.adapters.ContactsAdapter;
import com.collabora.xwperf.notxw_contacts.data.ContactsContentProvider;
import com.collabora.xwperf.notxw_contacts.data.ContactsStore;
import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

@EFragment(R.layout.fragment_contacts)
public class AllContactsFragment extends Fragment implements ITabScrollHider {
    private static final String TAG = AllContactsFragment.class.getSimpleName();
    private RecyclerView.OnScrollListener scrollListener;

    public static Fragment newInstance() {
        return AllContactsFragment_.builder().build();
    }

    private LinearLayoutManager layoutManager;
    private ContactsAdapter adapter;


    @ViewById(R.id.recycler_view)
    RecyclerView recyclerView;

    @AfterViews
    void init() {
        //init loader
        recyclerView.setHasFixedSize(false);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ContactsAdapter(getActivity(), null);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setOnScrollListener(scrollListener);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        getLoaderManager().restartLoader(11, null, contactsLoader);
    }

    private LoaderManager.LoaderCallbacks<Cursor> contactsLoader = new LoaderManager.LoaderCallbacks<Cursor>() {

        @Override
        public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
            return new CursorLoader(getActivity(), ContactsContentProvider.contentUri(ContactsStore.ContactTable.CONTENT_URI), null, null, null, ContactsStore.ContactTable.NAME);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
            adapter.changeCursor(cursor);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> cursorLoader) {
            adapter.changeCursor(null);
        }
    };

    @Override
    public void setScrollListener(RecyclerView.OnScrollListener scrollListener) {
        this.scrollListener = scrollListener;
    }
}

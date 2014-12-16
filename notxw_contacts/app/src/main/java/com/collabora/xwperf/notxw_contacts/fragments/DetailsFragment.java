package com.collabora.xwperf.notxw_contacts.fragments;

/*
 * Copyright 2014 Intel Corporation. All rights reserved.
 * License: BSD-3-clause-Intel, see LICENSE.txt
 */

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;


import com.collabora.xwperf.notxw_contacts.R;
import com.collabora.xwperf.notxw_contacts.data.ContactModel;
import com.collabora.xwperf.notxw_contacts.data.ContactsContentProvider;
import com.collabora.xwperf.notxw_contacts.data.ContactsStore;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;

import java.util.Date;

@EFragment(R.layout.fragment_contact_details)
public class DetailsFragment extends Fragment {
    private static final String TAG = DetailsFragment.class.getSimpleName();

    public static Fragment newInstance(int contactId) {
        return DetailsFragment_.builder().contactId(contactId).build();

    }

    @FragmentArg
    int contactId;

    @AfterViews
    void init() {
        getLoaderManager().initLoader(contactId, null, contactDetailsLoader);
    }

    private LoaderManager.LoaderCallbacks<Cursor> contactDetailsLoader = new LoaderManager.LoaderCallbacks<Cursor>() {

        @Override
        public Loader<Cursor> onCreateLoader(int itemId, Bundle bundle) {
            // we will use loader id as contact id
            return new CursorLoader(getActivity(), ContactsContentProvider.contentUri(ContactsStore.ContactTable.CONTENT_URI, itemId), null, null, null, null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
            if (cursor.moveToFirst()) {
                ContactModel contactModel = new ContactModel();
                contactModel.setContactId(cursor.getInt(0));//_id is always 0
                contactModel.setName(cursor.getString(cursor.getColumnIndex(ContactsStore.ContactTable.NAME)));
                contactModel.setEmail(cursor.getString(cursor.getColumnIndex(ContactsStore.ContactTable.EMAIL)));
                contactModel.setPhone(cursor.getString(cursor.getColumnIndex(ContactsStore.ContactTable.PHONE)));
                contactModel.setFavorite(cursor.getInt(cursor.getColumnIndex(ContactsStore.ContactTable.FAVORITE)) == 1);
                contactModel.setAvatarResId(cursor.getInt(cursor.getColumnIndex(ContactsStore.ContactTable.AVATAR)));
                Long birthday = cursor.getLong(cursor.getColumnIndex(ContactsStore.ContactTable.BIRTHDAY));
                if (birthday != null && birthday > 0)
                    contactModel.setBirthday(new Date(birthday));

                showItem(contactModel);
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> cursorLoader) {
            //nothing
        }
    };

    private void showItem(ContactModel contactModel) {
        //show
    }
}

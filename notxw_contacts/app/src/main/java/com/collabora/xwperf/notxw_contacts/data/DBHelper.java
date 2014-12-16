package com.collabora.xwperf.notxw_contacts.data;

/*
 * Copyright 2014 Intel Corporation. All rights reserved.
 * License: BSD-3-clause-Intel, see LICENSE.txt
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.Date;

public class DBHelper {

    //CRUD methods
    public static void insertGeneratedContacts(Context context, ArrayList<ContactModel> contacts) {
        ContentValues[] cvs = new ContentValues[contacts.size()];
        int i = 0;
        for (ContactModel contactModel : contacts) {
            ContentValues cv = new ContentValues();
            cv.put(ContactsStore.ContactTable.NAME, contactModel.getName());
            cv.put(ContactsStore.ContactTable.AVATAR, contactModel.getAvatarResId());
            if (contactModel.getBirthday() != null) {
                cv.put(ContactsStore.ContactTable.BIRTHDAY, contactModel.getBirthday().getTime());
            }
            cv.put(ContactsStore.ContactTable.EMAIL, contactModel.getEmail());
            cv.put(ContactsStore.ContactTable.FAVORITE, contactModel.isFavorite() ? 1 : 0);
            cv.put(ContactsStore.ContactTable.PHONE, contactModel.getPhone());
            cvs[i++] = cv;
        }
        context.getContentResolver().bulkInsert(ContactsContentProvider.contentUriBulkInsert(ContactsStore.ContactTable.CONTENT_URI, ContactsContentProvider.BulkInsertConflictMode.INSERT), cvs);
    }

    public static ContactModel getItem(Context context, int itemId) {
        Cursor cursor = context.getContentResolver().query(ContactsContentProvider.contentUri(ContactsStore.ContactTable.CONTENT_URI, itemId), null, null, null, null);
        ContactModel contactModel = null;
        if (cursor.moveToFirst()) {
            contactModel = new ContactModel();
            contactModel.setContactId(cursor.getInt(0));//_id is always 0
            contactModel.setName(cursor.getString(cursor.getColumnIndex(ContactsStore.ContactTable.NAME)));
            contactModel.setEmail(cursor.getString(cursor.getColumnIndex(ContactsStore.ContactTable.EMAIL)));
            contactModel.setPhone(cursor.getString(cursor.getColumnIndex(ContactsStore.ContactTable.PHONE)));
            contactModel.setFavorite(cursor.getInt(cursor.getColumnIndex(ContactsStore.ContactTable.FAVORITE)) == 1);
            contactModel.setAvatarResId(cursor.getInt(cursor.getColumnIndex(ContactsStore.ContactTable.AVATAR)));
            Long birthday = cursor.getLong(cursor.getColumnIndex(ContactsStore.ContactTable.BIRTHDAY));
            if (birthday != null && birthday > 0)
                contactModel.setBirthday(new Date(birthday));
        }
        cursor.close();
        return contactModel;
    }

    public static void clearDB(Context context) {
        //delete all data from table
        context.getContentResolver().delete(ContactsContentProvider.contentUriNoNotify(ContactsStore.ContactTable.CONTENT_URI), null, null);
    }

    public static void changeFavoriteValues(Context context, int newValue, Integer itemId) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ContactsStore.ContactTable.FAVORITE, newValue);
        context.getContentResolver().update(ContactsContentProvider.contentUri(ContactsStore.ContactTable.CONTENT_URI, itemId), contentValues, null, null);
    }
}

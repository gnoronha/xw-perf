package com.collabora.xwperf.notxw_contacts.adapters;
/*
 * Copyright (C) 2013 The Android Open Source Project
 * Copyright (C) 2014 Intel Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;

/**
 * Provide a {@link android.support.v7.widget.RecyclerView.Adapter} implementation with cursor
 * support.
 * <p/>
 * Child classes only need to implement {@link #onCreateViewHolder(android.view.ViewGroup, int)} and
 * {@link #onBindViewHolderCursor(android.support.v7.widget.RecyclerView.ViewHolder, android.database.Cursor)}.
 * <p/>
 * This class does not implement deprecated fields and methods from CursorAdapter! Incidentally,
 * only {@link android.widget.CursorAdapter#FLAG_REGISTER_CONTENT_OBSERVER} is available, so the
 * flag is implied, and only the Adapter behavior using this flag has been ported.
 *
 * @param <VH> {@inheritDoc}
 * @see android.support.v7.widget.RecyclerView.Adapter
 * @see android.widget.CursorAdapter
 * @see android.widget.Filterable
 */
public abstract class ContactsCursorAdapter<VH
        extends android.support.v7.widget.RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    private boolean mDataValid;
    private int mRowIDColumn;
    private Cursor mCursor;
    private ChangeObserver mChangeObserver;
    private DataSetObserver mDataSetObserver;

    public ContactsCursorAdapter(Cursor cursor) {
        init(cursor);
    }

    void init(Cursor c) {
        boolean cursorPresent = c != null;
        mCursor = c;
        mDataValid = cursorPresent;
        mRowIDColumn = cursorPresent ? c.getColumnIndexOrThrow("_id") : -1;

        mChangeObserver = new ChangeObserver();
        mDataSetObserver = new MyDataSetObserver();

        if (cursorPresent) {
            if (mChangeObserver != null) c.registerContentObserver(mChangeObserver);
            if (mDataSetObserver != null) c.registerDataSetObserver(mDataSetObserver);
        }
    }

    /**
     * This method will move the Cursor to the correct position and call
     * {@link #onBindViewHolderCursor(android.support.v7.widget.RecyclerView.ViewHolder,
     * android.database.Cursor)}.
     *
     * @param holder {@inheritDoc}
     * @param i      {@inheritDoc}
     */
    @Override
    public void onBindViewHolder(VH holder, int i) {
        if (!mDataValid) {
            throw new IllegalStateException("this should only be called when the cursor is valid");
        }
        if (!mCursor.moveToPosition(i)) {
            throw new IllegalStateException("couldn't move cursor to position " + i);
        }
        onBindViewHolderCursor(holder, mCursor);
    }

    /**
     * See {@link android.widget.CursorAdapter#bindView(android.view.View, android.content.Context,
     * android.database.Cursor)},
     * {@link #onBindViewHolder(android.support.v7.widget.RecyclerView.ViewHolder, int)}
     *
     * @param holder View holder.
     * @param cursor The cursor from which to get the data. The cursor is already
     *               moved to the correct position.
     */
    public abstract void onBindViewHolderCursor(VH holder, Cursor cursor);

    @Override
    public int getItemCount() {
        if (mDataValid && mCursor != null) {
            return mCursor.getCount();
        } else {
            return 0;
        }
    }

    /**
     * @see android.widget.ListAdapter#getItemId(int)
     */
    @Override
    public long getItemId(int position) {
        if (mDataValid && mCursor != null) {
            if (mCursor.moveToPosition(position)) {
                return mCursor.getLong(mRowIDColumn);
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

    public Cursor getCursor() {
        return mCursor;
    }

    /**
     * Change the underlying cursor to a new cursor. If there is an existing cursor it will be
     * closed.
     *
     * @param cursor The new cursor to be used
     */
    public void changeCursor(Cursor cursor) {
        Cursor old = swapCursor(cursor);
        if (old != null) {
            old.close();
        }
    }

    /**
     * Swap in a new Cursor, returning the old Cursor.  Unlike
     * {@link #changeCursor(Cursor)}, the returned old Cursor is <em>not</em>
     * closed.
     *
     * @param newCursor The new cursor to be used.
     * @return Returns the previously set Cursor, or null if there wasa not one.
     * If the given new Cursor is the same instance is the previously set
     * Cursor, null is also returned.
     */
    public Cursor swapCursor(Cursor newCursor) {
        if (newCursor == mCursor) {
            return null;
        }
        Cursor oldCursor = mCursor;
        if (oldCursor != null) {
            if (mChangeObserver != null) oldCursor.unregisterContentObserver(mChangeObserver);
            if (mDataSetObserver != null) oldCursor.unregisterDataSetObserver(mDataSetObserver);
        }
        mCursor = newCursor;
        if (newCursor != null) {
            if (mChangeObserver != null) newCursor.registerContentObserver(mChangeObserver);
            if (mDataSetObserver != null) newCursor.registerDataSetObserver(mDataSetObserver);
            mRowIDColumn = newCursor.getColumnIndexOrThrow("_id");
            mDataValid = true;
            // notify the observers about the new cursor
            notifyDataSetChanged();
        } else {
            mRowIDColumn = -1;
            mDataValid = false;
            // notify the observers about the lack of a data set
            // notifyDataSetInvalidated();
            notifyItemRangeRemoved(0, getItemCount());
        }
        return oldCursor;
    }

    /**
     * <p>Converts the cursor into a CharSequence. Subclasses should override this
     * method to convert their results. The default implementation returns an
     * empty String for null values or the default String representation of
     * the value.</p>
     *
     * @param cursor the cursor to convert to a CharSequence
     * @return a CharSequence representing the value
     */
    public CharSequence convertToString(Cursor cursor) {
        return cursor == null ? "" : cursor.toString();
    }


    /**
     * Called when the {@link ContentObserver} on the cursor receives a change notification.
     * Can be implemented by sub-class.
     *
     * @see ContentObserver#onChange(boolean)
     */
    protected void onContentChanged() {

    }

    private class ChangeObserver extends ContentObserver {
        public ChangeObserver() {
            super(new Handler());
        }

        @Override
        public boolean deliverSelfNotifications() {
            return true;
        }

        @Override
        public void onChange(boolean selfChange) {
            onContentChanged();
        }
    }

    private class MyDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            mDataValid = true;
            notifyDataSetChanged();
        }

        @Override
        public void onInvalidated() {
            mDataValid = false;
            notifyItemRangeRemoved(0, getItemCount());
        }
    }
}

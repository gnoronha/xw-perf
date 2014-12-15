package com.collabora.xwperf.notxw_contacts.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.collabora.xwperf.notxw_contacts.R;
import com.collabora.xwperf.notxw_contacts.adapters.ContactsAdapter;
import com.collabora.xwperf.notxw_contacts.data.ContactsContentProvider;
import com.collabora.xwperf.notxw_contacts.data.ContactsStore;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

@EFragment(R.layout.fragment_contacts)
public class FavoritesFragment extends Fragment implements ITabScrollHider {
    private static final String TAG = FavoritesFragment.class.getSimpleName();
    private RecyclerView.OnScrollListener scrollListener;

    public static Fragment newInstance() {
        return FavoritesFragment_.builder().build();
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
        Log.d(TAG," on view created");
        getActivity().getSupportLoaderManager().restartLoader(13, null, favLoader);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG," on resume");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG," on create");
    }

    private LoaderManager.LoaderCallbacks<Cursor> favLoader = new LoaderManager.LoaderCallbacks<Cursor>() {

        @Override
        public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

            String select = ContactsStore.ContactTable.FAVORITE + " = ? ";
            String[] selectArgs = new String[]{String.valueOf(1)};
            return new CursorLoader(getActivity(), ContactsContentProvider.contentUri(ContactsStore.ContactTable.CONTENT_URI), null, select, selectArgs, ContactsStore.ContactTable.NAME);
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

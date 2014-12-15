package com.collabora.xwperf.notxw_contacts.fragments;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;

import com.collabora.xwperf.notxw_contacts.R;

import org.androidannotations.annotations.EFragment;

@EFragment(R.layout.fragment_groups)
public class GroupsFragment extends Fragment implements ITabScrollHider {
    private static final String TAG = FavoritesFragment.class.getSimpleName();

    //nothing to do here
    public static Fragment newInstance() {
        return GroupsFragment_.builder().build();
    }

    @Override
    public void setScrollListener(RecyclerView.OnScrollListener scrollListener) {
        //nothing
    }

    @Override
    public void setSearchTerm(String searchTerm) {
        //nothing
    }
}

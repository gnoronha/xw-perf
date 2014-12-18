package com.collabora.xwperf.notxw_contacts.fragments;

/*
 * Copyright 2014 Intel Corporation. All rights reserved.
 * License: BSD-3-clause-Intel, see LICENSE.txt
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import com.collabora.xwperf.notxw_contacts.DetailsActivity;
import com.collabora.xwperf.notxw_contacts.DetailsActivity_;
import com.collabora.xwperf.notxw_contacts.R;
import com.collabora.xwperf.notxw_contacts.adapters.ContactsAdapter;
import com.collabora.xwperf.notxw_contacts.fragments.helpers.AnimatedTabsListener;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import static android.support.v7.widget.RecyclerView.OnScrollListener;

@EFragment(R.layout.fragment_main)
public class MainFragment extends Fragment implements SearchView.OnQueryTextListener {
    //Holder for our tabs
    public static Fragment newInstance() {
        return MainFragment_.builder().build();
    }

    public static final String TAB_CONTACTS = "contacts";
    public static final String TAB_FAV = "fav";
    public static final String TAB_GROUPS = "groups";
    public static final int KEY_ANIMATION = R.id.tab_fav;//application specific

    private static final String CURRENT_TAB = "CURRENT_TAB";
    private String currentTabTag;

    @ViewById(android.R.id.tabhost)
    TabHost tabHost;

    @ViewById(android.R.id.tabs)
    TabWidget tabView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null)
            currentTabTag = savedInstanceState.getString(CURRENT_TAB);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
    }

    @AfterViews
    void init() {
        tabHost.setup();
        tabHost.addTab(newTab(TAB_FAV, getString(R.string.favorites), R.drawable.ic_favorite));
        tabHost.addTab(newTab(TAB_CONTACTS, getString(R.string.all), R.drawable.ic_contact));
        tabHost.addTab(newTab(TAB_GROUPS, getString(R.string.groups), R.drawable.ic_groups));
        if (currentTabTag != null) {
            tabHost.setCurrentTabByTag(currentTabTag);
        } else {
            switchTab(TAB_FAV);
        }
        tabHost.setOnTabChangedListener(new AnimatedTabsListener(tabHost) {
            @Override
            public void onTabChanged(String tabId, boolean onStart) {
                switchTab(tabId);
            }
        });

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(CURRENT_TAB, tabHost.getCurrentTabTag());
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        //for search button
        onQueryTextChange(s);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        FragmentManager fm = getChildFragmentManager();
        ITabScrollHider fragment = (ITabScrollHider) fm.findFragmentByTag(currentTabTag);
        fragment.setSearchTerm(s);
        //reset tabView transition
        tabView.setTranslationY(0);
        tabView.setTag(KEY_ANIMATION, 0);
        return true;
    }

    private void switchTab(String tabId) {
        FragmentManager fm = getChildFragmentManager();
        if (fm.findFragmentByTag(tabId) == null) {
            Fragment fragment = getFragmentForTab(tabId);
            fm.beginTransaction()
                    .replace(getContainerViewId(tabId), fragment, tabId)
                    .commit();
            setContactClickListener((ITabScrollHider) fragment);
        }
        currentTabTag = tabId;
    }

    private Fragment getFragmentForTab(String tabId) {
        Fragment fragment = null;
        switch (tabId) {
            case TAB_CONTACTS:
                fragment = AllContactsFragment.newInstance();
                break;
            case TAB_FAV:
                fragment = FavoritesFragment.newInstance();
                break;
            case TAB_GROUPS:
                fragment = GroupsFragment.newInstance();
                break;
        }
        if (fragment != null) {
            setScrollListener((ITabScrollHider) fragment);
        }
        return fragment;
    }

    private void setScrollListener(ITabScrollHider fragment) {
        fragment.setScrollListener(new OnScrollListener() {
            private Object o;
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                o = tabView.getTag(KEY_ANIMATION);
                int oldDy = o == null ? 0 : (int) o;
                if ((oldDy += dy) < 0)
                    oldDy = 0;
                tabView.setTranslationY(-1 * oldDy);
                tabView.setTag(KEY_ANIMATION, oldDy);
            }
        });
    }

    private void setContactClickListener(ITabScrollHider fragment) {
        fragment.setContactClickListener(new ContactsAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemId) {
                Intent intent = new Intent(getActivity(), DetailsActivity_.class);
                intent.putExtra(DetailsActivity.EXTRA_ITEM_ID, itemId);
                startActivity(intent);
            }
        });
    }

    private int getContainerViewId(String tabId) {
        switch (tabId) {
            case TAB_CONTACTS:
                return R.id.tab_all;
            case TAB_FAV:
                return R.id.tab_fav;
            case TAB_GROUPS:
                return R.id.tab_groups;
        }
        throw new IllegalStateException("Check your layout! No container found for: " + tabId);
    }

    private TabHost.TabSpec newTab(String tabId, String tabCaption, int imgResId) {
        TabHost.TabSpec tabSpec = tabHost.newTabSpec(tabId);
        ViewGroup tab = (ViewGroup) getLayoutInflater(Bundle.EMPTY).inflate(R.layout.view_tabwidget_tab, null);
        TextView tabTextView = ((TextView) tab.findViewById(R.id.tab_text));
        tabTextView.setText(tabCaption);
        tabTextView.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(imgResId), null, null, null);
        tabSpec.setIndicator(tab);
        tabSpec.setContent(getContainerViewId(tabId));
        return tabSpec;
    }

}

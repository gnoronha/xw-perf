package com.collabora.xwperf.notxw_social;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;

import java.util.ArrayList;

@EFragment
public class MainFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    private static final String TAG = MainFragment.class.getSimpleName();
    private final boolean USE_NEW = true;

    public static Fragment newInstance() {
        return MainFragment_.builder().build();
    }

    private SwipeRefreshLayout swipeToRefreshLayout;

    private ListView feedListView;
    private IListAdapter adapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private FeedGenerator feedGenerator;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        feedGenerator = new FeedGenerator();
        View rootView;
        if (USE_NEW) {
            rootView = inflater.inflate(R.layout.fragment_main_recycler, container, false);
            recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
            recyclerView.setHasFixedSize(true);
            layoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(layoutManager);
            adapter = new RecyclerAdapter(getActivity());
            recyclerView.setAdapter((RecyclerAdapter) adapter);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
        } else {
            rootView = inflater.inflate(R.layout.fragment_main, container, false);
            feedListView = (ListView) rootView.findViewById(R.id.feed_list_view);
            adapter = new ListAdapter(getActivity(), new ArrayList<TweetModel>());
            feedListView.setAdapter((ListAdapter) adapter);
        }
        swipeToRefreshLayout = (SwipeRefreshLayout) rootView;
        swipeToRefreshLayout.setOnRefreshListener(this);
        generateData();
        return rootView;
    }

    @Background
    void generateData() {
        populateAdapter(feedGenerator.getFirstBatch(), false);
    }

    @Background
    void generateRefresh() {
        populateAdapter(feedGenerator.refresh(), true);
    }

    @Background
    void generateLoadMore() {
        populateAdapter(feedGenerator.loadMore(), false);
    }

    @UiThread
    void populateAdapter(ArrayList<TweetModel> tweets, boolean addToTop) {
        adapter.addTweetModels(tweets, addToTop);
        swipeToRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Log.d(TAG, "Settings selected");
            return true;
        } else if (id == R.id.action_performance) {
            Log.d(TAG, "Performance selected");
            return true;
        } else if (id == R.id.action_refresh) {
            onRefresh();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        swipeToRefreshLayout.setRefreshing(true);
        generateRefresh();
    }

}

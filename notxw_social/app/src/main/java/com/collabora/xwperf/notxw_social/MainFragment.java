package com.collabora.xwperf.notxw_social;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;

import java.util.ArrayList;

@EFragment
public class MainFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final int VISIBLE_THRESHOLD = 10;//would load new portion

    public static Fragment newInstance() {
        return MainFragment_.builder().build();
    }

    private SwipeRefreshLayout swipeToRefreshLayout;
    private RecyclerArrayAdapter adapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private FeedGenerator feedGenerator;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        feedGenerator = new FeedGenerator();
        View rootView = inflater.inflate(R.layout.fragment_main_recycler, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecyclerArrayAdapter(getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        setScrollListener();
        setDismissListener();
        swipeToRefreshLayout = (SwipeRefreshLayout) rootView;
        swipeToRefreshLayout.setOnRefreshListener(this);
        generateData(savedInstanceState == null);
        return rootView;
    }

    private void setDismissListener() {
        recyclerView.setOnTouchListener(new SwipeDismissRecyclerViewTouchListener(
                recyclerView,
                new SwipeDismissRecyclerViewTouchListener.DismissCallbacks() {
                    @Override
                    public boolean canDismiss(int position) {
                        return true;
                    }

                    @Override
                    public void onDismiss(RecyclerView recyclerView, int[] reverseSortedPositions) {
                        adapter.remove(reverseSortedPositions[0]);
                    }
                }));

    }

    private void setScrollListener() {
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!swipeToRefreshLayout.isRefreshing() && (layoutManager.getItemCount() - recyclerView.getChildCount())
                        <= (layoutManager.findFirstVisibleItemPosition() + VISIBLE_THRESHOLD)) {
                    swipeToRefreshLayout.setRefreshing(true);
                    generateLoadMore();
                }
            }
        });
    }

    @Background
    void generateData(boolean createJson) {
        FileUtils fileUtils = new FileUtils(getActivity());
        //generate JSON during startup, save to disk
        if (createJson)
            fileUtils.writeFeed(feedGenerator.getFirstBatch());
        //load JSON instead of generating in-memory
        populateAdapter(fileUtils.readFeed(), false);
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
        if (adapter.getItemCount() >= FeedGenerator.MAX_ITEMS) {
            Toast.makeText(getActivity(), getString(R.string.max_tweets_reached), Toast.LENGTH_LONG).show();
        } else {
            adapter.addTweetModels(tweets, addToTop);
        }
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
            Intent intent = new Intent(getActivity(), SettingsActivity_.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_performance) {
            Intent intent = new Intent(getActivity(), PerformanceActivity.class);
            startActivity(intent);
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

package com.collabora.xwperf.notxw_social;

import java.util.ArrayList;

public interface IListAdapter {
    public void addTweetModels(ArrayList<TweetModel> tweetModels, boolean addToTop);

    public int getCount();
}
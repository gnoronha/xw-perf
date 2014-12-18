package com.collabora.xwperf.notxw_contacts.fragments.helpers;
/*
 * Copyright 2014 Intel Corporation. All rights reserved.
 * License: BSD-3-clause-Intel, see LICENSE.txt
 */

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TabHost;

import com.collabora.xwperf.notxw_contacts.R;

public abstract class AnimatedTabsListener implements TabHost.OnTabChangeListener {
    private TabHost tabHost;
    private View previousView;
    private int currentTab;

    public AnimatedTabsListener(TabHost tabHost) {
        this.tabHost = tabHost;
        this.previousView = tabHost.getCurrentView();
    }

    @Override
    public void onTabChanged(String tabId) {
        onTabChanged(tabId, true);
        View currentView = tabHost.getCurrentView();
        Context context = currentView.getContext();
        Animation fadeOutAnim, fadeInAnim;
        if (tabHost.getCurrentTab() > currentTab) {
            fadeOutAnim = AnimationUtils.loadAnimation(context, R.anim.fade_out_left);
            fadeInAnim = AnimationUtils.loadAnimation(context, R.anim.fade_in_right);
        } else {
            fadeOutAnim = AnimationUtils.loadAnimation(context, R.anim.fade_out_right);
            fadeInAnim = AnimationUtils.loadAnimation(context, R.anim.fade_in_left);
        }
        previousView.setAnimation(fadeOutAnim);
        currentView.setAnimation(fadeInAnim);
        previousView = currentView;
        currentTab = tabHost.getCurrentTab();
    }

    public abstract void onTabChanged(String tabId, boolean onStart);
}

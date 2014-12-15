package com.collabora.xwperf.notxw_contacts.fragments.helpers;

import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TabHost;

public abstract class AnimatedTabsListener implements TabHost.OnTabChangeListener {

    private static final int ANIMATION_TIME = 240;
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
        if (tabHost.getCurrentTab() > currentTab) {
            previousView.setAnimation(outToLeftAnimation());
            currentView.setAnimation(inFromRightAnimation());
        } else {
            previousView.setAnimation(outToRightAnimation());
            currentView.setAnimation(inFromLeftAnimation());
        }
        previousView = currentView;
        currentTab = tabHost.getCurrentTab();
    }

    public abstract void onTabChanged(String tabId, boolean onStart);

    /**
     * Custom animation that animates in from right
     *
     * @return Animation the Animation object
     */
    private Animation inFromRightAnimation() {
        Animation inFromRight = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 1.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
        return setProperties(inFromRight);
    }

    /**
     * Custom animation that animates out to the right
     *
     * @return Animation the Animation object
     */
    private Animation outToRightAnimation() {
        Animation outToRight = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
        return setProperties(outToRight);
    }

    /**
     * Custom animation that animates in from left
     *
     * @return Animation the Animation object
     */
    private Animation inFromLeftAnimation() {
        Animation inFromLeft = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, -1.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
        return setProperties(inFromLeft);
    }

    /**
     * Custom animation that animates out to the left
     *
     * @return Animation the Animation object
     */
    private Animation outToLeftAnimation() {
        Animation outToLeft = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
        return setProperties(outToLeft);
    }

    /**
     * Helper method that sets some common properties
     *
     * @param animation the animation to give common properties
     * @return the animation with common properties
     */
    private Animation setProperties(Animation animation) {
        animation.setDuration(ANIMATION_TIME);
        animation.setInterpolator(new AccelerateInterpolator());
        return animation;
    }
}

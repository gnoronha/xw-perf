package com.collabora.xwperf.notxw_contacts;

/*
 * Copyright 2014 Intel Corporation. All rights reserved.
 * License: BSD-3-clause-Intel, see LICENSE.txt
 */

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.collabora.xwperf.fps_measure_module.FpsGraphView;
import com.collabora.xwperf.fps_measure_module.FpsMeterView;
import com.collabora.xwperf.fps_measure_module.IFpsListener;
import com.collabora.xwperf.notxw_contacts.fragments.MainFragment;

import org.androidannotations.annotations.EActivity;

@EActivity
public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    private FpsMeterView fpsMeter;
    private FpsGraphView fpsGraph;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean showDrawer = getResources().getBoolean(R.bool.show_drawer);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_widget);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(showDrawer);

        if (showDrawer) {
            DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.material_green_700));
            toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            toggle.setDrawerIndicatorEnabled(true);
            drawerLayout.setDrawerListener(toggle);
        }

        fpsMeter = (FpsMeterView) findViewById(R.id.fps_meter);
        fpsGraph = (FpsGraphView) findViewById(R.id.fps_graph_view);
        fpsGraph.setOnClickListener(this);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, MainFragment.newInstance())
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        fpsMeter.setFpsListener(new IFpsListener() {
            @Override
            public void onFpsCount(long fps) {
                fpsGraph.addValue(fps);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        fpsMeter.removeFpsListener();
    }

    @Override
    public void onClick(View v) {
        if (v == fpsGraph) {
            v.postInvalidate();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle != null && toggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (toggle != null)
            toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (toggle != null)
            toggle.onConfigurationChanged(newConfig);
    }

}


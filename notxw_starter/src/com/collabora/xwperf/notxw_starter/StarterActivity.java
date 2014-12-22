package com.collabora.xwperf.notxw_starter;

/*
 * Copyright 2014 Intel Corporation. All rights reserved.
 * License: BSD-3-clause-Intel, see LICENSE.txt
 */

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.Date;

public class StarterActivity extends Activity
    implements AdapterView.OnItemSelectedListener
{
    static String[] appLabels = {
        "Social (Polymer)",
        "Contacts (Polymer)",
        "Skeleton (Polymer)",
        "Social (Java)",
        "Contacts (Java)",
        "Skeleton (Java)",
    };
    static String[] appNames = {
        "com.collabora.xwperf.xwperf_social",
        "com.collabora.xwperf.xwperf_contacts",
        "com.collabora.xwperf.xwperf_skeleton",
        "com.collabora.xwperf.notxw_social",
        "com.collabora.xwperf.notxw_contacts",
        "com.collabora.xwperf.notxw_skeleton",
    };
    int selectedApp = -1;

    TextView logView;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        logView = (TextView) findViewById(R.id.log_view);

        Spinner spinner = (Spinner) findViewById(R.id.app_spinner);
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item, appLabels);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    public void onItemSelected(AdapterView<?> parent, View view,
            int pos, long id)
    {
        selectedApp = pos;
    }

    public void onNothingSelected(AdapterView<?> parent)
    {
        selectedApp = -1;
    }

    public void onGoButtonClicked(View view)
    {
        if (selectedApp > -1) {
            try {
                logView.append(String.format("starting %s (%s) at " +
                            "%s ms past the minute / %s ms since boot\n",
                            appLabels[selectedApp],
                            appNames[selectedApp],
                            (new Date()).getTime() % (60 * 1000),
                            SystemClock.elapsedRealtime()));
                startApp(appNames[selectedApp]);
                logView.append("... done\n");
            } catch (Exception e) {
                logView.append(String.format("failed to start %s: %s\n",
                            appNames[selectedApp], e));
            }
        }
    }

    void startApp(String reverseDomain)
        throws PackageManager.NameNotFoundException
    {
        PackageManager pm = getPackageManager();
        Intent i = pm.getLaunchIntentForPackage(reverseDomain);
        if (i == null) {
            throw new PackageManager.NameNotFoundException();
        }
        i.addCategory(Intent.CATEGORY_LAUNCHER);
        startActivity(i);
    }
}

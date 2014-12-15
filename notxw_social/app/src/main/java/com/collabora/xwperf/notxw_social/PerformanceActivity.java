package com.collabora.xwperf.notxw_social;

/*
 * Copyright 2014 Intel Corporation. All rights reserved.
 * License: BSD-3-clause-Intel, see LICENSE.txt
 */

import android.app.ListActivity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.collabora.xwperf.fps_measure_module.MeasurementLogger;

import java.util.List;

public class PerformanceActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MeasurementLogger logger = MeasurementLogger.getInstance();
        PerformanceAdapter<String> adapter = new PerformanceAdapter<>(this, logger.getLogs());
        getListView().setAdapter(adapter);
    }

    private class PerformanceAdapter<T> extends ArrayAdapter<T> {

        public PerformanceAdapter(Context context, List<T> objects) {
            super(context, android.R.layout.simple_list_item_1, android.R.id.text1, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            view.setBackgroundColor(position % 2 == 0 ? Color.TRANSPARENT : Color.LTGRAY);
            return view;
        }
    }
}

package com.collabora.xwperf.notxw_social;

/*
 * Copyright 2014 Intel Corporation. All rights reserved.
 * License: BSD-3-clause-Intel, see LICENSE.txt
 */

import android.app.Application;

import com.collabora.xwperf.fps_measure_module.MeasurementLogger;

public class SocialApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        MeasurementLogger.getInstance().addMark(MeasurementLogger.PerformanceMarks.MEASURE_LOAD);
    }
}

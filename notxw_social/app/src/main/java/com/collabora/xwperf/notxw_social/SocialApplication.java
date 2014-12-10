package com.collabora.xwperf.notxw_social;

import android.app.Application;

import com.collabora.xwperf.fps_measure_module.MeasurementLogger;

public class SocialApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        MeasurementLogger.getInstance().addMark(MeasurementLogger.PerformanceMarks.MEASURE_LOAD);
    }
}

package com.collabora.xwperf.notxw_skeleton;

import android.app.Application;

import com.collabora.xwperf.fps_measure_module.MeasurementLogger;


public class SkeletonApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //init singleton
        MeasurementLogger.getInstance();
    }

}

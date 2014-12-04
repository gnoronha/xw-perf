package com.collabora.xwperf.notxw_social;

import android.os.SystemClock;

import java.util.ArrayList;

public class MeasurementLogger {
    private ArrayList<String> logs;
    private long startTime;
    private static MeasurementLogger ourInstance = new MeasurementLogger();

    public static MeasurementLogger getInstance() {
        return ourInstance;
    }

    private MeasurementLogger() {
        logs = new ArrayList<>();
        startTime = SystemClock.elapsedRealtime();
    }

    public ArrayList<String> getLogs(){
        return logs;
    }

    public void addEvent(long timestamp, long duration, String eventDescription){

    }

}

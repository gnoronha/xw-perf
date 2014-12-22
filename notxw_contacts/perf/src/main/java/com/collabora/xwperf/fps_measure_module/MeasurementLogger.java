/*
 * Copyright 2014 Intel Corporation. All rights reserved.
 * License: BSD-3-clause-Intel, see LICENSE.txt
 */
package com.collabora.xwperf.fps_measure_module;

import android.os.SystemClock;

import java.util.ArrayList;

public class MeasurementLogger {

    private ArrayList<String> logs;
    private static long startTime;
    private static volatile MeasurementLogger instance;

    public static MeasurementLogger getInstance() {
        MeasurementLogger localInstance = instance;
        if (localInstance == null) {
            synchronized (MeasurementLogger.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new MeasurementLogger();
                }
            }
        }
        return localInstance;
    }

    private MeasurementLogger() {
        startTime = SystemClock.elapsedRealtime();
        logs = new ArrayList<>(700);//600 for frames and other
        logs.add("Start time (ms since the boot):\t" + startTime);
    }

    public ArrayList<String> getLogs() {
        return logs;
    }

    public void addMark(String eventDescription) {
        addMark(SystemClock.elapsedRealtime(), eventDescription);
    }

    public void addMark(long timestamp, String eventDescription) {
        logs.add(String.valueOf((timestamp - startTime) / 1000f) + "\t\t\t\t\t" + "Mark: " + eventDescription);
    }

    public void addMeasure(long timestamp, long duration, String eventDescription) {
        logs.add(String.valueOf((timestamp - startTime) / 1000f) + "\t\t" + duration + "\t\t" + "Measure: " + eventDescription);
    }

    public class PerformanceMarks {
        public static final String MARK_MY_PERF_BOX_READY = "mark_my_perf_box_ready";
        public static final String MEASURE_TO_NEXT_FRAME = "measure_to_next_frame";
        public static final String MARK_PERF_FRAME = "mark_perf_frame";
        public static final String MARK_PERF_COLLECTOR_ATTACH_BLINKENLIGHT = "mark_perf_collector_attach_blinkenlight";
        public static final String MARK_PERF_COLLECTOR_SETUP_END = "mark_perf_collector_setup_end";
        public static final String MARK_PERF_COLLECTOR_SETUP_START = "mark_perf_collector_setup_start";
        public static final String MARK_FEED_LOAD_BEGIN = "measure_feed_load_begin_";
        public static final String MARK_FEED_LOAD_END = "measure_feed_load_end_";
        public static final String MEASURE_FEED_LOAD = "measure_feed_load_";
        public static final String MEASURE_FILE_READ = "measure_file_read";
        public static final String MEASURE_FILE_WRITE = "measure_file_write";

        public static final String MARK_FRAME_COLLECT_FINISH = "finished doing first 300 frames";
    }
}

package com.collabora.xwperf.fps_measure_module;

import android.os.SystemClock;

import java.util.ArrayList;

public class MeasurementLogger {

    private ArrayList<String> logs;
    private static MeasurementLogger ourInstance = new MeasurementLogger();

    public static MeasurementLogger getInstance() {
        return ourInstance;
    }

    private MeasurementLogger() {
        logs = new ArrayList<>();
    }

    public ArrayList<String> getLogs() {
        return logs;
    }

    public void addMark(String eventDescription) {
        long timestamp = SystemClock.elapsedRealtime();
        logs.add(String.valueOf(timestamp) + "\t\t\t\t\t" + "Mark: " + eventDescription);
    }

    public void addMeasure(long timestamp, long duration, String eventDescription) {
        logs.add(String.valueOf(timestamp) + "\t\t" + duration + "\t\t" + "Measure: " + eventDescription);
    }

    public class PerformanceMarks {
        public static final String MARK_MY_PERF_BOX_READY = "mark_my_perf_box_ready";
        public static final String MEASURE_TO_NEXT_FRAME = "measure_to_next_frame";
        public static final String MARK_PERF_FRAME = "mark_perf_frame";
        public static final String MEASURE_LOAD = "measure_load";
        public static final String MARK_PERF_COLLECTOR_ATTACH_BLINKENLIGHT = "mark_perf_collector_attach_blinkenlight";
        public static final String MARK_PERF_COLLECTOR_SETUP_END = "mark_perf_collector_setup_end";
        public static final String MARK_PERF_COLLECTOR_SETUP_START = "mark_perf_collector_setup_start";
        public static final String MEASURE_FEED_LOAD = "measure_feed_load_";
        public static final String MEASURE_FILE_READ = "measure_file_read";
    }

}

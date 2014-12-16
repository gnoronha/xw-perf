package com.collabora.xwperf.notxw_contacts;

/*
 * Copyright 2014 Intel Corporation. All rights reserved.
 * License: BSD-3-clause-Intel, see LICENSE.txt
 */

import android.app.Application;
import android.os.SystemClock;

import com.collabora.xwperf.fps_measure_module.MeasurementLogger;
import com.collabora.xwperf.notxw_contacts.data.ContactGenerator;
import com.collabora.xwperf.notxw_contacts.data.DBHelper;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EApplication;

@EApplication
public class ContactsApplication extends Application {
    private MeasurementLogger logger;

    @Override
    public void onCreate() {
        super.onCreate();
        logger = MeasurementLogger.getInstance();
        populateData();
    }

    @Background
    void populateData() {
        long startTime = SystemClock.elapsedRealtime();
        DBHelper.clearDB(this);
        DBHelper.insertGeneratedContacts(this, ContactGenerator.generateContactList());
        logger.addMeasure(SystemClock.elapsedRealtime(), SystemClock.elapsedRealtime() - startTime, "measure_database_insert");
    }
}

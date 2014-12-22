package com.collabora.xwperf.notxw_social;

/*
 * Copyright 2014 Intel Corporation. All rights reserved.
 * License: BSD-3-clause-Intel, see LICENSE.txt
 */

import android.content.Context;
import android.content.Intent;

import com.collabora.xwperf.fps_measure_module.AbstractPerformanceActivity;

public class PerformanceActivity extends AbstractPerformanceActivity {
    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, PerformanceActivity.class));
    }
}

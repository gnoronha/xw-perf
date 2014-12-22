/*
 * Copyright 2014 Intel Corporation. All rights reserved.
 * License: BSD-3-clause-Intel, see LICENSE.txt
 */
package com.collabora.xwperf.fps_measure_module;

public interface IFpsListener {
    public void onFpsCount(long fps);
}

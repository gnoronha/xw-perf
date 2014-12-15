package com.collabora.xwperf.notxw_contacts.adapters;

/*
 * Copyright 2014 Intel Corporation. All rights reserved.
 * License: BSD-3-clause-Intel, see LICENSE.txt
 */

import android.view.LayoutInflater;
import android.view.View;

public interface INavItem {
    public int getViewType();

    public View getView(LayoutInflater inflater, int position, View convertView);
}

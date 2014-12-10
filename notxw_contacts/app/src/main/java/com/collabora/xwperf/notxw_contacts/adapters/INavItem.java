package com.collabora.xwperf.notxw_contacts.adapters;

import android.view.LayoutInflater;
import android.view.View;

public interface INavItem {
    public int getViewType();

    public View getView(LayoutInflater inflater, int position, View convertView);
}

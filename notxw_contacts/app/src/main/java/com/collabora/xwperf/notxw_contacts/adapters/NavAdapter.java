package com.collabora.xwperf.notxw_contacts.adapters;

/*
 * Copyright 2014 Intel Corporation. All rights reserved.
 * License: BSD-3-clause-Intel, see LICENSE.txt
 */

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.collabora.xwperf.notxw_contacts.PerformanceActivity;
import com.collabora.xwperf.notxw_contacts.R;

public class NavAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int ITEM_TYPE_TEXT = 1;
    private static final int ITEM_TYPE_DIVIDER = 2;

    private final LayoutInflater inflater;
    private Context context;

    private Pair<String, Integer>[] drawerItems = new Pair[]{
            new Pair<>("Options", ITEM_TYPE_TEXT),
            new Pair<>("Settings", ITEM_TYPE_TEXT),
            new Pair<>("Preferences", ITEM_TYPE_TEXT),
            new Pair<>("Misc", ITEM_TYPE_TEXT),
            new Pair<>("Other", ITEM_TYPE_TEXT),
            new Pair<>("Divider", ITEM_TYPE_DIVIDER),
            new Pair<>("Performance", ITEM_TYPE_TEXT)};

    public NavAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_TEXT) {
            return new TextViewHolder(inflater.inflate(R.layout.view_nav_text, parent, false));
        } else if (viewType == ITEM_TYPE_DIVIDER) {
            return new DividerViewHolder(inflater.inflate(R.layout.view_nav_div, parent, false));
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        return drawerItems[position].second;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int type = drawerItems[position].second;
        if (type == ITEM_TYPE_TEXT) {
            ((TextViewHolder) holder).textView.setText(drawerItems[position].first);
        }
    }

    @Override
    public int getItemCount() {
        return drawerItems.length;
    }

    public class TextViewHolder extends RecyclerView.ViewHolder implements RecyclerView.OnClickListener {
        TextView textView;

        public TextViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            textView = (TextView) itemView.findViewById(R.id.text1);
        }

        @Override
        public void onClick(View v) {
            String txt = textView.getText().toString();
            if (drawerItems[6].first.equals(txt)) {
                PerformanceActivity.startActivity(context);
            } else {
                Toast.makeText(context, R.string.not_implemented, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class DividerViewHolder extends RecyclerView.ViewHolder {
        public DividerViewHolder(View itemView) {
            super(itemView);
        }
    }

}

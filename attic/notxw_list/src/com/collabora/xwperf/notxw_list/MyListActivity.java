package com.collabora.xwperf.notxw_list;

import java.util.ArrayList;

import android.app.ListActivity;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.os.Bundle;

public class MyListActivity extends ListActivity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        ArrayList<String> values = new ArrayList<String>();

        for (int i = 0; i < 10000; i++) {
            values.add(String.format("Item %d", i));
        }

        ListAdapter adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                values);
        setListAdapter(adapter);
    }
}

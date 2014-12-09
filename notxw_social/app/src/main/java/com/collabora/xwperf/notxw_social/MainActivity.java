package com.collabora.xwperf.notxw_social;

import android.os.Bundle;
import android.view.View;

import org.androidannotations.annotations.EActivity;

@EActivity
public class MainActivity extends BaseActivity implements View.OnClickListener {

    @Override
    public void pushFragment(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, MainFragment.newInstance())
                    .commit();
        }
    }
}

package com.collabora.xwperf.notxw_social;

/*
 * Copyright 2014 Intel Corporation. All rights reserved.
 * License: BSD-3-clause-Intel, see LICENSE.txt
 */

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import org.androidannotations.annotations.EFragment;

@EFragment
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    public static final String TAG = SettingsFragment.class.getSimpleName();

    public static SettingsFragment newInstance() {
        return SettingsFragment_.builder().build();
    }

    private Preference visibilityPref;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        visibilityPref = findPreference(getString(R.string.show_location_key));
    }

    @Override
    public void onResume() {
        super.onResume();
        initValues();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    private void initValues() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (visibilityPref != null) {
            String currentValue = prefs.getString(getString(R.string.show_location_key), null);
            if (currentValue == null) {
                String[] visibilityValues = getResources().getStringArray(R.array.visibilityEntries);
                currentValue = visibilityValues[0];
                prefs.edit().putString(getString(R.string.show_location_key), currentValue).apply();
            }
            visibilityPref.setSummary(currentValue);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference pref = findPreference(key);

        if (pref instanceof ListPreference) {
            ListPreference listPref = (ListPreference) pref;
            pref.setSummary(listPref.getEntry());
        }
    }
}

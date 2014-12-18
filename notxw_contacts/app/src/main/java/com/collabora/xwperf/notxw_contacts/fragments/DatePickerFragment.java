package com.collabora.xwperf.notxw_contacts.fragments;

/*
 * Copyright 2014 Intel Corporation. All rights reserved.
 * License: BSD-3-clause-Intel, see LICENSE.txt
 */

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    public static final String EXTRA_INIT_DATE = "init_date";
    public static final String TAG = DatePickerFragment.class.getSimpleName();
    private OnDatePickedListener onDialogResultListener;

    public static DatePickerFragment newInstance(Date initDate) {
        DatePickerFragment f = new DatePickerFragment();
        Bundle args = new Bundle();
        args.putLong(EXTRA_INIT_DATE, initDate == null ? new Date().getTime() : initDate.getTime());
        f.setArguments(args);
        return f;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(getArguments().getLong(EXTRA_INIT_DATE)));
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, year, month, day);
        dialog.getDatePicker().setMaxDate(new Date().getTime() + 1);
        return dialog;
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        onDialogResultListener.onDateSelected(c.getTime());
    }

    public void setOnDatePickedListener(OnDatePickedListener listener) {
        this.onDialogResultListener = listener;
    }

    public interface OnDatePickedListener {
        public abstract void onDateSelected(Date value);
    }

}
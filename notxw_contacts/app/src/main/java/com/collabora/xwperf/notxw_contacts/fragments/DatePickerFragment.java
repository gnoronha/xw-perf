package com.collabora.xwperf.notxw_contacts.fragments;

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
        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putLong(EXTRA_INIT_DATE, initDate == null ? new Date().getTime() : initDate.getTime());
        f.setArguments(args);
        return f;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(getArguments().getLong(EXTRA_INIT_DATE)));
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        // Create a new instance of DatePickerDialog and return it
        DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, year, month, day);
        dialog.getDatePicker().setMaxDate(new Date().getTime() + 1);
        return dialog;
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
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
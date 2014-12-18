package com.collabora.xwperf.notxw_contacts.fragments;

/*
 * Copyright 2014 Intel Corporation. All rights reserved.
 * License: BSD-3-clause-Intel, see LICENSE.txt
 */

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.collabora.xwperf.notxw_contacts.R;
import com.collabora.xwperf.notxw_contacts.data.ContactModel;
import com.collabora.xwperf.notxw_contacts.data.ContactsContentProvider;
import com.collabora.xwperf.notxw_contacts.data.ContactsStore;
import com.collabora.xwperf.notxw_contacts.data.DBHelper;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import java.text.SimpleDateFormat;
import java.util.Date;

@EFragment(R.layout.fragment_contact_details)
public class DetailsFragment extends Fragment {
    private static final String TAG = DetailsFragment.class.getSimpleName();
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
    private ContactModel contactModel;
    private ColorGenerator generator = ColorGenerator.DEFAULT;

    public static Fragment newInstance(int contactId) {
        return DetailsFragment_.builder().contactId(contactId).build();
    }

    @ViewById(R.id.avatar_image)
    ImageView avatarImage;

    @ViewById(R.id.name_edit)
    EditText nameEdit;

    @ViewById(R.id.name_title)
    TextView nameTitle;

    @ViewById(R.id.email_edit)
    EditText emailEdit;

    @ViewById(R.id.phone_edit)
    EditText phoneEdit;

    @ViewById(R.id.birthday_edit)
    EditText birthdayEdit;

    @FragmentArg
    int contactId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.details_menu, menu);
        if (contactModel == null)
            return;

        View saveView = menu.findItem(R.id.action_favorites).getActionView();
        final ImageView favImage = (ImageView) saveView.findViewById(R.id.action_fav_image);
        saveView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactModel.setFavorite(!contactModel.isFavorite());
                favImage.setImageResource(contactModel.isFavorite() ? R.drawable.btn_rating_star_on_mtrl_alpha : R.drawable.btn_rating_star_off_mtrl_alpha);
            }
        });
        favImage.setImageResource(contactModel.isFavorite() ? R.drawable.btn_rating_star_on_mtrl_alpha : R.drawable.btn_rating_star_off_mtrl_alpha);
        showItem(contactModel);
        setTitle(contactModel.getName());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            //hide keyboard
            hideKeyBoard(getActivity(),nameTitle);
            if (validateData()) {
                updateItem(contactModel);
                getActivity().onBackPressed();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean validateData() {
        boolean result = true;
        if (nameEdit.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), R.string.error_name_empty, Toast.LENGTH_SHORT).show();
            nameTitle.setTextColor(getResources().getColor(R.color.graph_red));
            nameTitle.setText(getString(R.string.details_name_text)+"*");
            result = false;
        }
        contactModel.setName(nameEdit.getText().toString());
        contactModel.setEmail(emailEdit.getText().toString());
        contactModel.setPhone(phoneEdit.getText().toString());
        return result;
    }

    @AfterViews
    void init() {
        getLoaderManager().initLoader(contactId, null, contactDetailsLoader);
        nameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                setTitle(s.toString());
            }
        });
    }

    @Click(R.id.birthday_edit)
    void clickBirthdayEdit() {
        //show date picker
        DatePickerFragment newFragment = DatePickerFragment.newInstance(contactModel.getBirthday());
        newFragment.setOnDatePickedListener(new DatePickerFragment.OnDatePickedListener() {
            @Override
            public void onDateSelected(Date value) {
                contactModel.setBirthday(value);
                showItem(contactModel);
            }
        });
        newFragment.show(getFragmentManager(), DatePickerFragment.TAG);
    }

    private void setTitle(String title) {
        getActivity().setTitle(title);
    }

    private LoaderManager.LoaderCallbacks<Cursor> contactDetailsLoader = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int itemId, Bundle bundle) {
            return new CursorLoader(getActivity(), ContactsContentProvider.contentUri(ContactsStore.ContactTable.CONTENT_URI, itemId), null, null, null, null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
            if (cursor.moveToFirst()) {
                contactModel = DBHelper.getItemFromCursor(cursor);
                showItem(contactModel);
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> cursorLoader) {
            //nothing
        }
    };

    private void showItem(ContactModel contactModel) {
        //populate items
        nameEdit.setText(contactModel.getName());
        emailEdit.setText(contactModel.getEmail());
        phoneEdit.setText(contactModel.getPhone());
        Date birthDate = contactModel.getBirthday();
        if (birthDate != null)
            birthdayEdit.setText(simpleDateFormat.format(birthDate));
        int avatarResId = contactModel.getAvatarResId();
        if (avatarResId > 0) {
            Picasso.with(getActivity()).load(avatarResId).noFade().into(avatarImage);
        } else {
            avatarImage.setImageDrawable(TextDrawable.builder().buildRect(contactModel.getName().substring(0, 1), generator.getColor(contactModel.getName())));
        }
    }

    @Background
    void updateItem(ContactModel contactModel) {
        DBHelper.updateModel(getActivity(), contactModel);
    }

    public static void hideKeyBoard(Context context, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}

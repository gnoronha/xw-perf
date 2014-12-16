package com.collabora.xwperf.notxw_contacts.adapters;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.collabora.xwperf.notxw_contacts.CircleTransform;
import com.collabora.xwperf.notxw_contacts.R;
import com.collabora.xwperf.notxw_contacts.data.ContactsStore;
import com.collabora.xwperf.notxw_contacts.data.DBHelper;
import com.squareup.picasso.Picasso;

import static android.view.View.OnClickListener;

public class ContactsAdapter extends ContactsCursorAdapter<RecyclerView.ViewHolder> {
    private static final int KEY_ITEM_ID = R.id.avatar;
    private static final int KEY_OLD_VALUE = R.id.username;
    private static final int KEY_POSITION = R.id.favorite_star;

    private static final int TYPE_OFFSET = 123;
    private final boolean favAvailable;

    private ColorGenerator generator = ColorGenerator.DEFAULT;
    private LayoutInflater layoutInflater;

    private int nameId, favoriteId, avatarId;

    private Context context;

    public ContactsAdapter(Context context, Cursor cursor, boolean favAvailable) {
        super(cursor);
        this.favAvailable = favAvailable;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public Cursor swapCursor(Cursor newCursor) {
        if (newCursor != null)
            populateCursorColumns(newCursor);
        return super.swapCursor(newCursor);
    }

    public void populateCursorColumns(Cursor cursor) {
        nameId = cursor.getColumnIndex(ContactsStore.ContactTable.NAME);
        favoriteId = cursor.getColumnIndex(ContactsStore.ContactTable.FAVORITE);
        avatarId = cursor.getColumnIndex(ContactsStore.ContactTable.AVATAR);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return viewType == TYPE_OFFSET ?
                new OffsetViewHolder(layoutInflater.inflate(R.layout.view_offset, parent, false)) :
                new ContactsViewHolder(layoutInflater.inflate(R.layout.view_contact_item, parent, false), favAvailable);
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? TYPE_OFFSET : super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount() + 1;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position > 0)
            super.onBindViewHolder(holder, position - 1);
    }

    @Override
    public long getItemId(int position) {
        return position == 0 ? 0 : super.getItemId(position + 1);
    }

    @Override
    public void onBindViewHolderCursor(RecyclerView.ViewHolder viewHolder, Cursor cursor) {
        String name = cursor.getString(nameId);
        boolean isFavorite = cursor.getInt(favoriteId) == 1;
        int itemId = cursor.getInt(0);
        int avatarResId = cursor.getInt(avatarId);
        ContactsViewHolder local = ((ContactsViewHolder) viewHolder);
        local.usernameTextView.setText(name);
        if (avatarResId > 0) {
            Picasso.with(context).load(avatarResId).transform(new CircleTransform()).noFade()
                    .into(local.avatarImageView);
        } else {
            //generate avatar here
            local.avatarImageView.setImageDrawable(TextDrawable.builder().buildRound(name.substring(0, 1), generator.getColor(name)));
        }
        if (favAvailable) {
            local.favoriteStar.setChecked(isFavorite);
            local.favoriteStar.setTag(KEY_ITEM_ID, itemId);
            local.favoriteStar.setTag(KEY_OLD_VALUE, isFavorite);
            local.favoriteStar.setTag(KEY_POSITION, cursor.getPosition());
            local.favoriteStar.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //value has been changed
                    int itemId = (int) v.getTag(KEY_ITEM_ID);
                    boolean oldValue = (boolean) v.getTag(KEY_OLD_VALUE);
                    int position = (int) v.getTag(KEY_POSITION);
                    new FavoriteChangeTask().execute(oldValue ? 0 : 1, itemId, position);
                }
            });
        }
    }

    public static class ContactsViewHolder extends RecyclerView.ViewHolder {
        private ImageView avatarImageView;
        private TextView usernameTextView;
        private CheckBox favoriteStar;

        public ContactsViewHolder(View rootView, boolean favAvailable) {
            super(rootView);
            avatarImageView = (ImageView) rootView.findViewById(R.id.avatar);
            usernameTextView = (TextView) rootView.findViewById(R.id.username);
            favoriteStar = (CheckBox) rootView.findViewById(R.id.favorite_star);
            favoriteStar.setVisibility(favAvailable ? View.VISIBLE : View.GONE);
        }
    }

    public static class OffsetViewHolder extends RecyclerView.ViewHolder {
        public OffsetViewHolder(View rootView) {
            super(rootView);
        }
    }

    private class FavoriteChangeTask extends AsyncTask<Integer, Void, Integer> {
        @Override
        protected Integer doInBackground(Integer... params) {
            int newValue = params[0];
            int itemId = params[1];
            int position = params[2];
            DBHelper.changeFavoriteValues(context, newValue, itemId);
            return position;
        }
    }


}

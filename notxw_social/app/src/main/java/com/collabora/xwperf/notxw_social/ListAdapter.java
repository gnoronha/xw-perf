package com.collabora.xwperf.notxw_social;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ListAdapter extends ArrayAdapter<TweetModel> implements IListAdapter {

    private final LayoutInflater inflater;

    private SimpleDateFormat itemTimeFormat = new SimpleDateFormat("hh:mm:ss a");
    private ColorGenerator generator = ColorGenerator.DEFAULT;

    public ListAdapter(Context context, List<TweetModel> objects) {
        super(context, 0, objects);
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            View view = inflater.inflate(R.layout.social_list_item, parent, false);
            holder = ViewHolder.create((RelativeLayout) view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        TweetModel currentItem = getItem(position);
        String userName = currentItem.getUserModel().getUserName();
        holder.message.setText(currentItem.getMessage());
        holder.username.setText(userName);
        holder.login.setText(currentItem.getUserModel().getLogin());
        holder.dateStamp.setText(itemTimeFormat.format(currentItem.getTimestamp()));
        if (currentItem.getUserModel().getAvatar() > 0) {
            loadBitmap(currentItem.getUserModel().getAvatar(), holder.avatar);
        } else {
            //generate avatar here
            holder.avatar.setImageDrawable(TextDrawable.builder().buildRound(userName.substring(0, 1), generator.getColor(userName)));
        }

        return holder.rootView;
    }

    public void addTweetModels(ArrayList<TweetModel> tweets, boolean addToTop) {
        if (addToTop) {
            for (TweetModel tweetModel : tweets) {
                insert(tweetModel, 0);
            }
        } else {
            addAll(tweets);
        }
    }

    public void loadBitmap(int resId, ImageView imageView) {
        BitmapWorkerTask task = new BitmapWorkerTask(imageView);
        task.execute(resId);
    }

    private static class ViewHolder {
        public final RelativeLayout rootView;
        public final ImageView avatar;
        public final TextView username;
        public final TextView dateStamp;
        public final TextView login;
        public final TextView message;

        private ViewHolder(RelativeLayout rootView, ImageView avatar, TextView username, TextView dateStamp, TextView login, TextView message) {
            this.rootView = rootView;
            this.avatar = avatar;
            this.username = username;
            this.dateStamp = dateStamp;
            this.login = login;
            this.message = message;
        }

        public static ViewHolder create(RelativeLayout rootView) {

            ImageView avatar = (ImageView) rootView.findViewById(R.id.avatar);
            TextView username = (TextView) rootView.findViewById(R.id.username);
            TextView dateStamp = (TextView) rootView.findViewById(R.id.date);
            TextView login = (TextView) rootView.findViewById(R.id.login);
            TextView message = (TextView) rootView.findViewById(R.id.message);
            return new ViewHolder(rootView, avatar, username, dateStamp, login, message);
        }
    }

    private class BitmapWorkerTask extends AsyncTask<Integer, Void, Drawable> {
        private final WeakReference<ImageView> imageViewReference;

        public BitmapWorkerTask(ImageView imageView) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            imageViewReference = new WeakReference<>(imageView);
        }

        @Override
        protected Drawable doInBackground(Integer... params) {
            Bitmap avatarBitmap = BitmapFactory.decodeResource(getContext().getResources(), params[0]);
            return new RoundedAvatarDrawable(avatarBitmap);
        }

        @Override
        protected void onPostExecute(Drawable drawable) {
            if (imageViewReference != null && drawable != null) {
                final ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    imageView.setImageDrawable(drawable);
                }
            }
        }
    }
}

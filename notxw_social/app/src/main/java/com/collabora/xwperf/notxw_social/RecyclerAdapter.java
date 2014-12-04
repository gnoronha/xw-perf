package com.collabora.xwperf.notxw_social;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> implements IListAdapter {
    private ArrayList<TweetModel> tweetModels;

    private Context context;
    private SimpleDateFormat itemTimeFormat = new SimpleDateFormat("hh:mm:ss a");
    private ColorGenerator generator = ColorGenerator.DEFAULT;

    public RecyclerAdapter(Context context) {
        tweetModels = new ArrayList<>();
        this.context = context;
    }

    public void addTweetModels(ArrayList<TweetModel> tweetModels, boolean addToTop) {
        int initCount = getItemCount();
        if (addToTop) {
            this.tweetModels.addAll(0, tweetModels);
        } else {
            this.tweetModels.addAll(tweetModels);
        }
        notifyItemRangeInserted(addToTop ? 0 : initCount, tweetModels.size());
    }

    public void remove(int position){
        tweetModels.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewGroup v = (ViewGroup) LayoutInflater.from(parent.getContext()).inflate(R.layout.social_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        TweetModel currentItem = tweetModels.get(position);
        String login = currentItem.getUserModel().getUserName();
        holder.itemView.setVisibility(View.VISIBLE);
        holder.message.setText(currentItem.getMessage());
        holder.username.setText(login);
        holder.login.setText(currentItem.getUserModel().getLogin());
        holder.dateStamp.setText(itemTimeFormat.format(currentItem.getTimestamp()));
        if (currentItem.getUserModel().getAvatar() > 0) {
            loadBitmap(currentItem.getUserModel().getAvatar(), holder.avatar);
        } else {
            //generate avatar here
            holder.avatar.setImageDrawable(TextDrawable.builder().buildRound(login.substring(0, 1), generator.getColor(login)));
        }
    }

    public void loadBitmap(int resId, ImageView imageView) {
        BitmapWorkerTask task = new BitmapWorkerTask(imageView);
        task.execute(resId);
    }

    @Override
    public int getItemCount() {
        return tweetModels.size();
    }

    @Override
    public int getCount() {
        return getItemCount();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView username;
        private TextView login;
        private TextView message;
        private TextView dateStamp;
        private ImageView avatar;

        public ViewHolder(ViewGroup rootView) {
            super(rootView);
            username = (TextView) rootView.findViewById(R.id.username);
            login = (TextView) rootView.findViewById(R.id.login);
            message = (TextView) rootView.findViewById(R.id.message);
            dateStamp = (TextView) rootView.findViewById(R.id.date);
            avatar = (ImageView) rootView.findViewById(R.id.avatar);
        }
    }

    public class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
        private OnItemClickListener listener;

        GestureDetector gestureDetector;

        public RecyclerItemClickListener(Context context, OnItemClickListener listener) {
            this.listener = listener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
            View childView = view.findChildViewUnder(e.getX(), e.getY());
            if (childView != null && listener != null && gestureDetector.onTouchEvent(e)) {
                listener.onItemClick(childView, view.getChildPosition(childView));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) {
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    private class BitmapWorkerTask extends AsyncTask<Integer, Void, Drawable> {
        private final WeakReference<ImageView> imageViewReference;

        public BitmapWorkerTask(ImageView imageView) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            imageViewReference = new WeakReference<>(imageView);
        }

        @Override
        protected Drawable doInBackground(Integer... params) {
            Bitmap avatarBitmap = BitmapFactory.decodeResource(context.getResources(), params[0]);
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

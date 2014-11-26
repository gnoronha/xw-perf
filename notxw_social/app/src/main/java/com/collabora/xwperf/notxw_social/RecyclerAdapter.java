package com.collabora.xwperf.notxw_social;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> implements IListAdapter{
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

    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewGroup v = (ViewGroup) LayoutInflater.from(parent.getContext()).inflate(R.layout.social_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TweetModel currentItem = tweetModels.get(position);
        String login = currentItem.getUserModel().getUserName();
        holder.message.setText(currentItem.getMessage());
        holder.username.setText(login);
        holder.login.setText(currentItem.getUserModel().getLogin());
        holder.dateStamp.setText(itemTimeFormat.format(currentItem.getTimestamp()));
        if (currentItem.getUserModel().getAvatar() > 0) {
            Bitmap avatarBitmap = BitmapFactory.decodeResource(context.getResources(), currentItem.getUserModel().getAvatar());
            holder.avatar.setImageDrawable(new RoundedAvatarDrawable(avatarBitmap));
            holder.avatar.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            //generate avatar here
            holder.avatar.setImageDrawable(TextDrawable.builder().buildRound(login.substring(0, 1), generator.getColor(login)));
        }
    }

    @Override
    public int getItemCount() {
        return tweetModels.size();
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
}

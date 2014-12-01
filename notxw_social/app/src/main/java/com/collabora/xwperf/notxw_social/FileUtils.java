package com.collabora.xwperf.notxw_social;

import android.content.Context;
import android.util.JsonReader;
import android.util.JsonWriter;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;

public class FileUtils {
    private static final String TAG = FileUtils.class.getSimpleName();

    private static final String FEED_FILE_NAME = "tweets.json";

    private static final String USERNAME = "userName";
    private static final String LOGIN = "login";
    private static final String AVATAR = "avatar";
    private static final String MESSAGE = "message";
    private static final String TIMESTAMP = "timestamp";

    private final Context context;

    public FileUtils(Context context) {
        this.context = context;
        File jsonFile = new File(FEED_FILE_NAME);
        if (!jsonFile.exists()) {
            Log.d(TAG, "Not yet created");
        }
    }

    public ArrayList<TweetModel> readFeed() {
        ArrayList<TweetModel> feed = new ArrayList<>();
        try {
            JsonReader reader = new JsonReader(new InputStreamReader(context.openFileInput(FEED_FILE_NAME)));
            reader.beginArray();
            while (reader.hasNext()) {
                feed.add(readTweet(reader));
            }
            reader.endArray();
        } catch (FileNotFoundException e) {
            Log.e(TAG, "FileNotFound", e);
        } catch (IOException e) {
            Log.e(TAG, "IO", e);
        }
        return feed;
    }

    public void writeFeed(ArrayList<TweetModel> feed) {
        JsonWriter writer;
        try {
            writer = new JsonWriter(new OutputStreamWriter(context.openFileOutput(FEED_FILE_NAME, Context.MODE_PRIVATE)));
            writer.setIndent("  ");
            writeFeedArray(writer, feed);
            writer.close();
        } catch (FileNotFoundException e) {
            Log.e(TAG, "FileNotFound", e);
        } catch (IOException e) {
            Log.e(TAG, "IO", e);
        }
    }

    private void writeFeedArray(JsonWriter writer, ArrayList<TweetModel> feed) throws IOException {
        writer.beginArray();
        for (TweetModel tweetModel : feed) {
            writeTweet(writer, tweetModel);
        }
        writer.endArray();
    }

    private void writeTweet(JsonWriter writer, TweetModel tweetModel) throws IOException {
        writer.beginObject();
        writer.name(USERNAME).value(tweetModel.getUserModel().getUserName());
        writer.name(LOGIN).value(tweetModel.getUserModel().getLogin());
        writer.name(AVATAR).value(tweetModel.getUserModel().getAvatar());
        writer.name(MESSAGE).value(tweetModel.getMessage().toString());
        writer.name(TIMESTAMP).value(tweetModel.getTimestamp().getTime());
        writer.endObject();
    }

    private TweetModel readTweet(JsonReader reader) throws IOException {
        reader.beginObject();
        TweetModel tweetModel = new TweetModel();
        UserModel userModel = new UserModel();
        while (reader.hasNext()) {
            String name = reader.nextName();
            switch (name) {
                case MESSAGE:
                    tweetModel.setMessage(reader.nextString());
                    break;
                case TIMESTAMP:
                    tweetModel.setTimestamp(new Date(reader.nextLong()));
                    break;
                case LOGIN:
                    userModel.setLogin(reader.nextString());
                    break;
                case USERNAME:
                    userModel.setUserName(reader.nextString());
                    break;
                case AVATAR:
                    userModel.setAvatar(reader.nextInt());
                    break;
                default:
                    reader.skipValue();
                    break;
            }
        }
        reader.endObject();
        tweetModel.setUserModel(userModel);
        return tweetModel;
    }
}

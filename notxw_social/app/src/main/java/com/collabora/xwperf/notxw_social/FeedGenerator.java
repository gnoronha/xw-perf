package com.collabora.xwperf.notxw_social;

/*
 * Copyright 2014 Intel Corporation. All rights reserved.
 * License: BSD-3-clause-Intel, see LICENSE.txt
 */

import android.os.SystemClock;

import com.collabora.xwperf.fps_measure_module.MeasurementLogger;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class FeedGenerator {
    private MeasurementLogger logger = MeasurementLogger.getInstance();

    public static final int MAX_ITEMS = 1000;
    private static final int INITIAL_ITEMS_BATCH = 50;
    private static final int EXTRA_ITEMS_BATCH = 20;
    private static final int REFRESH_BATCH = 5;
    private static final String[] possibleParticipants = new String[]{
            "Amelia", "Olivia", "Emily", "Jessica", "Ava", "Isla", "Poppy", "Isabella", "Sophie", "Mia",
            "Oliver", "Jack", "Harry", "Jacob", "Charlie", "Thomas", "Oscar", "William", "James", "George"};
    private static final String[] emoticons = new String[]{"^_^", "o_O", "\\o/", "/o\\", ":)", ":(", ":)", ":(", ":/", ":D", "&lt;3",};

    private static final int[] possibleAvatars = new int[]{
            R.drawable.avatar_1,
            R.drawable.avatar_2,
            R.drawable.avatar_3,
            R.drawable.avatar_4,
            R.drawable.avatar_5,
            R.drawable.avatar_6,
            R.drawable.avatar_7,
            R.drawable.avatar_8,
            R.drawable.avatar_9,
            R.drawable.avatar_10,
            R.drawable.avatar_11,
            R.drawable.avatar_12
    };
    private ArrayList<UserModel> users;
    private Random random;

    public FeedGenerator() {
        users = new ArrayList<>(possibleParticipants.length);
        random = new Random();
        generateUsers();
    }

    public ArrayList<TweetModel> getFirstBatch() {
        return generateFeed(INITIAL_ITEMS_BATCH);
    }

    public ArrayList<TweetModel> loadMore() {
        return generateFeed(EXTRA_ITEMS_BATCH);
    }

    public ArrayList<TweetModel> refresh() {
        return generateFeed(REFRESH_BATCH);
    }

    private ArrayList<TweetModel> generateFeed(int numberOfItems) {
        long startGenerate = SystemClock.elapsedRealtime();
        long dateStamp = startGenerate;
        ArrayList<TweetModel> tweetModels = new ArrayList<>(numberOfItems);
        for (int i = 0; i < numberOfItems; i++) {
            TweetModel model = new TweetModel();
            model.setMessage(generateMessage());
            model.setUserModel(users.get(random.nextInt(users.size())));
            model.setTimestamp(new Date(dateStamp));
            tweetModels.add(model);
            dateStamp += 42000;//every 42 seconds
        }
        logger.addMeasure(startGenerate, SystemClock.elapsedRealtime() - startGenerate, MeasurementLogger.PerformanceMarks.MEASURE_FEED_LOAD + numberOfItems);
        return tweetModels;
    }

    private void generateUsers() {
        int j = 0;
        for (String possibleParticipant : possibleParticipants) {
            UserModel userModel = new UserModel();
            userModel.setUserName(possibleParticipant);
            userModel.setLogin("@" + possibleParticipant.toLowerCase());
            if (random.nextFloat() < 0.5) {
                userModel.setAvatar(possibleAvatars[j]);
                j = (j + 1) % possibleAvatars.length;
            }
            users.add(userModel);
        }
    }

    private String generateMessage() {
        // Used to generate gibberish. Deliberately omit the vowels
        // to minimize the chance that we generate something offensive.
        String possibleLetters = "qwrtyzxcvbnmsdfghjkl";
        StringBuilder sb = new StringBuilder();
        int nWords = (int) (Math.floor(random.nextFloat() * 10) + 3);
        int i;
        for (i = 0; i < nWords; i++) {
            if (random.nextFloat() < 0.1) {
                sb.append(" <a href=\"javascript:console.log(&quot;link clicked&quot;)\">@");
                sb.append(getRandomItem(possibleParticipants).toLowerCase());
                sb.append("</a>");
            } else if (Math.random() < 0.05) {
                sb.append(' ').append(getRandomItem(emoticons));
            } else {
                int nLetters = (int) (Math.floor(random.nextFloat() * 10) + 1);
                sb.append(" ");
                for (int j = 0; j < nLetters; j++) {
                    sb.append(possibleLetters.charAt(random.nextInt(possibleLetters.length())));
                }
            }
        }

        nWords = (int) Math.floor(Math.random() * 3);

        for (i = 0; i < nWords; i++) {
            int nLetters = (int) (Math.floor(Math.random() * 10) + 3);
            sb.append("<a href=\"javascript:console.log(&quot;link clicked&quot;)\">#");
            for (int j = 0; j < nLetters; j++) {
                sb.append(possibleLetters.charAt(random.nextInt(possibleLetters.length())));
            }
            sb.append("</a>");
        }
        return sb.toString();
    }

    private String getRandomItem(String[] strings) {
        return strings[random.nextInt(strings.length)];
    }
}

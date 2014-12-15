package com.collabora.xwperf.notxw_contacts.data;

/*
 * Copyright 2014 Intel Corporation. All rights reserved.
 * License: BSD-3-clause-Intel, see LICENSE.txt
 */

import android.os.SystemClock;

import com.collabora.xwperf.fps_measure_module.MeasurementLogger;
import com.collabora.xwperf.notxw_contacts.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class ContactGenerator {

    public static final int MAX_ITEMS = 500;

    private static final String[] domains = new String[]{"example.com", "example.net", "example.org"};

    private static final String[] emailPrefixes = new String[]{"", "me.", "mail.", "email."};

    private static final String[] forenames = new String[]{
            "Abigail", "Alexander", "Alfie", "Amelia", "Ava", "Charlie", "Daniel", "Dylan", "Elizabeth",
            "Ella", "Emily", "Emma", "Ethan", "George", "Harry", "Isabella", "Isla", "Jack", "Jacob",
            "James", "Jayden", "Jessica", "Lewis", "Liam", "Logan", "Lucas", "Lucy", "Madison",
            "Mason", "Mia", "Michael", "Millie", "Noah", "Oliver", "Olivia", "Oscar", "Poppy",
            "Riley", "Ruby", "Sophia", "Sophie", "Thomas", "William"};

    private static final String[] surnames = new String[]{
            "Alexander", "Ali", "Anderson", "Brown", "Campbell", "Clark", "Clarke", "Cox", "Davies",
            "Davis", "Doherty", "Driscoll", "Edwards", "Evans", "Graham", "Green", "Griffiths", "Hall",
            "Hamilton", "Hughes", "Jackson", "James", "Jenkins", "Johnson", "Johnston", "Jones", "Kelly",
            "Khan", "Lewis", "MacDonald", "Martin", "Mason", "McLaughlin", "Mitchell", "Moore", "Morgan",
            "Morrison", "Moss", "Murphy", "Murray", "Owen", "O\'Neill", "Patel", "Paterson", "Phillips",
            "Price", "Quinn", "Rees", "Reid", "Roberts", "Robertson", "Robinson", "Rodríguez", "Rose",
            "Ross", "Sanders", "Scott", "Smith", "Smyth", "Stewart", "Taylor", "Thomas", "Thompson",
            "Thomson", "Walker", "Watson", "White", "Williams", "Wilson", "Wood", "Wright", "Young"};

    private static final Integer[] possibleAvatars = new Integer[]{
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

    public static ArrayList<ContactModel> generateContactList() {
        MeasurementLogger logger = MeasurementLogger.getInstance();
        Random random = new Random();
        long startGenerate = SystemClock.elapsedRealtime();
        ArrayList<ContactModel> contacts = new ArrayList<>(MAX_ITEMS);

        for (int i = 0; i < MAX_ITEMS; i++) {
            ContactModel contact = new ContactModel();
            //name
            String forename = getRandomItem(random, forenames);
            String surname = getRandomItem(random, surnames);

            contact.setName(forename + " " + surname);
            //email
            String email = null;
            if (random.nextFloat() < 0.5) {
                email = forename.toLowerCase() + "@" + getRandomItem(random, emailPrefixes) + getRandomItem(random, domains);
            } else if (random.nextFloat() < 0.5) {
                email = forename.toLowerCase() + "." + surname + "@" + getRandomItem(random, emailPrefixes) + getRandomItem(random, domains);
            }
            contact.setEmail(email);
            //avatar
            if (random.nextFloat() < 0.5) {
                contact.setAvatarResId(getRandomItem(random, possibleAvatars));
            }
            // phone number
            // https://en.wikipedia.org/wiki/Fictitious_telephone_number
            if (random.nextFloat() < 0.75) {
                contact.setPhone("+44 7700 900" + Math.floor(random.nextFloat() * 999));
            }
            //birthday
            if (random.nextFloat() < 0.2) {
                // uniformly random between 1970-01-01 and 2000-01-01
                contact.setBirthday(new Date((long) (random.nextFloat() * new Date(2000, 1, 1).getTime())));
            }
            contact.setFavorite(i < 10);
            contacts.add(contact);
        }
        logger.addMeasure(startGenerate, SystemClock.elapsedRealtime() - startGenerate, MeasurementLogger.PerformanceMarks.MEASURE_FEED_LOAD + MAX_ITEMS);
        return contacts;
    }


    private static <T> T getRandomItem(Random random, T... objects) {
        return objects[random.nextInt(objects.length)];
    }
}

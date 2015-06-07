package com.hotmale.packagefriendfinder;

import android.app.IntentService;
import android.content.Intent;
import android.os.SystemClock;

import java.util.ArrayList;

/**
 * Created by Scott on 6/5/2015.
 */
public class BackgroundService extends IntentService implements AsyncResponse {

    public BackgroundService() {
        super("IntentService");
        db.delegate = this;
    }
    Database db;
    @Override
    protected void onHandleIntent(Intent intent) {
        // Get data from incoming intent

        String notificationText;

        while(true) {
            SystemClock.sleep(10000); // 10 seconds
            if(db.checkNotifications()) {
                notificationText = "You have a new request!";
            }
            // Do work son
        }

    }

    @Override
    public void processFinish(String output) {

    }

    @Override
    public void processFinish(ArrayList<String> output) {

    }
}

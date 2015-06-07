package com.hotmale.packagefriendfinder;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;

/**
 * Created by Scott on 6/5/2015.
 * service.
 * polls database.
 * maybe it will do notifications as well???
 */

public class Background extends IntentService implements AsyncResponse {

    Database db;

    public Background() {
        super("Background");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        // Get data from incoming intent
//        Bundle extras = intent.getExtras();

        while(true) {
            SystemClock.sleep(10000); // 10 seconds

            db = new Database(getApplicationContext());
            db.delegate = this;

            Database.Query q = db.newQuery(Database.TYPE.CHECKUPDATES, null);
            db.execute(q);
        }

    }

    @Override
    public void processFinish(Database.QueryResult q) {
        if(q.t == Database.TYPE.CHECKUPDATES) {
            for(Object o : q.output) {
                Database.Notification n = (Database.Notification) o;
                switch(n.type) {
                    case "friend_req": {
                        friendReqNotify(n);
                    }
                }
            }
        }
    }

    protected void friendReqNotify(Database.Notification n) {
//        NotificationCompat.Action action = new NotificationCompat.Action.Builder(
//
//        );

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.paint)
                        .setContentTitle("New Friend Request")
                        .setContentText(n.content)
//                        .addAction(action);
;

// Creates an explicit intent for an Activity in your app
//        Intent resultIntent = new Intent(this, ResultActivity.class);

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
// Adds the back stack for the Intent (but not the Intent itself)
//        stackBuilder.addParentStack(ResultActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
//        stackBuilder.addNextIntent(resultIntent);
//        PendingIntent resultPendingIntent =
//                stackBuilder.getPendingIntent(
//                        0,
//                        PendingIntent.FLAG_UPDATE_CURRENT
//                );
//        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        int mId = 1;
        mNotificationManager.notify(mId, mBuilder.build());
    }

}

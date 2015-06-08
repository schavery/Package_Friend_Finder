package com.hotmale.packagefriendfinder;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * Created by Scott on 6/5/2015.
 * service.
 * polls database.
 * maybe it will do notifications as well???
 */

public class Background extends IntentService implements AsyncResponse {

    Database db;
    NotificationCompat.Builder builder;
    NotificationManager notificationManager;

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
                    case "friend_request": {
                        friendReqNotify(n);
                    }
                }
            }
        }
    }

    protected void friendReqNotify(Database.Notification n) {
        Intent acceptFriendRequestI = new Intent(this, AcceptFriendRequestReceiver.class);
        acceptFriendRequestI.setAction("com.hotmale.packageFriendFinder.CFR");

        Bundle b = new Bundle();
        b.putInt("id", n.source);
        acceptFriendRequestI.putExtras(b);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, 0, acceptFriendRequestI, PendingIntent.FLAG_CANCEL_CURRENT);



        builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.perm_group_accessibility_features)
                .setContentTitle("New Friend Request")
                .setContentText(n.content)
                .addAction(R.drawable.perm_group_social_info, "Accept", pendingIntent);


        notificationManager = (NotificationManager) getSystemService(
                getApplicationContext().NOTIFICATION_SERVICE);

        notificationManager.notify(n.source, builder.build());
    }
}

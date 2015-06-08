package com.hotmale.packagefriendfinder;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by savery on 6/7/15.
 * not a default comment here
 */
public class AcceptFriendRequestReceiver extends BroadcastReceiver {
    public void onReceive(Context ctx, Intent intent) {
        int notifyID = intent.getExtras().getInt("id");

        NotificationManager notificationManager =
                (NotificationManager) ctx.getSystemService(ctx.NOTIFICATION_SERVICE);

        notificationManager.cancel(notifyID);

        // do a db
        Database db = new Database(ctx);

        Database.Query q = db.newQuery(
                Database.TYPE.ADDFRIEND, Integer.toString(notifyID));

        db.execute(q);
    }
}

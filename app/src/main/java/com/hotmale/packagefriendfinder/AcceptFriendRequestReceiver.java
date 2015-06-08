package com.hotmale.packagefriendfinder;

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
        Background b = new Background();

        b.friendReqComplete(notifyID);
    }
}

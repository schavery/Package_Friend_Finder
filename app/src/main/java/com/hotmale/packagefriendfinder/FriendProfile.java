package com.hotmale.packagefriendfinder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.hotmale.packagefriendfinder.Database.Query;
import com.hotmale.packagefriendfinder.Database.TYPE;

/**
 * Created by savery on 6/7/15.
 * do a profile.
 * jimmy crack corn
 */
public class FriendProfile extends ActionBarActivity
        implements AsyncResponse {

    Friends.Friend f;
    Database db;

    public FriendProfile() {
        f = new Friends.Friend();
    }

    public void processFinish(Database.QueryResult q) {
        // handle the thing.
        if(q.t == Database.TYPE.ADDFRIENDNOTIFY) {
            Toast.makeText(getApplicationContext(), "Sent",
                    Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar bar = getSupportActionBar();
        bar.setTitle("Profile");
        bar.setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.profile);

        db = new Database(this);
        db.delegate = this;

        if(savedInstanceState != null) {
            // not using this part yet.
            f.name = savedInstanceState.getString("name");
            f.id = savedInstanceState.getInt("id");
            f.is_my_friend = savedInstanceState.getBoolean("is_my_friend");
        } else {
            Intent i = getIntent();
            Bundle b = i.getExtras();

            f.name = b.getString("name");
            f.id = b.getInt("id");
            f.is_my_friend = b.getBoolean("is_my_friend");
        }

        TextView tv = (TextView) findViewById(R.id.profile_name);
        tv.setText(f.name);

        if(f.is_my_friend) {
            LinearLayout l = (LinearLayout) findViewById(R.id.not_friends);
            l.setVisibility(LinearLayout.GONE);
        } else {
            LinearLayout l = (LinearLayout) findViewById(R.id.is_friends);
            l.setVisibility(LinearLayout.GONE);
        }


        // set up button click handlers.

        final Button add_friend = (Button) findViewById(R.id.add_friend);
        View.OnClickListener afl = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Sending friend request",
                        Toast.LENGTH_SHORT).show();

                // disable the button
                add_friend.setEnabled(false);

                // XXX remember this somehow?
                // maybe it isn't possible :(

                // send db thing
                Query q = db.newQuery(TYPE.ADDFRIENDNOTIFY, Integer.toString(f.id));
                db.execute(q);
            }
        };
        add_friend.setOnClickListener(afl);

    }
}

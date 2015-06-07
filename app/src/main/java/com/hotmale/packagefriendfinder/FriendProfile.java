package com.hotmale.packagefriendfinder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by savery on 6/7/15.
 * do a profile.
 * jimmy crack corn
 */
public class FriendProfile extends ActionBarActivity {
    private Friends.Friend f;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar bar = getSupportActionBar();
        bar.setTitle("Profile");
        bar.setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.profile);

        f = new Friends.Friend();

        if(savedInstanceState != null) {
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
    }
}

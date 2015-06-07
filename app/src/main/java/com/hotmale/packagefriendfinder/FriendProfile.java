package com.hotmale.packagefriendfinder;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
public class FriendProfile extends Activity {
    private Friends.Friend f;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        LinearLayout root = (LinearLayout)findViewById(android.R.id.list).getParent().getParent().getParent();
        FrameLayout root = (FrameLayout) getWindow().getDecorView().findViewById(android.R.id.content);
        Toolbar bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.settings_toolbar, root, false);
        root.addView(bar, 0); // insert at top
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        setContentView(R.layout.profile);

//        Bundle b = getArguments();
        f = new Friends.Friend();

        if(savedInstanceState != null) {
            f.name = savedInstanceState.getString("name");
            f.id = savedInstanceState.getInt("id");
            f.is_my_friend = savedInstanceState.getBoolean("is_my_friend");
        }

    }

//    public FriendProfile() {}
//
//    @Override
//    public View onCreateView(LayoutInflater li, ViewGroup c, Bundle b) {
//        View v = li.inflate(R.layout.profile, c, false);
//
//        TextView name = (TextView) v.findViewById(R.id.profile_name);
//
//        name.setText(f.name);
//
//        return v;
//    }

//        // maybe don't use this? start activity instead.
//        public static FriendProfile newInstance(Friend f) {
//            FriendProfile fp = new FriendProfile();
//
//            Bundle args = new Bundle();
//            args.putBoolean("is_friend", f.is_my_friend);
//            args.putInt("id", f.id);
//            args.putString("name", f.name);
//
//            fp.setArguments(args);
//            return fp;
//        }

}

package com.hotmale.packagefriendfinder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import com.hotmale.packagefriendfinder.Database.Query;
import com.hotmale.packagefriendfinder.Database.TYPE;

/**
 * Created by savery on 6/3/15.
 * Same deal.
 */
public class Friends extends ListFragment implements AsyncResponse {
    // XXX implement shared preference on preference change hook.

    Database db;

    public void processFinish(Database.QueryResult output) {

        switch(output.t) {
            case USERLIST: {
                setupAdapter(output.output);
                break;
            }

            case GETBYNAME: {
                startFriendProfile(Integer.parseInt(output.output.get(0)));
                break;
            }

        }
    }

    private void startFriendProfile(int id) {
//        lastClickedFriend.id = id;
//        FriendProfile fp = FriendProfile.newInstance(lastClickedFriend);
        Intent i = new Intent(getActivity(), FriendProfile.class);
        Bundle extras = i.getExtras();
        extras.putString("name", lastClickedFriend.name);
        extras.putInt("id", id);
        extras.putBoolean("is_my_friend", lastClickedFriend.is_my_friend);
        startActivity(i);
    }

    private void setupAdapter(ArrayList<String> arrayList) {
        if(getView() != null) {
            // these lines will be useful when the custom list adapter works
            ArrayAdapter<String> aa = new FriendArrayAdapter(getActivity(), arrayList);

            // non-customizable way of doing it
//            ArrayAdapter<String> aa = new ArrayAdapter<>(
//                    getActivity(),
//                    android.R.layout.simple_list_item_1,
//                    arrayList
//            );

            setListAdapter(aa);
        }
    }


    @Override
    public void onAttach(Activity activity) {
        // set up the db only when we have a context
        super.onAttach(activity);
        db = new Database(activity);
        Query q = db.newQuery(TYPE.USERLIST, null);
        db.execute(q);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db.delegate = this;
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        String name = (String) getListAdapter().getItem(position);
        Friend fff = new Friend();

        if(name.matches("\\+.*")) {
            fff.is_my_friend = true;
            fff.name = name.substring(1);
        } else {
            fff.name = name;
        }

        lastClickedFriend = fff;

        db = new Database(getActivity());

        Query q = db.newQuery(TYPE.GETBYNAME, name);

        db.execute(q);
    }


    public static class FriendProfile extends Fragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {

        }

        // maybe don't use this? start activity instead.
        public static FriendProfile newInstance(Friend f) {
            FriendProfile fp = new FriendProfile();

            Bundle args = new Bundle();
            args.putBoolean("is_friend", f.is_my_friend);
            args.putInt("id", f.id);
            args.putString("name", f.name);

            fp.setArguments(args);
            return fp;
        }

    }

    /**
     * not the java way. sorry everyone
     */
    public class Friend {
        public String name;
        public int id;
        public boolean is_my_friend;
    }

    private Friend lastClickedFriend;


    /**
     * Should allow customization of rows in the friends list.
     * Doesn't work.
     */
    public class FriendArrayAdapter extends ArrayAdapter<String> {
        private final Context ctx;
        private final ArrayList<String> values;

        public FriendArrayAdapter(Context context, ArrayList<String> al) {
            super(context, -1, al);
            this.ctx = context;
            this.values = al;
        }

        @Override
        public View getView(int pos, View convertView, ViewGroup parent) {
            LayoutInflater li = (LayoutInflater) ctx.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);

            if(convertView == null) {
                convertView = li.inflate(R.layout.friend_item, parent, false);
                Log.d("getView", "convertView was null");
            } else {
                Log.d("getView", "convertView was not null");
            }


            TextView friend = (TextView) convertView.findViewById(R.id.friend_is_friend);
            TextView name = (TextView) convertView.findViewById(R.id.friend_name);

            String friend_name;
            if(values.get(pos).matches("\\+\\w")) {
                friend_name = values.get(pos).substring(1);
                friend.setText("Send package");
            } else {
                friend_name = values.get(pos);
                friend.setText("Add friend");
            }

            name.setText(friend_name);
//            Log.d("ArrayAdapter", "the friend name is " + friend_name);

            return convertView;
        }
    }

}


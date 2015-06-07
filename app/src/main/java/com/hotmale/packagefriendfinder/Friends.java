package com.hotmale.packagefriendfinder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import com.hotmale.packagefriendfinder.Database.Query;
import com.hotmale.packagefriendfinder.Database.TYPE;

/**
 * Created by savery on 6/3/15.
 * Same deal.
 */
public class Friends extends ListFragment
        implements AsyncResponse, OnSharedPreferenceChangeListener {

    Database db;

    // XXX doesn't fire
    public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
        if(key.equals("example_list")) {
            // then we have updated the user. we need to refresh the list
            Log.d("preference change", "key did match");
            db = new Database(getActivity());
            Query q = db.newQuery(TYPE.USERLIST, null);
            db.execute(q);
        } else {
            Log.d("preference change", "key didn't match");
        }
    }

    public void processFinish(Database.QueryResult output) {

        switch(output.t) {
            case USERLIST: {
                setupAdapter(output.output);
                break;
            }

//            case GETBYNAME: {
//                startFriendProfile(Integer.parseInt(output.output.get(0)));
//                break;
//            }

        }
    }

//    private void startFriendProfile(int id) {
////        lastClickedFriend.id = id;
////        FriendProfile fp = FriendProfile.newInstance(lastClickedFriend);
//        Intent i = new Intent(getActivity(), FriendProfile.class);
//        Bundle extras = i.getExtras();
//        extras.putString("name", lastClickedFriend.name);
//        extras.putInt("id", id);
//        extras.putBoolean("is_my_friend", lastClickedFriend.is_my_friend);
//        startActivity(i);
//    }

    private void setupAdapter(ArrayList<Object> arrayList) {
        // turn the objects into friends.
        ArrayList<Friend> af = new ArrayList<>();

        for(Object o : arrayList) {
            af.add((Friend) o);
        }

        if(getView() != null) {
            FriendArrayAdapter faa = new FriendArrayAdapter(getActivity(), af);
            setListAdapter(faa);
        }
    }

    /**
     * not the java way. sorry everyone
     */
    public static class Friend {
        public String name;
        public int id;
        public boolean is_my_friend;

        public Friend() {
            this.is_my_friend = false;
        }

        public String toString() {
            return name;
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
        Friend f = (Friend) getListAdapter().getItem(position);

        Intent i = new Intent(getActivity(), FriendProfile.class);

        i.putExtra("name", f.name);
        i.putExtra("id", f.id);
        i.putExtra("is_my_friend", f.is_my_friend);

        startActivity(i);
    }


    /**
     * Customize display of rows in friend list
     */
    public class FriendArrayAdapter extends ArrayAdapter<Friend> {
        private final Context ctx;
        private final ArrayList<Friend> values;

        public FriendArrayAdapter(Context context, ArrayList<Friend> al) {
            super(context, -1, al);

            this.ctx = context;
            this.values = al;
        }

        @Override
        public View getView(int pos, View convertView, ViewGroup parent) {
            LayoutInflater li = (LayoutInflater) ctx.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);

            convertView = li.inflate(R.layout.friend_item, parent, false);

            TextView friend = (TextView) convertView.findViewById(R.id.friend_is_friend);
            TextView name = (TextView) convertView.findViewById(R.id.friend_name);

            name.setText(values.get(pos).name);

            if(values.get(pos).is_my_friend) {
                friend.setText("Send package");
            } else {
                friend.setText("Add friend");
            }

            return convertView;
        }
    }

}


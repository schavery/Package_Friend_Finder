package com.hotmale.packagefriendfinder;

import android.app.Activity;
import android.content.Context;
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

    // unused.
    public void processFinish(String output) {}

    public void processFinish(ArrayList<String> output) {

        if(getView() != null) {
//            ArrayAdapter<String> aa = new FriendArrayAdapter(getActivity(), output);
//            setListAdapter(aa);
            ArrayAdapter<String> aa = new ArrayAdapter<>(
                    getActivity(),
                    android.R.layout.simple_list_item_1,
                    output
            );
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
//        FriendProfile fp = new FriendProfile();

//        String name = (String) getListAdapter().getItem(position);

    }


    public static class FriendProfile extends Fragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {

        }

    }


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
                friend.setText("friend");
            } else {
                friend_name = values.get(pos);
                friend.setText("");
            }

            name.setText(friend_name);
//            Log.d("ArrayAdapter", "the friend name is " + friend_name);

            return convertView;
        }
    }

}


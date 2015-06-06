package com.hotmale.packagefriendfinder;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by savery on 6/3/15.
 * Now it's no longer default.
 */
public class Database extends AsyncTask<Database.QueryType, Void, ArrayList<String>> {

    SharedPreferences sharedPref;
    int localUserID;

    // You have to instantiate the database based on what activity you're
    // loading from
    public Database(Context ctx) {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    private void getUserID() {
        // get id local preference
        localUserID = Integer.parseInt(sharedPref.getString("example_list", ""));
    }

    public enum QueryType {
        ADDFRIEND,
        RECOMMEND,
        USERLIST, // get all the user information to create intro list fragment
        MUTUALFRIENDS, // get list of friends of your friend
        DELIVERIES // use to get active deliveries
        //, ...
    }

    public AsyncResponse delegate = null;

    protected ArrayList<String> doInBackground(QueryType... queryTypes) {
        ArrayList<String> al = new ArrayList<>();

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        String url = "jdbc:postgresql://192.155.85.51:5432/hotmale?user=hotmale&password=bigfart";
        Connection conn;

        try {
            DriverManager.setLoginTimeout(5);
            conn = DriverManager.getConnection(url);
            PreparedStatement ps = null;

            QueryType firstQ = queryTypes[0];

            for(QueryType qt : queryTypes) {
                switch(qt) {
                    case USERLIST: {
                        // prep for the results
                        getUserID();
                        ps = conn.prepareStatement("SELECT * FROM users");
                        break;
                    }

                    case MUTUALFRIENDS: {
                        // create statement to insert into
                        ps = conn.prepareStatement("SELECT friends FROM users WHERE id=?");

                        getUserID();

                        // insert parameter
                        ps.setInt(1, localUserID);
                        break;
                    }

                    case DELIVERIES: {
                        ps = conn.prepareStatement(
                                "SELECT * FROM deliveries WHERE status != \"done\"");
                        break;
                    }
                }

                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    switch(qt) {
                        case USERLIST: {

                            if(rs.getInt("id") != localUserID) {
                                // we'll parse this later.
                                al.add(rs.getString("id") + ',' + rs.getString("name"));
                            }

                            // we'll have to loop through the list again.
                            if(rs.getInt("id") == localUserID) {
                                al.add("XPQ" + rs.getString("friends"));
                            }

                            break;
                        }

                        case MUTUALFRIENDS: {
                            // this is kinda shitty, you have to make two queries here.
                            String p;

                            p = rs.getString("friends");
                            String [] ids = p.split(",");
                            List<String> idList = Arrays.asList(ids);

                            al.addAll(idList);
                            break;
                        }

                        case DELIVERIES: {
                            al.add(rs.getString("status"));
                            break;
                        }
                    }
                }

                rs.close();
                ps.close();
                conn.close();

                // only do first query
                break;
            }

            if(firstQ == QueryType.USERLIST) {
                al = collateFriends(al);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return al;
    }

    private ArrayList<String> collateFriends(ArrayList<String> al) {
        String [] friends = new String [al.size() + 1];
        String friendList = "";

        for(String p : al) {
            if(p.matches("XPQ.*")) {
                friendList = p.substring(3);
            } else {
                String [] parts = p.split(",");
                int idx = Integer.parseInt(parts[0]);
                friends[idx] = parts[1];
            }
        }

        Log.d("collator", friendList + '\n');

        if(friendList.length() > 0) {
            String[] friendIDs = friendList.split(",");
            for (String fID : friendIDs) {
                int id = Integer.parseInt(fID);
                friends[id] = "+" + friends[id];
            }
        }

        al.clear();
        List<String> ls = Arrays.asList(friends);
        al.addAll(ls);
        al.removeAll(Collections.singleton(null));

        return al;
    }

    @Override
    protected void onPostExecute(ArrayList<String> value) {
        delegate.processFinish(value);
    }
}
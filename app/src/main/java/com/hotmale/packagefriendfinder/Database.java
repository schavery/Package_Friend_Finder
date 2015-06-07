package com.hotmale.packagefriendfinder;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by savery on 6/3/15.
 * Now it's no longer default.
 */
public class Database extends AsyncTask<Database.QueryType, Void, ArrayList<String>>
    implements SharedPreferences.OnSharedPreferenceChangeListener {

    SharedPreferences sharedPref;
    int localUserID;
    String status;

    // You have to instantiate the database based on what activity you're
    // loading from
    public Database(Context ctx) {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    private void getUserID() {
        // get id local preference
        localUserID = Integer.parseInt(sharedPref.getString("example_list", ""));
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }

    public enum QueryType {
        USERLIST, // get all the user information to create intro list fragment
        MUTUALFRIENDS, // get list of friends of your friend
        DELIVERIES, // use to get active deliveries
        NOTIFICATIONS // check for notifications
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

            for(QueryType qt : queryTypes) {
                switch(qt) {
                    case USERLIST: {
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
                    case NOTIFICATIONS: {
                        // look for unseen
                        ps = conn.prepareStatement(
                                "SELECT * FROM notifications WHERE status = \"unseen\" ");
                        break;
                    }
                }

                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    switch(qt) {
                        case USERLIST: {
                            String p;
                            p = rs.getString("name");
                            al.add(p);

                            getUserID();

                            // we'll have to loop through the list again.
                            if(rs.getInt("id") == localUserID) {
                                al.add("FRIENDS: " + rs.getString("friends"));
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
                        }

                        case DELIVERIES: {
                            al.add(rs.getString("status"));
                        }
                        case NOTIFICATIONS: {
                            // Check for the new values in the table
                            if(rs.getInt("id") == localUserID) {
                                status = "unseen";
                            }
                            else
                                status = "seen";
                        }
                    }
                }

                rs.close();
                ps.close();
                conn.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return al;
    }

    @Override
    protected void onPostExecute(ArrayList<String> value) {
        delegate.processFinish(value);
    }
    protected boolean checkNotifications() {
        execute(QueryType.NOTIFICATIONS);
        if(status == "unseen")
            return true;
        else
            return false;
    }
}
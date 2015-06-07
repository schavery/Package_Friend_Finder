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
public class Database extends AsyncTask<Database.Query, Void, Database.QueryResult> {

    private SharedPreferences sharedPref;
    private int localUserID;

    // You have to instantiate the database based on what activity you're
    // loading from
    public Database(Context ctx) {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    private void getUserID() {
        // get id local preference
        localUserID = Integer.parseInt(sharedPref.getString("example_list", ""));
    }

    public enum TYPE {
        ADDFRIEND,
        RECOMMEND,
        GETBYNAME,
        USERLIST, // get all the user information to create intro list fragment
        MUTUALFRIENDS, // get list of friends of your friend
        DELIVERIES // use to get active deliveries
        //, ...
    }

    public class Query {

        protected String content;
        protected TYPE t;

        public Query(TYPE t, String content) {
            this.content = content;
            this.t = t;
        }
    }

    public Query newQuery(TYPE t, String content) {
        return new Query(t, content);
    }

    /**
     * Wraps the return from a query so we don't look stupid.
     */
    public class QueryResult {
        public ArrayList<String> output;
        public TYPE t;

        public QueryResult(TYPE t, ArrayList<String> output) {
            this.output = output;
            this.t = t;
        }
    }


    public AsyncResponse delegate = null;


    /**
     * make all the queries.
     * @param queries
     * @return
     */
    protected QueryResult doInBackground(Query... queries) {
        ArrayList<String> al = new ArrayList<>();

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        String url = "jdbc:postgresql://192.155.85.51:5432/hotmale?user=hotmale&password=bigfart";
        Connection conn;

        Query firstQ = queries[0];

        try {
            DriverManager.setLoginTimeout(5);
            conn = DriverManager.getConnection(url);
            PreparedStatement ps = null;



            for(Query q : queries) {
                switch(q.t) {
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

                    case GETBYNAME: {
                        // content might have a + sign in front.
                        String s;
                        if(q.content.matches("\\+.*")) {
                            s = q.content.substring(1);
                        } else {
                            s = q.content;
                        }

                        ps = conn.prepareStatement(
                                "SELECT id FROM users WHERE name='" + s + "'"
                        );

//                        ps.setString(0, s);

                        Log.d("ye olde query string", ps.toString());
                        break;
                    }

                }

                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    switch(q.t) {
                        case USERLIST: {

                            if(rs.getInt("id") != localUserID) {
                                // we'll parse this later.
                                al.add(rs.getString("id") + ',' + rs.getString("name"));
                            }

                            // we'll have to loop through the list again.
                            if(rs.getInt("id") == localUserID) {
                                // xpq is just a string you wont find by chance
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

                        case GETBYNAME: {
                            al.add(rs.getString("id"));
                        }
                    }
                }

                rs.close();
                ps.close();
                conn.close();

                // only do first query
                break;
            }

            if(firstQ.t == TYPE.USERLIST) {
                al = collateFriends(al);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new QueryResult(firstQ.t, al);
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
    protected void onPostExecute(QueryResult value) {
        delegate.processFinish(value);
    }
}
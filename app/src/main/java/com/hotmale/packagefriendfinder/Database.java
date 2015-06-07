package com.hotmale.packagefriendfinder;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import com.hotmale.packagefriendfinder.Friends.Friend;

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
        public ArrayList<Object> output;
        public TYPE t;

        public QueryResult(TYPE t, ArrayList<Object> output) {
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
        ArrayList<Object> al = new ArrayList<>();

        UserList ul = new UserList(); // maybe there's a better place to put this.

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
                        // we need to have localuserid available later, so we call it now.
                        getUserID();

                        ps = conn.prepareStatement("SELECT * FROM users");
                        break;
                    }

                    case MUTUALFRIENDS: {
                        ps = conn.prepareStatement("SELECT friends FROM users WHERE id IN (?, ?)");

                        getUserID();

                        // insert parameter
                        ps.setInt(1, localUserID);
                        ps.setInt(2, Integer.parseInt(q.content));

                        break;
                    }

//                    case DELIVERIES: {
//                        ps = conn.prepareStatement(
//                                "SELECT * FROM deliveries WHERE status != \"done\"");
//                        break;
//                    }

//                    case GETBYNAME: {
//                        // content might have a + sign in front.
//                        String s;
//                        if(q.content.matches("\\+.*")) {
//                            s = q.content.substring(1);
//                        } else {
//                            s = q.content;
//                        }
//
//                        ps = conn.prepareStatement(
//                                // XXX why is prepared statement not working?
//                                "SELECT id FROM users WHERE name='" + s + "'"
//                        );
//
////                        ps.setString(0, s);
//
//                        Log.d("ye olde query string", ps.toString());
//                        break;
//                    }

                }

                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    switch(q.t) {
                        case USERLIST: {

                            if(rs.getInt("id") != localUserID) {
                                Friend f = new Friend();
                                f.id = Integer.parseInt(rs.getString("id"));
                                f.name = rs.getString("name");

                                ul.people.add(f);
                            }

                            // we'll have to loop through the list again,
                            // to set who is friend.
                            if(rs.getInt("id") == localUserID) {
                                ul.friendIds = rs.getString("friends");
                            }

                            break;
                        }

//                        case MUTUALFRIENDS: {
//                            // this is kinda shitty, you have to make two queries here.
//                            String p;
//
//                            p = rs.getString("friends");
//                            String [] ids = p.split(",");
//                            String[] unique = new HashSet<String>(Arrays.asList(ids)).toArray(new String[0]);
//                            List<String> idList = Arrays.asList(unique);
//
//                            al.addAll(idList);
//                            break;
//                        }

//                        case DELIVERIES: {
//                            al.add(rs.getString("status"));
//                            break;
//                        }

//                        case GETBYNAME: {
//                            al.add(rs.getString("id"));
//                        }
                    }
                }

                rs.close();
                ps.close();
                conn.close();

                // only do first query
                break;
            }

            if(firstQ.t == TYPE.USERLIST) {
                // sets Friend.is_my_friend in all results
                al = setFriends(ul);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new QueryResult(firstQ.t, al);
    }

    /**
     * lol more classes
     */
    private class UserList {
        public String friendIds;
        public ArrayList<Object> people;
    }

    private ArrayList<Object> setFriends(UserList ul) {
        String [] sIDs;

        String s = ul.friendIds.replaceAll("\\s+", "");
        sIDs = s.split(",");


        for(String sID : sIDs) {
            int id = Integer.parseInt(sID);

            for(Object o : ul.people) {
                if(((Friend) o).id == id) {
                    ((Friend) o).is_my_friend = true;
                }
            }
        }

        return ul.people;
    }

    @Override
    protected void onPostExecute(QueryResult value) {
        delegate.processFinish(value);
    }
}
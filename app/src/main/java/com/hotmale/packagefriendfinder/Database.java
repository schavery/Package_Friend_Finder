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
import java.util.HashMap;
import java.util.HashSet;

import com.hotmale.packagefriendfinder.Friends.Friend;

/**
 * Created by savery on 6/3/15.
 * Now it's no longer default.
 */
public class Database extends AsyncTask<Database.Query, Void, Database.QueryResult> {

    private SharedPreferences sharedPref;
    private int localUserID;
    private Context ctx;

    private final String addfriendcontent = " wants to be your friend";

    // You have to instantiate the database based on what activity you're
    // loading from
    public Database(Context ctx) {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
        this.ctx = ctx;
    }

    private void getUserID() {
        // get id local preference
        localUserID = Integer.parseInt(sharedPref.getString("example_list", ""));
    }

    public enum TYPE {
        ADDFRIEND,
        RECOMMEND,
        CHECKUPDATES,
        ADDFRIENDNOTIFY,
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

        getUserID();

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

                        ps = conn.prepareStatement("SELECT * FROM users");
                        break;
                    }

                    case MUTUALFRIENDS: {
                        ps = conn.prepareStatement("SELECT friends FROM users WHERE id IN (?, ?)");

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

                    case ADDFRIENDNOTIFY: {
                        ps = conn.prepareStatement(
                                "INSERT INTO notifications (target_id, type, content, source_id) "
                                + "VALUES (?, 'friend_request', ?, ?)");

                        int target = Integer.parseInt(q.content);
                        ps.setInt(1, target);

                        CharSequence [] titles = ctx.getResources()
                                .getTextArray(R.array.pref_example_list_titles);
                        String name = (String) titles[localUserID - 1];

                        ps.setString(2, name + addfriendcontent);

                        ps.setInt(3, localUserID);

                        break;
                    }

                    case CHECKUPDATES: {
                        ps = conn.prepareStatement(
                                "UPDATE notifications SET status = true "
                                + "WHERE target_id=? AND status=false RETURNING *"
                        );

                        ps.setInt(1, localUserID);

                        break;
                    }
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

                        case ADDFRIENDNOTIFY: {
                            // we don't need to return anything.
                            // XXX throws exception on empty result.
                            break;
                        }

                        case CHECKUPDATES: {
                            Notification n = new Notification();
                            n.type = rs.getString("type");
                            n.content = rs.getString("content");

                            al.add(n);
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
    public class UserList {
        public String friendIds;
        public ArrayList<Object> people;

        public UserList() {
            this.people = new ArrayList<>();
        }
    }

    public class Notification {
        public String content;
        public String type;

    }


    private ArrayList<Object> setFriends(UserList ul) {
        String [] sIDs;

        String s = ul.friendIds.replaceAll("\\s+", "");
        sIDs = s.split(",");


        if(sIDs.length > 0) {
            for (String sID : sIDs) {
                int id = Integer.parseInt(sID);

                for (Object o : ul.people) {
                    if (((Friend) o).id == id) {
                        ((Friend) o).is_my_friend = true;
                    }
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
package com.hotmale.packagefriendfinder;

import android.os.AsyncTask;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by savery on 6/3/15.
 * Now it's no longer default.
 */
public class Database extends AsyncTask<Void, Void, String> {

    public AsyncResponse delegate = null;

    protected String doInBackground(Void... params) {
        String retval = "";

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            retval = e.toString();
        }

        String url = "jdbc:postgresql://c.xndr.me:5432/hotmale?user=hotmale&password=bigfart";
        Connection conn;

        try {
            DriverManager.setLoginTimeout(5);
            conn = DriverManager.getConnection(url);
            Statement st = conn.createStatement();

            String sql;
            sql = "SELECT * from \"users\" WHERE 1=1";

            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                retval = rs.getString("name");
                //int temp =
                //retval = rs.getInt("dataVal");
            }

            rs.close();
            st.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            retval = e.toString();
        }

        return retval;
    }

    protected void onPostExecute(String value) {
        delegate.processFinish(value);
    }
}
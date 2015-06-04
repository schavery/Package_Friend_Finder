package com.hotmale.packagefriendfinder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by savery on 6/3/15.
 */
public class Database {

    public static String doQuery() {
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
            sql = "SELECT * from \"dummyTable\" WHERE id=1";

            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                retval = rs.getString("dataVal");
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

//    protected void onPostExecute(String value) {
//        resultArea.setText(value);
//    }
}
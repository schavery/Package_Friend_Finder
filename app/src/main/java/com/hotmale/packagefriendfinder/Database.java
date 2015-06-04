package com.hotmale.packagefriendfinder;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by savery on 6/3/15.
 */
public class Database extends AsyncTask<Void,Void,String> {


    @Override
    protected String doInBackground(Void... params) {
        String retval = "";

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            retval = e.toString();
        }

        String url = "jdbc:postgresql://192.155.85.51:5432/hotmale?user=hotmale&password=bigfart";
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


    protected void onPostExecute(String value) {
        Button bb = new Button();
        bb.setText(value);

    }
}
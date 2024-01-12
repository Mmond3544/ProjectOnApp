package com.example.qrscan;

import static java.lang.Class.forName;

import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;

public class connectionHelper {
    Connection con;
    String uname,pass,host,port,database;

    public Connection connectionclass() {
        host = "145.14.144.145";
        database = "id21481047_data";
        uname = "id21481047_mond";
        pass = "s-am@17_Mm";
        port = "80";

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection connection = null;
        String conurl = null;

        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            conurl = "jdbc:jtds:sqlserver://" + host +":"+ port + ";databasename = " + database + ";user = " + uname + ";password" + pass + ";";
            connection = DriverManager.getConnection(conurl);
        } catch (Exception ex) {
            Log.e("Error", ex.getMessage());
        }
        return connection;
    }
}

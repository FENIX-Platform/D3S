package org.fao.fenix.d3s.server.tools.h2;

import org.h2.Driver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class H2Server {

    private static boolean initialized = false;

    public static void startServer() throws Exception {
    }

    public static void stopServer() throws Exception {
    }

    public static boolean isInitialized() { return isInitialized(); }

    public static void init(Properties initProperties) throws Exception {
        stopServer();
        Class.forName("org.h2.Driver");
        DriverManager.registerDriver(new Driver());
    }

    public static Connection getConnectionToAggregation() throws SQLException { return H2Database.aggregation.getConnection(); }


    //TEST
    public static void main (String ... args) {
        try {
            Connection conn = getConnectionToAggregation();
            System.out.println(conn);
            conn.createStatement().execute("create table test1 (uid varchar(100), name varchar(256), age integer)");
            conn.createStatement().executeUpdate("insert into test1 (uid, name, age) values ('id1','Name 1',10)");

            conn.createStatement().execute(
                    "CREATE ALIAS doubleValue AS $$" +
                    "int doubleValue(int value) {" +
                    "    return value*2;" +
                    "}" +
                    "$$;");

            for (ResultSet r = conn.createStatement().executeQuery("select uid,name,doubleValue( age ) as age from test1"); r.next();)
                System.out.printf("uid=%s, name=%s, age=%d",r.getString("uid"),r.getString("name"),r.getInt("age"));
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}

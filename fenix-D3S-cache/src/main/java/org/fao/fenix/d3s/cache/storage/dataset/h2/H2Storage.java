package org.fao.fenix.d3s.cache.storage.dataset.h2;

import org.fao.fenix.commons.find.dto.filter.DataFilter;
import org.fao.fenix.commons.utils.database.Iterator;
import org.fao.fenix.d3s.cache.dto.StoreStatus;
import org.fao.fenix.d3s.cache.dto.dataset.Table;
import org.fao.fenix.d3s.cache.storage.dataset.DatasetStorage;
import org.fao.fenix.d3s.cache.tools.Server;
import org.h2.jdbcx.JdbcConnectionPool;
import org.h2.tools.RunScript;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

public abstract class H2Storage implements DatasetStorage {
    private boolean initialized = false;
    private JdbcConnectionPool pool;



    //STARTUP FLOW
    @Override
    public void open() throws Exception {
        if (!initialized) {
            Map<String, String> initProperties = org.fao.fenix.commons.utils.Properties.getInstance(
                    "/org/fao/fenix/config/cache/h2.properties",
                    "file:"+Server.CONFIG_FOLDER_PATH + "h2.properties"
            ).toMap();

            initPool(
                    initProperties.get("url"),
                    initProperties.get("usr"),
                    initProperties.get("psw"),
                    Integer.parseInt(initProperties.containsKey("max") ? initProperties.get("max") : "0")
            );

            runScript(initProperties.get("ddl"));
            runScript(initProperties.get("dml"));

            initialized = true;
        }
    }

    private void initPool(String url, String usr, String psw, int maxConnections) {
        pool = JdbcConnectionPool.create(url, usr, psw);
        if (maxConnections>0)
            pool.setMaxConnections(maxConnections);
    }

    private void runScript(String resourceFilePath) throws FileNotFoundException, SQLException {
        if (resourceFilePath!=null && resourceFilePath.trim().length()>0)
            runScript(H2Storage.class.getResourceAsStream(resourceFilePath));
    }
    private void runScript(InputStream input) throws FileNotFoundException, SQLException {
        if (input!=null) {
            Connection connection = getConnection();
            try {
                RunScript.execute(connection, new InputStreamReader(input));
            } finally {
                if (connection != null)
                    connection.close();
            }
        }
    }


    //SHUTDOWN FLOW
    @Override
    public void close() {
        if (pool!=null)
            pool.dispose();
    }


    //Standard utils
    @Override
    public Connection getConnection() throws SQLException {
        Connection connection = pool.getConnection();
        connection.setAutoCommit(false);
        return connection;
    }

    static {
        try {
            Class.forName(org.h2.Driver.class.getName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private String url, usr, psw;

/*    @Override
    public Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(url,usr,psw);
        connection.setAutoCommit(false);
        return connection;
    }
*/
}

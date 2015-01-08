package org.fao.fenix.d3s.cache.storage.dataset;

import org.fao.fenix.d3s.cache.tools.Server;
import org.h2.jdbcx.JdbcConnectionPool;
import org.h2.tools.RunScript;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public abstract class H2Database implements DatasetStorage {
    private boolean initialized = false;
    private JdbcConnectionPool pool;

    //STARTUP FLOW
    @Override
    public void open() throws Exception {
        if (!initialized) {
            Map<String, String> initProperties = org.fao.fenix.commons.utils.Properties.getInstance(
                    Server.CONFIG_FOLDER_PATH + "storage.properties",
                    "/org/fao/fenix/config/storage.properties"
            ).toMap(getStoragePropertiesPrefix());

            initPool(
                    initProperties.get("url"),
                    initProperties.get("usr"),
                    initProperties.get("psw"),
                    Integer.parseInt(initProperties.containsKey("max") ? initProperties.get("max") : "0")
            );

            runScript(initProperties.get("ddl"), getConnection());
            runScript(initProperties.get("dml"), getConnection());

            initialized = true;
        }
    }

    private void initPool(String url, String usr, String psw, int maxConnections) {
        pool = JdbcConnectionPool.create(url, usr, psw);
        if (maxConnections>0)
            pool.setMaxConnections(maxConnections);
    }

    private void runScript(String filePath, Connection connection) throws FileNotFoundException, SQLException {
        File file = filePath!=null ? new File(filePath) : null;
        if (file!=null)
            RunScript.execute(connection, new FileReader(file));
    }


    //SHUTDOWN FLOW
    @Override
    public void close() {
        pool.dispose();
    }


    //Standard utils
    public Connection getConnection() throws SQLException {
        return pool.getConnection();
    }


    //H2 specific server methods
    protected abstract String getStoragePropertiesPrefix();

}

package org.fao.fenix.server.tools.h2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public enum H2Database {
    aggregation("jdbc:h2:mem:aggregation", false);
    private String url;
    private boolean closeConnection;
    private Connection connection;
    private H2Database(String url, boolean closeConnection) { this.url = url; }
    public Connection getConnection() throws SQLException { return closeConnection || connection==null || connection.isClosed() ? connection=DriverManager.getConnection(url) : connection; }
    public void closeConnection(Connection conn) {
        try {
            if (closeConnection && conn!=null && !conn.isClosed())
                conn.close();
        } catch (SQLException e) {}
    }
}

package org.fao.fenix.d3s.server.tools.h2;

import org.fao.fenix.commons.msd.dto.dsd.DSDColumn;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;

public abstract class H2Dao {
    private static Map<Connection,H2Database> connections = new HashMap<Connection, H2Database>();


    public Connection getConnection(H2Database db) {
        try {
            Connection conn = db.getConnection();
            connections.put(conn,db);
            return conn;
        } catch (SQLException e) {
            return null;
        }
    }


    public void closeConnection(Connection conn) {
        try { connections.get(conn).closeConnection(conn); } catch (Exception ex) {}
    }


    //Utils
    protected String createTemporaryTable(H2Database db, String baseName, DSDColumn[] structure, int[] sqlStructure) throws Exception {
        String tableName = "TMP_"+baseName+'_'+System.currentTimeMillis();

        StringBuilder query = new StringBuilder("CREATE TABLE ").append(tableName).append(" (");
        for (int i=0; i<structure.length; i++) {
            switch (sqlStructure[i]) {
                case Types.JAVA_OBJECT:
                    query.append(structure[i].getColumnId()).append(" OTHER, ");
                    break;
                case Types.INTEGER:
                    query.append(structure[i].getColumnId()).append(" INTEGER, ");
                    break;
                case Types.DOUBLE:
                    query.append(structure[i].getColumnId()).append(" DOUBLE, ");
                    break;
                case Types.VARCHAR:
                    query.append(structure[i].getColumnId()).append(" VARCHAR, ");
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported type mapping for internal H2 database");
            }
        }
        query.setLength(query.length()-2);
        query.append(')');

        Connection connection = null;
        try {
            connection = getConnection(db);
            connection.createStatement().execute(query.toString());
        } finally {
            closeConnection(connection);
        }

        return tableName;
    }

    protected void dropTable(H2Database db, String tableName) throws SQLException {
        Connection connection = null;
        try {
            connection = getConnection(H2Database.aggregation);
            connection.createStatement().execute("DROP TABLE "+tableName);
        } finally {
            closeConnection(connection);
        }
    }



}

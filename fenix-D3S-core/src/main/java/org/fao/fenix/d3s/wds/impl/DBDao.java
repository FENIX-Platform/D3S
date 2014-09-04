package org.fao.fenix.d3s.wds.impl;

import org.fao.fenix.d3s.wds.Dao;

import javax.inject.Inject;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

public abstract class DBDao extends Dao {
    @Inject protected DatabaseUtils utils;

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    protected Connection getConnection(String datasource) throws SQLException {
        return DriverManager.getConnection(
                initProperties.getProperty(datasource+".url"),
                initProperties.getProperty(datasource+".usr"),
                initProperties.getProperty(datasource+".psw")
        );
    }

    protected void setData(ResultSet rows) throws SQLException {
        final Iterator<Object[]> rowsIterator = utils.getDataIterator(rows);
        data = new Iterable<Object[]>() {
            @Override
            public Iterator<Object[]> iterator() {
                return rowsIterator;
            }
        };
    }



}

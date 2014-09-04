package org.fao.fenix.d3s.wds.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

public class DatabaseUtils {

    public void fillStatement(PreparedStatement statement, int[] types, Object... row) throws SQLException {
        for (int i=0; i<row.length; i++)
            if (row[i]!=null)
                statement.setObject(i+1, row[i], types[i]);
            else
                statement.setNull(i+1, types[i]);
    }

    public Iterator<Object[]> getDataIterator(final ResultSet source) throws SQLException {
        return new Iterator<Object[]>() {
            private int columnsNumber = source.getMetaData().getColumnCount();
            private Object[] next = null;
            private boolean consumed = true;

            private Object[] loadNext() throws SQLException {
                if (consumed) {
                    consumed = false;
                    if (source.next()) {
                        next = new Object[columnsNumber];
                        for (int i = 0; i < columnsNumber; i++)
                            next[i] = source.getObject(i + 1);
                    } else
                        next = null;
                }

                return next;
            }

            @Override
            public boolean hasNext() {
                try {
                    return loadNext()!=null;
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public Object[] next() {
                Object[] row = null;
                try {
                    row = loadNext();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                consumed = true;
                return row;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}

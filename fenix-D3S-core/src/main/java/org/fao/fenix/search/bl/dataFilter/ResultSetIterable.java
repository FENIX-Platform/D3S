package org.fao.fenix.search.bl.dataFilter;

import java.sql.ResultSet;
import java.util.Iterator;


public class ResultSetIterable implements Iterable<Object[]> {

    private ResultSet rs;
    public ResultSetIterable(ResultSet rs) { this.rs = rs; }

    @Override
    public Iterator<Object[]> iterator() {
        return new Iterator<Object[]>() {
            private boolean nextTaken=false, next=false;
            private int columnsNumber=-1;
            {try {columnsNumber = rs.getMetaData().getColumnCount();} catch (Exception e) {}}
            @Override
            public boolean hasNext() {
                if (!nextTaken && columnsNumber>0) {
                    try {
                        if (!(next=rs.next())) {
                            rs.close();

                        }
                    } catch (Exception e) {next=false;}
                    nextTaken = true;
                }
                return next;
            }

            @Override
            public Object[] next() {
                if (hasNext())
                    try {
                        Object[] row = new Object[columnsNumber];
                        for (int i=0; i<columnsNumber; i++)
                            row[i]=rs.getObject(i+1);
                        return row;
                    } catch (Exception e) { } finally {
                        nextTaken = false;
                    }
                return null;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}

package org.fao.fenix.d3s.server.tools.orient;

import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

public class DatabaseStandards {
    private static ThreadLocal<ODatabase> connection = new ThreadLocal<>();
    private static ThreadLocal<Page> paginationInfo = new ThreadLocal<>();
    private static ThreadLocal<Order> orderingInfo = new ThreadLocal<>();


    protected <T extends ODatabase> T getConnection() {
        try {
            return (T) connection.get();
        } catch (ClassCastException ex) {
            return null;
        }
    }

    public void setConnection(ODatabase c) {
        connection.set(c);
    }

    public Page getPaginationInfo() {
        return paginationInfo.get();
    }

    public void setPaginationInfo(Page p) {
        paginationInfo.set(p);
    }

    public Order getOrderingInfo() {
        return orderingInfo.get();
    }

    public void setOrderingInfo(Order o) {
        orderingInfo.set(o);
    }

}

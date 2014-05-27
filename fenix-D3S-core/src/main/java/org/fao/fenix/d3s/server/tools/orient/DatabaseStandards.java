package org.fao.fenix.d3s.server.tools.orient;

import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

public class DatabaseStandards {
    private static ThreadLocal<ODatabase> connection = new ThreadLocal<>();
    private static ThreadLocal<Page> paginationInfo = new ThreadLocal<>();
    private static ThreadLocal<Order> orderingInfo = new ThreadLocal<>();


    public ODatabase getConnection() {
        return connection.get();
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


    public OObjectDatabaseTx getOConnection() {
        ODatabase conn = connection.get();
        return conn!=null && conn instanceof OObjectDatabaseTx ? (OObjectDatabaseTx)conn : null;
    }
    public ODatabaseDocumentTx getDConnection() {
        ODatabase conn = connection.get();
        return conn!=null && conn instanceof ODatabaseDocumentTx ? (ODatabaseDocumentTx)conn : null;
    }
    public OrientGraph getGConnection() {
        ODatabase conn = connection.get();
        return conn!=null && conn instanceof OrientGraph ? (OrientGraph)conn : null;
    }
}

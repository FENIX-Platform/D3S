package org.fao.fenix.d3s.server.tools.orient;

import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;

public class DatabaseStandards {
    private static ThreadLocal<OGraphDatabase> connection = new ThreadLocal<>();
    private static ThreadLocal<Page> paginationInfo = new ThreadLocal<>();


    public OGraphDatabase getConnection() {
        return connection.get();
    }

    public void setConnection(OGraphDatabase c) {
        connection.set(c);
    }

    public Page getPaginationInfo() {
        return paginationInfo.get();
    }

    public void setPaginationInfo(Page p) {
        paginationInfo.set(p);
    }
}

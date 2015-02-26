package org.fao.fenix.d3s.server.tools.orient;

import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import org.fao.fenix.commons.utils.Language;
import org.fao.fenix.commons.utils.Order;
import org.fao.fenix.commons.utils.Page;

import javax.servlet.http.HttpServletRequest;

public class DatabaseStandards {
    public static ThreadLocal<HttpServletRequest> request = new ThreadLocal<>();
    public static ThreadLocal<OObjectDatabaseTx> connection = new ThreadLocal<>();
    public static ThreadLocal<Page> paginationInfo = new ThreadLocal<>();
    public static ThreadLocal<Order> orderingInfo = new ThreadLocal<>();
    public static ThreadLocal<Language[]> languageInfo = new ThreadLocal<>();


    public OObjectDatabaseTx getConnection() {
        return connection.get();
    }

    public void setConnection(OObjectDatabaseTx c) {
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

    public HttpServletRequest getRequest() {
        return request.get();
    }

    public void setRequest(HttpServletRequest r) {
        request.set(r);
    }

    public static Language[] getLanguageInfo() {
        return languageInfo.get();
    }

    public static void setLanguageInfo(Language[] l) {
        languageInfo.set(l);
    }
}

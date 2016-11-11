package org.fao.fenix.d3s.server.dto;

import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import org.fao.fenix.commons.utils.Language;
import org.fao.fenix.commons.utils.Order;
import org.fao.fenix.commons.utils.Page;

import javax.servlet.http.HttpServletRequest;

public class DatabaseStandardsCopy extends DatabaseStandards {
    public HttpServletRequest request;
    public OObjectDatabaseTx connection;
    public Page paginationInfo;
    public Order orderingInfo;
    public Language[] languageInfo;
    public Integer limit;

    @Override
    public HttpServletRequest getRequest() {
        return request;
    }

    @Override
    public Integer getLimit() {
        return limit;
    }

    @Override
    public OObjectDatabaseTx getConnection() {
        return connection;
    }

    @Override
    public Order getOrderingInfo() {
        return orderingInfo;
    }

    @Override
    public Page getPaginationInfo() {
        return paginationInfo;
    }

    @Override
    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public void setConnection(OObjectDatabaseTx connection) {
        this.connection = connection;
    }

    @Override
    public void setPaginationInfo(Page paginationInfo) {
        this.paginationInfo = paginationInfo;
    }

    @Override
    public void setOrderingInfo(Order orderingInfo) {
        this.orderingInfo = orderingInfo;
    }
/*
    public void setLanguageInfo(Language[] languageInfo) {
        this.languageInfo = languageInfo;
    }
*/
    public void setLimit(String limit) {
        this.limit = limit!=null && limit.trim().length()>0 ? new Integer(limit) : null;
    }


}

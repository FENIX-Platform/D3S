package org.fao.fenix.d3s.server.dto;

import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import org.fao.fenix.commons.utils.Language;
import org.fao.fenix.commons.utils.Order;
import org.fao.fenix.commons.utils.Page;

import javax.servlet.http.HttpServletRequest;

public class DatabaseStandardsCopy  {
    public HttpServletRequest request;
    public OObjectDatabaseTx connection;
    public Page paginationInfo;
    public Order orderingInfo;
    public Language[] languageInfo;
    public Integer limit;


    public HttpServletRequest getRequest() {
        return request;
    }


    public Integer getLimit() {
        return limit;
    }


    public OObjectDatabaseTx getConnection() {
        return connection;
    }


    public Order getOrderingInfo() {
        return orderingInfo;
    }


    public Page getPaginationInfo() {
        return paginationInfo;
    }

    public Language[] getLanguageInfo() {
        return languageInfo;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }


    public void setConnection(OObjectDatabaseTx connection) {
        this.connection = connection;
    }


    public void setPaginationInfo(Page paginationInfo) {
        this.paginationInfo = paginationInfo;
    }


    public void setOrderingInfo(Order orderingInfo) {
        this.orderingInfo = orderingInfo;
    }

    public void setLanguageInfo(Language[] languageInfo) {
        this.languageInfo = languageInfo;
    }

    public void setLimit(String limit) {
        this.limit = limit!=null && limit.trim().length()>0 ? new Integer(limit) : null;
    }

    public DatabaseStandards clone(DatabaseStandards clone) {
        clone.setConnection(getConnection());
        clone.setLimit(getLimit()!=null?String.valueOf(getLimit()):null);
        clone.setOrderingInfo(getOrderingInfo());
        clone.setPaginationInfo(getPaginationInfo());
        clone.setRequest(getRequest());
        clone.setLanguageInfo(getLanguageInfo());
        return clone;
    }

}
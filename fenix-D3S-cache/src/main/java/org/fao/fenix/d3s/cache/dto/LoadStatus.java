package org.fao.fenix.d3s.cache.dto;

import java.util.Date;

public class LoadStatus {
    public enum Status {
        unavailable, refresh, ready, loading
    }

    private Status status;
    private Integer count;
    private Date lastUpdate;


    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}

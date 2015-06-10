package org.fao.fenix.d3s.cache.dto;

import java.util.Date;

public class StoreStatus {
    public enum Status {
        ready, loading, incomplete
    }

    private Status status;
    private Long count;
    private Date lastUpdate = new Date();
    private Date timeout;


    public StoreStatus() {
    }

    public StoreStatus(Status status, Long count, Date lastUpdate, Date timeout) {
        this.status = status;
        this.count = count;
        this.lastUpdate = lastUpdate;
        this.timeout = timeout;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Date getTimeout() {
        return timeout;
    }

    public void setTimeout(Date timeout) {
        this.timeout = timeout;
    }
}

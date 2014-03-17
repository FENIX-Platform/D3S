package org.fao.fenix.d3s.backup.dto;

import org.fao.fenix.commons.utils.CompletenessIterator;

import java.util.Date;

public class BackupOperation {

    private CompletenessIterator<?>[] data;
    private long size;

    private String databaseName;
    private int revision;
    private Date started;
    private BackupStatus status;
    private String errorMessage;

    public BackupOperation(String databaseName, int revision, BackupStatus initialStatus, CompletenessIterator<?>[] data, long size) {
        started = new Date();
        this.databaseName = databaseName;
        this.revision = revision;
        status = initialStatus;

        this.data = data;
        this.size = size==0 ? 1 : size;
    }


    public String getDatabaseName() {
        return databaseName;
    }

    public int getRevision() {
        return revision;
    }

    public Date getStarted() {
        return started;
    }

    public float getPercentage() {
        long processed = 0;
        if (data!=null)
            for (CompletenessIterator<?> dataSegment : data)
                processed+=dataSegment.getIndex();
        return Math.min(100,(100.0f/size)*processed);
    }

    public BackupStatus getStatus() {
        return status;
    }
    public void setStatus(BackupStatus status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}

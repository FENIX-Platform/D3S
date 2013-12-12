package org.fao.fenix.backup.dto;

import java.util.Date;

public class BackupMeta implements Comparable<BackupMeta> {
    private Date timestamp;
    private Integer revision;
    private String database;
    private String ddl;


    public BackupMeta() { }
    public BackupMeta(String database, Integer revision) {
        this.database = database;
        this.revision = revision;
    }
    public BackupMeta(String database, Integer revision, String ddl, Date timestamp) {
        this.database = database;
        this.revision = revision;
        this.ddl = ddl;
        this.timestamp = timestamp;
    }




    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getRevision() {
        return revision;
    }

    public void setRevision(Integer revision) {
        this.revision = revision;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getDdl() {
        return ddl;
    }

    public void setDdl(String ddl) {
        this.ddl = ddl;
    }


    //Compare
    private String key() {
        return (database!=null?database:"")+'_'+(revision!=null?revision:"");
    }
    @Override
    public boolean equals(Object obj) {
        return obj instanceof BackupMeta && key().equals(((BackupMeta) obj).key());
    }

    @Override
    public int hashCode() {
        return key().hashCode();
    }

    @Override
    public int compareTo(BackupMeta o) {
        return key().compareTo(o.key());
    }


}

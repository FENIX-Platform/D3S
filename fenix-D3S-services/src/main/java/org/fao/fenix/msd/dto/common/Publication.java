package org.fao.fenix.msd.dto.common;

import org.fao.fenix.server.utils.DataUtils;

import java.util.Date;

public class Publication extends Link {
    private String id;
    private Date publicationDate;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public Date getPublicationDate() {
        return publicationDate;
    }
    public void setPublicationDate(Date publicationDate) {
        this.publicationDate = DataUtils.roundDate(publicationDate);
    }
}

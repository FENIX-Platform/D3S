package org.fao.fenix.d3s.msd.services.impl.find;


import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.fenix.d3s.server.tools.orient.OrientDao;

import java.util.Collection;

public class Bridge extends OrientDao {


    private String createQuery(Collection<String> ids) throws Exception {
        StringBuilder queryFilter = new StringBuilder();
        for (String id : ids)
            queryFilter.append("'" + id + "' , ");
        queryFilter.setLength(queryFilter.length() - 2);
        return "SELECT FROM MeIdentification WHERE index|id in [ " + queryFilter + " ]";
    }

    public Collection<MeIdentification> getMetadata(Collection<String> ids) throws Exception {
        return select(MeIdentification.class, createQuery(ids));
    }

}

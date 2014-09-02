package org.fao.fenix.d3s.msd.dao;

import com.orientechnologies.orient.core.id.ORID;
import org.fao.fenix.commons.msd.dto.full.DSDColumnSubject;
import org.fao.fenix.d3s.server.tools.orient.OrientDao;

public class SubjectDao extends OrientDao {


    public DSDColumnSubject getSubject(String rid) throws Exception {
        ORID orid = DSDColumnSubject.toRID(rid);
        return orid!=null ? loadBean(rid,DSDColumnSubject.class) : select(DSDColumnSubject.class, "SELECT FROM DSDColumnSubject WHERE name = ?", rid).iterator().next();
    }
}

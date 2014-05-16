package org.fao.fenix.d3s.msd.dao.common;

import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import org.fao.fenix.commons.msd.dto.common.Select;
import org.fao.fenix.d3s.server.tools.orient.OrientDao;
import org.fao.fenix.d3s.server.tools.orient.OrientDatabase;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class MSDDao extends OrientDao {


	//Identity standard load
	public Collection<ODocument> select(String query, Object[] parameters) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
            return (Collection<ODocument>)database.query(new OSQLSynchQuery<ODocument>(query), parameters);
		} finally {
			if (database!=null)
				database.close();
		}
	}

    public Collection<Map<String,Object>> select(Select queryInfo) throws Exception {
        Collection<ODocument> dataO = select(queryInfo.getQuery(), queryInfo.getParameters());
        if (dataO!=null && dataO.size()>0) {
            Collection<Map<String,Object>> data = new LinkedList<>();
            for (ODocument recordO : dataO) {
                Map<String,Object> record = new HashMap<>();
                toMap(record, recordO, queryInfo.getClasses(), queryInfo.getLevels());
                if (record.size()>0)
                    data.add(record);
            }
            return data;
        } else
            return null;
	}
}

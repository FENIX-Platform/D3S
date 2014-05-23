package org.fao.fenix.d3s.wds.impl.countryStat;

import java.util.*;

import org.fao.fenix.commons.search.dto.filter.ResourceFilter;
import org.fao.fenix.d3s.wds.impl.OrientDao;

import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

import javax.enterprise.context.Dependent;

@Dependent
public class CountryStatDAO extends OrientDao {

    @Override
    @SuppressWarnings("unchecked")
	public void load(ResourceFilter filter, ODocument dataset) throws Exception {
        Collection<Map<String,Object>> rowData = new LinkedList<Map<String, Object>>();
        //Load data
        Collection<Object> parameters = new LinkedList<>();
        OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<>(createQuery(filter,parameters,dataset));
        //Return result
        data = createRowIterable(dataset, loadAsynch(dataset, query, parameters));
	}

    @Override
    protected Map<String, Object> processRow(ODocument rowO, OGraphDatabase connection) throws Exception {
        Map<String,Object> row = new LinkedHashMap<>();
        for (Iterator<Map.Entry<String,Object>> i=rowO.iterator(); i.hasNext();) {
            Map.Entry<String,Object> field = i.next();
            Object value = field.getValue();
            if (value instanceof Collection)
                value = ((Collection)value).isEmpty() ? null : ((Collection)value).iterator().next();
            row.put(field.getKey(), value!=null && value instanceof ODocument ? ((ODocument)value).field("code") : value);
        }
        return row;
    }

	@Override
	public void store(Iterable<Object[]> data, ODocument dataset) throws Exception {
		throw new UnsupportedOperationException();
	}


    //Find method support
    private String createQuery(ResourceFilter filter, Collection<Object> parameters, ODocument dataset) {
        StringBuilder query = new StringBuilder("SELECT FROM Dataset WHERE datasetID = ?");
        parameters.add(dataset.field("uid"));

        String whereConditionByDimensionFilter = createQueryWhereCondition(filter, parameters, dataset);
        if (whereConditionByDimensionFilter!=null)
            query.append(" AND ").append(whereConditionByDimensionFilter);

        return query.toString();
    }
}

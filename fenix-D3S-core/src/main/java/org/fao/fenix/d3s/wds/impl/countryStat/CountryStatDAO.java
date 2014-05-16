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
        OGraphDatabase database = null;
        Collection<Map<String,Object>> rowData = new LinkedList<Map<String, Object>>();
        //Load data
        try {
            //Load data (switch database to CountrySTAT Orient database)
            ODatabaseRecordThreadLocal.INSTANCE.set( database = getDataDatabase((ODocument)dataset.field("dsd.datasource")) );
            Collection<Object> parameters = new LinkedList<Object>();
            OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>(createQuery(filter,parameters,dataset));
            Collection<ODocument> dataO = (Collection<ODocument>)database.query(query,parameters.toArray());
            //Prepare data for output
            Map<String,Object> row;
            for (ODocument rowO : dataO) {
                rowData.add(row = new LinkedHashMap<String, Object>());
                for (Iterator<Map.Entry<String,Object>> i=rowO.iterator(); i.hasNext();) {
                    Map.Entry<String,Object> field = i.next();
                    Object value = field.getValue();
                    if (value instanceof Collection)
                        value = ((Collection)value).isEmpty() ? null : ((Collection)value).iterator().next();
                    row.put(field.getKey(), value!=null && value instanceof ODocument ? ((ODocument)value).field("code") : value);
                }
            }
        } finally {
            if (database!=null)
                database.close();
            //Restore MDS database as the default one
            ODatabaseRecordThreadLocal.INSTANCE.set( getFlow().getMsdDatabase() );
        }
        //Return result
        data = createRowIterable(dataset, rowData);
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

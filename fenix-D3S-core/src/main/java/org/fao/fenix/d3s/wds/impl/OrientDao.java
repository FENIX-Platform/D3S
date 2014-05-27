package org.fao.fenix.d3s.wds.impl;

import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import org.fao.fenix.commons.msd.dto.type.dsd.DSDDataType;
import org.fao.fenix.commons.search.dto.filter.ResourceFilter;
import org.fao.fenix.commons.search.dto.filter.ColumnValueFilter;
import org.fao.fenix.d3s.wds.Dao;
import org.fao.fenix.d3s.server.tools.orient.OrientServer;

import javax.inject.Inject;
import java.util.*;


public abstract class OrientDao extends Dao implements Runnable {
    @Inject OrientServer orientServer;

    //ASYNCHRONOUS LOADING MANAGEMENT

    private enum DaoAction {load, store};
    private DaoAction dataSourceAction;
    private Exception daoError;
    private String dataSourceURL,dataSourceUSR,dataSourcePSW;
    private Collection<Map<String,Object>> dataSourceRowData;
    private OSQLSynchQuery<ODocument> dataSourceQuery;
    private Collection<Object> dataSourceParameters;

    protected Collection<Map<String,Object>> loadAsynch(ODocument dataset, OSQLSynchQuery<ODocument> query, Collection<Object> parameters) throws Exception {
        Map<String,String> reference = dataset.field("dsd.datasource.reference");
        dataSourceURL = reference.get("url");
        dataSourceUSR = reference.get("usr");
        dataSourcePSW = reference.get("psw");

        this.dataSourceAction = DaoAction.load;
        dataSourceRowData = new LinkedList<>();
        dataSourceQuery = query;
        dataSourceParameters = parameters;

        Thread daoThread = new Thread(this);
        daoThread.start();
        daoThread.join();

        if (daoError!=null)
            throw daoError;

        return dataSourceRowData;
    }

    @Override
    public void run() {
        OGraphDatabase connection = null;
        try {
            connection = orientServer.getDatabase(dataSourceURL,dataSourceUSR,dataSourcePSW);

            if (dataSourceAction==DaoAction.load) {
                Collection<ODocument> dataO = (Collection<ODocument>)connection.query(dataSourceQuery,dataSourceParameters.toArray());
                for (ODocument rowO : dataO)
                    dataSourceRowData.add (processRow(rowO,connection));
            } else {
                daoError = new UnsupportedOperationException();
            }

        } catch (Exception ex) {
            daoError = ex;
        } finally {
            if (connection!=null)
                connection.close();
        }
    }

    protected abstract Map<String,Object> processRow(ODocument rowO, OGraphDatabase connection) throws Exception;

    //UTILS

    //Create data iterable
    protected Iterable<Object[]> createRowIterable(ODocument metadata, final Iterable<Map<String,Object>> data) throws Exception {
        Collection<ODocument> columnsO = metadata.field("dsd.columns");
        final int size = columnsO.size();
        final String[] columnsID = new String[size];
        final Object[] rowTemplate = new Object[size];

        int i=0;
        for (ODocument columnO : columnsO) {
            String virtual = columnO.field("virtualColumn");
            if (virtual!=null)
                if ("INTERNAL".equals(virtual)) {
                    Collection<Object> values = columnO.field("values");
                    if (values==null || values.size()!=1)
                        throw new Exception("Column '"+columnO.field("column")+"' into dataset '"+metadata.field("uid")+"' has an INTERNAL virtual column whit more than one or no values");
                    Object value = values.iterator().next();
                    value = value instanceof ORID ? getConnection().load((ORID) value) : value;
                    rowTemplate[i] = value!=null && value instanceof ODocument ? ((ODocument)value).field("code") : value;
                } else
                    throw new UnsupportedOperationException("External virtual column is unsupported yet");
            columnsID[i++] = columnO.field("column");
        }

        return new Iterable<Object[]>() {
            @Override
            public Iterator<Object[]> iterator() {
                return new Iterator<Object[]>() {
                    Iterator<Map<String,Object>> dataIterator = data.iterator();

                    @Override public boolean hasNext() { return dataIterator.hasNext(); }
                    @Override public void remove() { throw new UnsupportedOperationException(); }
                    @Override
                    public Object[] next() {
                        Map<String,Object> rowO = dataIterator.next();
                        Object[] row = Arrays.copyOf(rowTemplate,size);
                        for (int i=0; i<columnsID.length; i++)
                            if (row[i]==null)
                                row[i] = rowO.get(columnsID[i]);
                        return row;
                    }
                };
            }
        };
    }


    //Where condition build support
    protected String createQueryWhereCondition(ResourceFilter filter, Collection<Object> parameters, ODocument dataset) {
        StringBuilder query = new StringBuilder();
        LinkedHashMap<String, Collection<ColumnValueFilter>> dimensionFilter = filter!=null ? filter.getData() : null;
        Map<String,ODocument> colByDim = getFlow().getColumnsByDimension(dataset);

        if (dimensionFilter!=null)
            whereCondition(colByDim, dimensionFilter, query, parameters);

        return query.length()>0 ? query.substring(4) : null;
    }

    private void whereCondition(Map<String,ODocument> colByDim, Map<String, Collection<ColumnValueFilter>> filter, StringBuilder query, Collection<Object> parameterValues) {
    	for (Map.Entry<String , Collection<ColumnValueFilter>> filterEntry : filter.entrySet()) {
            query.append(" AND ");
            whereCondition(colByDim.get(filterEntry.getKey()), filterEntry.getValue(), query, parameterValues);
        }
    }

    private void whereCondition(ODocument column, Collection<ColumnValueFilter> filterValues, StringBuilder query, Collection<Object> parameterValues) {
        Iterator<ColumnValueFilter> i = filterValues.iterator();
        query.append("( ");
        whereCondition(column, i.next(), query, parameterValues);
        while (i.hasNext()) {
            query.append("OR ");
            whereCondition(column, i.next(), query, parameterValues);
        }
        query.append(')');
    }

    private void whereCondition(ODocument column, ColumnValueFilter filterValue, StringBuilder query, Collection<Object> parameterValues) {
        String columnName = (String)column.field("column");
        DSDDataType columnType = DSDDataType.getByCode((String)column.field("datatype"));
        switch (filterValue.getType()) {
            case text:
                query.append(columnName).append(" = ? ");
                parameterValues.add(filterValue.getText());
                break;
            case like:
                query.append(columnName).append(" LIKE ? ");
                parameterValues.add(filterValue.getRegExp().trim().equals("") ? "%%" : filterValue.getRegExp().replace('*','%'));
                break;
            case iText:
                query.append(columnName).append('.').append(filterValue.getLanguage().toUpperCase()).append(" = ? ");
                parameterValues.add(filterValue.getText());
                break;
            case iLike:
                query.append(columnName).append('.').append(filterValue.getLanguage().toUpperCase()).append(" LIKE ? ");
                parameterValues.add(filterValue.getRegExp().trim().equals("") ? "%%" : filterValue.getRegExp().replace('*','%'));
                break;
            case dateInterval:
                query.append(columnName).append(" BETWEEN ? AND ? ");
                if (columnType==DSDDataType.year) {
                    Calendar c = Calendar.getInstance();
                    c.setTime(filterValue.getFromDate());
                    parameterValues.add(c.get(Calendar.YEAR));
                    c.setTime(filterValue.getToDate());
                    parameterValues.add(c.get(Calendar.YEAR));
                } else {
                    parameterValues.add(filterValue.getFromDate());
                    parameterValues.add(filterValue.getToDate());
                }
                break;
            case numberInterval:
                query.append(columnName).append(" BETWEEN ? AND ? ");
                parameterValues.add(filterValue.getFrom());
                parameterValues.add(filterValue.getTo());
                break;
            case code:
            	if (column.field("codeSystem")==null) {
                    query.append("( ").append(columnName).append(".system.system = ? AND ").append(columnName).append(".system.version = ? AND ").append(columnName).append(".code = ? ) ");
                    parameterValues.add(filterValue.getCode().getSystemKey());
                    parameterValues.add(filterValue.getCode().getSystemVersion());
            	} else {
                    query.append(columnName).append(" = ? ");
            	}
                parameterValues.add(filterValue.getCode().getCode());
                break;
            case document:
                query.append(columnName).append(" = ? ");
                parameterValues.add(toRID(filterValue.getId()));
                break;
        }
    }

}

package org.fao.fenix.wds.impl;

import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.fao.fenix.msd.dto.dsd.type.DSDDataType;
import org.fao.fenix.search.dto.SearchFilter;
import org.fao.fenix.search.dto.valueFilters.ColumnValueFilter;
import org.fao.fenix.server.tools.orient.OrientServer;
import org.fao.fenix.wds.Dao;

import java.util.*;


public abstract class OrientDao extends Dao {

    @SuppressWarnings("unchecked")
	protected OGraphDatabase getDataDatabase(ODocument datasource) {
        Map<String,String> reference = (Map<String,String>)datasource.field("reference");
        return OrientServer.getDatabase(reference.get("url"),reference.get("usr"),reference.get("psw"));
        //return OrientServer.getDatabase("remote:localhost:2425/CountrySTAT_1.0",reference.get("usr"),reference.get("psw"));
    }


    //Create data iterable
    protected Iterable<Object[]> createRowIterable(ODocument metadata, final Iterable<Map<String,Object>> data) throws Exception {
        Collection<ODocument> columnsO = metadata.field("dsd.columns");
        final int size = columnsO.size();
        final String[] columnsID = new String[size];
        final Object[] rowTemplate = new Object[size];

        OGraphDatabase database = getFlow().getMsdDatabase();
        int i=0;
        for (ODocument columnO : columnsO) {
            String virtual = columnO.field("virtualColumn");
            if (virtual!=null)
                if ("INTERNAL".equals(virtual)) {
                    Collection<Object> values = columnO.field("values");
                    if (values.size()!=1)
                        throw new Exception("Column '"+columnO.field("column")+"' into dataset '"+metadata.field("uid")+"' has an INTERNAL virtual column whit more than one or no values");
                    Object value = values.iterator().next();
                    value = value instanceof ORID ? org.fao.fenix.server.tools.orient.OrientDao.getDocument((ORID)value,database) : value;
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
    protected String createQueryWhereCondition(SearchFilter filter, Collection<Object> parameters, ODocument dataset) {
        StringBuilder query = new StringBuilder();
        LinkedHashMap<String, Collection<ColumnValueFilter>> dimensionFilter = filter!=null ? filter.getDimensions() : null;
        Map<String,ODocument> colByDim = getFlow().getColumnsByDimension(dataset);

        if (dimensionFilter!=null)
            whereCondition(colByDim, dimensionFilter, query, parameters);

        return query.length()>0 ? " WHERE " + query.substring(4) : "";
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

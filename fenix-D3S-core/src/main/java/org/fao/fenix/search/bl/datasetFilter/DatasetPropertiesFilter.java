package org.fao.fenix.search.bl.datasetFilter;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.fao.fenix.msd.dao.dm.DMIndexStore;
import org.fao.fenix.msd.dto.dsd.type.DSDDataType;
import org.fao.fenix.search.dto.SearchFilter;
import org.fao.fenix.search.dto.valueFilters.ColumnValueFilter;
import org.fao.fenix.search.dto.valueFilters.ValueFilterType;
import org.fao.fenix.server.utils.DataUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

@Component
public class DatasetPropertiesFilter extends DatasetFilter {
    @Autowired private DMIndexStore indexStoreDao;


    @SuppressWarnings("unchecked")
    @Override
    public Collection<ODocument> filter(SearchFilter baseFilter, Collection<ODocument> source) throws Exception {
        //Retrieve database instance
        OGraphDatabase database = getFlow().getMsdDatabase();
        //Prepare query
        StringBuilder query = new StringBuilder("SELECT FROM DMMain WHERE uid IS NOT NULL");
        Collection<Object> parameterValues = new LinkedList<Object>();
        whereCondition(baseFilter.getFields(), baseFilter.getDimensions(), query, parameterValues);
        //Execute query
        OSQLSynchQuery<ODocument> queryO = new OSQLSynchQuery<ODocument>(query.toString());
        return (Collection<ODocument>)database.query(queryO, parameterValues.toArray());
    }

    //WHERE CONDITION UTILS
    private void whereCondition(Map<String, Collection<ColumnValueFilter>> fields, Map<String, Collection<ColumnValueFilter>> dimensions, StringBuilder query, Collection<Object> parameterValues) throws Exception {
        Collection<ColumnValueFilter> values;
        for (Map.Entry<String , Collection<ColumnValueFilter>> filterEntry : fields.entrySet())
            if ((values=filterEntry.getValue())!=null && values.size()>0) {
                query.append(" AND ");
                whereCondition(filterEntry.getKey(), filterEntry.getValue(), query, parameterValues);
            }
        for (Map.Entry<String , Collection<ColumnValueFilter>> dimensionEntry : dimensions.entrySet())
            if ((values= dimensionEntry.getValue())!=null && values.size()>0) {
                query.append(" AND ");
                whereConditionDimension(dimensionEntry.getKey(), dimensionEntry.getValue(), query, parameterValues);
            }
    }
    //FIELDS
    private void whereCondition(String fieldName, Collection<ColumnValueFilter> filterValues, StringBuilder query, Collection<Object> parameterValues) throws Exception {
        Iterator<ColumnValueFilter> i = filterValues.iterator();
        ColumnValueFilter firstFilter = i.hasNext() ? i.next() : null;
        if (firstFilter!=null)
            if (firstFilter.getType()==ValueFilterType.enumeration) {
                String indexFieldName = indexStoreDao.getIndexedFieldName(fieldName);
                query.append(indexFieldName).append(" in [ ?");
                parameterValues.add(firstFilter.getEnumeration());
                while (i.hasNext()) {
                    query.append(",?");
                    parameterValues.add(i.next().getEnumeration());
                }
                query.append(" ]");
            } else {
                query.append("( ");
                whereCondition(fieldName, firstFilter, query, parameterValues);
                while (i.hasNext()) {
                    query.append(" OR ");
                    whereCondition(fieldName, i.next(), query, parameterValues);
                }
                query.append(')');
            }
        else
            query.append("1 = 1");
    }
    private void whereCondition(String fieldName, ColumnValueFilter filterValue, StringBuilder query, Collection<Object> parameterValues) throws Exception {
        String indexFieldName = indexStoreDao.getIndexedFieldName(fieldName);
        OProperty property = getFlow().getMsdDatabase().getMetadata().getSchema().getClass("DMMain").getProperty(indexFieldName);
        String text;
        String[] words;

        switch (filterValue.getType()) {
            case enumeration:
                query.append(indexFieldName).append(" = ? ");
                parameterValues.add(filterValue.getEnumeration());
                break;
            case text:
                words = DMIndexStore.filterIndexText(filterValue.getText()).split(" ");
                for (int i=0; i<words.length; i++) {
                    if (i>0) query.append("OR ");
                    query.append(indexFieldName).append(" CONTAINSTEXT ? ");
                    parameterValues.add(words[i]);
                }
                break;
            case like:
                query.append(indexFieldName).append(" LIKE ? ");
                parameterValues.add(filterValue.getRegExp().trim().equals("") ? "%%" : filterValue.getRegExp().replace('*','%'));
                break;
            case iText:
                words = DMIndexStore.filterIndexText(filterValue.getText()).split(" ");
                for (int i=0; i<words.length; i++) {
                    if (i>0) query.append("OR ");
                    query.append(indexFieldName).append('_').append(filterValue.getLanguage().toUpperCase()).append(" CONTAINSTEXT ? ");
                    parameterValues.add(words[i]);
                }
                break;
            case iLike:
                text = filterValue.getRegExp().trim().equals("") ? "%%" : filterValue.getRegExp().replace('*','%');
                query.append(" OR ").append(indexFieldName).append('_').append(filterValue.getLanguage().toUpperCase()).append(" LIKE ? ");
                parameterValues.add(text);
                break;
            case dateInterval:
                if (property==null) //It's a period indexed as separated fields with "_from" and "_to" suffix
                    query.append("( ").append(indexFieldName).append("_from >= ? AND ").append(indexFieldName).append("_to <= ? ) ");
                else //It's a standard date field
                    query.append(indexFieldName).append(" BETWEEN ? AND ? ");
                parameterValues.add(filterValue.getFromDate());
                parameterValues.add(filterValue.getToDate());
                break;
            case numberInterval:
                query.append(indexFieldName).append(" BETWEEN ? AND ? ");
                parameterValues.add(filterValue.getFrom());
                parameterValues.add(filterValue.getTo());
                break;
            case code:
                query.append(indexFieldName).append(" CONTAINS ? ");
                parameterValues.add(getFlow().getLoadedCode(filterValue.getCode()));
                break;
            case document:
                query.append(indexFieldName).append(property.getType()==OType.LINKLIST ? " = ? " : " CONTAINS ? "); //field can be LINK or LINKLIST only
                parameterValues.add(toRID(filterValue.getId()));
                break;
            default:
                throw new Exception("Undefinded filter type from property "+fieldName);
        }
    }
    //DIMENSIONS
    private void whereConditionDimension(String dimensionName, Collection<ColumnValueFilter> filterValues, StringBuilder query, Collection<Object> parameterValues) throws Exception {
        Iterator<ColumnValueFilter> i = filterValues.iterator();
        query.append("( ");
        whereConditionDimension(dimensionName, i.next(), query, parameterValues);
        while (i.hasNext()) {
            query.append("OR ");
            whereConditionDimension(dimensionName, i.next(), query, parameterValues);
        }
        query.append(')');
    }
    private void whereConditionDimension(String dimensionName, ColumnValueFilter filterValue, StringBuilder query, Collection<Object> parameterValues) throws Exception {
        switch (filterValue.getType()) {
            case enumeration: //good for customCodes
                query.append(indexStoreDao.getIndexedDimensionName(dimensionName,DSDDataType.enumeration)).append(" CONTAINS ? ");
                parameterValues.add(filterValue.getEnumeration());
                break;
            case dateInterval:
                int queryLengthBefore = query.length();
                //years support
                try {
                    Collection<Long> years = DataUtils.getTimeSeries(filterValue.getFromDate(),filterValue.getToDate(), DSDDataType.year);
                    if (years != null) {
                        for (int i=0; i<years.size(); i++)
                            query.append(indexStoreDao.getIndexedDimensionName(dimensionName,DSDDataType.year)).append(" CONTAINS ? OR ");
                        parameterValues.addAll(years);
                        query.delete(query.length()-3,query.length());
                    }
                } catch (Exception ex) {}

                //months support
                if (filterValue.isMonthly())
                    try {
                        Collection<Long> months = DataUtils.getTimeSeries(filterValue.getFromDate(),filterValue.getToDate(), DSDDataType.month);
                        if (months != null) {
                            if (query.length()>queryLengthBefore)
                                query.append("OR ");
                            for (int i=0; i<months.size(); i++)
                                query.append(indexStoreDao.getIndexedDimensionName(dimensionName,DSDDataType.month)).append(" CONTAINS ? OR ");
                            parameterValues.addAll(months);
                            query.delete(query.length()-3,query.length());
                        }
                    } catch (Exception ex) {}

                //day support
                if (filterValue.isDayly())
                    try {
                        Collection<Long> days = DataUtils.getTimeSeries(filterValue.getFromDate(),filterValue.getToDate(), DSDDataType.date);
                        if (days != null) {
                            if (query.length()>queryLengthBefore)
                                query.append("OR ");
                            for (int i=0; i<days.size(); i++)
                                query.append(indexStoreDao.getIndexedDimensionName(dimensionName,DSDDataType.date)).append(" CONTAINS ? OR ");
                            parameterValues.addAll(days);
                            query.delete(query.length()-3,query.length());
                        }
                    } catch (Exception ex) {}

                break;
            case numberInterval:
                for (long v = filterValue.getFrom(); v<=filterValue.getTo(); v++) {
                    query.append(indexStoreDao.getIndexedDimensionName(dimensionName,DSDDataType.number)).append(" CONTAINS ? OR ");
                    parameterValues.add(v);
                }
                query.delete(query.length() - 3, query.length());
                //TODO: Add support for single double value
                break;
            case code:
                query.append(indexStoreDao.getIndexedDimensionName(dimensionName,DSDDataType.code)).append(" CONTAINS ? ");
                parameterValues.add(getFlow().getLoadedCode(filterValue.getCode()));
                break;
            case document:
                query.append(indexStoreDao.getIndexedDimensionName(dimensionName,DSDDataType.document)).append(" CONTAINS ? ");
                parameterValues.add(toRID(filterValue.getId()));
                break;
        }
    }





    //ORID COMPOSITION TEST

    public static void main (String ... args) {
        Collection<Collection<ORID>> idrs = new LinkedList<Collection<ORID>>();
        for (int m=2; m<200; m+=2) { //100 reads
            Collection<ORID> idrsRow = new LinkedList<ORID>();
            idrs.add(idrsRow);
            for (int i=0; i<1000; i++)
                idrsRow.add(toRID("10_"+(i*m)));
        }


        long mid, max, tot;
        int iterations = 1;
        long[] buffer = new long[1000000];
        Collection<ORID> result;


        result = new LinkedList<ORID>();
        Set<ORID> interception = new HashSet<ORID>();
        tot = System.currentTimeMillis();
        for (int i=0; i<iterations; i++) {
            //Fill buffer & interception
            for (Collection<ORID> idrsRow : idrs)
                for (ORID idr : idrsRow) {
                    buffer[(int)idr.getClusterPosition().longValue()]++;
                    interception.add(idr);
                }
            //Union
            for (int idi=0; idi<buffer.length; idi++)
                if (buffer[idi]==100)
                    result.add(toRID("10_"+idi));
            //Reset
            Arrays.fill(buffer,0);
        }
        tot = System.currentTimeMillis()-tot;
        mid = tot/iterations;

        System.out.printf("Con fill: mid=%d - tot=%d - size=%d - unionSize=%d - interceptionSize=%d\n\n", mid,tot,100*1000,result.size(),interception.size());

    }
}
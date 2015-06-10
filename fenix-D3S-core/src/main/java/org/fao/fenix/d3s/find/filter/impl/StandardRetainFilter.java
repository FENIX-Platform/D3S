package org.fao.fenix.d3s.find.filter.impl;

import org.fao.fenix.commons.find.dto.condition.ConditionFilter;
import org.fao.fenix.commons.find.dto.condition.ConditionTime;
import org.fao.fenix.commons.find.dto.filter.TimeFilter;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.fenix.d3s.find.filter.Filter;

import java.util.*;

//Standard query builder algorithm with intersection between field's values
public class StandardRetainFilter extends Filter {

    @Override
    public Collection<MeIdentification> filter(ConditionFilter ... filter) throws Exception {
        Collection<MeIdentification> resources = new LinkedHashSet<>();

        if (filter.length>0)
            resources.addAll(filter(filter[0]));
        if (resources.size()>0 && filter.length>1)
            for (int i=1; i<filter.length; i++)
                resources.retainAll(filter(filter[i]));

        return resources;
    }

    private Collection<MeIdentification> filter(ConditionFilter filter) throws Exception  {
        Collection<MeIdentification> resources = new LinkedHashSet<>();
        StringBuilder query = new StringBuilder("SELECT FROM MeIdentification WHERE ");

        switch (filter.filterType) {
            case code:
                query.append(filter.indexedFieldName).append(" CONTAINS ?");
                for (Object value : filter.values)
                    resources.addAll(select(MeIdentification.class, query.toString(), value));
                break;
            case contact:
                query.append(filter.indexedFieldName).append(" LUCENE ?");
                for (Object value : filter.values)
                    resources.addAll(select(MeIdentification.class, query.toString(), value));
                break;
            case id:
            case enumeration:
                query.append(filter.indexedFieldName).append(" = ?");
                for (Object value : filter.values)
                    resources.addAll(select(MeIdentification.class, query.toString(), value));
                break;
            case time:
                for (Object value : filter.values) {
                    TimeFilter time = (TimeFilter)value;
                    StringBuilder timeQuery = new StringBuilder();
                    Collection<Object> params = new LinkedList<>();
                    if (time.from!=null) {
                        timeQuery.append(filter.indexedFieldName).append(ConditionTime.toFieldNameSuffix).append(" >= ? AND ");
                        params.add(time.getFrom(14));
                    }
                    if (time.to!=null) {
                        timeQuery.append(filter.indexedFieldName).append(ConditionTime.fromFieldNameSuffix).append(" <= ? AND ");
                        params.add(time.getTo(14));
                    }

                    if (params.size()>0)
                        resources.addAll(select(MeIdentification.class, query.toString() + timeQuery.substring(0,timeQuery.length()-5), params.toArray()));
                }
                break;
            default:
        }

        return resources;
    }


    @Override
    public String createQuery(Collection<Object> params, ConditionFilter ... filter) throws Exception {
        throw new UnsupportedOperationException();
    }
}

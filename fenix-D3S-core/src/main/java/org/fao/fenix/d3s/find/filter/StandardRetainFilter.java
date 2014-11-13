package org.fao.fenix.d3s.find.filter;

import org.fao.fenix.commons.find.dto.condition.ConditionFilter;
import org.fao.fenix.commons.find.dto.condition.ConditionTime;
import org.fao.fenix.commons.find.dto.type.FieldFilterType;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;

import java.util.*;

//Standard query builder algorithm with intersection between field's values
public class StandardRetainFilter extends Filter {

    @Override
    public Collection<MeIdentification> filter(ConditionFilter ... filter) throws Exception {
        Collection<MeIdentification> resources = new HashSet<>();

        if (filter.length>0)
            resources.addAll(filter(filter[0]));
        if (resources.size()>0 && filter.length>1)
            for (int i=1; i<filter.length; i++)
                resources.retainAll(filter(filter[i]));

        return resources;
    }

    private Collection<MeIdentification> filter(ConditionFilter filter) throws Exception  {
        Collection<MeIdentification> resources = new HashSet<>();
        StringBuilder query = new StringBuilder("SELECT FROM MeIdentification WHERE ");

        switch (filter.filterType) {
            case code:
                query.append(filter.fieldName).append(" CONTAINS ?");
                for (Object value : filter.values)
                    resources.addAll(select(MeIdentification.class, query.toString(), value));
                break;
            case contact:
                query.append(filter.fieldName).append(" LUCENE ?");
                for (Object value : filter.values)
                    resources.addAll(select(MeIdentification.class, query.toString(), value));
                break;
            case id:
            case enumeration:
                query.append(filter.fieldName).append(" = ?");
                for (Object value : filter.values)
                    resources.addAll(select(MeIdentification.class, query.toString(), value));
                break;
            case time:
                for (Object value : filter.values) {
                    ConditionTime time = (ConditionTime)value;
                    StringBuilder timeQuery = new StringBuilder();
                    Collection<Object> params = new LinkedList<>();
                    if (time.from!=null) {
                        timeQuery.append(filter.fieldName).append(ConditionTime.toFieldNameSuffix).append(" >= ? OR ");
                        params.add(time.from);
                    }
                    if (time.to!=null) {
                        timeQuery.append(filter.fieldName).append(ConditionTime.fromFieldNameSuffix).append(" <= ? OR ");
                        params.add(time.to);
                    }

                    if (params.size()>0)
                        resources.addAll(select(MeIdentification.class, query.toString() + timeQuery.substring(0,timeQuery.length()-4), params.toArray()));
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

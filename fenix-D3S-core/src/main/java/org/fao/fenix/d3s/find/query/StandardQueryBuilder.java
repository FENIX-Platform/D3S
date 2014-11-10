package org.fao.fenix.d3s.find.query;

import org.fao.fenix.commons.find.dto.condition.ConditionFilter;
import org.fao.fenix.commons.find.dto.condition.ConditionTime;

import java.util.Collection;

//Standard query builder algorithm with 'and' between fields and 'or' between field's values
public class StandardQueryBuilder implements QueryBuilder {

    @Override
    public String createQuery(Collection<ConditionFilter> filter, Collection<Object> params) throws Exception {
        StringBuilder queryFilter = new StringBuilder();
        for (ConditionFilter filterCondition : filter) {
            switch (filterCondition.filterType) {
                case code:
                    params.addAll(filterCondition.values);
                    queryFilter.append(" AND (");
                    for (int i=0, l=filterCondition.values.size(); i<l; i++)
                        queryFilter.append(filterCondition.fieldName).append(" CONTAINS ? OR ");
                    queryFilter.setLength(queryFilter.length()-4);
                    queryFilter.append(')');
                    break;
                case contact:
                    params.addAll(filterCondition.values);
                    queryFilter.append(" AND ").append(filterCondition.fieldName).append(" LUCENE ?");
                    break;
                case id:
                case enumeration:
                    params.addAll(filterCondition.values);
                    queryFilter.append(" AND (");
                    for (int i=0, l=filterCondition.values.size(); i<l; i++)
                        queryFilter.append(filterCondition.fieldName).append(" = ? OR ");
                    queryFilter.setLength(queryFilter.length()-4);
                    queryFilter.append(')');
                    break;
                case time:
                    queryFilter.append(" AND (");
                    for (Object timeObject : filterCondition.values) {
                        ConditionTime time = (ConditionTime)timeObject;
                        if (time.from!=null) {
                            queryFilter.append(filterCondition.fieldName).append(ConditionTime.toFieldNameSuffix).append(" >= ? OR ");
                            params.add(time.from);
                        }
                        if (time.to!=null) {
                            queryFilter.append(filterCondition.fieldName).append(ConditionTime.fromFieldNameSuffix).append(" <= ? OR ");
                            params.add(time.to);
                        }
                    }
                    queryFilter.setLength(queryFilter.length()-4);
                    queryFilter.append(')');
                default:
            }
        }
        return "SELECT FROM MeIdentification" + (queryFilter.length()>0 ? " WHERE " + queryFilter.substring(4) : "");
    }
}

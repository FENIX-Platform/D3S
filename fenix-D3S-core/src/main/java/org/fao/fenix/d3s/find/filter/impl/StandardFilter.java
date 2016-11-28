package org.fao.fenix.d3s.find.filter.impl;

import org.fao.fenix.commons.find.dto.condition.ConditionFilter;
import org.fao.fenix.commons.find.dto.condition.ConditionTime;
import org.fao.fenix.d3s.find.filter.Filter;

import java.util.Collection;

//Standard query builder algorithm with 'and' between fields and 'or' between field's values
public class StandardFilter extends Filter {

    @Override
    protected String createQuery(Collection<Object> params, ConditionFilter ... filter) throws Exception {
        StringBuilder queryFilter = new StringBuilder();
        for (ConditionFilter filterCondition : filter) {
            switch (filterCondition.filterType) {
                case code:
                    params.addAll(filterCondition.values);
                    queryFilter.append(" AND (");
                    for (int i=0, l=filterCondition.values.size(); i<l; i++)
                        queryFilter.append(filterCondition.indexedFieldName).append(" CONTAINS ? OR ");
                    queryFilter.setLength(queryFilter.length()-4);
                    queryFilter.append(')');
                    break;
                case contact:
                case free:
                    params.addAll(filterCondition.values);
                    queryFilter.append(" AND ").append(filterCondition.indexedFieldName).append(" LUCENE ?");
                    break;
                case id:
                case enumeration:
                    params.addAll(filterCondition.values);
                    queryFilter.append(" AND (");
                    for (int i=0, l=filterCondition.values.size(); i<l; i++)
                        queryFilter.append(filterCondition.indexedFieldName).append(" = ? OR ");
                    queryFilter.setLength(queryFilter.length()-4);
                    queryFilter.append(')');
                    break;
                case time:
                    queryFilter.append(" AND (");
                    for (Object timeObject : filterCondition.values) {
                        ConditionTime time = (ConditionTime)timeObject;

                        queryFilter.append('(').append(filterCondition.indexedFieldName).append(ConditionTime.toFieldNameSuffix).append(" >= ? AND ");
                        queryFilter.append(filterCondition.indexedFieldName).append(ConditionTime.fromFieldNameSuffix).append(" <= ? ) OR ");
                        params.add(time.getFrom(14,false));
                        params.add(time.getTo(14,false));
                    }
                    queryFilter.setLength(queryFilter.length()-4);
                    queryFilter.append(')');
                default:
            }
        }
        return "SELECT FROM MeIdentification" + (queryFilter.length()>0 ? " WHERE " + queryFilter.substring(4) : "");
    }

}

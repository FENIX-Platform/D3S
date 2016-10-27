package org.fao.fenix.d3s.find.filter.impl;

import org.fao.fenix.commons.find.dto.condition.ConditionFilter;
import org.fao.fenix.commons.find.dto.condition.ConditionTime;
import org.fao.fenix.commons.find.dto.type.FieldFilterType;
import org.fao.fenix.d3s.find.filter.Filter;

import java.util.Collection;

//Standard query builder algorithm with intersection between field's values
public class StandardIntersectFilter extends Filter {

    private String createUnion(int index, Collection<Object> params, String fieldName, String operator) {
        StringBuilder queryUnion = new StringBuilder();
        StringBuilder queryFilter = new StringBuilder();

        for (Object param : params) {
            String letName = "$q"+index++;
            queryFilter.append(", ").append(letName).append(" = (SELECT FROM MeIdentification WHERE ").append(fieldName).append(' ').append(operator).append(" '").append(param).append("')");
            queryUnion.append(",").append(letName);
        }

        if (params.size()==1)
            return queryFilter.toString();
        else
            return ", $q"+index+" = (SELECT FROM MeIdentification WHERE @rid IN ( SELECT UNIONALL("+queryUnion.substring(1)+") LET "+queryFilter.substring(2) + " ))";
    }

    @Override
    public String createQuery(Collection<Object> params, ConditionFilter ... filter) throws Exception {
        StringBuilder queryIntersect = new StringBuilder();
        StringBuilder queryFilter = new StringBuilder();
        StringBuilder queryFilterTime = new StringBuilder();
        int index=0;
        for (ConditionFilter filterCondition : filter) {
            String operator = null;
            switch (filterCondition.filterType) {
                case code: operator = "CONTAINS"; break;
                case contact:
                case free:
                    operator = "LUCENE"; break;
                case id:
                case enumeration: operator = "="; break;
                default:
            }
            if (operator!=null) {
                queryFilter.append(createUnion(index, filterCondition.values, filterCondition.indexedFieldName, operator));

                int valuesCount = filterCondition.values.size();
                index += valuesCount>1 ? valuesCount : 0;
                queryIntersect.append(",").append("$q"+index++);
            }
        }
        for (ConditionFilter filterCondition : filter) {
            if (filterCondition.filterType==FieldFilterType.time) {
                queryFilterTime.append(" AND (");
                for (Object timeObject : filterCondition.values) {
                    ConditionTime time = (ConditionTime) timeObject;
                    if (time.from != null)
                        queryFilterTime.append(filterCondition.indexedFieldName).append(ConditionTime.toFieldNameSuffix).append(" >= ").append(time.getFrom(14)).append(" OR ");
                    if (time.to != null)
                        queryFilterTime.append(filterCondition.indexedFieldName).append(ConditionTime.fromFieldNameSuffix).append(" <= ").append(time.getTo(14)).append(" OR ");
                }
                queryFilterTime.setLength(queryFilter.length() - 4);
                queryFilterTime.append(')');
            }
        }

        String query = queryIntersect.length()>0 ? "SELECT FROM MeIdentification WHERE @rid IN ( SELECT INTERSECT(" + queryIntersect.substring(1) + ") LET " + queryFilter.substring(2) + " )" : "SELECT FROM MeIdentification";
        return query + (queryFilterTime.length() > 0 ? queryFilterTime : "");
    }
}

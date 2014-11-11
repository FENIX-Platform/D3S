package org.fao.fenix.d3s.find.query;

import org.fao.fenix.commons.find.dto.condition.ConditionFilter;
import org.fao.fenix.commons.find.dto.condition.ConditionTime;
import org.fao.fenix.commons.find.dto.type.FieldFilterType;

import java.util.Collection;

//Standard query builder algorithm with intersection between field's values
public class StandardIntersectQueryBuilder implements QueryBuilder {

    private String createUnion(int index, int length, String fieldName, String operator) {
        StringBuilder queryUnion = new StringBuilder();
        StringBuilder queryFilter = new StringBuilder();

        for (int i=0; i<length; i++) {
            String letName = "$q"+index++;
            queryFilter.append(", ").append(letName).append(" = (SELECT FROM MeIdentification WHERE ").append(fieldName).append(' ').append(operator).append(" ?)");
            queryUnion.append(",").append(letName);
        }

        if (length==1)
            return queryFilter.toString();
        else
            return ", $q"+index+" = (SELECT FROM MeIdentification WHERE @rid IN ( SELECT UNIONALL("+queryUnion.substring(1)+") LET "+queryFilter.substring(2) + " ))";
    }

    @Override
    public String createQuery(Collection<ConditionFilter> filter, Collection<Object> params) throws Exception {
        StringBuilder queryIntersect = new StringBuilder();
        StringBuilder queryFilter = new StringBuilder();
        StringBuilder queryFilterTime = new StringBuilder();
        int index=0,l;
        for (ConditionFilter filterCondition : filter) {
            String operator = null;
            switch (filterCondition.filterType) {
                case code: operator = "CONTAINS"; break;
                case contact: operator = "LUCENE"; break;
                case id:
                case enumeration: operator = "="; break;
                default:
            }
            if (operator!=null) {
                params.addAll(filterCondition.values);
                String queryUnion = createUnion(index,l=filterCondition.values.size(),filterCondition.fieldName,operator);

                index += l>1 ? l : 0;
                queryIntersect.append(",").append("$q"+index++);
                queryFilter.append(queryUnion);
            }
        }
        for (ConditionFilter filterCondition : filter) {
            if (filterCondition.filterType==FieldFilterType.time) {
                queryFilterTime.append(" AND (");
                for (Object timeObject : filterCondition.values) {
                    ConditionTime time = (ConditionTime) timeObject;
                    if (time.from != null) {
                        queryFilterTime.append(filterCondition.fieldName).append(ConditionTime.toFieldNameSuffix).append(" >= ? OR ");
                        params.add(time.from);
                    }
                    if (time.to != null) {
                        queryFilterTime.append(filterCondition.fieldName).append(ConditionTime.fromFieldNameSuffix).append(" <= ? OR ");
                        params.add(time.to);
                    }
                }
                queryFilterTime.setLength(queryFilter.length() - 4);
                queryFilterTime.append(')');
            }
        }

        String query = queryIntersect.length()>0 ? "SELECT FROM MeIdentification WHERE @rid IN ( SELECT INTERSECT(" + queryIntersect.substring(1) + ") LET " + queryFilter.substring(2) + " )" : "SELECT FROM MeIdentification";
        return query + (queryFilterTime.length() > 0 ? queryFilterTime : "");
    }
}

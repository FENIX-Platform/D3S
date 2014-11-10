package org.fao.fenix.d3s.find.query;

import org.fao.fenix.commons.find.dto.condition.ConditionFilter;
import org.fao.fenix.commons.find.dto.condition.ConditionTime;
import org.fao.fenix.commons.find.dto.type.FieldFilterType;

import java.util.Collection;

//Standard query builder algorithm with intersection between field's values
public class StandardIntersectQueryBuilder implements QueryBuilder {

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
                l=filterCondition.values.size();
                StringBuilder queryUnion = new StringBuilder();
                for (int i=0; i<l; i++) {
                    String letName = "$q"+index++;
                    queryFilter.append(", ").append(letName).append(" = (SELECT FROM MeIdentification WHERE ").append(filterCondition.fieldName).append(' ').append(operator).append(" ?)\n");
                    queryUnion.append(" ,").append(letName);
                }
                if (l>1) //Create a union
                    queryIntersect.append(" , UNION (").append(queryUnion.substring(2)).append(')');
                else
                    queryIntersect.append(queryUnion.toString());
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

        String query = queryIntersect.length()>0 ? "SELECT FROM ( SELECT INTERSECT (" + queryIntersect.substring(2) + ") LET " + queryFilter.substring(2) + " )" : "SELECT FROM MeIdentification";
        return query + (queryFilterTime.length() > 0 ? " WHERE " + queryFilterTime.substring(4) : "");
    }
}

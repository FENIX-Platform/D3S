package org.fao.fenix.d3s.msd.services.impl.engine;

import com.orientechnologies.orient.core.record.impl.ODocument;
import org.fao.fenix.commons.find.dto.condition.ConditionFilter;
import org.fao.fenix.commons.find.dto.condition.ConditionTime;
import org.fao.fenix.commons.utils.find.Engine;
import org.fao.fenix.d3s.msd.find.engine.SearchEngine;
import org.fao.fenix.d3s.server.tools.orient.OrientDao;

import javax.enterprise.context.Dependent;
import java.util.Collection;
import java.util.LinkedList;

@Engine("fenix")
@Dependent
public class DefaultEngine extends OrientDao implements SearchEngine {

    private final String ID_FIELD = "index|id";

    public String createQuery(Collection<Object> params, ConditionFilter... filter) throws Exception {
        StringBuilder queryFilter = new StringBuilder();
        for (ConditionFilter filterCondition : filter) {
            switch (filterCondition.filterType) {
                case code:
                    params.addAll(filterCondition.values);
                    queryFilter.append(" AND (");
                    for (int i = 0, l = filterCondition.values.size(); i < l; i++)
                        queryFilter.append(filterCondition.indexedFieldName).append(" CONTAINS ? OR ");
                    queryFilter.setLength(queryFilter.length() - 4);
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
                    for (int i = 0, l = filterCondition.values.size(); i < l; i++)
                        queryFilter.append(filterCondition.indexedFieldName).append(" = ? OR ");
                    queryFilter.setLength(queryFilter.length() - 4);
                    queryFilter.append(')');
                    break;
                case time:
                    queryFilter.append(" AND (");
                    for (Object timeObject : filterCondition.values) {
                        ConditionTime time = (ConditionTime) timeObject;

                        queryFilter.append('(').append(filterCondition.indexedFieldName).append(ConditionTime.toFieldNameSuffix).append(" >= ? AND ");
                        queryFilter.append(filterCondition.indexedFieldName).append(ConditionTime.fromFieldNameSuffix).append(" <= ? ) OR ");
                        params.add(time.getFrom(14, false));
                        params.add(time.getTo(14, false));
                    }
                    queryFilter.setLength(queryFilter.length() - 4);
                    queryFilter.append(')');
                default:
            }
        }
        return "SELECT "+ID_FIELD+" FROM MeIdentification" + (queryFilter.length() > 0 ? " WHERE " + queryFilter.substring(4) : "") + " group by "+ID_FIELD+ "";
    }


    public Collection<String> getUids(Collection<Object> params, ConditionFilter... filter) throws Exception {
        Collection<ODocument> ids = select(createQuery(params, filter), params.toArray());
        return (ids != null && !ids.isEmpty()) ? getIDS(ids) : new LinkedList<String>();
    }

    @Override
    public Collection<String> getUids(ConditionFilter... filter) throws Exception {
        Collection<Object> params = new LinkedList<>();
        return  getUids(params, filter);
    }


    //Utils
    private Collection<String> getIDS(Collection<ODocument> collection) {
        Collection<String> ids = new LinkedList<>();
        for (ODocument document : collection)
            ids.add(document.field(ID_FIELD).toString());
        return ids;
    }
}





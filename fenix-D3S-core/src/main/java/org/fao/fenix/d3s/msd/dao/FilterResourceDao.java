package org.fao.fenix.d3s.msd.dao;

import org.fao.fenix.commons.find.dto.condition.ConditionFilter;
import org.fao.fenix.commons.find.dto.condition.ConditionTime;
import org.fao.fenix.commons.find.dto.filter.*;
import org.fao.fenix.commons.find.dto.type.FieldFilterType;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;

import javax.inject.Inject;
import java.util.*;

public class FilterResourceDao extends ResourceDao {
    @Inject CodeListResourceDao codeListResourceDao;


    @Override
    public Collection loadData(MeIdentification metadata) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void insertData(MeIdentification metadata, Collection data) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void updateData(MeIdentification metadata, Collection data, boolean overwrite) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteData(MeIdentification metadata) throws Exception {
        throw new UnsupportedOperationException();
    }



    public Collection<MeIdentification> filter (ResourceFilter filter) throws Exception {
        Collection<Object> params = new LinkedList<>();
        String query = createQuery(normalizedFilter(filter), params);
        return query!=null ? select(MeIdentification.class, query, params.toArray()) : null;
    }


    //TODO Da isolare in un plugin apposito
    private String createQuery (Collection<ConditionFilter> filter, Collection<Object> params) throws Exception {
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



    //Utils
    private Collection<ConditionFilter> normalizedFilter(ResourceFilter filter) throws Exception {
        Collection<ConditionFilter> normalizedFilter = new LinkedList<>();
        if (filter!=null)
            for (Map.Entry<String, FieldFilter> filterEntry : filter.entrySet()) {
                String fieldName = ("index."+filterEntry.getKey()).replace('.','|');
                FieldFilter fieldFilter = filterEntry.getValue();
                FieldFilterType filterType = fieldFilter!=null ? fieldFilter.getFilterType() : null;

                if (filterType!=null) {
                    switch (filterType) {
                        case code:
                            Collection<Object> codes = new LinkedList<>();
                            for (CodesFilter codesFilter : fieldFilter.codes)
                                codes.addAll(getFilterCodes(codesFilter));
                            if (codes.size()>0)
                                normalizedFilter.add(new ConditionFilter(fieldName, filterType, codes));
                            break;
                        case contact:
                            Map<String, String> contactsFilter = new HashMap<>();
                            for (ContactFilter contactFilter : fieldFilter.contacts) {
                                String contactText = contactsFilter.get(contactFilter.role);
                                contactsFilter.put(contactFilter.role, (contactText==null ? "" : contactText+' ') + contactFilter.text);
                            }
                            for (Map.Entry<String, String> contactsFilterEntry : contactsFilter.entrySet())
                                normalizedFilter.add(new ConditionFilter(fieldName+'|'+contactsFilterEntry.getKey(), filterType, Arrays.asList((Object)contactsFilterEntry.getValue())));
                            break;
                        case enumeration:
                            Collection<Object> enumeration = new LinkedList<>();
                            enumeration.addAll(fieldFilter.enumeration);
                            normalizedFilter.add(new ConditionFilter(fieldName, filterType, enumeration));
                            break;
                        case time:
                            Collection<Object> timeFilters = new LinkedList<>();
                            timeFilters.addAll(fieldFilter.time);
                            normalizedFilter.add(new ConditionFilter(fieldName, filterType, timeFilters));
                        default:
                    }
                }
            }
        return normalizedFilter;
    }

    private Collection<String> getFilterCodes(CodesFilter codesFilter) throws Exception {
        Collection<String> codes = new LinkedList<>();
        String codeListID = codesFilter!=null && codesFilter.uid!=null ? codesFilter.uid + (codesFilter.version!=null ? '|'+codesFilter.version : "") : null;

        if (codeListID!=null)
            if (codesFilter.codes!=null && codesFilter.codes.size()>0)
                for (String filterCode : codesFilter.codes)
                    codes.add(codeListID+'|'+filterCode);
            else
                codes.add(codeListID);
            //TODO support level and levels parameters

        return codes;
    }
}

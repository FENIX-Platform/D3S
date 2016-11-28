package org.fao.fenix.d3s.msd.dao;

import org.fao.fenix.commons.find.dto.condition.ConditionFilter;
import org.fao.fenix.commons.find.dto.condition.ConditionTime;
import org.fao.fenix.commons.find.dto.filter.*;
import org.fao.fenix.commons.find.dto.filter.StandardFilter;
import org.fao.fenix.commons.find.dto.type.FieldFilterType;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.fenix.d3s.find.filter.Filter;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.*;

public class FilterResourceDao extends ResourceDao {
    @Inject private CodeListResourceDao codeListResourceDao;
    @Inject private Instance<Filter> queryBuilders;
    private static String queryBuildersPackage = Filter.class.getPackage().getName()+".impl.";


    @Override
    public void fetch(MeIdentification metadata) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public Long getSize(MeIdentification metadata) throws Exception {
        throw new UnsupportedOperationException();
    }

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

    @Override
    public void clean(MeIdentification metadata) throws Exception {
        throw new UnsupportedOperationException();
    }


    public Collection<MeIdentification> filter (StandardFilter filter, String businessName) throws Exception {
        try {
            Class<? extends Filter> businessClass = businessName!=null ? (Class<? extends Filter>) Class.forName(queryBuildersPackage+businessName) : org.fao.fenix.d3s.find.filter.impl.StandardFilter.class;
            Collection<MeIdentification> resources = queryBuilders.select(businessClass).iterator().next().filter(normalizedFilter(filter));
            return resources.size()>0 ? resources : new LinkedList<MeIdentification>();
        } catch (ClassNotFoundException ex) {
            throw new Exception("Cannot find specified filtering logic implementation class: "+queryBuildersPackage+businessName);
        } catch (ClassCastException ex){
            throw new Exception("Filtering logic class must extend class "+Filter.class.getName());
        }
    }




    //Utils
    private ConditionFilter[] normalizedFilter(StandardFilter filter) throws Exception {
        LinkedList<ConditionFilter> normalizedFilter = new LinkedList<>();
        if (filter!=null)
            for (Map.Entry<String, FieldFilter> filterEntry : filter.entrySet()) {
                String fieldName = filterEntry.getKey();
                String indexedFieldName = ("index."+fieldName).replace('.','|');
                FieldFilter fieldFilter = filterEntry.getValue();
                FieldFilterType filterType = fieldFilter!=null ? fieldFilter.retrieveFilterType() : null;

                if (filterType!=null) {
                    switch (filterType) {
                        case id:
                            Collection<Object> ids = new LinkedList<>();
                            for (IdFilter idFilter : fieldFilter.ids)
                                if (idFilter.uid!=null)
                                    ids.add(idFilter.uid + (idFilter.version!=null && !idFilter.version.trim().equals("")? '|'+idFilter.version : ""));
                            if (ids.size()>0)
                                normalizedFilter.add(new ConditionFilter(fieldName, indexedFieldName, filterType, ids));
                            break;
                        case code:
                            Collection<Object> codes = new LinkedList<>();
                            for (CodesFilter codesFilter : fieldFilter.codes)
                                codes.addAll(getFilterCodes(codesFilter));
                            if (codes.size()>0)
                                normalizedFilter.add(new ConditionFilter(fieldName, indexedFieldName, filterType, codes));
                            break;
                        case contact:
                            Map<String, String> contactsFilter = new HashMap<>();
                            for (ContactFilter contactFilter : fieldFilter.contacts) {
                                String contactText = contactsFilter.get(contactFilter.role);
                                contactsFilter.put(contactFilter.role, (contactText==null ? "" : contactText+' ') + contactFilter.text);
                            }
                            for (Map.Entry<String, String> contactsFilterEntry : contactsFilter.entrySet())
                                normalizedFilter.add(new ConditionFilter(fieldName, indexedFieldName+'|'+contactsFilterEntry.getKey(), filterType, Arrays.asList((Object)contactsFilterEntry.getValue())));
                            break;
                        case free:
                            normalizedFilter.addFirst(new ConditionFilter(fieldName, indexedFieldName, filterType, Arrays.asList((Object)fieldFilter.text)));
                            break;
                        case enumeration:
                            Collection<Object> enumeration = new LinkedList<>();
                            enumeration.addAll(fieldFilter.enumeration);
                            normalizedFilter.add(new ConditionFilter(fieldName, indexedFieldName, filterType, enumeration));
                            break;
                        case time:
                            Collection<Object> timeFilters = new LinkedList<>();
                            if (fieldFilter.time!=null)
                                for (TimeFilter time : fieldFilter.time)
                                    timeFilters.add(new ConditionTime(time.from, time.to));
                            normalizedFilter.add(new ConditionFilter(fieldName, indexedFieldName, filterType, timeFilters));
                        default:
                    }
                }
            }
        return normalizedFilter.toArray(new ConditionFilter[normalizedFilter.size()]);
    }

    private Collection<String> getFilterCodes(CodesFilter codesFilter) throws Exception {
        Collection<String> codes = new LinkedList<>();
        if (codesFilter!=null) {
            String codeListID = codesFilter.uid != null ? codesFilter.uid + '|' + (codesFilter.version != null ? codesFilter.version : "") : "|";

            if (codesFilter.codes != null && codesFilter.codes.size() > 0)
                for (String filterCode : codesFilter.codes)
                    codes.add(codeListID + '|' + filterCode);
            else if(codesFilter.uid != null)
                codes.add(codeListID);
            //TODO support level and levels parameters
        }
        return codes;
    }
}

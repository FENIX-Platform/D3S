package org.fao.fenix.d3s.msd.dao;

import org.fao.fenix.commons.find.dto.condition.ConditionFilter;
import org.fao.fenix.commons.find.dto.filter.*;
import org.fao.fenix.commons.find.dto.type.FieldFilterType;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.fenix.d3s.find.filter.StandardIntersectFilter;
import org.fao.fenix.d3s.find.filter.Filter;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.*;

public class FilterResourceDao extends ResourceDao {
    @Inject private CodeListResourceDao codeListResourceDao;
    @Inject private Instance<Filter> queryBuilders;
    private static String queryBuildersPackage = Filter.class.getPackage().getName()+'.';


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



    public Collection<MeIdentification> filter (ResourceFilter filter, String businessName) throws Exception {
        try {
            Class<? extends Filter> businessClass = businessName!=null ? (Class<? extends Filter>) Class.forName(queryBuildersPackage+businessName) : StandardIntersectFilter.class;
            Collection<MeIdentification> resources = queryBuilders.select(businessClass).iterator().next().filter(normalizedFilter(filter));
            return resources.size()>0 ? resources : null;
        } catch (ClassNotFoundException ex) {
            throw new Exception("Cannot find specified filtering logic implementation class: "+queryBuildersPackage+businessName);
        } catch (ClassCastException ex){
            throw new Exception("Filtering logic class must extend class "+Filter.class.getName());
        }
    }




    //Utils
    private ConditionFilter[] normalizedFilter(ResourceFilter filter) throws Exception {
        Collection<ConditionFilter> normalizedFilter = new LinkedList<>();
        if (filter!=null)
            for (Map.Entry<String, FieldFilter> filterEntry : filter.entrySet()) {
                String fieldName = ("index."+filterEntry.getKey()).replace('.','|');
                FieldFilter fieldFilter = filterEntry.getValue();
                FieldFilterType filterType = fieldFilter!=null ? fieldFilter.getFilterType() : null;

                if (filterType!=null) {
                    switch (filterType) {
                        case id:
                            Collection<Object> ids = new LinkedList<>();
                            for (IdFilter idFilter : fieldFilter.ids)
                                if (idFilter.uid!=null)
                                    ids.add(idFilter.uid + (idFilter.version!=null && !idFilter.version.trim().equals("")? '|'+idFilter.version : ""));
                            if (ids.size()>0)
                                normalizedFilter.add(new ConditionFilter(fieldName, filterType, ids));
                            break;
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
        return normalizedFilter.toArray(new ConditionFilter[normalizedFilter.size()]);
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

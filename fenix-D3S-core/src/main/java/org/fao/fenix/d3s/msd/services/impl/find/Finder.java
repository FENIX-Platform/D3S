package org.fao.fenix.d3s.msd.services.impl.find;
import org.fao.fenix.commons.find.dto.condition.ConditionFilter;
import org.fao.fenix.commons.find.dto.condition.ConditionTime;
import org.fao.fenix.commons.find.dto.filter.*;
import org.fao.fenix.commons.find.dto.type.FieldFilterType;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.fenix.commons.utils.find.Engine;
import org.fao.fenix.d3s.msd.dao.MetadataResourceDao;
import org.fao.fenix.d3s.msd.find.business.Business;
import org.fao.fenix.d3s.msd.find.business.BusinessFactory;
import org.fao.fenix.d3s.msd.find.engine.SearchEngine;
import org.fao.fenix.d3s.msd.find.engine.SearchEngineFactory;
import org.fao.fenix.d3s.server.dto.DatabaseStandards;

import javax.inject.Inject;
import java.util.*;

public class Finder {

    @Inject SearchEngineFactory searchEngineFactory;
    @Inject BusinessFactory businessFactory;
    @Inject DatabaseStandards databaseStandards;

    @Inject MetadataResourceDao metadataDao;

    //business
    public Collection<MeIdentification> filter (StandardFilter filter, String businessName, Collection<String> engines) throws Exception {

        if(filter == null || filter.isEmpty())
            throw new Exception("Please fill the filter fields before");

        // Get the context from the filter
        ConditionFilter[] normalizedFilter = normalizeFilter(filter);

        // creation of the search engine plugins and the business plugin
        Collection<SearchEngine> searchEngines = searchEngineFactory.getEngines(engines);
        Business business = businessFactory.getBusiness(businessName);

        // creation of the map with the values taken from the plugins
        Map<String,Collection<String>> idsSearchEngine = new HashMap<>();
        for (SearchEngine engine : searchEngines) {
            Collection<String> ids = engine.getUids(normalizedFilter);
            if( ids!= null && ids.size()>0)
                 idsSearchEngine.put(engine.getClass().getAnnotation(Engine.class).value(), ids);
        }

        //Merge ids lists
        Collection<String> ids = idsSearchEngine.isEmpty() ? new LinkedList<String>() : business.getOrderedUid(idsSearchEngine);

        //Return correspondent metadata
        Collection<MeIdentification> metadataList = new LinkedList<>();
        for (String id : ids) {
            MeIdentification metadata = metadataDao.loadMetadata(id, null);
            if (metadata!=null)
                metadataList.add(metadata);
        }
        return metadataList;
    }


    //Utils
    private ConditionFilter[] normalizeFilter(StandardFilter filter) throws Exception {
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

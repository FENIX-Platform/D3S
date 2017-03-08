package org.fao.fenix.d3s.msd.services.impl.find;
import org.fao.fenix.commons.find.dto.condition.ConditionFilter;
import org.fao.fenix.commons.find.dto.condition.ConditionTime;
import org.fao.fenix.commons.find.dto.filter.*;
import org.fao.fenix.commons.find.dto.type.FieldFilterType;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.fenix.d3s.msd.find.engine.SearchEngine;
import org.fao.fenix.d3s.msd.find.engine.SearchEngineFactory;
import org.fao.fenix.d3s.server.dto.DatabaseStandards;

import javax.inject.Inject;
import java.util.*;

public class Finder {

    @Inject Bridge bridge;
    @Inject SearchEngineFactory searchEngineFactory;
    @Inject DatabaseStandards databaseStandards;

    public Collection<MeIdentification> filter (StandardFilter filter, String businessName, String engineName) throws Exception {
        Collection<String> contexts = getContext(normalizeFilter(filter));

        Collection<SearchEngine> searchEngines = searchEngineFactory.getEngines(contexts, engineName);

        Collection<String> uids = new LinkedList<>();
        for (SearchEngine engine : searchEngines) {
            Collection<String> ids = engine.getUids(normalizeFilter(filter));
            if(ids!= null)
                uids.addAll(engine.getUids(normalizeFilter(filter)));
        }

        Collection<String> sortedIds = sortByValue(createPriorityMap(uids)).keySet();
        System.out.println("here");
        return bridge.getMetadata(sortedIds);


        // creation of the plugin

        // get of the uids
        // get of the resources
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


    private Collection<String> getContext (ConditionFilter[] filters) {
        Collection<String> contexts = new LinkedList<>();
        for(ConditionFilter filter: filters)
            if(filter.fieldName.equals("dsd.contextSystem"))
                for(Object value: filter.values)
                    contexts.add(value.toString());
        return contexts;

    }


    private Map<String, Integer> createPriorityMap ( Collection<String> ids) {

        Map<String, Integer> priorityMap = new HashMap<>();
        if(priorityMap == null)
            priorityMap = new HashMap<>();

        for(String id: ids) {
            if (!priorityMap.containsKey(id))
                priorityMap.put(id, 0);
            priorityMap.put(id,priorityMap.get(id)+1);
        }
        return priorityMap;
    }


    private  Map<String, Integer> sortByValue(Map<String, Integer> unsortedMap) {

        List<Map.Entry<String, Integer>> list = new LinkedList<>(unsortedMap.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        Map<String, Integer> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }








}

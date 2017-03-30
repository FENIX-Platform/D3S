package org.fao.fenix.d3s.msd.services.impl.find.business;

import org.fao.fenix.commons.utils.annotations.find.Business;

import java.util.*;

@Business("union")
public class UnionBusiness implements org.fao.fenix.d3s.msd.find.business.Business {


    @Override
    public Collection<String> getOrderedUid(Map<String, Collection<String>> ids) throws Exception {
        Collection<String> result = new LinkedHashSet<>();
        if (ids!=null)
            for (String key : ids.keySet())
                result.addAll(ids.get(key));
        return result;

    }
}

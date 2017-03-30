package org.fao.fenix.d3s.msd.services.impl.find.business;

import org.fao.fenix.commons.utils.annotations.find.Business;

import java.util.*;

@Business("intersect")
public class IntersectBusiness implements org.fao.fenix.d3s.msd.find.business.Business {


    @Override
    public Collection<String> getOrderedUid(Map<String, Collection<String>> ids) throws Exception {
        Collection<String> result = new LinkedHashSet<>();
        if (ids!=null)
            for (String key : ids.keySet())
                if (result.isEmpty())
                    result.addAll(ids.get(key));
                else
                    result.retainAll(ids.get(key));
        return result;
    }

}

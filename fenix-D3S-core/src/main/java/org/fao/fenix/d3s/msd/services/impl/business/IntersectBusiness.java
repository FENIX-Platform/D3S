package org.fao.fenix.d3s.msd.services.impl.business;

import org.fao.fenix.commons.utils.find.Business;

import java.util.*;

@Business("intersect")
public class IntersectBusiness implements org.fao.fenix.d3s.msd.find.business.Business {


    @Override
    public Collection<String> getOrderedUid(Map<String, LinkedList<String>> ids) throws Exception {
        if (ids == null || ids.size() == 0)
            return new LinkedList<>();

        Collection<String> result = new HashSet<>();
        for (String key : ids.keySet()) {
            if (result.isEmpty())
                result.addAll(ids.get(key));
            else
                result.retainAll(ids.get(key));
        }
        return result;
    }

}

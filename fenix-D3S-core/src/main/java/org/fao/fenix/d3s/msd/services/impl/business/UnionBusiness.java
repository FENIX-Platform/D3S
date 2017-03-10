package org.fao.fenix.d3s.msd.services.impl.business;

import org.fao.fenix.commons.utils.find.Business;

import javax.enterprise.context.Dependent;
import java.util.*;

@Business("union")
@Dependent

public class UnionBusiness implements org.fao.fenix.d3s.msd.find.business.Business {


    @Override
    public Collection<String> getOrderedUid(Map<String, LinkedList<String>> ids) throws Exception {
        if (ids == null || ids.size() == 0)
            return new LinkedList<>();

        Collection<String> result = new HashSet<>();
        for (String key : ids.keySet())
            result.addAll(ids.get(key));
        return result;

    }
}

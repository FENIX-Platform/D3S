package org.fao.fenix.d3s.find.filter;

import org.fao.fenix.commons.find.dto.condition.ConditionFilter;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.fenix.d3s.server.tools.orient.OrientDao;

import java.util.Collection;
import java.util.LinkedList;

public abstract class Filter extends OrientDao {

    protected abstract String createQuery(Collection<Object> params, ConditionFilter ... filter) throws Exception;

    public Collection<MeIdentification> filter(ConditionFilter ... filter) throws Exception {
        Collection<Object> params = new LinkedList<>();
        return select(MeIdentification.class, createQuery(params, filter), params.toArray());
    }
}

package org.fao.fenix.d3s.find.query;

import org.fao.fenix.commons.find.dto.condition.ConditionFilter;

import java.util.Collection;

public interface QueryBuilder {

    public String createQuery (Collection<ConditionFilter> filter, Collection<Object> params) throws Exception;
}

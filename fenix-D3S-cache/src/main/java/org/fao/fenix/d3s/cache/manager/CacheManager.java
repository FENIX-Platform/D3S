package org.fao.fenix.d3s.cache.manager;


import org.fao.fenix.commons.find.dto.filter.StandardFilter;
import org.fao.fenix.commons.msd.dto.full.DSDDataset;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.fenix.commons.utils.Order;
import org.fao.fenix.commons.utils.Page;
import org.fao.fenix.d3s.cache.dto.LoadStatus;

import java.util.Iterator;

public interface CacheManager {

    public void init() throws Exception;


    public Iterator<Object[]> load(MeIdentification<DSDDataset> metadata, Order order, Page page) throws Exception;
    public void store(MeIdentification<DSDDataset> metadata, Iterator<Object[]> data) throws Exception;
    public LoadStatus status(MeIdentification<DSDDataset> metadata) throws Exception;
    //pagination is supported only if destination exists
    public Iterator<Object[]> filter(MeIdentification<DSDDataset>[] resources, MeIdentification<DSDDataset> destination, StandardFilter filter, Order order, Page page) throws Exception;
}

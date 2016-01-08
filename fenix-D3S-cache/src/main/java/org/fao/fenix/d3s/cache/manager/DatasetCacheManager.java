package org.fao.fenix.d3s.cache.manager;

import org.fao.fenix.commons.msd.dto.data.Resource;
import org.fao.fenix.commons.msd.dto.full.Code;
import org.fao.fenix.commons.msd.dto.full.DSDCodelist;
import org.fao.fenix.commons.msd.dto.full.DSDDataset;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;

import java.util.Collection;
import java.util.Iterator;

public interface DatasetCacheManager extends CacheManager<DSDDataset, Object[]> {

    /**
     * Store/append/overwrite resource data into the cache. The execution is asynchronous and the 'status' function can be used to know the status.
     * If an exception is catch during the creation, the resource data will be set to 'incomplete' and will be unavailable as a source for other load/filter operations.
     * If overwrite is true, old data will be erased.
     * If overwrite is false, resource status will be checked.
     *   If it is 'incomplete' the store function will try to use the Iterator skip function. If the skip is unavailable the store function will overwrite the data.
     *   If it is 'ready' all available data will be added.
     * If a valid timeout is specified the resource will be removed during clean operation execution
     * @param metadata Referenced resource metadata.
     * @param data New data.
     * @param overwrite Overwrite flag.
     * @param timeout New timeout in seconds for the current resource. A null value or a value < 0 will maintain the existing one. A value = 0 (default) means no timeout.
     * @throws Exception A ConcurrentModificationException will be thrown if a store activity is already running on the same resource.
     */
    public void store(MeIdentification<DSDDataset> metadata, Iterator<Object[]> data, boolean overwrite, Long timeout, Collection<Resource<DSDCodelist,Code>> codeLists) throws Exception;

}

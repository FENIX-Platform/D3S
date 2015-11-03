package org.fao.fenix.d3s.cache.manager.listener;

import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.fenix.d3s.cache.storage.dataset.DatasetStorage;

import java.sql.Connection;

public interface DatasetCacheListener {
    /**
     * After creation event
     * @param datasetInfo Dataset information related to it's cache storage. Connection is null.
     * @return true to block event propagation
     */
    boolean created(DatasetAccessInfo datasetInfo);
    /**
     * Before write session event
     * @param datasetInfo Dataset information related to it's cache storage.
     * @return true to block event propagation
     */
    boolean updating(DatasetAccessInfo datasetInfo);
    /**
     * After write session event
     * @param datasetInfo Dataset information related to it's cache storage.
     * @return true to block event propagation
     */
    boolean updated(DatasetAccessInfo datasetInfo);
    /**
     * Before remove event
     * @param datasetInfo Dataset information related to it's cache storage. Connection is null.
     * @return true to block event propagation
     */
    boolean removing(DatasetAccessInfo datasetInfo);
}

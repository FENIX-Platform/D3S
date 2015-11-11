package org.fao.fenix.d3s.cache.manager.listener;

import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.fenix.d3s.cache.storage.dataset.DatasetStorage;

import java.sql.Connection;
import java.sql.SQLException;

public interface DatasetCacheListener {

    /**
     * Before write session event
     * @param datasetInfo Dataset information related to it's cache storage.
     * @return true to block event propagation
     */
    boolean updating(DatasetAccessInfo datasetInfo) throws Exception;
    /**
     * After write session event
     * @param datasetInfo Dataset information related to it's cache storage.
     * @return true to block event propagation
     */
    boolean updated(DatasetAccessInfo datasetInfo) throws Exception;
    /**
     * Before remove event
     * @param datasetInfo Dataset information related to it's cache storage. Connection is null.
     * @return true to block event propagation
     */
    boolean removing(DatasetAccessInfo datasetInfo) throws Exception;
}

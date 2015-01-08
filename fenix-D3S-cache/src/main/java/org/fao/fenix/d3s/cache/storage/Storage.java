package org.fao.fenix.d3s.cache.storage;

import org.fao.fenix.d3s.cache.dto.StoreStatus;

import java.util.Map;

public interface Storage {

    /**
     * Init the current storage.
     * @throws Exception
     */
    public void open() throws Exception;

    /**
     * Dispose current storage system resources.
     */
    public void close();

    /**
     * Load current cached resources storage metadata.
     * @return The resources status map by resource id.
     * @throws Exception
     */
    public Map<String,StoreStatus> loadMetadata() throws Exception;

    /**
     * Load a specific resource storage metadata.
     * @param resourceId Referenced resource internal id.
     * @return Resource status.
     * @throws Exception
     */
    public StoreStatus loadMetadata(String resourceId) throws Exception;

    /**
     * Store resource metadata. This function is intended for internal use by the implementing class.
     * @param resourceId Referenced resource internal id.
     * @param status Resource status.
     */
    public void storeMetadata(String resourceId, StoreStatus status);

    /**
     * Overwrite/append a set of resources metadata. This function can be used for import purposes.
     * @param metadata Resources storage metadata.
     * @param overwrite Flag to set overwrite or append mode.
     */
    public void storeMetadata(Map<String,StoreStatus> metadata, boolean overwrite);

    /**
     * Restore a clean status for the storage by metadata validation/update and removing timed out data.
     * @throws Exception
     */
    public void clean() throws Exception;

    /**
     * Remove any existing information from the storage.
     * @throws Exception
     */
    public void reset() throws Exception;
}

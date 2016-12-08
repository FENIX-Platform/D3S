package org.fao.fenix.d3s.cache.manager;


import org.fao.fenix.commons.msd.dto.full.DSD;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.fenix.commons.utils.Order;
import org.fao.fenix.commons.utils.Page;
import org.fao.fenix.d3s.cache.dto.StoreStatus;
import org.fao.fenix.d3s.cache.storage.Storage;

import java.util.Collection;
import java.util.Iterator;


public interface CacheManager<M extends DSD, D> {
    /**
     * Start the initialization of the cache manager component. It should be called one time per instance.
     * @throws Exception
     */
    public void init(Storage storage) throws Exception;

    /**
     * Get the cache manager data block size used during a store activity execution.
     * @return The block size as number of items.
     */
    public int getStoreBufferSize();

    /**
     * Get current cache manager default storage manager.
     * @return Current storage manager.
     */
    public Storage getStorage();

    /**
     * Remove all of the timed out and incomplete resources.
     * @return Number of removed resources.
     */
    public int clean() throws Exception;


    /**
     * Load existing data from cache with ordering and pagination options. The execution will wait data availability in case a store activity is running on the same resource.
     * Data availability depends on required ordering and pagination properties. If order parameter is full the load function will wait store activity completion. If page parameter is full the load function will wait page data availability or the store activity completion.
     * @param metadata Referenced resource metadata.
     * @param order Ordering properties.
     * @param page Pagination properties.
     * @return Required data. An empty Iterator if the selection is empty. Null if the resource isn't available.
     * @throws Exception
     */
    public Iterator<D> load(MeIdentification<M> metadata, Order order, Page page) throws Exception;

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
    public void store(MeIdentification<M> metadata, Iterator<D> data, boolean overwrite, Long timeout) throws Exception;

    /**
     * Remove specified resource data from cache if it exists.
     * @param metadata Referenced resource metadata.
     * @throws Exception
     */
    public void remove(MeIdentification<M> metadata) throws Exception;

    /**
     * Retrieve the store status of the specified resource data.
     * @param metadata Referenced resource metadata.
     * @return Store status. Null if resource don't exists.
     * @throws Exception
     */
    public StoreStatus status(MeIdentification<M> metadata) throws Exception;

    /**
     * Retrieve the cached resource validity comparing metadata and cached resource dates.
     * @param metadata Referenced resource metadata.
     * @return Store status. Null if resource don't exists.
     * @throws Exception
     */
    public boolean checkUpdateDateIsValid(MeIdentification<M> metadata, StoreStatus status) throws Exception;

    /**
     * Retrive the resource's data block size. The unit depends on the kind of data stored into the cache (e.g. the number of rows for a dataset or the number of codes for a codelist).
     * @param metadata Referenced resource metadata.
     * @return Data block size. Null if resource don't exists.
     * @throws Exception
     */
    public Long size(MeIdentification<M> metadata) throws Exception;

    /**
     * Return the standard internal id used to identify the resource into the cache. For dataset type of cache it corresponds to the table name.
     * @param metadata Resource metadata.
     * @return Internal resource
     */
    String getID(MeIdentification metadata);
}

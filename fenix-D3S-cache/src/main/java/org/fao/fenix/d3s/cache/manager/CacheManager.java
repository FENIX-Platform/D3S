package org.fao.fenix.d3s.cache.manager;


import org.fao.fenix.commons.find.dto.filter.DataFilter;
import org.fao.fenix.commons.find.dto.filter.StandardFilter;
import org.fao.fenix.commons.msd.dto.data.Resource;
import org.fao.fenix.commons.msd.dto.full.DSD;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.fenix.commons.utils.Order;
import org.fao.fenix.commons.utils.Page;
import org.fao.fenix.d3s.cache.dto.StoreStatus;

import java.util.Iterator;

public interface CacheManager<M extends DSD, D> {
    /**
     * Start the initialization of the cache manager component. It should be called one time per instance.
     * @throws Exception
     */
    public void init() throws Exception;

    /**
     * Get the cache manager data block size used during a store activity execution.
     * @return The block size as number of items.
     */
    public int getStoreBufferSize();

    /**
     * Remove all of the timed out and incomplete resources.
     * @return Number of removed resources.
     */
    public int clean();


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
     * If a valid timeout is specified the resource will be removed in lazy mode (when any operation is called on the specified resource or when the clean operation is executed)
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
     * @return Store status.
     * @throws Exception
     */
    public StoreStatus status(MeIdentification<M> metadata) throws Exception;


    /**
     * This method store/append/overwrite new resource data filtering existing resources data. The execution is asynchronous and the 'status' function can be used to know the status.
     * Destination resource metadata will be used to define new data block structure and (combined with rows filter) to select only required information from the source resources.
     * If an exception is catch during the creation, the resource data will be set to 'incomplete' and will be unavailable as a source for other load/filter operations.
     *
     * Resources with a valid 'data' property will be stored as cached resources before to proceed (with overwrite = true).
     * Resources without a valid 'data' property must be already present into the cache.
     *
     * If overwrite is true, old data will be erased.
     * If overwrite is false, resource status will be checked.
     *   If it is 'incomplete' the store function will try to use the Iterator skip function. If the skip is unavailable the store function will overwrite the data.
     *   If it is 'ready' all available data will be added.
     * If a valid timeout is specified the resource will be removed in lazy mode (when any operation is called on the specified resource or when the clean operation is executed)
     * @param resources Existing/new resources metadata and data.
     * @param rowsFilter Filter for rows selection over the different existing resources.
     * @param destination New resource metadata.
     * @param overwrite Overwrite flag.
     * @param timeout New timeout in seconds for the current resource. A null value or a value < 0 will maintain the existing one. A value = 0 (default) means no timeout.
     * @throws Exception An exception is thrown if some of the sources data blocks don't exists or is incompatible.
     */
    public void filter(Resource<M,D>[] resources, StandardFilter rowsFilter, MeIdentification<M> destination, boolean overwrite, Long timeout) throws Exception;

    /**
     * This method filter existing resources data. The execution is synchronous and returned result is volatile.
     * All of the involved resources must be already available into the cache.
     * @param resourcesMetadata Referenced resources metadata.
     * @param filter Data filter for columns and rows.
     * @param order Ordering properties.
     * @return Iterator connected to the generated data.
     * @throws Exception
     */
    public Iterator<D> filter(MeIdentification<M>[] resourcesMetadata, DataFilter filter, Order order) throws Exception;
}

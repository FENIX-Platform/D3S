package org.fao.fenix.d3s.cache.storage.dataset;

import org.fao.fenix.commons.find.dto.filter.DataFilter;
import org.fao.fenix.commons.utils.Iterator;
import org.fao.fenix.commons.utils.Order;
import org.fao.fenix.commons.utils.Page;
import org.fao.fenix.d3s.cache.dto.StoreStatus;
import org.fao.fenix.d3s.cache.dto.dataset.Table;

import java.util.Map;

public abstract class DefaultStorage extends H2Database {


    //DATA
    @Override
    public void create(Table tableStructure) throws Exception {
    }


    @Override
    public Iterator<Object[]> load(Order ordering, Page pagination, DataFilter filter, String... sourceTablesName) throws Exception {
        return null;
    }

    @Override
    public StoreStatus store(String tableName, Iterator<Object[]> data, int size, boolean overwrite) throws Exception {
        return null;
    }

    @Override
    public StoreStatus store(String tableName, DataFilter filter, boolean overwrite, String... sourceTablesName) throws Exception {
        return null;
    }

    @Override
    public void delete(String tableName) throws Exception {

    }



    //METADATA
    @Override
    public Map<String, StoreStatus> loadMetadata() throws Exception {
        return null;
    }

    @Override
    public StoreStatus loadMetadata(String resourceId) throws Exception {
        return null;
    }

    @Override
    public void storeMetadata(String resourceId, StoreStatus status) {

    }

    @Override
    public void storeMetadata(Map<String, StoreStatus> metadata, boolean overwrite) {

    }

    @Override
    public void clean() throws Exception {

    }

    @Override
    public void reset() throws Exception {

    }
}

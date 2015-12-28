package org.fao.fenix.d3s.cache.storage.dataset;

import org.fao.fenix.d3s.cache.storage.StorageName;

import javax.inject.Singleton;


@Singleton
@StorageName("h2")
public class DefaultFixedStorage extends DefaultStorage {

    @Override
    protected String getStoragePropertiesPrefix() {
        return "fixed";
    }

}

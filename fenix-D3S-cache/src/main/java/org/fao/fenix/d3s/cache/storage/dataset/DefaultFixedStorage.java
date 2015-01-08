package org.fao.fenix.d3s.cache.storage.dataset;

import javax.inject.Singleton;


@Singleton
public class DefaultFixedStorage extends DefaultStorage {

    @Override
    protected String getStoragePropertiesPrefix() {
        return "fixed";
    }

}

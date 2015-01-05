package org.fao.fenix.d3s.cache.storage.impl;

import org.fao.fenix.d3s.cache.storage.DatasetStorage;
import org.fao.fenix.d3s.cache.tools.h2.H2Database;

import javax.inject.Singleton;

@Singleton
public class H2DatasetDefaultStorage extends H2Database implements DatasetStorage {
    @Override
    protected String getStoragePropertiesPrefix() {
        return "fixed";
    }
}

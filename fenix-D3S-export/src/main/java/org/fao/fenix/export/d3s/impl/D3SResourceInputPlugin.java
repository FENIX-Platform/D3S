package org.fao.fenix.export.d3s.impl;

import org.fao.fenix.commons.msd.dto.data.Resource;
import org.fao.fenix.commons.msd.dto.full.DSDDataset;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.fenix.d3s.msd.services.rest.ResourcesService;
import org.fao.fenix.export.core.dto.data.CoreData;
import org.fao.fenix.export.core.dto.data.CoreTableData;
import org.fao.fenix.export.core.input.plugin.Input;
import org.fao.fenix.export.d3s.legacy.ExportManagerPlugin;

import java.util.Iterator;
import java.util.Map;

public class D3SResourceInputPlugin extends Input {
    Resource<DSDDataset,Object[]> resource;

    @Override
    public void init(Map<String, Object> config, Resource resource) {
        this.resource = resource;
    }

    @Override
    public CoreData getResource() {
        final Resource<DSDDataset,Object[]> rawData = resource;
        return new CoreData<Object[]>() {
            @Override
            public MeIdentification getMetadata() {
                return rawData.getMetadata();
            }

            @Override
            public Iterator<Object[]> getData() {
                return rawData.getData().iterator();
            }
        };
    }

}

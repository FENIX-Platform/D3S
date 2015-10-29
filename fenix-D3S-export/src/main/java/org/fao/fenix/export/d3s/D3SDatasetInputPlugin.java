package org.fao.fenix.export.d3s;

import org.fao.fenix.commons.msd.dto.data.Resource;
import org.fao.fenix.commons.msd.dto.full.DSDDataset;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.fenix.d3s.msd.services.rest.ResourcesService;
import org.fao.fenix.export.core.dto.data.CoreData;
import org.fao.fenix.export.core.input.plugin.Input;

import java.util.Iterator;
import java.util.Map;

public class D3SDatasetInputPlugin extends Input {
    String uid;
    String version;
    ResourcesService service;

    @Override
    public void init(Map<String, Object> config, Resource resource) {
        MeIdentification metadata = resource!=null ? resource.getMetadata() : null;
        uid = metadata!=null ? metadata.getUid() : null;
        version = metadata!=null ? metadata.getVersion() : null;
        service = ExportManager.getResourcesService(config!=null && config.containsKey("lang") ? (String)config.get("lang") : "EN");
    }

    @Override
    public CoreData getResource() {
        try {
            long time = System.currentTimeMillis();
            final Resource<DSDDataset,Object[]> data = service.loadResource(uid, version);
            System.out.println("Time = "+(System.currentTimeMillis()-time));
            if (data!=null)
                return new CoreData<Object[]>() {
                    @Override
                    public MeIdentification getMetadata() {
                        return data.getMetadata();
                    }
                    @Override
                    public Iterator<Object[]> getData() {
                        return data.getData()!=null ? data.getData().iterator() : null;
                    }
                };
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

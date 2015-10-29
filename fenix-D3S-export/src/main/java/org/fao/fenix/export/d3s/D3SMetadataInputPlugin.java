package org.fao.fenix.export.d3s;

import org.fao.fenix.commons.msd.dto.data.Resource;
import org.fao.fenix.commons.msd.dto.full.DSDDataset;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.fenix.d3s.msd.services.rest.ResourcesService;
import org.fao.fenix.export.core.dto.data.CoreData;
import org.fao.fenix.export.core.input.plugin.Input;

import java.util.Iterator;
import java.util.Map;

public class D3SMetadataInputPlugin extends Input {
    String uid;
    String version;
    ResourcesService service;

    @Override
    public void init(Map<String, Object> config, Resource resource) {
        MeIdentification metadata = resource!=null ? resource.getMetadata() : null;
        uid = metadata!=null ? metadata.getUid() : null;
        version = metadata!=null ? metadata.getVersion() : null;
        service = ExportManager.getResourcesService(config.containsKey("lang") ? (String)config.get("lang") : "EN");
    }

    @Override
    public CoreData getResource() {
        try {
            final MeIdentification<DSDDataset> metadata = service.loadMetadata(uid, version);
            if (metadata!=null)
                return new CoreData<Object[]>() {
                    @Override
                    public MeIdentification getMetadata() {
                        return metadata;
                    }
                    @Override
                    public Iterator<Object[]> getData() {
                        return null;
                    }
                };
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

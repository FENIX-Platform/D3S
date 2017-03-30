package org.fao.fenix.export.d3s.legacy;

import org.fao.fenix.commons.msd.dto.data.Resource;
import org.fao.fenix.commons.msd.dto.full.DSDDataset;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.fenix.d3s.msd.services.rest.ResourcesService;
import org.fao.fenix.export.core.dto.data.CoreData;
import org.fao.fenix.export.core.dto.data.CoreTableData;
import org.fao.fenix.export.core.input.plugin.Input;

import java.util.Iterator;
import java.util.Map;

public class D3SDatasetInputPlugin extends Input {
    String uid;
    String version;
    ResourcesService service;
    Resource<DSDDataset,Object[]> resource;

    @Override
    public void init(Map<String, Object> config, Resource resource) {
        this.resource = resource;
        MeIdentification<DSDDataset> metadata = resource!=null ? (MeIdentification<DSDDataset>) resource.getMetadata() : null;
        uid = metadata!=null ? metadata.getUid() : null;
        version = metadata!=null ? metadata.getVersion() : null;
        service = ExportManagerPlugin.getResourcesService(config!=null && config.containsKey("lang") ? (String)config.get("lang") : "EN");
    }

    @Override
    public CoreData getResource() {
        if(!isResourceFull()) {
            try {
                long time = System.currentTimeMillis();
                final Resource<DSDDataset, Object[]> data = service.loadResource(uid, version);
                System.out.println("Time = " + (System.currentTimeMillis() - time));
                if (data != null)
                    return new CoreData<Object[]>() {
                        @Override
                        public MeIdentification getMetadata() {
                            return data.getMetadata();
                        }

                        @Override
                        public Iterator<Object[]> getData() {
                            return data.getData() != null ? data.getData().iterator() : null;
                        }
                    };
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
           return new CoreTableData(this.resource.getMetadata(),this.resource.getData().iterator());
        }
        return null;

    }


    private boolean isResourceFull() {
        return resource!= null && resource.getMetadata()!= null && resource.getMetadata().getDsd()!= null
                && (resource.getMetadata().getDsd()).getColumns()!= null
                &&(resource.getMetadata().getDsd()).getColumns().size()>0;

    }
}

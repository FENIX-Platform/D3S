package org.fao.fenix.sdmx.d3s;

import org.fao.fenix.commons.msd.dto.data.Resource;
import org.fao.fenix.commons.msd.dto.full.*;
import org.fao.fenix.commons.utils.FileUtils;
import org.fao.fenix.commons.utils.JSONUtils;

import java.util.HashMap;
import java.util.Map;


public class ResourceFactory {
    private static Map<String,Resource<DSDDataset, Object[]>> datasetInstances = new HashMap<>();
    private static Map<String,Resource<DSDCodelist, Code>> codelistInstances = new HashMap<>();

    public static Resource<DSDDataset, Object[]> getDatasetInstance(String name) throws Exception {
        Resource<DSDDataset, Object[]> resource = datasetInstances.get(name);
        if (resource==null)
            datasetInstances.put(name, resource=getDatasetFakeResource(name));
        return resource;
    }

    public static Resource<DSDCodelist, Code> getCodelistInstance(String name) throws Exception {
        Resource<DSDCodelist, Code> resource = codelistInstances.get(name);
        if (resource==null)
            codelistInstances.put(name, resource=getCodelistFakeResource(name));
        return resource;
    }

    private static Resource<DSDDataset, Object[]> getDatasetFakeResource(String name) throws Exception {
        return JSONUtils.decode(new FileUtils().readTextFile(ResourceFactory.class.getResourceAsStream("/data/" + name + ".json")), Resource.class, DSDDataset.class, Object[].class);
    }

    private static Resource<DSDCodelist, Code> getCodelistFakeResource(String name) throws Exception {
        return JSONUtils.decode(new FileUtils().readTextFile(ResourceFactory.class.getResourceAsStream("/data/" + name + ".json")), Resource.class, DSDCodelist.class, Code.class);
    }
}

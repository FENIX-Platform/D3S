package org.fao.fenix.d3s.msd.dao;

import org.fao.fenix.commons.msd.dto.data.Resource;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.fenix.d3s.server.tools.orient.OrientDao;

import java.util.Collection;

public abstract class ResourceDao<D> extends OrientDao {

    //LOAD RESOURCE

    public Resource<D> loadResource(String rid) throws Exception {
        MeIdentification metadata = loadMetadata(rid);
        return metadata!=null ? new Resource<>(metadata, loadData(metadata)) : null;
    }

    public MeIdentification loadMetadata(String rid) throws Exception {
        return loadBean(rid, MeIdentification.class);
    }

    public Collection<D> loadData(String rid) throws Exception {
        return loadData(loadMetadata(rid));
    }

    //STORE RESOURCE

    public MeIdentification insertMetadata (MeIdentification metadata) throws Exception {
        return metadata!=null ? newCustomEntity(metadata) : null;
    }
    public MeIdentification updateMetadata (MeIdentification metadata, boolean overwrite) throws Exception {
        return metadata!=null && metadata.getRID()!=null ? saveCustomEntity(metadata,overwrite) : null;
    }
    public Resource<D> insertResource (Resource<D> resource) throws Exception {
        MeIdentification metadata = insertMetadata(resource.getMetadata());
        return metadata!=null ? new Resource (metadata, insertData(metadata, resource.getData())) : null;
    }

    public Resource<D> updateResource (Resource<D> resource, boolean overwrite) throws Exception {
        MeIdentification metadata = updateMetadata(resource.getMetadata(), overwrite);
        return metadata!=null ? new Resource (metadata, updateData(metadata, resource.getData(), overwrite)) : null;
    }

    //DATA LOAD AND STORE

    public abstract Collection<D> loadData(MeIdentification metadata) throws Exception;
    protected abstract Collection<D> insertData(MeIdentification metadata, Collection<D> data) throws Exception;
    protected abstract Collection<D> updateData(MeIdentification metadata, Collection<D> data, boolean overwrite) throws Exception;

}

package org.fao.fenix.d3s.msd.services.rest;

import org.fao.fenix.commons.msd.dto.data.Resource;
import org.fao.fenix.commons.msd.dto.data.ResourceProxy;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.fenix.commons.msd.dto.templates.ResponseBeanFactory;
import org.fao.fenix.commons.msd.dto.type.RepresentationType;
import org.fao.fenix.d3s.msd.dao.CodeListResourceDao;
import org.fao.fenix.d3s.msd.dao.MetadataResourceDao;
import org.fao.fenix.d3s.msd.dao.ResourceDao;
import org.fao.fenix.d3s.msd.services.spi.Resources;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.ws.rs.Path;
import java.util.Collection;

@Path("msd/resources")
public class ResourcesService implements Resources {
    @Inject private Instance<ResourceDao> daoFactory;
    @Inject private MetadataResourceDao metadataDao;


    //RESOURCES

    @Override
    public ResourceProxy getResource(String rid) throws Exception {
        MeIdentification metadata = metadataDao.loadMetadata(rid);
        return metadata!=null ? getResourceProxy(new Resource(metadata, getData(metadata))) : null;
    }

    @Override
    public ResourceProxy insertResource(Resource resource) throws Exception {
        return resource.getMetadata()!=null ? getResourceProxy( getDao(loadRepresentationType(resource.getMetadata())).insertResource(resource) ) : null;
    }

    @Override
    public ResourceProxy updateResource(Resource resource) throws Exception {
        return resource.getMetadata()!=null ? getResourceProxy( getDao(loadRepresentationType(resource.getMetadata())).updateResource(resource,true) ) : null;
    }

    @Override
    public ResourceProxy appendResource(Resource resource) throws Exception {
        return resource.getMetadata()!=null ? getResourceProxy( getDao(loadRepresentationType(resource.getMetadata())).updateResource(resource,false) ) : null;
    }

    //METADATA

    @Override
    public Object getMetadata(String rid) throws Exception {
        return getMetadataProxy(metadataDao.loadMetadata(rid));
    }

    @Override
    public Object insertMetadata(MeIdentification metadata) throws Exception {
        return getMetadataProxy(metadataDao.insertMetadata(metadata));
    }

    @Override
    public Object updateMetadata(MeIdentification metadata) throws Exception {
        return getMetadataProxy(metadataDao.updateMetadata(metadata, true));
    }

    @Override
    public Object appendMetadata(MeIdentification metadata) throws Exception {
        return getMetadataProxy(metadataDao.updateMetadata(metadata, false));
    }

    //DATA

    @Override
    public Collection getData(String rid) throws Exception {
        return getData(metadataDao.loadMetadata(rid));
    }


    //Utils
    private Collection getData (MeIdentification metadata) throws Exception {
        ResourceDao dataDao = getDao(loadRepresentationType(metadata));
        return dataDao!=null ? dataDao.loadData(metadata) : null;
    }

    private ResourceDao getDao (RepresentationType representationType) {
        ResourceDao dataDao = null;
        switch (representationType) {
            case codelist: dataDao = daoFactory.select(CodeListResourceDao.class).iterator().next();
        }
        return dataDao;
    }

    private Object getMetadataProxy(MeIdentification metadata) throws Exception {
        Class metadataProxyClass = getMetadataProxyClass(loadRepresentationType(metadata));
        return metadataProxyClass!=null ? ResponseBeanFactory.getInstance(metadata, metadataProxyClass) : null;
    }

    private Class getMetadataProxyClass (RepresentationType representationType) {
        switch (representationType) {
            case codelist: return org.fao.fenix.commons.msd.dto.templates.codeList.MeIdentification.class;
        }
        return null;
    }

    private RepresentationType loadRepresentationType(MeIdentification metadata) throws Exception {
        RepresentationType representationType = metadata!=null && metadata.getMeContent()!=null ? metadata.getMeContent().getResourceRepresentationType() : null;
        if (representationType==null && metadata.getORID()!=null) {
            metadata = metadataDao.loadMetadata(metadata.getRID());
            representationType = metadata!=null && metadata.getMeContent()!=null ? metadata.getMeContent().getResourceRepresentationType() : null;
        }
        return representationType;
    }

    private ResourceProxy getResourceProxy(Resource resource) throws Exception {
        switch (loadRepresentationType(resource.getMetadata())) {
            case codelist:
                return ResourceProxy.getInstance(
                        resource,
                        org.fao.fenix.commons.msd.dto.templates.codeList.MeIdentification.class,
                        org.fao.fenix.commons.msd.dto.templates.codeList.Code.class
                );
        }
        return null;
    }

}

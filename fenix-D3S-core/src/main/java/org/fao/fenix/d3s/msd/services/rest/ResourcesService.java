package org.fao.fenix.d3s.msd.services.rest;

import com.orientechnologies.orient.core.id.ORID;
import org.fao.fenix.commons.msd.dto.data.Resource;
import org.fao.fenix.commons.msd.dto.data.ResourceProxy;
import org.fao.fenix.commons.msd.dto.full.DSD;
import org.fao.fenix.commons.msd.dto.full.DSDDataset;
import org.fao.fenix.commons.msd.dto.templates.ResponseBeanFactory;
import org.fao.fenix.commons.msd.dto.templates.ResponseHandler;
import org.fao.fenix.commons.msd.dto.templates.codeList.Code;
import org.fao.fenix.commons.msd.dto.templates.identification.MeIdentification;
import org.fao.fenix.commons.msd.dto.type.RepresentationType;
import org.fao.fenix.d3s.msd.dao.CodeListResourceDao;
import org.fao.fenix.d3s.msd.dao.DatasetResourceDao;
import org.fao.fenix.d3s.msd.dao.MetadataResourceDao;
import org.fao.fenix.d3s.msd.dao.ResourceDao;
import org.fao.fenix.d3s.msd.services.spi.Resources;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.NoContentException;
import java.util.Collection;

@Path("msd/resources")
public class ResourcesService implements Resources {
    @Inject private Instance<ResourceDao> daoFactory;
    @Inject private MetadataResourceDao metadataDao;


    //RESOURCES

    @Override
    public ResourceProxy getResource(String rid, boolean full, boolean dsd) throws Exception {
        org.fao.fenix.commons.msd.dto.full.MeIdentification metadata = loadMetadata(rid, null);
        return metadata!=null ? getResourceProxy(metadata, getData(metadata), full, dsd) : null;
    }
    @Override
    public ResourceProxy getResourceByUID(String uid, String version, boolean full, boolean dsd) throws Exception {
        org.fao.fenix.commons.msd.dto.full.MeIdentification metadata = loadMetadata(uid, version);
        return metadata!=null ? getResourceProxy(metadata, getData(metadata), full, dsd) : null;
    }

    @Override
    public MeIdentification insertResource(Resource resource) throws Exception {
        return resource.getMetadata()!=null ? ResponseBeanFactory.getInstance( getDao(loadRepresentationType(resource.getMetadata())).insertResource(resource), MeIdentification.class ) : null;
    }

    @Override
    public MeIdentification updateResource(Resource resource) throws Exception {
        return resource.getMetadata()!=null ? ResponseBeanFactory.getInstance(getDao(loadRepresentationType(resource.getMetadata())).updateResource(resource, true), MeIdentification.class) : null;
    }

    @Override
    public MeIdentification appendResource(Resource resource) throws Exception {
        return resource.getMetadata()!=null ? ResponseBeanFactory.getInstance(getDao(loadRepresentationType(resource.getMetadata())).updateResource(resource, false), MeIdentification.class) : null;
    }

    //METADATA
    @Override
    public Object getMetadata(String rid, boolean full, boolean dsd) throws Exception {
        org.fao.fenix.commons.msd.dto.full.MeIdentification metadata = loadMetadata(rid,null);
        return getMetadataProxy(metadata, full, dsd);
    }
    @Override
    public Object getMetadataByUID(String uid, String version, boolean full, boolean dsd) throws Exception {
        org.fao.fenix.commons.msd.dto.full.MeIdentification metadata = loadMetadata(uid, version);
        return getMetadataProxy(metadata, full, dsd);
    }

    @Override
    public MeIdentification insertMetadata(org.fao.fenix.commons.msd.dto.full.MeIdentification metadata) throws Exception {
        return ResponseBeanFactory.getInstance(metadataDao.insertMetadata(metadata), MeIdentification.class);
    }

    @Override
    public MeIdentification updateMetadata(org.fao.fenix.commons.msd.dto.full.MeIdentification metadata) throws Exception {
        return ResponseBeanFactory.getInstance(metadataDao.updateMetadata(metadata, true), MeIdentification.class);
    }

    @Override
    public MeIdentification appendMetadata(org.fao.fenix.commons.msd.dto.full.MeIdentification metadata) throws Exception {
        return ResponseBeanFactory.getInstance(metadataDao.updateMetadata(metadata, false), MeIdentification.class);
    }

    //DATA

    @Override
    public Collection getData(String rid) throws Exception {
        return getData(loadMetadata(rid,null));
    }
    @Override
    public Collection getDataByUID(String uid, String version) throws Exception {
        return getData(loadMetadata(uid, version));
    }





    //Utils
    private org.fao.fenix.commons.msd.dto.full.MeIdentification loadMetadata(String id, String version) throws Exception {
        org.fao.fenix.commons.msd.dto.full.MeIdentification metadata = metadataDao.loadMetadata(id, version);
        if (metadata==null)
            throw new NoContentException("Cannot find resource (id: "+id+(version!=null ? '-'+version : "")+')');
        return metadata;
    }
    /*
    private org.fao.fenix.commons.msd.dto.full.MeIdentification fullMetadata(org.fao.fenix.commons.msd.dto.full.MeIdentification metadata, boolean includeDsd) throws Exception {
        DSD dsd = null;
        String metadataRID = metadata.getRID();
        String dsdRID = (dsd=metadata.getDsd())!=null ? dsd.getRID() : null;

        metadata = metadataDao.getConnection().detachAll(metadata,true);
        metadata.setRID(metadataRID);
        if (!includeDsd) {
            metadata.setDsd(dsdRID!=null ? new DSDDataset(dsdRID) : null);
        }
        return metadata;
    }
    */
    private Collection getData (org.fao.fenix.commons.msd.dto.full.MeIdentification metadata) throws Exception {
        ResourceDao dataDao = getDao(loadRepresentationType(metadata));
        return dataDao!=null ? dataDao.loadData(metadata) : null;
    }

    private Object getMetadataProxy(org.fao.fenix.commons.msd.dto.full.MeIdentification metadata, boolean full, boolean dsd) throws Exception {
        Class metadataProxyClass = getMetadataProxyClass(loadRepresentationType(metadata), full, dsd);
        return metadataProxyClass!=null ? ResponseBeanFactory.getInstance(metadata, metadataProxyClass) : null;
    }

    private ResourceProxy getResourceProxy(org.fao.fenix.commons.msd.dto.full.MeIdentification metadata, Collection data, boolean full, boolean dsd) throws Exception {
        RepresentationType type = loadRepresentationType(metadata);
        return new ResourceProxy(
                ResponseBeanFactory.getInstance(metadata, getMetadataProxyClass(type, full, dsd)),
                data,
                getTemplateDataClass(type)
        );
    }

    //Representation type based selections
    private RepresentationType loadRepresentationType(org.fao.fenix.commons.msd.dto.full.MeIdentification metadata) throws Exception {
        RepresentationType representationType = metadata!=null && metadata.getMeContent()!=null ? metadata.getMeContent().getResourceRepresentationType() : null;
        if (representationType==null) {
            metadata =  metadata.getORID()!=null ? metadataDao.loadMetadata(metadata.getRID(), null) : metadataDao.loadMetadata(metadata.getUid(),metadata.getVersion());
            representationType = metadata!=null && metadata.getMeContent()!=null ? metadata.getMeContent().getResourceRepresentationType() : null;
        }
        if (representationType==null)
            throw new Exception("Unknown resource type. The field 'meContent.resourceRepresentationType' is mandatory.");
        return representationType;
    }

    private Class getMetadataProxyClass (RepresentationType representationType, boolean full, boolean dsd) {
        if (full)
            return dsd ? org.fao.fenix.commons.msd.dto.templates.dsd.MeIdentification.class : org.fao.fenix.commons.msd.dto.templates.standardDsd.MeIdentification.class;
        switch (representationType) {
            case codelist: return org.fao.fenix.commons.msd.dto.templates.codeList.MeIdentification.class;
            case dataset: return org.fao.fenix.commons.msd.dto.templates.codeList.MeIdentification.class;
        }
        return null;
    }

    private ResourceDao getDao (RepresentationType representationType) {
        switch (representationType) {
            case codelist: return daoFactory.select(CodeListResourceDao.class).iterator().next();
            case dataset: return daoFactory.select(DatasetResourceDao.class).iterator().next();
        }
        return null;
    }

    private Class<? extends ResponseHandler> getTemplateDataClass (RepresentationType representationType) {
        switch (representationType) {
            case codelist: return Code.class;
        }
        return null;
    }



}

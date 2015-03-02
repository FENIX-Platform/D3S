package org.fao.fenix.d3s.msd.services.rest;

import com.orientechnologies.orient.object.enhancement.OObjectProxyMethodHandler;
import javassist.util.proxy.Proxy;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;
import org.fao.fenix.commons.msd.dto.JSONEntity;
import org.fao.fenix.commons.msd.dto.data.Resource;
import org.fao.fenix.commons.find.dto.filter.StandardFilter;
import org.fao.fenix.commons.msd.dto.data.ResourceProxy;
import org.fao.fenix.commons.msd.dto.templates.ResponseBeanFactory;
import org.fao.fenix.commons.msd.dto.templates.ResponseHandler;
import org.fao.fenix.commons.msd.dto.templates.codeList.Code;
import org.fao.fenix.commons.msd.dto.templates.standard.combined.MetadataDSD;
import org.fao.fenix.commons.msd.dto.templates.identification.MeIdentification;
import org.fao.fenix.commons.msd.dto.templates.standard.combined.Metadata;
import org.fao.fenix.commons.msd.dto.templates.standard.combined.DSD;
import org.fao.fenix.commons.msd.dto.type.RepresentationType;
import org.fao.fenix.d3s.msd.dao.*;
import org.fao.fenix.d3s.msd.services.spi.Resources;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Path;
import javax.ws.rs.core.NoContentException;
import java.util.*;

@Path("msd/resources")
public class ResourcesService implements Resources {
    @Inject private Instance<ResourceDao> daoFactory;
    @Inject private MetadataResourceDao metadataDao;
    @Inject private FilterResourceDao filterResourceDao;


    //MASSIVE METADATA

    @Override
    public Collection<Object> getMetadata(StandardFilter filter, String businessName, boolean full, boolean dsd, boolean extend) throws Exception {
        Collection<org.fao.fenix.commons.msd.dto.full.MeIdentification> resources = filterResourceDao.filter(filter, businessName);
        if (resources!=null && resources.size()>0) {
            Collection result = new LinkedList();
            for (org.fao.fenix.commons.msd.dto.full.MeIdentification resource : resources)
                result.add(getMetadataProxy(resource, full, dsd, extend));
            return result;
        } else
            return null;
    }

    @Override
    public <T extends org.fao.fenix.commons.msd.dto.full.MeIdentification> Collection<MeIdentification> insertMetadata(Collection<T> metadata) throws Exception {
        return ResponseBeanFactory.getInstances(metadataDao.insertMetadata(metadata), MeIdentification.class);
    }

    @Override
    public <T extends org.fao.fenix.commons.msd.dto.full.MeIdentification> Collection<MeIdentification> updateMetadata(Collection<T> metadata) throws Exception {
        return ResponseBeanFactory.getInstances(metadataDao.updateMetadata(metadata, true), MeIdentification.class);
    }

    @Override
    public <T extends org.fao.fenix.commons.msd.dto.full.MeIdentification> Collection<MeIdentification> appendMetadata(Collection<T> metadata) throws Exception {
        return ResponseBeanFactory.getInstances(metadataDao.updateMetadata(metadata,false), MeIdentification.class);
    }

    @Override
    public Integer deleteMetadata(StandardFilter filter, String businessName) throws Exception {
        Collection<org.fao.fenix.commons.msd.dto.full.MeIdentification> resources = filterResourceDao.filter(filter, businessName);
        if (resources!=null) {
            metadataDao.deleteMetadata(resources.toArray(new org.fao.fenix.commons.msd.dto.full.MeIdentification[resources.size()]));
            return resources.size();
        } else
            return 0;
    }



    //RESOURCES

    @Override
    public ResourceProxy getResource(String rid, boolean full, boolean dsd, boolean extend) throws Exception {
        org.fao.fenix.commons.msd.dto.full.MeIdentification metadata = loadMetadata(rid, null);
        return metadata!=null ? getResourceProxy(metadata, getData(metadata), full, dsd, extend) : null;
    }

    @Override
    public ResourceProxy getResourceByUID(String uid, boolean full, boolean dsd, boolean extend) throws Exception {
        return getResourceByUID(uid, null, full, dsd, extend);
    }

    @Override
    public ResourceProxy getResourceByUID(String uid, String version, boolean full, boolean dsd, boolean extend) throws Exception {
        org.fao.fenix.commons.msd.dto.full.MeIdentification metadata = loadMetadata(uid, version);
        return metadata!=null ? getResourceProxy(metadata, getData(metadata), full, dsd, extend) : null;
    }

    @Override
    public MeIdentification insertResource(Resource resource) throws Exception {
        if (resource==null || resource.getMetadata()==null)
            throw new BadRequestException();
        return ResponseBeanFactory.getInstance( getDao(loadRepresentationType(resource.getMetadata())).insertResource(resource), MeIdentification.class );
    }

    @Override
    public MeIdentification updateResource(Resource resource) throws Exception {
        if (resource==null || resource.getMetadata()==null)
            throw new BadRequestException();
        return ResponseBeanFactory.getInstance(getDao(loadRepresentationType(resource.getMetadata())).updateResource(resource, true), MeIdentification.class);
    }

    @Override
    public MeIdentification appendResource(Resource resource) throws Exception {
        if (resource==null || resource.getMetadata()==null)
            throw new BadRequestException();
        return ResponseBeanFactory.getInstance(getDao(loadRepresentationType(resource.getMetadata())).updateResource(resource, false), MeIdentification.class);
    }

    @Override
    public String deleteResource(String rid) throws Exception {
        return deleteResource(loadMetadata(rid, null));
    }

    @Override
    public String deleteResourceByUID(String uid) throws Exception {
        return deleteResource(loadMetadata(uid, null));
    }

    @Override
    public String deleteResourceByUID(String uid, String version) throws Exception {
        return deleteResource(loadMetadata(uid, version));
    }


    //METADATA

    @Override
    public Object getMetadata(String rid, boolean full, boolean dsd, boolean extend) throws Exception {
        return getMetadataProxy(loadMetadata(rid, null), full, dsd, extend);
    }

    @Override
    public Object getMetadataByUID(String uid, boolean full, boolean dsd, boolean extend) throws Exception {
        return getMetadataByUID(uid, null, full, dsd, extend);
    }

    @Override
    public Object getMetadataByUID(String uid, String version, boolean full, boolean dsd, boolean extend) throws Exception {
        return getMetadataProxy(loadMetadata(uid, version), full, dsd, extend);
    }

    @Override
    public <T extends org.fao.fenix.commons.msd.dto.full.MeIdentification> MeIdentification insertMetadata(T metadata) throws Exception {
        if (metadata==null)
            throw new BadRequestException();
        return ResponseBeanFactory.getInstance(metadataDao.insertMetadata(metadata), MeIdentification.class);
    }

    @Override
    public <T extends org.fao.fenix.commons.msd.dto.full.MeIdentification> MeIdentification updateMetadata(T metadata) throws Exception {
        if (metadata==null)
            throw new BadRequestException();
        return ResponseBeanFactory.getInstance(metadataDao.updateMetadata(metadata, true), MeIdentification.class);
    }

    @Override
    public <T extends org.fao.fenix.commons.msd.dto.full.MeIdentification> MeIdentification appendMetadata(T metadata) throws Exception {
        if (metadata==null)
            throw new BadRequestException();
        return ResponseBeanFactory.getInstance(metadataDao.updateMetadata(metadata, false), MeIdentification.class);
    }

    @Override
    public String deleteMetadata(String rid) throws Exception {
        return metadataDao.deleteMetadata(rid,null) ? "" : null;
    }

    @Override
    public String deleteMetadataByUID(String uid) throws Exception {
        return metadataDao.deleteMetadata(uid,null) ? "" : null;
    }

    @Override
    public String deleteMetadataByUID(String uid, String version) throws Exception {
        return metadataDao.deleteMetadata(uid,version) ? "" : null;
    }

    @Override
    public void restoreLinks() throws Exception {
        metadataDao.restoreLinks();
    }


    //DSD only

    @Override
    public Object getDsd(String rid) throws Exception {
        Object metadata = metadataDao.loadBean(JSONEntity.toRID(rid));
        return metadata!=null ? ResponseBeanFactory.getInstance(metadata, getDSDProxyClass(metadata)) : null;
    }

    @Override
    public <T extends org.fao.fenix.commons.msd.dto.full.DSD> org.fao.fenix.commons.msd.dto.templates.identification.DSD updateDsd(T metadata) throws Exception {
        return ResponseBeanFactory.getInstance(metadataDao.saveCustomEntity(true, metadata)[0], org.fao.fenix.commons.msd.dto.templates.identification.DSD.class);
    }

    @Override
    public <T extends org.fao.fenix.commons.msd.dto.full.DSD> org.fao.fenix.commons.msd.dto.templates.identification.DSD appendDsd(T metadata) throws Exception {
        return ResponseBeanFactory.getInstance(metadataDao.saveCustomEntity(false, metadata)[0], org.fao.fenix.commons.msd.dto.templates.identification.DSD.class);
    }

    @Override
    public void deleteDsd(String rid) throws Exception {
        metadataDao.delete(rid);
    }

    //DATA

    @Override
    public Collection getData(String rid) throws Exception {
        org.fao.fenix.commons.msd.dto.full.MeIdentification metadata = loadMetadata(rid,null);
        return getDataProxy(metadata, getData(metadata));
    }

    @Override
    public Collection getDataByUID(String uid) throws Exception {
        return getDataByUID(uid, null);
    }

    @Override
    public Collection getDataByUID(String uid, String version) throws Exception {
        org.fao.fenix.commons.msd.dto.full.MeIdentification metadata = loadMetadata(uid, version);
        return getDataProxy(metadata, getData(metadata));
    }

    @Override
    public String deleteData(String rid) throws Exception {
        return deleteData(loadMetadata(rid, null));
    }

    @Override
    public String deleteDataByUID(String uid) throws Exception {
        return deleteData(loadMetadata(uid, null));
    }

    @Override
    public String deleteDataByUID(String uid, String version) throws Exception {
        return deleteData(loadMetadata(uid, null));
    }

    @Override
    public Collection findMetadata(StandardFilter filter, String businessName, boolean full, boolean dsd, boolean extend) throws Exception {
        Collection<org.fao.fenix.commons.msd.dto.full.MeIdentification> resources = filterResourceDao.filter(filter, businessName);
        if (resources!=null && resources.size()>0) {
            if (full || dsd) {
                Collection result = new LinkedList();
                for (org.fao.fenix.commons.msd.dto.full.MeIdentification resource : resources)
                    result.add(getMetadataProxy(resource, full, dsd, extend));
                return result;
            } else
                return ResponseBeanFactory.getInstances(resources, MeIdentification.class);
        } else
            return null;
    }


    //Utils
    private org.fao.fenix.commons.msd.dto.full.MeIdentification loadMetadata(String id, String version) throws Exception {
        org.fao.fenix.commons.msd.dto.full.MeIdentification metadata = metadataDao.loadMetadata(id, version);
        if (metadata==null)
            throw new NoContentException("Cannot find resource (id: "+id+(version!=null ? '-'+version : "")+')');
        return metadata;
    }

    private Collection getData (org.fao.fenix.commons.msd.dto.full.MeIdentification metadata) throws Exception {
        ResourceDao dataDao = getDao(loadRepresentationType(metadata));
        return dataDao!=null ? dataDao.loadData(metadata) : null;
    }

    private Object getMetadataProxy(org.fao.fenix.commons.msd.dto.full.MeIdentification metadata, boolean full, boolean dsd, boolean extend) throws Exception {
        Class metadataProxyClass = getMetadataProxyClass(loadRepresentationType(metadata), full, dsd, extend);
        return metadataProxyClass!=null ? ResponseBeanFactory.getInstance(metadata, metadataProxyClass) : null;
    }

    private Collection getDataProxy(org.fao.fenix.commons.msd.dto.full.MeIdentification metadata, Collection data) throws Exception {
        Class dataProxyClass = getTemplateDataClass(loadRepresentationType(metadata));
        return dataProxyClass!=null && data!=null ? ResponseBeanFactory.getInstances(data, dataProxyClass) : data;
    }

    private ResourceProxy getResourceProxy(org.fao.fenix.commons.msd.dto.full.MeIdentification metadata, Collection data, boolean full, boolean dsd, boolean extend) throws Exception {
        RepresentationType type = loadRepresentationType(metadata);
        return new ResourceProxy(
                ResponseBeanFactory.getInstance(metadata, getMetadataProxyClass(type, full, dsd, extend)),
                data, getTemplateDataClass(type)
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

    private ResourceDao getDao (RepresentationType representationType) {
        switch (representationType) {
            case codelist: return daoFactory.select(CodeListResourceDao.class).iterator().next();
            case dataset: return daoFactory.select(DatasetResourceDao.class).iterator().next();
        }
        return null;
    }

    private static final String standardTemplatesBasePackage = Metadata.class.getPackage().getName();
    private static final String exportTemplatesBasePackage = org.fao.fenix.commons.msd.dto.templates.export.combined.Metadata.class.getPackage().getName();
    private Class getMetadataProxyClass (RepresentationType representationType, boolean full, boolean dsd, boolean extend) throws ClassNotFoundException {
        String templateClassName = null;
        if (full && dsd)
            templateClassName = MetadataDSD.class.getSimpleName();
        else if (full)
            templateClassName = Metadata.class.getSimpleName();
        else if (dsd)
            templateClassName = DSD.class.getSimpleName();

        if (templateClassName!=null)
            return Class.forName((extend ? exportTemplatesBasePackage : standardTemplatesBasePackage)+'.' + representationType + '.' + templateClassName);

        switch (representationType) {
            case codelist: return org.fao.fenix.commons.msd.dto.templates.codeList.MeIdentification.class;
            case dataset: return org.fao.fenix.commons.msd.dto.templates.codeList.MeIdentification.class;
            case document: return org.fao.fenix.commons.msd.dto.templates.codeList.MeIdentification.class;
            case geographic: return org.fao.fenix.commons.msd.dto.templates.codeList.MeIdentification.class;
            default: return null;
        }
    }

    private Class<? extends ResponseHandler> getTemplateDataClass (RepresentationType representationType) {
        switch (representationType) {
            case codelist: return Code.class;
        }
        return null;
    }


    private Class<? extends org.fao.fenix.commons.msd.dto.templates.standard.dsd.DSD> getDSDProxyClass(Object entity) throws ClassNotFoundException {
        if (entity!=null) {
            String className = entity instanceof ProxyObject ? ((OObjectProxyMethodHandler) ProxyFactory.getHandler((Proxy) entity)).getDoc().getClassName() : entity.getClass().getSimpleName();
            return (Class<? extends org.fao.fenix.commons.msd.dto.templates.standard.dsd.DSD>) Class.forName(org.fao.fenix.commons.msd.dto.templates.standard.dsd.DSD.class.getPackage().getName()+'.'+className);
        }
        return null;
    }


    private String deleteData(org.fao.fenix.commons.msd.dto.full.MeIdentification metadata) throws Exception {
        if (metadata!=null) {
            getDao(loadRepresentationType(metadata)).deleteData(metadata);
            return "";
        } else
            return null;
    }

    private String deleteResource(org.fao.fenix.commons.msd.dto.full.MeIdentification metadata) throws Exception {
        if (metadata!=null) {
            getDao(loadRepresentationType(metadata)).deleteResource(metadata);
            return "";
        } else
            return null;
    }





}

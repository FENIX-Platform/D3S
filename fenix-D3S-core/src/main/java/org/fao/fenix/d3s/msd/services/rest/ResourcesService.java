package org.fao.fenix.d3s.msd.services.rest;

import com.orientechnologies.orient.object.enhancement.OObjectProxyMethodHandler;
import javassist.util.proxy.Proxy;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;
import org.apache.log4j.Logger;
import org.fao.fenix.commons.msd.dto.JSONEntity;
import org.fao.fenix.commons.msd.dto.data.MetadataList;
import org.fao.fenix.commons.msd.dto.data.ReplicationFilter;
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
import org.fao.fenix.d3s.server.dto.DatabaseStandards;
import org.fao.fenix.d3s.wds.WDSDaoFactory;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotAcceptableException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.core.NoContentException;
import java.lang.reflect.Method;
import java.util.*;

@Path("msd/resources")
public class ResourcesService implements Resources {
    private static final Logger LOGGER = Logger.getLogger("access");


    @Inject private Instance<ResourceDao> daoFactory;
    @Inject private MetadataResourceDao metadataDao;
    @Inject private FilterResourceDao filterResourceDao;
    @Inject private WDSDaoFactory wdsDaoFactory;
    @Inject private DatabaseStandards parameters;


    //MASSIVE METADATA
/*
    @Override
    public Collection<Object> getMetadata(StandardFilter filter, String businessName, boolean full, boolean dsd, boolean export) throws Exception {
        Collection<org.fao.fenix.commons.msd.dto.full.MeIdentification> resources = filterResourceDao.filter(filter, businessName);
        if (resources!=null && resources.size()>0) {
            Collection result = new LinkedList();
            for (org.fao.fenix.commons.msd.dto.full.MeIdentification resource : resources)
                result.add(getMetadataProxy(resource, full, dsd, export));
            return result;
        } else
            return null;
    }
*/


    @Override
    public Collection<MeIdentification> insertMetadata(MetadataList metadata) throws Exception {
        return ResponseBeanFactory.getInstances(MeIdentification.class, getHierarchy(writeMetadata(metadata, true, true)));
    }

    @Override
    public Collection<MeIdentification> updateMetadata(MetadataList metadata) throws Exception {
        return ResponseBeanFactory.getInstances(MeIdentification.class, getHierarchy(writeMetadata(metadata, false, true)));
    }

    @Override
    public Collection<MeIdentification> appendMetadata(MetadataList metadata) throws Exception {
        return ResponseBeanFactory.getInstances(MeIdentification.class, getHierarchy(writeMetadata(metadata, false, false)));
    }

    @Override
    public Integer deleteMetadata(StandardFilter filter, String businessName) throws Exception {
        Collection<org.fao.fenix.commons.msd.dto.full.MeIdentification> resources = filterResourceDao.filter(filter, businessName);
        if (resources != null) {
            try {
                metadataDao.transaction();
                for (org.fao.fenix.commons.msd.dto.full.MeIdentification metadata : resources) {
                    ResourceDao dao = getDao(loadRepresentationType(metadata));
                    if (dao == null)
                        dao = metadataDao;
                    dao.deleteMetadata(false, metadata);
                }
                metadataDao.commit();
                return resources.size();
            } catch (Exception ex) {
                metadataDao.rollback();
                throw ex;
            }
        } else
            return 0;
    }

    @Override
    public <T extends org.fao.fenix.commons.msd.dto.full.DSD> Collection<MeIdentification> appendReplicationMetadata(ReplicationFilter<T> replicationFilter, String businessName) throws Exception {
        Collection<org.fao.fenix.commons.msd.dto.full.MeIdentification> storedMetadata = new LinkedList<>();

        Collection<org.fao.fenix.commons.msd.dto.full.MeIdentification> resources = filterResourceDao.filter(replicationFilter.getFilter(), businessName);
        if (resources != null && resources.size() > 0) {
            Collection<String> resourcesId = new LinkedList<>();
            for (org.fao.fenix.commons.msd.dto.full.MeIdentification resource : resources)
                resourcesId.add(resource.getRID());

            org.fao.fenix.commons.msd.dto.full.MeIdentification<T> metadata = replicationFilter.getMetadata();
            ResourceDao dao = getDao(loadRepresentationType(resources.iterator().next()));
            if (dao == null)
                dao = metadataDao;

            if (metadata != null && resourcesId.size() > 0) {
                try {
                    metadata.setUid(null);
                    metadata.setVersion(null);
                    metadataDao.transaction();
                    for (String rid : resourcesId) {
                        metadata.setRID(rid);

                        storedMetadata.add(dao.updateMetadata(metadata, false, false));
                    }
                    metadataDao.commit();
                } catch (Exception ex) {
                    metadataDao.rollback();
                    throw ex;
                }
            }
        }

        return ResponseBeanFactory.getInstances(MeIdentification.class, getHierarchy(storedMetadata));
    }


    //RESOURCES

    @Override
    public ResourceProxy getResource(String rid, boolean full, boolean dsd, boolean export, boolean datasource) throws Exception {
        LOGGER.info("Resource LOAD: @rid = " + rid + " - @full = " + full + " - @dsd = " + dsd + " - @export = " + export);
        return getResourceProxy(loadMetadata(rid, null), full, dsd, export, datasource);
    }

    @Override
    public ResourceProxy getResourceByUID(String uid, boolean full, boolean dsd, boolean export, boolean datasource) throws Exception {
        return getResourceProxy(loadMetadata(uid, null), full, dsd, export, datasource);
    }

    @Override
    public ResourceProxy getResourceByUID(String uid, String version, boolean full, boolean dsd, boolean export, boolean datasource) throws Exception {
        LOGGER.info("Resource LOAD: @uid = " + uid + " - @version = " + version + " - @full = " + full + " - @dsd = " + dsd + " - @export = " + export);
        return getResourceProxy(loadMetadata(uid, version), full, dsd, export, datasource);
    }

    @Override
    public MeIdentification insertResource(Resource resource) throws Exception {
        if (resource == null || resource.getMetadata() == null)
            throw new BadRequestException();
        LOGGER.info("Resource INSERT: @uid = "+resource.getMetadata().getUid()+" - @version = "+resource.getMetadata().getVersion());
        org.fao.fenix.commons.msd.dto.full.MeIdentification proxy = getDao(loadRepresentationType(resource.getMetadata())).insertResource(resource);
        return ResponseBeanFactory.getInstance(MeIdentification.class, proxy.loadHierarchy());
    }

    @Override
    public MeIdentification updateResource(Resource resource) throws Exception {
        if (resource == null || resource.getMetadata() == null)
            throw new BadRequestException();
        LOGGER.info("Resource UPDATE: @uid = "+resource.getMetadata().getUid()+" - @version = "+resource.getMetadata().getVersion());
        org.fao.fenix.commons.msd.dto.full.MeIdentification proxy = getDao(loadRepresentationType(resource.getMetadata())).updateResource(resource, true);
        return proxy!=null ? ResponseBeanFactory.getInstance(MeIdentification.class, proxy.loadHierarchy()) : null;
    }

    @Override
    public MeIdentification appendResource(Resource resource) throws Exception {
        if (resource == null || resource.getMetadata() == null)
            throw new NoContentException("No metadata");
        LOGGER.info("Resource APPEND: @uid = "+resource.getMetadata().getUid()+" - @version = "+resource.getMetadata().getVersion());
        org.fao.fenix.commons.msd.dto.full.MeIdentification proxy = getDao(loadRepresentationType(resource.getMetadata())).updateResource(resource, false);
        return proxy!=null ? ResponseBeanFactory.getInstance(MeIdentification.class, proxy.loadHierarchy()) : null;
    }

    @Override
    public String deleteResource(String rid) throws Exception {
        LOGGER.info("Resource DELETE: @rid = " + rid);
        return deleteResource(loadMetadata(rid, null));
    }

    @Override
    public String deleteResourceByUID(String uid) throws Exception {
        LOGGER.info("Resource DELETE: @uid = " + uid);
        return deleteResource(loadMetadata(uid, null));
    }

    @Override
    public String deleteResourceByUID(String uid, String version) throws Exception {
        LOGGER.info("Resource DELETE: @uid = " + uid + " - @version = " + version);
        return deleteResource(loadMetadata(uid, version));
    }


    //METADATA

    @Override
    public Object getMetadata(String rid, boolean full, boolean dsd, boolean export, Integer levels) throws Exception {
        LOGGER.info("Metadata LOAD: @rid = " + rid + " - @full = " + full + " - @dsd = " + dsd + " - @export = " + export);
        return getMetadataProxy(loadMetadata(rid, null), full, dsd, export, levels);
    }

    @Override
    public Object getMetadataByUID(String uid, boolean full, boolean dsd, boolean export, Integer levels) throws Exception {
        return getMetadataByUID(uid, null, full, dsd, export, levels);
    }

    @Override
    public Object getMetadataByUID(String uid, String version, boolean full, boolean dsd, boolean export, Integer levels) throws Exception {
        LOGGER.info("Metadata LOAD: @uid = " + uid + " - @version = " + version + " - @full = " + full + " - @dsd = " + dsd + " - @export = " + export);
        return getMetadataProxy(loadMetadata(uid, version), full, dsd, export, levels);
    }

    @Override
    public <T extends org.fao.fenix.commons.msd.dto.full.MeIdentification> MeIdentification insertMetadata(T metadata) throws Exception {
        if (metadata == null)
            throw new BadRequestException();
        LOGGER.info("Metadata INSERT: @uid = "+metadata.getUid()+" - @version = "+metadata.getVersion());
        return ResponseBeanFactory.getInstance(MeIdentification.class, metadataDao.insertMetadata(metadata).loadHierarchy());
    }

    @Override
    public <T extends org.fao.fenix.commons.msd.dto.full.MeIdentification> MeIdentification updateMetadata(T metadata) throws Exception {
        if (metadata == null)
            throw new BadRequestException();
        LOGGER.info("Metadata UPDATE: @uid = "+metadata.getUid()+" - @version = "+metadata.getVersion());
        org.fao.fenix.commons.msd.dto.full.MeIdentification updatedMetadata = metadataDao.updateMetadata(metadata, true);
        return updatedMetadata!=null ? ResponseBeanFactory.getInstance(MeIdentification.class, updatedMetadata.loadHierarchy()) : null;
    }

    @Override
    public <T extends org.fao.fenix.commons.msd.dto.full.MeIdentification> MeIdentification appendMetadata(T metadata) throws Exception {
        if (metadata == null)
            throw new BadRequestException();
        LOGGER.info("Metadata APPEND: @uid = "+metadata.getUid()+" - @version = "+metadata.getVersion());
        org.fao.fenix.commons.msd.dto.full.MeIdentification updatedMetadata = metadataDao.updateMetadata(metadata, false);
        return updatedMetadata!=null ? ResponseBeanFactory.getInstance(MeIdentification.class, updatedMetadata.loadHierarchy()) : null;
    }

    @Override
    public String deleteMetadata(String rid) throws Exception {
        LOGGER.info("Metadata DELETE: @rid = " + rid);
        return metadataDao.deleteMetadata(rid, null) ? "" : null;
    }

    @Override
    public String deleteMetadataByUID(String uid) throws Exception {
        LOGGER.info("Metadata DELETE: @uid = " + uid);
        return metadataDao.deleteMetadata(uid, null) ? "" : null;
    }

    @Override
    public String deleteMetadataByUID(String uid, String version) throws Exception {
        LOGGER.info("Metadata DELETE: @uid = " + uid + " - @version = " + version);
        return metadataDao.deleteMetadata(uid, version) ? "" : null;
    }

    @Override
    public void restoreLinks() throws Exception {
        LOGGER.info("Metadata LINKS RESTORE");
        metadataDao.restoreLinks();
    }


    //DSD only

    @Override
    public Object getDsd(String rid) throws Exception {
        LOGGER.info("DSD GET: @rid = " + rid);
        Object metadata = metadataDao.loadBean(JSONEntity.toRID(rid));
        return metadata!=null ? ResponseBeanFactory.getInstance(getDSDProxyClass(metadata), metadata) : null;
    }

    @Override
    public <T extends org.fao.fenix.commons.msd.dto.full.DSD> org.fao.fenix.commons.msd.dto.templates.identification.DSD updateDsd(T metadata) throws Exception {
        LOGGER.info("DSD UPDATE: @rid = " + metadata.getRID());
        updateLastUpdateDate(metadata = metadataDao.saveCustomEntity(true, metadata)[0]);
        return ResponseBeanFactory.getInstance(org.fao.fenix.commons.msd.dto.templates.identification.DSD.class, metadata);
    }

    @Override
    public <T extends org.fao.fenix.commons.msd.dto.full.DSD> org.fao.fenix.commons.msd.dto.templates.identification.DSD appendDsd(T metadata) throws Exception {
        LOGGER.info("DSD APPEND: @rid = " + metadata.getRID());
        updateLastUpdateDate(metadata = metadataDao.saveCustomEntity(false, metadata)[0]);
        return ResponseBeanFactory.getInstance(org.fao.fenix.commons.msd.dto.templates.identification.DSD.class, metadata);
    }

    @Override
    public void deleteDsd(String rid) throws Exception {
        LOGGER.info("DSD DELETE: @rid = " + rid);
        org.fao.fenix.commons.msd.dto.full.MeIdentification metadata = metadataDao.loadMetadataByDSD(JSONEntity.toRID(rid));
        metadataDao.updateMetadata(metadata, true);
        metadataDao.delete(rid);
    }

    //DATA

    @Override
    public void fetch(String rid) throws Exception {
        LOGGER.info("Data FETCH: @rid = " + rid);
        fetch(loadMetadata(rid, null));
    }

    @Override
    public void fetchByUID(String uid) throws Exception {
        LOGGER.info("Data FETCH: @uid = " + uid);
        fetch(loadMetadata(uid, null));
    }

    @Override
    public void fetchByUID(String uid, String version) throws Exception {
        LOGGER.info("Data FETCH: @uid = " + uid + " @version = " + version);
        fetch(loadMetadata(uid, version));
    }

    public void fetch(org.fao.fenix.commons.msd.dto.full.MeIdentification metadata) throws Exception {
        LOGGER.info("Data FETCH by metadata : @uid = " + metadata.getUid() + " @version = " + metadata.getVersion());
        getDao(loadRepresentationType(metadata)).fetch(metadata);
    }

    @Override
    public Collection getData(String rid) throws Exception {
        LOGGER.info("Data GET: @rid = " + rid);
        return getDataProxy(loadMetadata(rid, null));
    }

    @Override
    public Collection getDataByUID(String uid) throws Exception {
        LOGGER.info("Data GET: @uid = " + uid);
        return getDataProxy(loadMetadata(uid, null));
    }

    @Override
    public Collection getDataByUID(String uid, String version) throws Exception {
        LOGGER.info("Data GET: @uid = " + uid + " @version = " + version);
        return getDataProxy(loadMetadata(uid, version));
    }

    private Collection getDataProxy(org.fao.fenix.commons.msd.dto.full.MeIdentification metadata) throws Exception {
        return getDataProxy(metadata, loadData(metadata));
    }

    @Override
    public String deleteData(String rid) throws Exception {
        LOGGER.info("Data DELETE: @rid = " + rid);
        return deleteData(loadMetadata(rid, null));
    }

    @Override
    public String deleteDataByUID(String uid) throws Exception {
        LOGGER.info("Data DELETE: @uid = " + uid);
        return deleteData(loadMetadata(uid, null));
    }

    @Override
    public String deleteDataByUID(String uid, String version) throws Exception {
        LOGGER.info("Data DELETE: @uid = " + uid + " @version = " + version);
        return deleteData(loadMetadata(uid, null));
    }


    //FIND
    private final int MAX_METADATA_LIST_SIZE = 250;

    @Override
    public Collection findMetadata(StandardFilter filter, String businessName, boolean full, boolean dsd, boolean export) throws Exception {
        LOGGER.info("Metadata FIND: @logic = " + businessName + " - @full = " + full + " - @dsd = " + dsd + " - @export = " + export + " - @filterSize = " + (filter != null ? filter.size() : 0));
        LOGGER.debug("Metadata FIND: @filter... " + filter);
        Collection<org.fao.fenix.commons.msd.dto.full.MeIdentification> resources = filterResourceDao.filter(filter, businessName);

        Integer maxSize = parameters.getLimit();
        if (resources.size()>(maxSize!=null && maxSize>0? maxSize : MAX_METADATA_LIST_SIZE))
            throw new NotAcceptableException();
        if (resources != null && resources.size() > 0) {
            if (full || dsd) {
                Collection result = new LinkedList();
                for (org.fao.fenix.commons.msd.dto.full.MeIdentification resource : resources)
                    result.add(getMetadataProxy(resource, full, dsd, export, null));
                return result;
            } else
                return ResponseBeanFactory.getInstances(MeIdentification.class, resources);
        } else
            return null;
    }


    //UTILS
    //Manage raw info
    public Resource loadResource(String id, String version) throws Exception {
        org.fao.fenix.commons.msd.dto.full.MeIdentification metadata = loadMetadata(id, version);
        return new Resource(metadata, loadData(metadata));
    }

    public org.fao.fenix.commons.msd.dto.full.MeIdentification loadMetadata(String id, String version) throws Exception {
        org.fao.fenix.commons.msd.dto.full.MeIdentification metadata = metadataDao.loadMetadata(id, version);
        if (metadata == null)
            throw new NoContentException("Cannot find resource (id: " + id + (version != null ? '-' + version : "") + ')');
        return metadata;
    }

    private Collection loadData(org.fao.fenix.commons.msd.dto.full.MeIdentification metadata) throws Exception {
        ResourceDao dataDao = getDao(loadRepresentationType(metadata));
        return dataDao != null ? dataDao.loadData(metadata) : null;
    }

    private Long getSize(org.fao.fenix.commons.msd.dto.full.MeIdentification metadata) throws Exception {
        ResourceDao dataDao = getDao(loadRepresentationType(metadata));
        return dataDao != null ? dataDao.getSize(metadata) : null;
    }

    private Map<String, Map<String, String>> getDatasources(org.fao.fenix.commons.msd.dto.full.MeIdentification metadata) {
        org.fao.fenix.commons.msd.dto.full.DSD dsd = metadata != null ? metadata.getDsd() : null;
        String[] datasourcesName = dsd != null ? dsd.getDatasources() : null;
        Map<String, Map<String, String>> datasources = new HashMap<>();

        if (datasourcesName != null && datasourcesName.length > 0) {
            for (String datasourceName : datasourcesName) {
                Map<String, String> properties = datasourceName != null ? wdsDaoFactory.getDatasourceProperties(datasourceName) : null;
                if (properties != null && properties.size() > 0)
                    datasources.put(datasourceName, properties);
            }
        }

        return datasources;
    }

    private String deleteData(org.fao.fenix.commons.msd.dto.full.MeIdentification metadata) throws Exception {
        if (metadata != null) {
            getDao(loadRepresentationType(metadata)).deleteData(metadata);
            return "";
        } else
            return null;
    }

    private String deleteResource(org.fao.fenix.commons.msd.dto.full.MeIdentification metadata) throws Exception {
        if (metadata != null) {
            getDao(loadRepresentationType(metadata)).deleteResource(metadata);
            return "";
        } else
            return null;
    }

    //Retrieve info proxy
    private Object getMetadataProxy(org.fao.fenix.commons.msd.dto.full.MeIdentification metadata, boolean full, boolean dsd, boolean export, Integer levels) throws Exception {
        Class metadataProxyClass = getMetadataProxyClass(loadRepresentationType(metadata), full, dsd, export);
        return getMetadataProxyLogic(
                metadata,
                metadataProxyClass,
                getDao(loadRepresentationType(metadata)),
                getSetChildrenMethod(metadataProxyClass),
                levels!=null ? (levels<=0 ? Integer.MAX_VALUE : levels) : null
        );
    }
    private Method getSetChildrenMethod (Class metadataProxyClass) {
        Method setChildrenMethod = null;
        for (;setChildrenMethod==null && metadataProxyClass!=null; metadataProxyClass = metadataProxyClass.getSuperclass())
            try { setChildrenMethod = metadataProxyClass.getMethod("setChildren", Collection.class); } catch (NoSuchMethodException ex) {}
        return setChildrenMethod;
    }

    private Object getMetadataProxyLogic(org.fao.fenix.commons.msd.dto.full.MeIdentification metadata, Class metadataProxyClass, ResourceDao dao, Method setChildrenMethod, Integer levels) throws Exception {
        if (metadata==null || metadataProxyClass==null)
            return null;
        Object proxy = ResponseBeanFactory.getInstance(metadataProxyClass, metadata.loadHierarchy());

        if (setChildrenMethod!=null && levels!=null && levels>1) {
            Collection childrenProxy = new LinkedList();
            for (org.fao.fenix.commons.msd.dto.full.MeIdentification child : (Collection<org.fao.fenix.commons.msd.dto.full.MeIdentification>)dao.loadChildren(metadata))
                childrenProxy.add(getMetadataProxyLogic(child,metadataProxyClass,dao,setChildrenMethod,levels-1));
            if (childrenProxy.size()>0)
                setChildrenMethod.invoke(proxy, childrenProxy);
        }

        return proxy;
    }


    private Collection getDataProxy(org.fao.fenix.commons.msd.dto.full.MeIdentification metadata, Collection data) throws Exception {
        Class dataProxyClass = getTemplateDataClass(loadRepresentationType(metadata));
        return dataProxyClass!=null && data!=null ? ResponseBeanFactory.getInstances(dataProxyClass, data) : data;
    }

    private ResourceProxy getResourceProxy(org.fao.fenix.commons.msd.dto.full.MeIdentification metadata, boolean full, boolean dsd, boolean export, boolean datasource) throws Exception {
        RepresentationType type = loadRepresentationType(metadata);

        if (type != RepresentationType.dataset || (metadata.getDsd() != null
                && metadata.getDsd().getDatasources() != null
                && metadata.getDsd().getDatasources().length > 0
                && ((org.fao.fenix.commons.msd.dto.full.DSDDataset) metadata.getDsd()).getColumns() != null
                && ((org.fao.fenix.commons.msd.dto.full.DSDDataset) metadata.getDsd()).getColumns().size() > 0
        )) {
            Collection data = loadData(metadata);
            Long size = getSize(metadata);
            size = size != null ? size : (data != null ? (long) data.size() : null);


            return new ResourceProxy(
                    ResponseBeanFactory.getInstance(getMetadataProxyClass(type, full, dsd, export), metadata.loadHierarchy()),
                    data, getTemplateDataClass(type),
                    datasource ? getDatasources(metadata) : null,
                    size, parameters.getLimit()
            );
        } else {
            Long size = ((Integer) (0)).longValue();

            return new ResourceProxy(
                    ResponseBeanFactory.getInstance(getMetadataProxyClass(type, full, dsd, export), metadata.loadHierarchy()),
                    null, getTemplateDataClass(type),
                    null,
                    size, parameters.getLimit()
            );

        }
    }

    //Representation type based proxy class selections
    private RepresentationType loadRepresentationType(org.fao.fenix.commons.msd.dto.full.MeIdentification metadata) throws Exception {
        RepresentationType representationType = metadata != null && metadata.getMeContent() != null ? metadata.getMeContent().getResourceRepresentationType() : null;
        if (representationType == null) {
            metadata = metadata.getORID() != null ? metadataDao.loadMetadata(metadata.getRID(), null) : metadataDao.loadMetadata(metadata.getUid(), metadata.getVersion());
            representationType = metadata != null && metadata.getMeContent() != null ? metadata.getMeContent().getResourceRepresentationType() : null;
        }
        if (representationType == null)
            throw new Exception("Unknown resource type. The field 'meContent.resourceRepresentationType' is mandatory.");
        return representationType;
    }

    private ResourceDao getDao(RepresentationType representationType) {
        if (representationType != null)
            switch (representationType) {
                case codelist:
                    return daoFactory.select(CodeListResourceDao.class).iterator().next();
                case dataset:
                    return daoFactory.select(DatasetResourceDao.class).iterator().next();
                case geographic:
                    return daoFactory.select(MetadataResourceDao.class).iterator().next();
            }
        return null;
    }

    private static final String standardTemplatesBasePackage = Metadata.class.getPackage().getName();
    private static final String exportTemplatesBasePackage = org.fao.fenix.commons.msd.dto.templates.export.combined.Metadata.class.getPackage().getName();

    private Class getMetadataProxyClass(RepresentationType representationType, boolean full, boolean dsd, boolean export) throws ClassNotFoundException {
        String templateClassName = null;
        if (full && dsd)
            templateClassName = MetadataDSD.class.getSimpleName();
        else if (full)
            templateClassName = Metadata.class.getSimpleName();
        else if (dsd)
            templateClassName = DSD.class.getSimpleName();

        if (templateClassName != null)
            return Class.forName((export ? exportTemplatesBasePackage : standardTemplatesBasePackage) + '.' + representationType + '.' + templateClassName);
        else
            return MeIdentification.class;
    }

    private Class<? extends ResponseHandler> getTemplateDataClass(RepresentationType representationType) {
        switch (representationType) {
            case codelist:
                return Code.class;
        }
        return null;
    }


    private Class<? extends org.fao.fenix.commons.msd.dto.templates.standard.dsd.DSD> getDSDProxyClass(Object entity) throws ClassNotFoundException {
        if (entity != null) {
            String className = entity instanceof ProxyObject ? ((OObjectProxyMethodHandler) ProxyFactory.getHandler((Proxy) entity)).getDoc().getClassName() : entity.getClass().getSimpleName();
            return (Class<? extends org.fao.fenix.commons.msd.dto.templates.standard.dsd.DSD>) Class.forName(org.fao.fenix.commons.msd.dto.templates.standard.dsd.DSD.class.getPackage().getName() + '.' + className);
        }
        return null;
    }


    //Metadata normalization
    private void updateLastUpdateDate(org.fao.fenix.commons.msd.dto.full.DSD dsd) throws Exception {
        org.fao.fenix.commons.msd.dto.full.MeIdentification metadata = metadataDao.loadMetadataByDSD(dsd.getORID());
        org.fao.fenix.commons.msd.dto.full.MeIdentification toSave = new org.fao.fenix.commons.msd.dto.full.MeIdentification();
        toSave.setUid(metadata.getUid());
        toSave.setVersion(metadata.getVersion());
        toSave.setLastUpdate(new Date());
        metadataDao.updateMetadata(toSave, false);
    }


    //Massive metadata write
    private Collection<org.fao.fenix.commons.msd.dto.full.MeIdentification> writeMetadata(Collection<org.fao.fenix.commons.msd.dto.full.MeIdentification> metadata, boolean insert, boolean overwrite) throws Exception {
        Collection<org.fao.fenix.commons.msd.dto.full.MeIdentification> storedMetadata = new LinkedList<>();
        if (metadata != null) {
            metadataDao.transaction();
            try {
                for (org.fao.fenix.commons.msd.dto.full.MeIdentification m : metadata) {
                    ResourceDao dao = getDao(loadRepresentationType(m));
                    if (dao == null && !insert) {
                        org.fao.fenix.commons.msd.dto.full.MeIdentification mProxy = m.getRID() != null ? metadataDao.loadMetadata(m.getRID(), null) : metadataDao.loadMetadata(m.getUid(), m.getVersion());
                        if (mProxy == null)
                            throw new NotFoundException();
                        dao = getDao(loadRepresentationType(mProxy));
                    }
                    if (dao == null)
                        dao = metadataDao;
                    storedMetadata.add(insert ? dao.insertMetadata(m, false) : dao.updateMetadata(m, overwrite, false));
                }
                metadataDao.commit();
            } catch (Exception ex) {
                metadataDao.rollback();
                throw ex;
            }
        }
        return storedMetadata;
    }


    private Collection<org.fao.fenix.commons.msd.dto.full.MeIdentification[]> getHierarchy(Collection<org.fao.fenix.commons.msd.dto.full.MeIdentification> metadataList) {
        Collection<org.fao.fenix.commons.msd.dto.full.MeIdentification[]> hierarchy = new LinkedList<>();
        for (org.fao.fenix.commons.msd.dto.full.MeIdentification metadata : metadataList)
            hierarchy.add(metadata.loadHierarchy());
        return hierarchy;
    }

}

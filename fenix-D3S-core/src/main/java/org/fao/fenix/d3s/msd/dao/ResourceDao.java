package org.fao.fenix.d3s.msd.dao;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.fao.fenix.commons.msd.dto.data.Resource;
import org.fao.fenix.commons.msd.dto.full.DSDDataset;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.fenix.d3s.server.tools.orient.OrientDao;

import javax.ws.rs.core.NoContentException;
import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;
import java.util.regex.Pattern;

public abstract class ResourceDao<D> extends OrientDao {

    //LOAD RESOURCE

    public MeIdentification loadMetadata(String id, String version) throws Exception {
        return isRID(id,version) ? loadBean(id, MeIdentification.class) : loadMetadataByUID(id,version);
    }
    public MeIdentification loadMetadataByUID(String uid, String version) throws Exception {
        if (uid==null)
            return null;
        Collection<MeIdentification> resources = version==null ?
                select(MeIdentification.class, "select from MeIdentification where index|uid = ?", null, null, uid) :
                select(MeIdentification.class, "select from MeIdentification where index|uid = ? and index|version = ?", null, null, uid, version);
        return resources.size()>0 ? resources.iterator().next() : null;
    }
    public ODocument loadMetadataOByUID(String uid, String version) throws Exception {
        if (uid==null)
            return null;
        Collection<ODocument> resources =  version==null ?
                select("select from MeIdentification where index|uid = ?", null, null, uid) :
                select("select from MeIdentification where index|uid = ? and index|version = ?", null, null, uid, version);
        return resources.size()>0 ? resources.iterator().next() : null;
    }


    //STORE RESOURCE

    public MeIdentification insertMetadata (MeIdentification metadata) throws Exception {
        if (metadata.getUid()==null || metadata.getUid().trim().equals("")) {
            UUID uid = UUID.randomUUID();
            metadata.setUid("D3S_"+Math.abs(uid.getMostSignificantBits())+Math.abs(uid.getLeastSignificantBits()));
        }
        return metadata!=null ? newCustomEntity(metadata) : null;
    }
    public MeIdentification updateMetadata (MeIdentification metadata, boolean overwrite) throws Exception {
        if (metadata!=null) {
            if (metadata.getRID()==null) {
                ODocument metadataO = loadMetadataOByUID(metadata.getUid(), metadata.getVersion());
                metadata.setORID(metadataO!=null ? metadataO.getIdentity() : null);
            }
            if (metadata.getRID()!=null)
                return  metadata.isIdentificationOnly() ? loadMetadata(metadata.getRID(), null) : saveCustomEntity(metadata,overwrite);
        }
        throw new NoContentException("Cannot find bean. Resource icdentification is mandatory to execute update operation.");
    }
    public MeIdentification insertResource (Resource<D> resource) throws Exception {
        MeIdentification metadata = insertMetadata(resource.getMetadata());
        if (metadata!=null)
            insertData(metadata, resource.getData());
        return metadata;
    }

    public MeIdentification updateResource (Resource<D> resource, boolean overwrite) throws Exception {
        MeIdentification metadata = updateMetadata(resource.getMetadata(), overwrite);
        if (metadata!=null)
            updateData(metadata, resource.getData(), overwrite);
        return metadata;
    }

    //DELETE RESOURCE
    public boolean deleteMetadata(String id, String version) throws Exception {
        MeIdentification metadata = loadMetadata(id,version);
        if (metadata!=null)
            deleteMetadata(metadata);
        return metadata!=null;
    }

    public boolean deleteResource (String id, String version) throws Exception {
        MeIdentification metadata = loadMetadata(id,version);

        if (metadata!=null) {
            deleteData(metadata);
            deleteMetadata(metadata);
        }
        return metadata!=null;
    }

    public void deleteResource (MeIdentification metadata) throws Exception {
        if (metadata!=null) {
            deleteData(metadata);
            deleteMetadata(metadata);
        }
    }

    public void deleteMetadata(MeIdentification metadata) throws Exception {
        if (metadata!=null) {
            DSDDataset dsd = metadata.getDsd();
            if (dsd != null)
                getConnection().delete(dsd);
            getConnection().delete(metadata);
        }
    }


    //DATA LOAD AND STORE

    public abstract Collection<D> loadData(MeIdentification metadata) throws Exception;
    protected abstract void insertData(MeIdentification metadata, Collection<D> data) throws Exception;
    protected abstract void updateData(MeIdentification metadata, Collection<D> data, boolean overwrite) throws Exception;
    public abstract void deleteData(MeIdentification metadata) throws Exception;

    //Utils
    Pattern ridPattern = Pattern.compile("^\\d+_\\d+$");
    private boolean isRID(String rid, String version) {
        return version==null && rid!=null && ridPattern.matcher(rid).matches();
    }

    public <T> Collection<T> toList(final Iterator<T> iterator) {
        return iterator==null ? null : new Collection<T>() {
            @Override
            public int size() {
                return iterator.hasNext() ? Integer.MAX_VALUE : 0;
            }

            @Override
            public boolean isEmpty() {
                System.out.println("isEmpty...");
                return !iterator.hasNext();
            }

            @Override
            public boolean contains(Object o) {
                throw new UnsupportedOperationException();
            }

            @Override
            public Iterator<T> iterator() {
                return iterator;
            }

            @Override
            public Object[] toArray() {
                throw new UnsupportedOperationException();
            }

            @Override
            public <T1> T1[] toArray(T1[] a) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean add(T t) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean remove(Object o) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean containsAll(Collection<?> c) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean addAll(Collection<? extends T> c) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean removeAll(Collection<?> c) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean retainAll(Collection<?> c) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void clear() {
                throw new UnsupportedOperationException();
            }
        };
    }


    //Restore links
    public void restoreLinks() throws Exception {
        ODatabaseDocument connection = getConnection().getUnderlying();
        //Restore resources
        Iterator<ODocument> resources = connection.browseClass("MeIdentification");
        while (resources.hasNext()) {
            ODocument resource = resources.next();
            resource.field("uid",resource.field("uid"));
            resource.save();
        }
        //Restore dataset DSD
        resources = connection.browseClass("DSD");
        while (resources.hasNext()) {
            ODocument resource = resources.next();
            resource.field("datasource", resource.field("datasource"), OType.STRING);
            connection.save(resource);
        }
    }

}

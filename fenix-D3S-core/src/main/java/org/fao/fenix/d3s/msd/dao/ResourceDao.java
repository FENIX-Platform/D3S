package org.fao.fenix.d3s.msd.dao;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import org.fao.fenix.commons.msd.dto.data.Resource;
import org.fao.fenix.commons.msd.dto.full.*;
import org.fao.fenix.d3s.server.tools.orient.OrientDao;

import javax.ws.rs.core.NoContentException;
import java.util.*;
import java.util.regex.Pattern;

public abstract class ResourceDao<M extends DSD, D> extends OrientDao {

    //MASSIVE METADATA
    public Collection<MeIdentification<M>> insertMetadata (Collection<MeIdentification<M>> metadata) throws Exception {
        Collection<MeIdentification<M>> storedMetadata = new LinkedList<>();
        if (metadata!=null) {
            getConnection().begin();
            try {
                for (MeIdentification<M> m : metadata)
                    storedMetadata.add(insertMetadata(m, false));
                getConnection().commit();
            } catch (Exception ex) {
                getConnection().rollback();
                throw ex;
            }
        }
        return storedMetadata;
    }
    public Collection<MeIdentification<M>> updateMetadata (Collection<MeIdentification<M>> metadata, boolean overwrite) throws Exception {
        Collection<MeIdentification<M>> storedMetadata = new LinkedList<>();
        if (metadata!=null) {
            getConnection().begin();
            try {
                for (MeIdentification<M> m : metadata)
                    storedMetadata.add(updateMetadata(m, overwrite, false));
                getConnection().commit();
            } catch (Exception ex) {
                getConnection().rollback();
                throw ex;
            }
        }
        return storedMetadata;
    }
    public Collection<MeIdentification<M>> replicateMetadata (Collection<String> metadataRid, MeIdentification<M> metadata) throws Exception {
        Collection<MeIdentification<M>> storedMetadata = new LinkedList<>();
        if (metadata!=null && metadataRid!=null) {
            try {
                metadata.setUid(null);
                metadata.setVersion(null);
                getConnection().begin();
                for (String rid : metadataRid) {
                    long time = System.currentTimeMillis();
                    metadata.setRID(rid);
                    storedMetadata.add(updateMetadata(metadata, false, false));
                    time = System.currentTimeMillis()-time;
                    System.out.println("Stored "+rid+" in "+time+" ms");
                }
                getConnection().commit();
            } catch (Exception ex) {
                getConnection().rollback();
                throw ex;
            }
        }
        return storedMetadata;
    }




    //LOAD RESOURCE

    public MeIdentification<M> loadMetadata(String id, String version) throws Exception {
        return isRID(id,version) ? loadBean(id, MeIdentification.class) : loadMetadataByUID(id,version);
    }
    public MeIdentification<M> loadMetadataByUID(String uid, String version) throws Exception {
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
    public MeIdentification<M> loadMetadataByDSD(ORID dsdRid) throws Exception {
        if (dsdRid==null)
            return null;

        Collection<MeIdentification> resources = select(MeIdentification.class, "select from MeIdentification where dsd = ?", dsdRid);
        return resources.size()>0 ? resources.iterator().next() : null;
    }


    //STORE RESOURCE

    public MeIdentification<M> insertMetadata (MeIdentification<M> metadata) throws Exception {
        return insertMetadata(metadata, true);
    }
    private MeIdentification<M> insertMetadata (MeIdentification<M> metadata, boolean transaction) throws Exception {
        if (metadata.getUid()==null || metadata.getUid().trim().equals("")) {
            UUID uid = UUID.randomUUID();
            metadata.setUid("D3S_"+Math.abs(uid.getMostSignificantBits())+Math.abs(uid.getLeastSignificantBits()));
        }
        setMetadataDefaults(metadata, true);
        return newCustomEntity(false, transaction, metadata);
    }

    public MeIdentification<M> updateMetadata (MeIdentification<M> metadata, boolean overwrite) throws Exception {
        return updateMetadata(metadata, overwrite, true);
    }
    private MeIdentification<M> updateMetadata (MeIdentification<M> metadata, boolean overwrite, boolean transaction) throws Exception {
        if (metadata!=null) {
            if (metadata.getRID()==null) {
                ODocument metadataO = loadMetadataOByUID(metadata.getUid(), metadata.getVersion());
                metadata.setORID(metadataO!=null ? metadataO.getIdentity() : null);
            }
            if (metadata.getRID()!=null) {
                if (metadata.isIdentificationOnly())
                    return loadMetadata(metadata.getRID(), null);
                else {
                    setMetadataDefaults(metadata, false);
                    return saveCustomEntity(overwrite, false, transaction, metadata)[0];
                }
            }
        }
        return null;
    }
    public MeIdentification<M> insertResource (Resource<M,D> resource) throws Exception {
        getConnection().begin();
        try {
            MeIdentification<M> metadata = insertMetadata(resource.getMetadata());
            if (metadata != null)
                insertData(metadata, resource.getData());
            getConnection().commit();
            return metadata;
        } catch (Exception ex) {
            getConnection().rollback();
            throw ex;
        }
    }

    public MeIdentification<M> updateResource (Resource<M,D> resource, boolean overwrite) throws Exception {
        getConnection().begin();
        try {
            MeIdentification<M> metadata = updateMetadata(resource.getMetadata(), overwrite);
            if (metadata!=null)
                updateData(metadata, resource.getData(), overwrite);
            getConnection().commit();
            return metadata;
        } catch (Exception ex) {
            getConnection().rollback();
            throw ex;
        }
    }

    //DELETE RESOURCE
    public boolean deleteMetadata(String id, String version) throws Exception {
        MeIdentification metadata = loadMetadata(id,version);
        if (metadata!=null)
            deleteMetadata(metadata);
        return metadata!=null;
    }

    public boolean deleteResource (String id, String version) throws Exception {
        return deleteResource(loadMetadata(id, version));
    }

    public boolean deleteResource (MeIdentification<M> metadata) throws Exception {
        getConnection().begin();
        try {
            if (metadata!=null) {
                deleteMetadata(metadata);
                deleteData(metadata);
                getConnection().commit();
                return true;
            } else
                return false;
        } catch (Exception ex) {
            getConnection().rollback();
            throw ex;
        }
    }

    public void deleteMetadata(MeIdentification<M> ... metadata) throws Exception {
        deleteMetadata(true, metadata);
    }
    private void deleteMetadata(boolean transaction, MeIdentification<M> ... metadata) throws Exception {
        OObjectDatabaseTx transactionConnection = transaction ? getConnection() : null;

        if (metadata!=null)
            try {
                if (transactionConnection!=null)
                    transactionConnection.begin();

                OObjectDatabaseTx connection = transactionConnection!=null ? transactionConnection : getConnection();
                for (MeIdentification<M> m : metadata) {
                    DSD dsd = m.getDsd();
                    if (dsd != null)
                        connection.delete(dsd);
                    connection.delete(m);
                }
                if (transactionConnection!=null)
                    transactionConnection.commit();
            } catch (Exception ex) {
                if (transactionConnection!=null)
                    transactionConnection.rollback();
                throw ex;
            }
    }


    //DATA LOAD AND STORE

    public abstract void fetch(MeIdentification<M> metadata) throws Exception;
    public abstract Long getSize(MeIdentification<M> metadata) throws Exception;
    public abstract Collection<D> loadData(MeIdentification<M> metadata) throws Exception;
    protected abstract void insertData(MeIdentification<M> metadata, Collection<D> data) throws Exception;
    protected abstract void updateData(MeIdentification<M> metadata, Collection<D> data, boolean overwrite) throws Exception;
    public abstract void deleteData(MeIdentification<M> metadata) throws Exception;

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
            resource.setDirty();
            connection.save(resource);
        }
        //Restore dataset DSD
        resources = connection.browseClass("DSD");
        while (resources.hasNext()) {
            ODocument resource = resources.next();
            resource.setDirty();
            connection.save(resource);
        }
    }


    //Utils
    private void setMetadataDefaults(MeIdentification<M> metadata, boolean creation) {
        Date currentDate = new Date();

        if (metadata!=null) {
            //Set last update date
            metadata.setLastUpdate(currentDate);
            //Set creation date
            if (creation && metadata.getCreationDate()==null)
                metadata.setCreationDate(currentDate);
        }
    }

}

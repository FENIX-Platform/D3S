package org.fao.fenix.d3s.msd.dao;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import org.fao.fenix.commons.msd.dto.data.Resource;
import org.fao.fenix.commons.msd.dto.full.*;
import org.fao.fenix.d3s.msd.listener.MetadataListener;
import org.fao.fenix.d3s.msd.listener.MetadataListenerFactory;
import org.fao.fenix.d3s.server.tools.orient.OrientDao;

import javax.inject.Inject;
import java.util.*;
import java.util.regex.Pattern;

public abstract class ResourceDao<M extends DSD, D> extends OrientDao {
    @Inject private MetadataListenerFactory metadataListenerFactory;

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
    public MeIdentification<M> insertMetadata (MeIdentification<M> metadata, boolean transaction) throws Exception {
        if (metadata.getUid()==null || metadata.getUid().trim().equals("")) {
            UUID uid = UUID.randomUUID();
            metadata.setUid("D3S_"+Math.abs(uid.getMostSignificantBits())+Math.abs(uid.getLeastSignificantBits()));
        }
        fireMetadataEvent(MetadataEvent.insert,metadata,null);
        return newCustomEntity(false, transaction, addCreationDate(setUpdateDate(metadata)));
    }

    public MeIdentification<M> updateMetadata (MeIdentification<M> metadata, boolean overwrite) throws Exception {
        return updateMetadata(metadata, overwrite, true);
    }
    public MeIdentification<M> updateMetadata (MeIdentification<M> metadata, boolean overwrite, boolean transaction) throws Exception {
        if (metadata!=null) {
            MeIdentification<M> existingMetadata = metadata.getRID()==null ? loadMetadataByUID(metadata.getUid(), metadata.getVersion()) : loadBean(metadata.getRID(), MeIdentification.class);
            if (existingMetadata!=null) {
                if (metadata.isIdentificationOnly())
                    return existingMetadata;
                metadata.setRID(existingMetadata.getRID());
                fireMetadataEvent(overwrite ? MetadataEvent.update : MetadataEvent.append, metadata, existingMetadata);
                return saveCustomEntity(overwrite, false, transaction, addCreationDate(metadata))[0];
            }
        }
        return null;
    }
    public MeIdentification<M> insertResource (Resource<M,D> resource) throws Exception {
        getConnection().begin();
        try {
            MeIdentification<M> metadata = insertMetadata(addCreationDate(setUpdateDate(resource.getMetadata())));
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
            MeIdentification<M> metadata = resource.getMetadata();
            overwrite = overwrite && !metadata.isIdentificationOnly();
            metadata = updateMetadata(setUpdateDate(resource.getMetadata()), overwrite);
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
            deleteMetadata(false,metadata);
        return metadata!=null;
    }

    public boolean deleteResource (String id, String version) throws Exception {
        return deleteResource(loadMetadata(id, version));
    }

    public boolean deleteResource (MeIdentification<M> metadata) throws Exception {
        transaction();
        try {
            if (metadata!=null) {
                deleteMetadata(false, metadata);
                deleteData(metadata);
                commit();
                return true;
            } else
                return false;
        } catch (Exception ex) {
            rollback();
            throw ex;
        }
    }

    public void deleteMetadata(boolean transaction, MeIdentification<M> ... metadata) throws Exception {
        try {
            if (transaction)
                transaction();
            OObjectDatabaseTx connection = getConnection();
            for (MeIdentification<M> m : metadata) {
                fireMetadataEvent(MetadataEvent.delete, null, m);
                DSD dsd = m.getDsd();
                if (dsd != null)
                    connection.delete(dsd);
                connection.delete(m);
            }
            if (transaction)
                commit();
        } catch (Exception ex) {
            if (transaction)
                rollback();
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

    //EVENTS MANAGEMENT
    private enum MetadataEvent {
        insert, update, append, delete
    }
    private void fireMetadataEvent(MetadataEvent event, MeIdentification<M> newMetadata, MeIdentification<M> existingMetadata) throws Exception {
        String context = getMetadataContext(newMetadata);
        if (context==null)
            context = getMetadataContext(existingMetadata);
        switch (event) {
            case insert:
                for (MetadataListener listener : metadataListenerFactory.getListeners(context))
                    listener.insert(newMetadata);
                break;
            case update:
                for (MetadataListener listener : metadataListenerFactory.getListeners(context))
                    listener.update(existingMetadata,newMetadata);
                break;
            case append:
                for (MetadataListener listener : metadataListenerFactory.getListeners(context))
                    listener.append(existingMetadata,newMetadata);
                break;
            case delete:
                for (MetadataListener listener : metadataListenerFactory.getListeners(context))
                    listener.remove(existingMetadata);
                break;
        }
    }

    //Utils
    private MeIdentification<M> addCreationDate(MeIdentification<M> metadata) {
        if (metadata!=null) {
            Date creationDate = metadata.getCreationDate();
            if (creationDate==null)
                metadata.setCreationDate(new Date());
        }
        return metadata;
    }

    private MeIdentification<M> setUpdateDate(MeIdentification<M> metadata) {
        if (metadata!=null) {
            MeMaintenance meMaintenance = metadata.getMeMaintenance();
            if (meMaintenance==null)
                metadata.setMeMaintenance(meMaintenance = new MeMaintenance());
            SeUpdate seUpdate = meMaintenance.getSeUpdate();
            if (seUpdate==null)
                meMaintenance.setSeUpdate(seUpdate=new SeUpdate());
            seUpdate.setUpdateDate(new Date());
        }
        return metadata;
    }

    private String getMetadataContext(MeIdentification<M> metadata) {
        DSD dsd = metadata!=null ? metadata.getDsd() : null;
        return dsd!=null ? dsd.getContextSystem() : null;
    }

}

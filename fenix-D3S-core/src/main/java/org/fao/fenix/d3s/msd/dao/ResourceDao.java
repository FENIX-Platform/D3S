package org.fao.fenix.d3s.msd.dao;

import com.orientechnologies.orient.core.record.impl.ODocument;
import org.fao.fenix.commons.msd.dto.data.Resource;
import org.fao.fenix.commons.msd.dto.full.DSDColumn;
import org.fao.fenix.commons.msd.dto.full.DSDDataset;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.fenix.d3s.server.tools.orient.OrientDao;

import javax.ws.rs.core.NoContentException;
import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Pattern;

public abstract class ResourceDao<D> extends OrientDao {

    //LOAD RESOURCE

    public MeIdentification loadMetadata(String id, String version) throws Exception {
        return isRID(id,version) ? loadBean(id, MeIdentification.class) : loadMetadataByUID(id,version);
    }
    public MeIdentification loadMetadataByUID(String uid, String version) throws Exception {
        Collection<MeIdentification> resources = select(MeIdentification.class, "select from MeIdentification where index_id = ?", uid+(version!=null ? version : ""));
        return resources.size()>0 ? resources.iterator().next() : null;
    }
    public ODocument loadMetadataOByUID(String uid, String version) throws Exception {
        Collection<ODocument> resources = select("select from MeIdentification where index_id = ?", uid+(version!=null ? version : ""));
        return resources.size()>0 ? resources.iterator().next() : null;
    }


    //STORE RESOURCE

    public MeIdentification insertMetadata (MeIdentification metadata) throws Exception {
        return metadata!=null ? newCustomEntity(metadata) : null;
    }
    public MeIdentification updateMetadata (MeIdentification metadata, boolean overwrite) throws Exception {
        if (metadata!=null) {
            if (metadata.getRID()==null) {
                ODocument metadataO = loadMetadataOByUID(metadata.getUid(), metadata.getVersion());
                metadata.setORID(metadataO!=null ? metadataO.getIdentity() : null);
            }
            if (metadata.getRID()!=null)
                return saveCustomEntity(metadata,overwrite);
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

    //DATA LOAD AND STORE

    public abstract Collection<D> loadData(MeIdentification metadata) throws Exception;
    protected abstract void insertData(MeIdentification metadata, Collection<D> data) throws Exception;
    protected abstract void updateData(MeIdentification metadata, Collection<D> data, boolean overwrite) throws Exception;

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

}
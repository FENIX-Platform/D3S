package org.fao.fenix.d3s.msd.services.rest.providers.csvResources;

import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import org.fao.fenix.commons.msd.dto.full.Code;
import org.fao.fenix.commons.msd.dto.data.Resource;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.fenix.commons.msd.dto.type.RepresentationType;
import org.fao.fenix.d3s.msd.dao.MetadataResourceDao;
import org.fao.fenix.d3s.msd.services.rest.providers.CSVProvider;
import org.fao.fenix.d3s.server.tools.orient.DatabaseStandards;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.*;

@Provider
@Consumes("application/csv")
public class CSVResourceProvider extends CSVProvider<Resource> {
    @Inject MetadataResourceDao dao;

    @Override
    public boolean isReadable(Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return aClass.equals(Resource.class);
    }

    @Override
    protected Resource<Code> read(MeIdentification metadata, Properties structure, Iterable<String[]> data) throws Exception {
        RepresentationType resourceType = getRepresentationType(metadata);
        if (resourceType!=null)
            switch (resourceType) {
                case codelist: return CSVCodeListProvider.getResource(metadata, structure, data);
            }
        return new Resource<>();
    }


    //Utils
    private RepresentationType getRepresentationType(MeIdentification metadata) throws Exception {
        RepresentationType representationType = metadata!=null && metadata.getMeContent()!=null ? metadata.getMeContent().getResourceRepresentationType() : null;

        if (representationType==null && metadata!=null) {
            //Load metadata
            MeIdentification storedMetadata = dao.loadMetadata(metadata.getRID(), null);
            if (storedMetadata == null)
                storedMetadata = dao.loadMetadata(metadata.getUid(), metadata.getVersion());
            //Recalculate representation type
            representationType = storedMetadata!=null ? storedMetadata.getMeContent().getResourceRepresentationType() : null;
        }

        return representationType;
    }

}

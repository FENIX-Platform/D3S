package org.fao.fenix.d3s.msd.services.rest.providers.csvResources;

import org.fao.fenix.commons.msd.dto.data.Resource;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.fenix.commons.msd.dto.type.RepresentationType;
import org.fao.fenix.d3s.msd.services.rest.providers.CSVProvider;
import org.fao.fenix.d3s.msd.services.rest.providers.csvResources.impl.CSVCodeListProvider;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.*;

@Provider
@Consumes("application/csv")
public class CSVResourceProvider extends CSVProvider<Resource> {

    @Override
    public boolean isReadable(Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return aClass.equals(Resource.class);
    }

    @Override
    protected Resource read(RepresentationType resourceType, MeIdentification metadata, Properties structure, Iterable<String[]> data) throws Exception {
        switch (resourceType) {
            case codelist: return CSVCodeListProvider.getResource((org.fao.fenix.commons.msd.dto.data.codelist.MeIdentification) metadata, structure, data);
            case dataset: return null;
            case geographic: return null;
            case document: return null;
            default: return null;
        }
    }

}

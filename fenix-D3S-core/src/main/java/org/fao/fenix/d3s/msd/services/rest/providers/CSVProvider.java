package org.fao.fenix.d3s.msd.services.rest.providers;

import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.fenix.commons.msd.dto.type.RepresentationType;
import org.fao.fenix.commons.utils.CSVReader;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Properties;

public abstract class CSVProvider<T> extends JsonProvider implements MessageBodyReader<T> {


    @Override
    public T readFrom(Class<T> tClass, Type type, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> stringStringMultivaluedMap, InputStream inputStream) throws IOException, WebApplicationException {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

            String metadataSection = readSection(in, "--structure--");
            RepresentationType resourceType = getRepresentationType(metadataSection,null);
            MeIdentification metadata = decodeMetadata(metadataSection, resourceType);

            Properties structure = readStructureProperties(readSection(in,"--data--"));
            String csvSeparator = structure.getProperty("csvSeparator", ";").trim();
            CSVReader csvReader = new CSVReader(in,csvSeparator);

            return read(resourceType,metadata,structure,csvReader);
        } catch (Exception e) {
            throw new WebApplicationException(e);
        }
    }


    protected abstract T read(RepresentationType resourceType, MeIdentification metadata, Properties structure, Iterable<String[]> data) throws Exception;



    //Utils

    private Properties readStructureProperties(String source) throws IOException {
        Properties structureProperties = new Properties();
        structureProperties.load(new StringReader(source));
        return structureProperties;
    }
}

package org.fao.fenix.d3s.msd.services.rest.providers;

import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.fenix.commons.utils.CSVReader;
import org.fao.fenix.commons.utils.JSONUtils;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.NoSuchElementException;
import java.util.Properties;


public abstract class CSVProvider<T>  implements MessageBodyReader<T> {

    private String readSection(BufferedReader in, String placeHolder) throws IOException {
        StringBuilder buffer = new StringBuilder();
        for (String row=in.readLine(); row!=null && !row.trim().equals(placeHolder); row=in.readLine())
            buffer.append(row).append('\n');
        //Remove BOM character if it exists
        return buffer.length()>0 && buffer.charAt(0) == 65279 ? buffer.substring(1) : buffer.toString();
    }

    private MeIdentification readMetadata (String source) throws Exception {
        try {
            return JSONUtils.toObject(source, MeIdentification.class);
        } catch (NoSuchElementException ex) {
            return null;
        }
    }

    private Properties readStructureProperties(String source) throws IOException {
        Properties structureProperties = new Properties();
        structureProperties.load(new StringReader(source));
        return structureProperties;
    }

    @Override
    public T readFrom(Class<T> tClass, Type type, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> stringStringMultivaluedMap, InputStream inputStream) throws IOException, WebApplicationException {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

            MeIdentification metadata = readMetadata(readSection(in,"--structure--"));
            Properties structure = readStructureProperties(readSection(in,"--data--"));
            String csvSeparator = structure.getProperty("csvSeparator", ";").trim();
            CSVReader csvReader = new CSVReader(in,csvSeparator);

            return read(metadata,structure,csvReader);
        } catch (Exception e) {
            throw new WebApplicationException(e);
        }
    }


    protected abstract T read(MeIdentification metadata, Properties structure, Iterable<String[]> data) throws Exception;
}

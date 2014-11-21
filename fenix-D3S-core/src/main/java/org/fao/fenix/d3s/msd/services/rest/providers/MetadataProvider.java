package org.fao.fenix.d3s.msd.services.rest.providers;

import org.fao.fenix.commons.msd.dto.full.MeIdentification;

import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;


@Provider
@Consumes(MediaType.APPLICATION_JSON)
public class MetadataProvider<T extends MeIdentification> extends JsonProvider implements MessageBodyReader<T> {

    @Override
    public boolean isReadable(Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return true;
    }

    @Override
    public T readFrom(Class<T> resourceClass, Type type, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> stringStringMultivaluedMap, InputStream inputStream) throws IOException, WebApplicationException {
        try {
            String content = readContent(inputStream);
            return (T)decode(content, MeIdentification.class, getRepresentationType(content, null));
        } catch (Exception e) {
            throw new WebApplicationException(e);
        }
    }

}

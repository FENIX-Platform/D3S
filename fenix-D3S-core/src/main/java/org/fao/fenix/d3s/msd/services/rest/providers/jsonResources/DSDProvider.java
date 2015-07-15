package org.fao.fenix.d3s.msd.services.rest.providers.jsonResources;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.fao.fenix.commons.msd.dto.JSONEntity;
import org.fao.fenix.commons.msd.dto.full.DSD;
import org.fao.fenix.commons.utils.JSONUtils;
import org.fao.fenix.d3s.msd.services.rest.providers.JsonProvider;
import org.fao.fenix.d3s.server.dto.DatabaseStandards;

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
public class DSDProvider <T extends DSD> extends JsonProvider implements MessageBodyReader<T> {

    @Override
    public boolean isReadable(Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return true;
    }

    @Override
    public T readFrom(Class<T> resourceClass, Type type, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> stringStringMultivaluedMap, InputStream inputStream) throws IOException, WebApplicationException {
        try {
            String content = readContent(inputStream);
            return JSONUtils.decode(content, getDSDClass(content));
        } catch (Exception e) {
            throw new WebApplicationException(e);
        }
    }


    //Utils
    private Class<T> getDSDClass(String content) throws Exception {
        ObjectMapper jacksonMapper = new ObjectMapper();
        JsonNode metadataNode = jacksonMapper.readTree(content);

        String rid = metadataNode!=null ? metadataNode.path("rid").textValue() : null;
        ODocument metadataO = rid!=null ? (ODocument)DatabaseStandards.connection.get().getUnderlying().load(JSONEntity.toRID(rid)) : null;

        return metadataO!=null ? (Class<T>)Class.forName(DSD.class.getPackage().getName()+'.'+metadataO.getClassName()) : null;
    }

}

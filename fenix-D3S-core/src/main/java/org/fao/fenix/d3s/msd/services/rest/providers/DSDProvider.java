package org.fao.fenix.d3s.msd.services.rest.providers;

import com.orientechnologies.orient.core.record.impl.ODocument;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.fao.fenix.commons.msd.dto.JSONEntity;
import org.fao.fenix.commons.msd.dto.full.DSD;
import org.fao.fenix.commons.msd.dto.full.DSDDataset;
import org.fao.fenix.d3s.server.tools.orient.DatabaseStandards;

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
public class DSDProvider <T extends DSD> implements MessageBodyReader<T> {

    @Override
    public boolean isReadable(Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return true;
    }

    @Override
    public T readFrom(Class<T> resourceClass, Type type, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> stringStringMultivaluedMap, InputStream inputStream) throws IOException, WebApplicationException {
        try {
            ObjectMapper jacksonMapper = new ObjectMapper();
            JsonNode resourceNode = jacksonMapper.readTree(inputStream);
            String dsdClassName = getDSDClassName(resourceNode);

            TypeReference resourceType = null;
            if (DSDDataset.class.getSimpleName().equals(dsdClassName))
                resourceType = new TypeReference<DSDDataset>(){};

            return resourceType!=null ? (T)jacksonMapper.readValue(resourceNode, resourceType) : null;
        } catch (Exception e) {
            throw new WebApplicationException(e);
        }
    }


    //Utils
    private String getDSDClassName(JsonNode metadataNode) {
        String rid = metadataNode!=null ? metadataNode.path("rid").getTextValue() : null;
        ODocument metadataO = rid!=null ? (ODocument)DatabaseStandards.connection.get().getUnderlying().load(JSONEntity.toRID(rid)) : null;
        return metadataO!=null ? metadataO.getClassName() : null;
    }
}

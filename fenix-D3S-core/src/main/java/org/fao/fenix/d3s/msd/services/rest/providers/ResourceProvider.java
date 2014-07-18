package org.fao.fenix.d3s.msd.services.rest.providers;

import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.fao.fenix.commons.msd.dto.JSONEntity;
import org.fao.fenix.commons.msd.dto.data.Resource;
import org.fao.fenix.commons.msd.dto.full.Code;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.fenix.commons.msd.dto.type.RepresentationType;
import org.fao.fenix.d3s.server.tools.orient.DatabaseStandards;

import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;
import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;


@Provider
@Consumes(MediaType.APPLICATION_JSON)
public class ResourceProvider implements MessageBodyReader<Resource> {

    @Override
    public boolean isReadable(Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return true;
    }

    @Override
    public Resource readFrom(Class<Resource> resourceClass, Type type, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> stringStringMultivaluedMap, InputStream inputStream) throws IOException, WebApplicationException {
        try {
            ObjectMapper jacksonMapper = new ObjectMapper();
            JsonNode resourceNode = jacksonMapper.readTree(inputStream);

            RepresentationType representationType = getRepresentationType(resourceNode.get("metadata"));
            TypeReference resourceType = null;
            if (representationType!=null)
                switch (representationType) {
                    case codelist: resourceType = new TypeReference<Resource<Code>>() { }; break;

                }

            return jacksonMapper.readValue(resourceNode, resourceType);
        } catch (Exception e) {
            throw new WebApplicationException(e);
        }
    }


    //Utils
    private RepresentationType getRepresentationType(JsonNode metadataNode) {
        String representationTypeLabel = metadataNode!=null ? metadataNode.path("meContent").path("resourceRepresentationType").getTextValue() : null;
        RepresentationType representationType = representationTypeLabel!=null ? RepresentationType.valueOf(representationTypeLabel) : null;

        if (representationType==null) {
            String rid = metadataNode!=null ? metadataNode.path("rid").getTextValue() : null;
            if (rid!=null) {
                OObjectDatabaseTx connection = ((OObjectDatabaseTx) DatabaseStandards.connection.get());
                MeIdentification metadata = connection.load(JSONEntity.toRID(rid));
                if (metadata!=null)
                    representationType = metadata.getMeContent().getResourceRepresentationType();
            }
        }

        return representationType;
    }

    private String read(BufferedReader in) throws IOException {
        StringBuilder buffer = new StringBuilder();
        for (String row=in.readLine(); row!=null; row=in.readLine())
            buffer.append(row).append('\n');
        return buffer.toString();
    }


}

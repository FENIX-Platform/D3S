package org.fao.fenix.d3s.msd.services.rest.providers.jsonResources;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.fao.fenix.commons.msd.dto.data.MetadataList;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.fenix.d3s.msd.services.rest.providers.JsonProvider;

import javax.ws.rs.BadRequestException;
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
public class MetadataListProvider extends JsonProvider implements MessageBodyReader<MetadataList> {

    @Override
    public boolean isReadable(Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return true;
    }

    @Override
    public MetadataList readFrom(Class<MetadataList> aClass, Type type, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> multivaluedMap, InputStream inputStream) throws IOException, WebApplicationException {
        try {
            String listContent = readContent(inputStream);
            JsonNode metadataListNode = listContent!=null ? jacksonMapper.readTree(listContent) : null;

            if (metadataListNode!=null && metadataListNode.isArray()) {
                MetadataList metadataList = new MetadataList();
                for (JsonNode metadataNode : metadataListNode) {
                    String content = metadataNode.toString();
                    metadataList.add(decodeMetadata(content, getRepresentationType(content, null)));
                }
                return metadataList;
            } else
                return null;
        } catch (Exception e) {
            throw new BadRequestException(e);
        }
    }

}

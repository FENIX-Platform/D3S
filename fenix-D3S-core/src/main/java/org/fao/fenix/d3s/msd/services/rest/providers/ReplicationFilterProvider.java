package org.fao.fenix.d3s.msd.services.rest.providers;

import com.fasterxml.jackson.databind.JsonNode;
import org.fao.fenix.commons.find.dto.filter.StandardFilter;
import org.fao.fenix.commons.msd.dto.data.ReplicationFilter;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;

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
public class ReplicationFilterProvider extends JsonProvider implements MessageBodyReader<ReplicationFilter> {

    @Override
    public boolean isReadable(Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return true;
    }

    @Override
    public ReplicationFilter readFrom(Class<ReplicationFilter> resourceClass, Type type, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> stringStringMultivaluedMap, InputStream inputStream) throws IOException, WebApplicationException {
        try {
            String replicationContent = readContent(inputStream);
            JsonNode replicationFilterNode = replicationContent!=null ? jacksonMapper.readTree(replicationContent) : null;

            if (replicationFilterNode!=null && replicationFilterNode.isObject()) {
                ReplicationFilter replicationFilter = new ReplicationFilter();

                JsonNode metadataNode = replicationFilterNode.get("metadata");
                String metadataContent = metadataNode!=null ? metadataNode.toString() : null;
                replicationFilter.setMetadata(metadataContent!=null ? decodeMetadata(metadataContent, getRepresentationType(metadataContent, null)) : null);

                JsonNode filterNode = replicationFilterNode.get("filter");
                String filterContent = filterNode!=null ? filterNode.toString() : null;
                replicationFilter.setFilter(filterContent != null ? decode(filterContent, StandardFilter.class) : null);

                return replicationFilter;
            } else
                return null;
        } catch (Exception e) {
            throw new BadRequestException(e);
        }
    }

}

package org.fao.fenix.d3s.msd.services.rest.providers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import org.fao.fenix.commons.msd.dto.JSONEntity;
import org.fao.fenix.commons.msd.dto.data.Resource;
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
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;


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
            String content = readContent(inputStream);

            switch (getRepresentationType(jacksonMapper.readTree(content).get("metadata"))) {
                case codelist:      return jacksonMapper.readValue(content, org.fao.fenix.commons.msd.dto.data.codelist.Resource.class);
                case dataset:       return jacksonMapper.readValue(content, org.fao.fenix.commons.msd.dto.data.dataset.Resource.class);
                case geographic:    return jacksonMapper.readValue(content, org.fao.fenix.commons.msd.dto.data.geographic.Resource.class);
                case document:      return jacksonMapper.readValue(content, org.fao.fenix.commons.msd.dto.data.document.Resource.class);
                default: return null;
            }
        } catch (Exception e) {
            throw new WebApplicationException(e);
        }
    }


    //Utils
    protected RepresentationType getRepresentationType(JsonNode metadataNode) {
        String representationTypeLabel = metadataNode!=null ? metadataNode.path("meContent").path("resourceRepresentationType").textValue() : null;

        if (representationTypeLabel==null) {
            String rid = metadataNode!=null ? metadataNode.path("rid").textValue() : null;
            String uid = metadataNode!=null ? metadataNode.path("uid").textValue() : null;
            String version = metadataNode!=null ? metadataNode.path("version").textValue() : null;

            ODatabaseDocument connection = DatabaseStandards.connection.get().getUnderlying();
            ODocument metadataO = null;
            if (rid!=null)
                metadataO = connection.load(JSONEntity.toRID(rid));
            else if (uid!=null) {
                List<ODocument> metadataOList = connection.query(new OSQLSynchQuery<MeIdentification>("select from MeIdentification where index_id = ?"), uid + (version != null ? version : ""));
                metadataO = metadataOList!=null && !metadataOList.isEmpty() ? metadataOList.iterator().next() : null;
            }

            representationTypeLabel = metadataO!=null ? (String)metadataO.field("meContent.resourceRepresentationType") : null;
        }

        return representationTypeLabel!=null ? RepresentationType.valueOf(representationTypeLabel) : RepresentationType.dataset;
    }

    private String readContent(InputStream inputStream) {
        try (Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name()).useDelimiter("\\A")) {
            return scanner.hasNext() ? scanner.next() : "";
        }
    }
}

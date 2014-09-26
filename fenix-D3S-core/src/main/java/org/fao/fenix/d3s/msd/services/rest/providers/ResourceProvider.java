package org.fao.fenix.d3s.msd.services.rest.providers;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.fao.fenix.commons.msd.dto.JSONEntity;
import org.fao.fenix.commons.msd.dto.data.Resource;
import org.fao.fenix.commons.msd.dto.full.Code;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.fenix.commons.msd.dto.type.RepresentationType;
import org.fao.fenix.d3s.msd.dao.MetadataResourceDao;
import org.fao.fenix.d3s.server.tools.orient.DatabaseStandards;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;
import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;


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
                    case codelist:  resourceType = new TypeReference<Resource<Code>>() { }; break;
                    case dataset:   resourceType = new TypeReference<Resource<Object[]>>() { }; break;
                }
            if (resourceType==null)
                resourceType = new TypeReference<Resource<Object[]>>() { };

            return jacksonMapper.readValue(resourceNode, resourceType);
        } catch (Exception e) {
            throw new WebApplicationException(e);
        }
    }


    //Utils
    private RepresentationType getRepresentationType(JsonNode metadataNode) {
        String representationTypeLabel = metadataNode!=null ? metadataNode.path("meContent").path("resourceRepresentationType").getTextValue() : null;

        if (representationTypeLabel==null) {
            String rid = metadataNode!=null ? metadataNode.path("rid").getTextValue() : null;
            String uid = metadataNode!=null ? metadataNode.path("uid").getTextValue() : null;
            String version = metadataNode!=null ? metadataNode.path("version").getTextValue() : null;

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

        return representationTypeLabel!=null ? RepresentationType.valueOf(representationTypeLabel) : null;
    }
}
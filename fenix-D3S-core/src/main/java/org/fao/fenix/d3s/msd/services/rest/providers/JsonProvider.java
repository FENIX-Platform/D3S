package org.fao.fenix.d3s.msd.services.rest.providers;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.tinkerpop.gremlin.Tokens;
import org.fao.fenix.commons.msd.dto.JSONEntity;
import org.fao.fenix.commons.msd.dto.data.Resource;
import org.fao.fenix.commons.msd.dto.full.*;
import org.fao.fenix.commons.msd.dto.type.RepresentationType;
import org.fao.fenix.commons.utils.CSVReader;
import org.fao.fenix.d3s.msd.dao.MetadataResourceDao;
import org.fao.fenix.d3s.server.tools.orient.DatabaseStandards;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;


public abstract class JsonProvider {
    private ObjectMapper jacksonMapper = new ObjectMapper();

    //Utils
    protected <T> T decode(String source, Class<T> baseClass, RepresentationType resourceType) throws Exception {
        JavaType type = null;
        switch (resourceType) {
            case codelist:      type = jacksonMapper.getTypeFactory().constructParametricType(baseClass, DSDCodelist.class, Code.class); break;
            case dataset:       type = jacksonMapper.getTypeFactory().constructParametricType(baseClass, DSDDataset.class, Object[].class); break;
            case geographic:    type = jacksonMapper.getTypeFactory().constructParametricType(baseClass, DSDGeographic.class, Object.class); break;
            case document:      type = jacksonMapper.getTypeFactory().constructParametricType(baseClass, DSDDocument.class, Object.class); break;
            default: return null;
        }
        return (T)jacksonMapper.readValue(source, type);
    }

    protected <T> T decode(String source, Class<T> beanClass) throws Exception {
        return jacksonMapper.readValue(source,beanClass);
    }
    
    protected RepresentationType getRepresentationType(String source, String metadataField) throws Exception {
        JsonNode metadataNode = source!=null ? jacksonMapper.readTree(source) : null;
        if (metadataField!=null && metadataNode!=null)
            for (String field : metadataField.split("\\."))
                metadataNode = metadataNode.get(field);

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
                List<ODocument> metadataOList = connection.query(new OSQLSynchQuery<MeIdentification>("select from MeIdentification where index|id = ?"), uid + (version != null ? '|'+version : ""));
                metadataO = metadataOList!=null && !metadataOList.isEmpty() ? metadataOList.iterator().next() : null;
            }

            representationTypeLabel = metadataO!=null ? (String)metadataO.field("meContent.resourceRepresentationType") : null;
        }

        return representationTypeLabel!=null ? RepresentationType.valueOf(representationTypeLabel) : RepresentationType.dataset;
    }

    protected String readSection(BufferedReader in, String placeHolder) throws IOException {
        StringBuilder buffer = new StringBuilder();
        for (String row=in.readLine(); row!=null && !row.trim().equals(placeHolder); row=in.readLine())
            buffer.append(row).append('\n');
        //Remove BOM character if it exists
        return buffer.length()>0 && buffer.charAt(0) == 65279 ? buffer.substring(1) : buffer.toString();
    }

    protected String readContent(InputStream inputStream) {
        try (Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name()).useDelimiter("\\A")) {
            return scanner.hasNext() ? scanner.next() : "";
        }
    }
}

package org.fao.fenix.d3s.msd.services.rest.providers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import org.fao.fenix.commons.msd.dto.JSONEntity;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.fenix.commons.msd.dto.type.RepresentationType;
import org.fao.fenix.commons.utils.CSVReader;
import org.fao.fenix.commons.utils.JSONUtils;
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
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Properties;


public abstract class CSVProvider<T>  implements MessageBodyReader<T> {
    @Inject MetadataResourceDao dao;
    @Inject ResourceProvider resourceProvider;


    @Override
    public T readFrom(Class<T> tClass, Type type, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> stringStringMultivaluedMap, InputStream inputStream) throws IOException, WebApplicationException {
        try {
            ObjectMapper jacksonMapper = new ObjectMapper();
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

            String metadataSection = readSection(in, "--structure--");
            RepresentationType resourceType = getRepresentationType(jacksonMapper.readTree(metadataSection).get("metadata"));
            MeIdentification metadata = null;
            switch (resourceType) {
                case codelist:      metadata = jacksonMapper.readValue(metadataSection, org.fao.fenix.commons.msd.dto.data.codelist.MeIdentification.class); break;
                case dataset:       metadata = jacksonMapper.readValue(metadataSection, org.fao.fenix.commons.msd.dto.data.dataset.MeIdentification.class); break;
                case geographic:    metadata = jacksonMapper.readValue(metadataSection, org.fao.fenix.commons.msd.dto.data.geographic.MeIdentification.class); break;
                case document:      metadata = jacksonMapper.readValue(metadataSection, org.fao.fenix.commons.msd.dto.data.document.MeIdentification.class); break;
            }

            Properties structure = readStructureProperties(readSection(in,"--data--"));
            String csvSeparator = structure.getProperty("csvSeparator", ";").trim();
            CSVReader csvReader = new CSVReader(in,csvSeparator);

            return read(resourceType,metadata,structure,csvReader);
        } catch (Exception e) {
            throw new WebApplicationException(e);
        }
    }


    protected abstract T read(RepresentationType resourceType, MeIdentification metadata, Properties structure, Iterable<String[]> data) throws Exception;



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

    private String readSection(BufferedReader in, String placeHolder) throws IOException {
        StringBuilder buffer = new StringBuilder();
        for (String row=in.readLine(); row!=null && !row.trim().equals(placeHolder); row=in.readLine())
            buffer.append(row).append('\n');
        //Remove BOM character if it exists
        return buffer.length()>0 && buffer.charAt(0) == 65279 ? buffer.substring(1) : buffer.toString();
    }


    private Properties readStructureProperties(String source) throws IOException {
        Properties structureProperties = new Properties();
        structureProperties.load(new StringReader(source));
        return structureProperties;
    }
}

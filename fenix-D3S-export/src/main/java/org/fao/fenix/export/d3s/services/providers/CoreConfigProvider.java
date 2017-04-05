package org.fao.fenix.export.d3s.services.providers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.fao.fenix.commons.process.dto.Process;
import org.fao.fenix.commons.utils.JSONUtils;
import org.fao.fenix.commons.utils.Order;
import org.fao.fenix.commons.utils.Page;
import org.fao.fenix.d3p.process.ProcessFactory;
import org.fao.fenix.d3s.server.dto.DatabaseStandards;
import org.fao.fenix.export.core.dto.CoreConfig;
import org.fao.fenix.export.core.utils.parser.JSONParser;
import org.fao.fenix.export.d3s.dto.FlowExportParameters;

import javax.inject.Inject;
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
import java.util.Collection;
import java.util.LinkedList;

@Provider
@Consumes(MediaType.APPLICATION_JSON)
public class CoreConfigProvider implements MessageBodyReader<CoreConfig> {

    @Override
    public boolean isReadable(Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return true;
    }

    @Override
    public CoreConfig readFrom(Class<CoreConfig> aClass, Type type, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> multivaluedMap, InputStream inputStream) throws IOException, WebApplicationException {
        try {
            return JSONParser.createFenixCore(inputStream);
        } catch (Exception e) {
            throw new WebApplicationException(e);
        }
    }

}

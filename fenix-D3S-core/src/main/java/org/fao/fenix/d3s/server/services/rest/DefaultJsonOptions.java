package org.fao.fenix.d3s.server.services.rest;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig.Feature;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@Provider
@Produces(MediaType.APPLICATION_JSON)
public class DefaultJsonOptions implements ContextResolver<ObjectMapper> {

    private final ObjectMapper mapper;

    public DefaultJsonOptions() {
        mapper = new ObjectMapper();
        mapper.enable(Feature.INDENT_OUTPUT);
        mapper.disable(Feature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_EMPTY);
    }

    @Override
    public ObjectMapper getContext(Class<?> type) {
        return mapper;
    }
}
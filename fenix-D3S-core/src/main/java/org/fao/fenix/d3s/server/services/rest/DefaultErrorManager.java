package org.fao.fenix.d3s.server.services.rest;

import org.apache.log4j.Logger;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.*;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.net.URI;
import java.net.URISyntaxException;

@Provider
public class DefaultErrorManager implements ExceptionMapper<Throwable> {
    private static final Logger LOGGER = Logger.getLogger(DefaultErrorManager.class);

    @Override
    public Response toResponse(Throwable e) {

        if (e instanceof NoContentException)
            return Response.noContent().entity("").build();
        else if (e instanceof WebApplicationException) {
            LOGGER.error("Uncaught error.",e);
            return e.getCause() != null ? Response.serverError().entity(e.getCause().getMessage()).build() : ((WebApplicationException) e).getResponse();
        } else {
            LOGGER.error("Uncaught error.",e);
            return Response.serverError().entity(e.getMessage()).build();
        }
    }
}

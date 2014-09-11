package org.fao.fenix.d3s.server.services.rest;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.*;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.net.URI;
import java.net.URISyntaxException;

@Provider
public class DefaultErrorManager implements ExceptionMapper<Exception> {
    @Override
    public Response toResponse(Exception e) {

        if (e instanceof NoContentException)
            return Response.noContent().entity("").build();
        else if (e instanceof WebApplicationException)
            return e.getCause()!=null ? Response.serverError().entity(e.getCause().getMessage()).build() : ((WebApplicationException) e).getResponse();
        else {
            e.printStackTrace();
            return Response.serverError().entity(e.getMessage()).build();
        }
    }
}

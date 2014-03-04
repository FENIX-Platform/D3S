package org.fao.fenix.d3s.server.services.rest;

import javax.ws.rs.core.*;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class DefaultErrorManager implements ExceptionMapper<Exception> {
    @Override
    public Response toResponse(Exception e) {
        if (e instanceof NoContentException)
            return Response.noContent().build();
        else
            return Response.serverError().entity(e.getMessage()).build();
    }
}

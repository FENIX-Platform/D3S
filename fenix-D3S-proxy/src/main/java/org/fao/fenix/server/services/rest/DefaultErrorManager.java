package org.fao.fenix.server.services.rest;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class DefaultErrorManager implements ExceptionMapper<Exception> {
    @Override
    public Response toResponse(Exception e) {
        String message = e instanceof InternalServerErrorException ? "Origin server error: ("+e.getMessage()+") "+((InternalServerErrorException)e).getResponse().readEntity(String.class) : e.getMessage();
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
    }
}

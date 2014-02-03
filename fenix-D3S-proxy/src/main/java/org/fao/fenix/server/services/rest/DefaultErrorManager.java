package org.fao.fenix.server.services.rest;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class DefaultErrorManager implements ExceptionMapper<Exception> {
    @Override
    public Response toResponse(Exception e) {
        Response response = e instanceof InternalServerErrorException ? ((InternalServerErrorException)e).getResponse() : null;

        if (response!=null && response.getStatus()==Response.Status.NO_CONTENT.getStatusCode())
            return Response.noContent().build();
        else if (response!=null)
            return Response.serverError().entity("Origin server error: ("+e.getMessage()+") "+response.readEntity(String.class)).build();
        else
            return Response.serverError().entity(e.getMessage()).build();
    }
}

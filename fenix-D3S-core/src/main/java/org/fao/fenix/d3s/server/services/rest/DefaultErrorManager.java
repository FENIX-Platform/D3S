package org.fao.fenix.d3s.server.services.rest;

import org.apache.log4j.Logger;
import org.fao.fenix.d3s.server.dto.DatabaseStandards;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotAcceptableException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.*;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class DefaultErrorManager implements ExceptionMapper<Throwable> {
    private static final Logger LOGGER = Logger.getLogger(DefaultErrorManager.class);

    @Override
    public Response toResponse(Throwable e) {
        HttpServletRequest httpRequest = DatabaseStandards.request.get();
        Response response = null;

        if (e instanceof NoContentException)
            response = Response.noContent().entity("").build();
        else if (e instanceof BadRequestException) {
            LOGGER.error("Bad request error.",e);
            response = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } else if (e instanceof NotAcceptableException) {
            LOGGER.warn("Result limit exceeded exception", e);
            response = Response.status(Response.Status.REQUESTED_RANGE_NOT_SATISFIABLE).entity("Result limit exceeded exception").build();
        } else if (e instanceof WebApplicationException) {
            LOGGER.error("Uncaught error.", e);
            response = e.getCause() != null ? Response.serverError().entity(e.getCause().getMessage()).build() : ((WebApplicationException) e).getResponse();
        } else {
            LOGGER.error("Uncaught error.",e);
            response = Response.serverError().entity(e.getMessage()).build();
        }

        if (response!=null)
            httpRequest.setAttribute("errorEntity", response.getEntity()!=null ? response.getEntity() : "Unexpected error");

        return response;
    }
}

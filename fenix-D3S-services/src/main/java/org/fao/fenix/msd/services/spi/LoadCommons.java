package org.fao.fenix.msd.services.spi;

import org.jboss.resteasy.annotations.GZIP;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public interface LoadCommons {

	@GET
    @GZIP
	@Path("contact/{contactID}")
	public Response getContactIdentity(@Context HttpServletRequest request, @PathParam("contactID") String contactID);
	@GET
    @GZIP
	@Path("contact/byFulltext")
	public Response getContactIdentitiesByFullText(@Context HttpServletRequest request, @QueryParam("text") String text);
	@GET
    @GZIP
	@Path("contact/byFields")
	public Response getContactIdentitiesByFields(@Context HttpServletRequest request, @QueryParam("institution") @DefaultValue("") String institution, @QueryParam("department") @DefaultValue("") String department, @QueryParam("name") @DefaultValue("") String name, @QueryParam("surname") @DefaultValue("") String surname, @QueryParam("context") @DefaultValue("") String context);
    @GET
    @GZIP
    @Path("publication/{publicationID}")
    public Response getPublication(@Context HttpServletRequest request, @PathParam("publicationID") String publicationID);
	
}

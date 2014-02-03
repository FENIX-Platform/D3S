package org.fao.fenix.msd.services.spi;

import org.fao.fenix.msd.dto.common.ContactIdentity;
import org.fao.fenix.msd.dto.common.Publication;
import org.jboss.resteasy.annotations.GZIP;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;

@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Consumes
public interface LoadCommons {

	@GET
    @GZIP
	@Path("contact/{contactID}")
	public ContactIdentity getContactIdentity(@Context HttpServletRequest request, @PathParam("contactID") String contactID) throws Exception;
	@GET
    @GZIP
	@Path("contact/byFulltext")
	public Collection<ContactIdentity> getContactIdentitiesByFullText(@Context HttpServletRequest request, @QueryParam("text") String text) throws Exception;
	@GET
    @GZIP
	@Path("contact/byFields")
	public Collection<ContactIdentity> getContactIdentitiesByFields(@Context HttpServletRequest request, @QueryParam("institution") @DefaultValue("") String institution, @QueryParam("department") @DefaultValue("") String department, @QueryParam("name") @DefaultValue("") String name, @QueryParam("surname") @DefaultValue("") String surname, @QueryParam("context") @DefaultValue("") String context) throws Exception;
    @GET
    @GZIP
    @Path("publication/{publicationID}")
    public Publication getPublication(@Context HttpServletRequest request, @PathParam("publicationID") String publicationID) throws Exception;
	
}

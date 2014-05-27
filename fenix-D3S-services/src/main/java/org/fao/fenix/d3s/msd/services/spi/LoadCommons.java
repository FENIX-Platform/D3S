package org.fao.fenix.d3s.msd.services.spi;

import org.fao.fenix.commons.msd.dto.full.common.ContactIdentity;
import org.fao.fenix.commons.msd.dto.full.common.Publication;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collection;

@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Consumes
public interface LoadCommons {

	@GET
	@Path("contact/{contactID}")
	public ContactIdentity getContactIdentity(@PathParam("contactID") String contactID) throws Exception;
	@GET
	@Path("contact/byFulltext")
	public Collection<ContactIdentity> getContactIdentitiesByFullText(@QueryParam("text") String text) throws Exception;
	@GET
	@Path("contact/byFields")
	public Collection<ContactIdentity> getContactIdentitiesByFields(@QueryParam("institution") @DefaultValue("") String institution, @QueryParam("department") @DefaultValue("") String department, @QueryParam("name") @DefaultValue("") String name, @QueryParam("surname") @DefaultValue("") String surname, @QueryParam("context") @DefaultValue("") String context) throws Exception;
    @GET
    @Path("publication/{publicationID}")
    public Publication getPublication(@PathParam("publicationID") String publicationID) throws Exception;
	
}

package org.fao.fenix.msd.services.rest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.NoContentException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.fao.fenix.msd.dto.common.ContactIdentity;
import org.fao.fenix.msd.dto.common.Publication;
import org.fao.fenix.msd.services.impl.Delete;
import org.fao.fenix.msd.services.impl.Store;
import org.fao.fenix.server.tools.spring.SpringContext;

@Path("msd/cm")
public class StoreCommons implements org.fao.fenix.msd.services.spi.StoreCommons {


	@Override
	public String newContactIdentity(@Context HttpServletRequest request, ContactIdentity contactIdentity) throws Exception {
		return SpringContext.getBean(Store.class).newContactIdentity(contactIdentity);
	}
	@Override
	public void updateContactIdentity(@Context HttpServletRequest request, ContactIdentity contactIdentity) throws Exception {
        if (SpringContext.getBean(Store.class).updateContactIdentity(contactIdentity, false)<=0)
            throw new NoContentException("");
	}
	@Override
	public void appendContactIdentity(@Context HttpServletRequest request, ContactIdentity contactIdentity) throws Exception {
        if (SpringContext.getBean(Store.class).updateContactIdentity(contactIdentity, true)<=0)
            throw new NoContentException("");
	}
	@Override
	public void deleteContactIdentity(@Context HttpServletRequest request, String contactID) throws Exception {
        if (SpringContext.getBean(Delete.class).deleteContactIdentity(contactID)<=0)
            throw new NoContentException("");
	}

    @Override
    public String newPublication(@Context HttpServletRequest request, Publication publication) throws Exception {
        return SpringContext.getBean(Store.class).newPublication(publication);
    }

}

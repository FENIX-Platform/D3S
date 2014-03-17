package org.fao.fenix.d3s.msd.services.rest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.NoContentException;

import org.fao.fenix.commons.msd.dto.common.ContactIdentity;
import org.fao.fenix.commons.msd.dto.common.Publication;
import org.fao.fenix.d3s.msd.services.impl.Store;
import org.fao.fenix.d3s.msd.services.impl.Delete;
import org.fao.fenix.d3s.server.tools.spring.SpringContext;

@Path("msd/cm")
public class StoreCommons implements org.fao.fenix.d3s.msd.services.spi.StoreCommons {
    @Context HttpServletRequest request;


	@Override
	public String newContactIdentity(ContactIdentity contactIdentity) throws Exception {
		return SpringContext.getBean(Store.class).newContactIdentity(contactIdentity);
	}
	@Override
	public void updateContactIdentity(ContactIdentity contactIdentity) throws Exception {
        if (SpringContext.getBean(Store.class).updateContactIdentity(contactIdentity, false)<=0)
            throw new NoContentException("");
	}
	@Override
	public void appendContactIdentity(ContactIdentity contactIdentity) throws Exception {
        if (SpringContext.getBean(Store.class).updateContactIdentity(contactIdentity, true)<=0)
            throw new NoContentException("");
	}
	@Override
	public void deleteContactIdentity(String contactID) throws Exception {
        if (SpringContext.getBean(Delete.class).deleteContactIdentity(contactID)<=0)
            throw new NoContentException("");
	}

    @Override
    public String newPublication(Publication publication) throws Exception {
        return SpringContext.getBean(Store.class).newPublication(publication);
    }

}

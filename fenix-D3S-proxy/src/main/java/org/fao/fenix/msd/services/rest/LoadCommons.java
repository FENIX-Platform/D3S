package org.fao.fenix.msd.services.rest;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

import org.fao.fenix.msd.dto.common.ContactIdentity;
import org.fao.fenix.msd.dto.common.Publication;
import org.fao.fenix.server.services.rest.Service;

@Path("msd/cm")
public class LoadCommons extends Service implements org.fao.fenix.msd.services.spi.LoadCommons {
    @Context HttpServletRequest request;

    @Override
    public ContactIdentity getContactIdentity(String contactID) throws Exception {
        return getProxy(org.fao.fenix.msd.services.spi.LoadCommons.class).getContactIdentity(contactID);
    }

    @Override
    public Collection<ContactIdentity> getContactIdentitiesByFullText(String text) throws Exception {
        return getProxy(org.fao.fenix.msd.services.spi.LoadCommons.class).getContactIdentitiesByFullText(text);
    }

    @Override
    public Collection<ContactIdentity> getContactIdentitiesByFields(String institution, String department, String name, String surname, String context) throws Exception {
        return getProxy(org.fao.fenix.msd.services.spi.LoadCommons.class).getContactIdentitiesByFields(institution, department, name, surname, context);
    }

    @Override
    public Publication getPublication(String publicationID) throws Exception {
        return getProxy(org.fao.fenix.msd.services.spi.LoadCommons.class).getPublication(publicationID);
    }
}

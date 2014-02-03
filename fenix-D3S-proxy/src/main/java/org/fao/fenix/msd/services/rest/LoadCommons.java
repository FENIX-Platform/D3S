package org.fao.fenix.msd.services.rest;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;

import org.fao.fenix.msd.dto.common.ContactIdentity;
import org.fao.fenix.msd.dto.common.Publication;
import org.fao.fenix.server.services.rest.Service;

@Path("msd/cm")
public class LoadCommons extends Service implements org.fao.fenix.msd.services.spi.LoadCommons {

    @Override
    public ContactIdentity getContactIdentity(HttpServletRequest request, String contactID) throws Exception {
        return getProxy(org.fao.fenix.msd.services.spi.LoadCommons.class).getContactIdentity(request,contactID);
    }

    @Override
    public Collection<ContactIdentity> getContactIdentitiesByFullText(HttpServletRequest request, String text) throws Exception {
        return getProxy(org.fao.fenix.msd.services.spi.LoadCommons.class).getContactIdentitiesByFullText(request, text);
    }

    @Override
    public Collection<ContactIdentity> getContactIdentitiesByFields(HttpServletRequest request, String institution, String department, String name, String surname, String context) throws Exception {
        return getProxy(org.fao.fenix.msd.services.spi.LoadCommons.class).getContactIdentitiesByFields(request, institution, department, name, surname, context);
    }

    @Override
    public Publication getPublication(HttpServletRequest request, String publicationID) throws Exception {
        return getProxy(org.fao.fenix.msd.services.spi.LoadCommons.class).getPublication(request, publicationID);
    }
}

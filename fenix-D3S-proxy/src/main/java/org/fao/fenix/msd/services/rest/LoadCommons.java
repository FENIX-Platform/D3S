package org.fao.fenix.msd.services.rest;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.fao.fenix.msd.dto.common.ContactIdentity;
import org.fao.fenix.msd.dto.common.Publication;
import org.fao.fenix.server.services.rest.Service;

public class LoadCommons extends Service implements org.fao.fenix.msd.services.spi.LoadCommons {

    @Override
    public Response getContactIdentity(HttpServletRequest request, String contactID) {
        return defaultCall(request, ContactIdentity.class, contactID);
    }

    @Override
    public Response getContactIdentitiesByFullText(HttpServletRequest request, String text) {
        return defaultCall(request, Collection.class, text);
    }

    @Override
    public Response getContactIdentitiesByFields(HttpServletRequest request, String institution, String department, String name, String surname, String context) {
        return defaultCall(request, Collection.class, institution, department, name, surname, context);
    }

    @Override
    public Response getPublication(HttpServletRequest request, String publicationID) {
        return defaultCall(request, Publication.class, publicationID);
    }
}

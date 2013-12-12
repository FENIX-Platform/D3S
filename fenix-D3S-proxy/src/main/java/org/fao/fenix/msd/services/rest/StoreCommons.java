package org.fao.fenix.msd.services.rest;

import org.fao.fenix.msd.dto.common.ContactIdentity;
import org.fao.fenix.msd.dto.common.Publication;
import org.fao.fenix.server.services.rest.Service;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

public class StoreCommons extends Service implements org.fao.fenix.msd.services.spi.StoreCommons {
    @Override
    public Response newContactIdentity(HttpServletRequest request, ContactIdentity contactIdentity) {
        return defaultCall(request, String.class, contactIdentity);
    }

    @Override
    public Response updateContactIdentity(HttpServletRequest request, ContactIdentity contactIdentity) {
        return defaultCall(request, null, contactIdentity);
    }

    @Override
    public Response appendContactIdentity(HttpServletRequest request, ContactIdentity contactIdentity) {
        return defaultCall(request, null, contactIdentity);
    }

    @Override
    public Response deleteContactIdentity(HttpServletRequest request, String contactID) {
        return defaultCall(request, null, contactID);
    }

    @Override
    public Response newPublication(HttpServletRequest request, Publication publication) {
        return defaultCall(request, String.class, publication);
    }
}

package org.fao.fenix.msd.services.rest;

import org.fao.fenix.msd.dto.common.ContactIdentity;
import org.fao.fenix.msd.dto.common.Publication;
import org.fao.fenix.server.services.rest.Service;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;

@Path("msd/cm")
public class StoreCommons extends Service implements org.fao.fenix.msd.services.spi.StoreCommons {
    @Override
    public String newContactIdentity(HttpServletRequest request, ContactIdentity contactIdentity) throws Exception {
        return getProxy(org.fao.fenix.msd.services.spi.StoreCommons.class).newContactIdentity(request, contactIdentity);
    }

    @Override
    public void updateContactIdentity(HttpServletRequest request, ContactIdentity contactIdentity) throws Exception {
        getProxy(org.fao.fenix.msd.services.spi.StoreCommons.class).updateContactIdentity(request, contactIdentity);
    }

    @Override
    public void appendContactIdentity(HttpServletRequest request, ContactIdentity contactIdentity) throws Exception {
        getProxy(org.fao.fenix.msd.services.spi.StoreCommons.class).appendContactIdentity(request, contactIdentity);
    }

    @Override
    public void deleteContactIdentity(HttpServletRequest request, String contactID) throws Exception {
        getProxy(org.fao.fenix.msd.services.spi.StoreCommons.class).deleteContactIdentity(request, contactID);
    }

    @Override
    public String newPublication(HttpServletRequest request, Publication publication) throws Exception {
        return getProxy(org.fao.fenix.msd.services.spi.StoreCommons.class).newPublication(request, publication);
    }
}

package org.fao.fenix.d3s.server.services.rest;


import org.fao.fenix.commons.msd.dto.JSONEntity;
import org.fao.fenix.d3s.server.dto.DatabaseStandards;
import org.fao.fenix.commons.utils.Page;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.Collection;

@Provider
public class ResponseInterceptor implements ContainerResponseFilter {
    //@Resource private HttpServletRequest httpRequest;

    @Override
    public void filter(ContainerRequestContext containerRequestContext, final ContainerResponseContext containerResponseContext) throws IOException {
        HttpServletRequest httpRequest = DatabaseStandards.request.get();
        //Support standard POST services
        if (containerRequestContext.getMethod().equals("POST") && Response.Status.OK.equals(containerResponseContext.getStatusInfo())) {
            containerResponseContext.setStatus(Response.Status.CREATED.getStatusCode());
            containerResponseContext.getHeaders().putSingle("Location", createGetPath(containerResponseContext.getEntity(), httpRequest));
        }
        //Support empty collections
        if (containerResponseContext.getEntityClass()!=null && Collection.class.isAssignableFrom(containerResponseContext.getEntityClass()) && ((Collection)containerResponseContext.getEntity()).size()==0)
            containerResponseContext.setStatusInfo(Response.Status.NO_CONTENT);
        //Support paginated select
        if (containerRequestContext.getMethod().equals("GET") && Response.Status.OK.equals(containerResponseContext.getStatusInfo()) && httpRequest.getParameter("perPage")!=null)
            containerResponseContext.getHeaders().putSingle("Location", createPagePath(httpRequest));
    }


    //Utils
    private String createGetPath(Object entity, HttpServletRequest httpRequest) {
        String serviceURL = httpRequest.getRequestURL().toString();
        if (entity!=null && entity instanceof JSONEntity)
            return serviceURL+(serviceURL.endsWith("/")?"":'/')+((JSONEntity)entity).getRID();
        else
            return null;
    }

    private String createPagePath(HttpServletRequest httpRequest) {
        Page pageInfo = new Page(httpRequest);
        return httpRequest.getRequestURL().toString()+'?'+"perPage="+pageInfo.perPage+"&page="+(pageInfo.page+1);
    }
}

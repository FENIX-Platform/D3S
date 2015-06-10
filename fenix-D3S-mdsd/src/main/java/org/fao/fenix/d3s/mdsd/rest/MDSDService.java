package org.fao.fenix.d3s.mdsd.rest;


import org.fao.fenix.d3s.mdsd.MDSDGenerator;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("mdsd")
public class MDSDService {
    private static MDSDGenerator mdsdGenerator = new MDSDGenerator();
    private static String mdsd;

    @GET
    @Produces(MediaType.APPLICATION_JSON+"; charset=utf-8")
    public String getResource() throws Exception {
        return mdsd==null ? mdsd=mdsdGenerator.generate() : mdsd;
    }
}

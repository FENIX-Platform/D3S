package org.fao.fenix.d3s.msd.services.spi;

import org.fao.fenix.commons.msd.dto.templates.codeList.CodeList;
import org.fao.fenix.commons.utils.PATCH;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.Date;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface CodeLists {

    @GET
    public Collection<CodeList> getCodeLists(@QueryParam("from") Date from, @QueryParam("to") Date to) throws Exception;
    @GET
    @Path("/{rid}")
    public CodeList getCodeList(@PathParam("rid") String rid) throws Exception;
    @POST
    public CodeList insertCodeList(org.fao.fenix.commons.msd.dto.full.CodeList codeList) throws Exception;
    @PATCH
    public CodeList updateCodeList(org.fao.fenix.commons.msd.dto.full.CodeList codeList) throws Exception;
    @PUT
    public CodeList appendCodeList(org.fao.fenix.commons.msd.dto.full.CodeList codeList) throws Exception;
    
    


}

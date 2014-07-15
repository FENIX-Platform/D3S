package org.fao.fenix.d3s.msd.services.spi;

import org.fao.fenix.commons.msd.dto.data.Resource;
import org.fao.fenix.commons.msd.dto.templates.codeList.CodeList;
import org.fao.fenix.commons.msd.dto.templates.codeList.MeIdentification;
import org.fao.fenix.commons.utils.PATCH;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.Date;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface CodeLists {

    @GET
    @Path("/full")
    public Collection<CodeList> getCodeLists(@QueryParam("from") Date from, @QueryParam("to") Date to) throws Exception;
    @GET
    @Path("/full/{rid}")
    public CodeList getCodeList(@PathParam("rid") String rid) throws Exception;
    @POST
    @Path("/full")
    public CodeList insertCodeList(Resource<org.fao.fenix.commons.msd.dto.full.Code> codeList) throws Exception;
    @PATCH
    @Path("/full")
    public CodeList updateCodeList(Resource<org.fao.fenix.commons.msd.dto.full.Code> codeList) throws Exception;
    @PUT
    @Path("/full")
    public CodeList appendCodeList(Resource<org.fao.fenix.commons.msd.dto.full.Code> codeList) throws Exception;


    @GET
    public Collection<MeIdentification> getCodeListsResource(@QueryParam("from") Date from, @QueryParam("to") Date to) throws Exception;
    @GET
    @Path("/{rid}")
    public MeIdentification getCodeListResource(@PathParam("rid") String rid) throws Exception;
    @POST
    public MeIdentification insertCodeListResource(org.fao.fenix.commons.msd.dto.full.MeIdentification codeList) throws Exception;
    @PATCH
    public MeIdentification updateCodeListResource(org.fao.fenix.commons.msd.dto.full.MeIdentification codeList) throws Exception;
    @PUT
    public MeIdentification appendCodeListResource(org.fao.fenix.commons.msd.dto.full.MeIdentification codeList) throws Exception;


    @GET
    @Path("/data/{rid}")
    public MeIdentification getCodeListData(@PathParam("rid") String rid) throws Exception;
    @POST
    @Path("/data/{rid}")
    public MeIdentification insertCodeListData(@PathParam("rid") String rid, Collection<org.fao.fenix.commons.msd.dto.full.Code> codeList) throws Exception;
    @PATCH
    @Path("/data/{rid}")
    public MeIdentification updateCodeListData(@PathParam("rid") String rid, Collection<org.fao.fenix.commons.msd.dto.full.Code> codeList) throws Exception;
    @PUT
    @Path("/data/{rid}")
    public MeIdentification appendCodeListData(@PathParam("rid") String rid, Collection<org.fao.fenix.commons.msd.dto.full.Code> codeList) throws Exception;




}

package org.fao.fenix.export.d3s.services;

import org.fao.fenix.commons.msd.dto.data.Resource;
import org.fao.fenix.commons.msd.dto.data.ResourceProxy;
import org.fao.fenix.commons.msd.dto.full.DSDDataset;
import org.fao.fenix.commons.utils.UIDUtils;
import org.fao.fenix.d3p.services.Processes;
import org.fao.fenix.d3s.msd.services.rest.ResourcesService;
import org.fao.fenix.d3s.server.dto.DatabaseStandards;
import org.fao.fenix.export.core.controller.GeneralController;
import org.fao.fenix.export.core.dto.CoreConfig;
import org.fao.fenix.export.core.dto.CoreOutputHeader;
import org.fao.fenix.export.core.dto.PluginConfig;
import org.fao.fenix.export.d3s.dto.FlowExportParameters;
import org.fao.fenix.export.d3s.dto.ResourceExportParameters;
import org.fao.fenix.export.d3s.impl.ResultsCache;
import org.fao.fenix.export.d3s.utils.MetadataUtils;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Map;

@Path("export")
public class ExportService {
    @Inject ResourcesService resourcesService;
    @Inject Processes processesService;
    @Inject MetadataUtils metadataUtils;
    @Inject ResultsCache resultsCache;
    @Inject UIDUtils uidUtils;
    @Inject DatabaseStandards requestObjects;
    @Inject GeneralController generalController;

    @POST
    @Path("/flow")
    @Consumes(MediaType.APPLICATION_JSON)
    public String exportFlow(FlowExportParameters config) throws Exception {
        //Apply process
        Object rawData = processesService.apply(config.flow, toString(config.flowManagerList));
        //Validate data (only single resource results are supported)
        if (rawData instanceof Map)
            throw new BadRequestException("Only flows that produces single resource results are supported");
        //Produce a response that will use export general controller to write output
        String resultId = uidUtils.getId();
        initGeneralController(getResource((ResourceProxy) rawData), config.outConfig);
        resultsCache.put(resultId,generalController);
        return resultId;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public String exportResource(ResourceExportParameters config) throws Exception {
        //Retrieve dataset and detach metadata from Orient
        Resource data = resourcesService.loadResource(config.uid, config.version);
        data = getResource(new ResourceProxy(data.getMetadata(), data.getData()));
        //data.setMetadata((MeIdentification<DSDDataset>)requestObjects.getConnection().detach(data.getMetadata(), true)); TODO find Orient bug workaround

        //Produce a response that will use export general controller to write output
        String resultId = uidUtils.getId();
        initGeneralController(data, config.outConfig);
        resultsCache.put(resultId,generalController);
        return resultId;
    }

    @GET
    @Path("/{resultId}")
    public Response getResult (@PathParam("resultId") String resultId) throws Exception {
        GeneralController exportGeneralController = resultsCache.remove(resultId);
        return exportGeneralController!=null ? createResponse(exportGeneralController) : Response.noContent().build();
    }


    //Utils
    private void initGeneralController(Resource data, PluginConfig outputPluginConfig) throws Exception {
        PluginConfig inputPluginConfig = new PluginConfig();
        inputPluginConfig.setPlugin("d3sInputPluginStandard");
        generalController.init(new CoreConfig(inputPluginConfig, outputPluginConfig, data));
    }

    private Response createResponse(final GeneralController exportController) throws Exception {
        StreamingOutput stream = new StreamingOutput() {
            public void write(OutputStream output) throws IOException, WebApplicationException {
                try {
                    exportController.write(output); }
                catch (Exception e) { throw new WebApplicationException(e); }
            }
        };
        CoreOutputHeader header = exportController.getHeader();
        return Response.ok(stream, header.getType().getContentType()).header("content-disposition", "attachment; filename=\""+header.getName()+"\"").build();
    }

    private String toString(String[] array) {
        return array!=null ? Arrays.toString(array).replaceAll("[\\[\\]]","").replaceAll("\\, ",",") : null;
    }

    private Resource<DSDDataset,Object[]> getResource (ResourceProxy rawData) throws Exception {
        Resource<DSDDataset,Object[]> data = new Resource<>();
        data.setData(rawData.getData());
        data.setMetadata(metadataUtils.getProcessMetadata(rawData.getMetadata()));
        return data;
    }
}

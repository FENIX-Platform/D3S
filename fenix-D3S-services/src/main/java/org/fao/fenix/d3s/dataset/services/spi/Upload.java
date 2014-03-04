package org.fao.fenix.d3s.dataset.services.spi;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.fao.fenix.d3s.dataset.dto.DatasetUploadJson;

@Produces
@Consumes(MediaType.APPLICATION_JSON)
public interface Upload {


	@POST
	@Path("upload")
	public String uploadDataset(DatasetUploadJson data) throws Exception;
	
}

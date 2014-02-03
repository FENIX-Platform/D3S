package org.fao.fenix.dataset.services.spi;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.fao.fenix.dataset.dto.DatasetUploadForm;
import org.fao.fenix.dataset.dto.DatasetUploadJson;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

@Produces
@Consumes(MediaType.APPLICATION_JSON)
public interface Upload {


	@POST
	@Path("upload")
	public String uploadDataset(DatasetUploadJson data) throws Exception;
	
}

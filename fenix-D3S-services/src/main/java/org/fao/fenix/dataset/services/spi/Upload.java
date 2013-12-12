package org.fao.fenix.dataset.services.spi;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.fao.fenix.dataset.dto.DatasetUploadForm;
import org.fao.fenix.dataset.dto.DatasetUploadJson;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;


@Path("dataset")
public interface Upload {

	
	@POST
	@Path("upload/multipart")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadDataset(@MultipartForm DatasetUploadForm data);
	
	@POST
	@Path("upload")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response uploadDataset(DatasetUploadJson data);
	
}

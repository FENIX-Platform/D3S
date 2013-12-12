package org.fao.fenix.proxy.services.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.fao.fenix.proxy.services.dto.DatasetUpload;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

@Path("test")
public class Test {

	@GET
	public String test() {
		return "A bello!!!";
	}
	
	@POST
	@Path("upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadDataset(@MultipartForm DatasetUpload formData) {
		System.out.println("Name:"+formData.getFileName());
		System.out.println("File:"+(formData.getDataFile()!=null ? new String(formData.getDataFile()) : null));
		return Response.ok().build();
	}
	
}

package org.fao.fenix.dataset.services.rest;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.fao.fenix.dataset.dto.DatasetUpload;
import org.fao.fenix.dataset.dto.DatasetUploadForm;
import org.fao.fenix.dataset.dto.DatasetUploadJson;
import org.fao.fenix.dataset.services.DatasetStore;
import org.fao.fenix.dataset.services.impl.DefaultDatasetStore;
import org.fao.fenix.server.tools.spring.SpringContext;


@Path("dataset")
public class Upload implements org.fao.fenix.dataset.services.spi.Upload {


	@Override
	public Response uploadDataset(DatasetUploadJson data) { return defaultUploadDataset(data); }
	
	private <T> Response defaultUploadDataset(DatasetUpload<T> data) {
		DatasetStore store = SpringContext.getBean(DefaultDatasetStore.class);
		try {
			return Response.ok(store.storeDataset(data)).build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
}

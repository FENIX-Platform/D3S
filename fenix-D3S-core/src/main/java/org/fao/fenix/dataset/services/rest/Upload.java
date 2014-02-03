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

import java.util.Collection;


@Path("dataset")
public class Upload implements org.fao.fenix.dataset.services.spi.Upload {


	@Override
	public String uploadDataset(DatasetUploadJson data) throws Exception {
        return defaultUploadDataset(data);
    }
	
	private <T> String defaultUploadDataset(DatasetUpload<T> data) throws Exception {
		return SpringContext.getBean(DefaultDatasetStore.class).storeDataset(data);
	}
}

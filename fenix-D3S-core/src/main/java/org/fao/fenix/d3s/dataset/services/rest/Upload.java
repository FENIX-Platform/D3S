package org.fao.fenix.d3s.dataset.services.rest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

import org.fao.fenix.d3s.dataset.dto.DatasetUpload;
import org.fao.fenix.d3s.dataset.dto.DatasetUploadJson;
import org.fao.fenix.d3s.dataset.services.impl.DefaultDatasetStore;
import org.fao.fenix.d3s.server.tools.spring.SpringContext;


@Path("dataset")
public class Upload implements org.fao.fenix.d3s.dataset.services.spi.Upload {
    @Context HttpServletRequest request;


    @Override
	public String uploadDataset(DatasetUploadJson data) throws Exception {
        return defaultUploadDataset(data);
    }
	
	private <T> String defaultUploadDataset(DatasetUpload<T> data) throws Exception {
		return SpringContext.getBean(DefaultDatasetStore.class).storeDataset(data);
	}
}

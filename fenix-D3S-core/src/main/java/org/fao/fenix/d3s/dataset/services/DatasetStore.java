package org.fao.fenix.d3s.dataset.services;

import org.fao.fenix.d3s.dataset.dto.DatasetUpload;

public interface DatasetStore {
	
	public <T> String storeDataset(DatasetUpload<T> data) throws Exception;

}

package org.fao.fenix.dataset.services;

import org.fao.fenix.dataset.dto.DatasetUpload;

public interface DatasetStore {
	
	public <T> String storeDataset(DatasetUpload<T> data) throws Exception;

}

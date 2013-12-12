package org.fao.fenix.dataset.services.impl;

import org.fao.fenix.dataset.dto.DatasetUpload;
import org.fao.fenix.dataset.services.DatasetStore;
import org.fao.fenix.dataset.uploadConverter.DatasetConverter;
import org.springframework.stereotype.Component;

@Component
public class DefaultDatasetStore implements DatasetStore {

	@Override
	public <T> String storeDataset(DatasetUpload<T> dataset) throws Exception {
		DatasetConverter<T> converter = DatasetConverter.getInstance(dataset);
		String uid = null;
		if (converter!=null) {
			for (Object[] row : converter) {
				//TODO
			}
		}
		//Return uid
		return uid;
	}

	

}

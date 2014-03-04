package org.fao.fenix.d3s.dataset.services.impl;

import org.fao.fenix.d3s.dataset.dto.DatasetUpload;
import org.fao.fenix.d3s.dataset.services.DatasetStore;
import org.fao.fenix.d3s.dataset.uploadConverter.DatasetConverter;
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

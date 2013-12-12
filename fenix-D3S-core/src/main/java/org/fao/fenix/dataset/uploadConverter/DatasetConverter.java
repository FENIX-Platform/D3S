package org.fao.fenix.dataset.uploadConverter;

import java.io.InputStream;

import org.fao.fenix.dataset.dto.DatasetUpload;

public abstract class DatasetConverter <T> implements Iterable<Object[]> {
	
	private InputStream input;
	public InputStream getInput() { return input; }

	public static <T> DatasetConverter<T> getInstance(DatasetUpload<T> dataset) throws Exception {
		String className = dataset.getFormat()!=null ? dataset.getFormat().getConverterClassName() : null;
		if (className!=null) {
			@SuppressWarnings("unchecked")
			DatasetConverter<T> converter = (DatasetConverter<T>) Class.forName(className).newInstance();
			converter.init(dataset.getData());
			return converter;
		} else
			return null;
	}
	
	
	protected abstract DatasetConverter<T> init(T data);

}

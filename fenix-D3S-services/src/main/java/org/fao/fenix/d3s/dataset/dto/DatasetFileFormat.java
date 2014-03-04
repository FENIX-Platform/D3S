package org.fao.fenix.d3s.dataset.dto;


public enum DatasetFileFormat {
	
	csv("org.fao.fenix.dataset.converter.impl.CSVDatasetConverter.class"),
	xls("org.fao.fenix.dataset.converter.impl.XLSDatasetConverter.class"),
	xlsx("org.fao.fenix.dataset.converter.impl.XLSXDatasetConverter.class"),
	json("org.fao.fenix.dataset.converter.impl.JSONDatasetConverter.class"),
	matrix("org.fao.fenix.dataset.converter.impl.MatrixDatasetConverter.class"),
	bin(null);
	
	private String converterClassName;
	private DatasetFileFormat(String converterClassName) { this.converterClassName = converterClassName; }
	public String getConverterClassName() { return converterClassName; }

	

}

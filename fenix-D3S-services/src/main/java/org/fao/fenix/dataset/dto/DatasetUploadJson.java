package org.fao.fenix.dataset.dto;

import java.util.Collection;
import java.util.LinkedList;


public class DatasetUploadJson extends DatasetUpload<Collection<Object[]>> {
	
	public DatasetUploadJson() {
		data = new LinkedList<Object[]>();
	}
	
//	@Override
//	public Iterator<Object[]> iterator() {
//		try { return data.iterator();
//		} catch (Exception e) { return null; }
//	}
	
	//Utils
	public void addRow(Object[] row) {
		data.add(row);
	}
}

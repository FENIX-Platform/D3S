package org.fao.fenix.proxy.services.dto;

import javax.ws.rs.FormParam;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.providers.multipart.PartType;

public class DatasetUpload {
	
	private String fileName;
	private byte[] dataFile;
	
	public String getFileName() {
		return fileName;
	}
	@FormParam("name")
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public byte[] getDataFile() {
		return dataFile;
	}
	@FormParam("data")
	@PartType(MediaType.APPLICATION_OCTET_STREAM)
	public void setDataFile(byte[] dataFile) {
		this.dataFile = dataFile;
	}
	
	

}

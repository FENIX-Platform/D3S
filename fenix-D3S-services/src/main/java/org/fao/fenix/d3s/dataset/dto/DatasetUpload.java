package org.fao.fenix.d3s.dataset.dto;

import javax.ws.rs.FormParam;
import javax.ws.rs.core.MediaType;

import org.fao.fenix.commons.msd.dto.dm.DM;
import org.jboss.resteasy.annotations.providers.multipart.PartType;

public abstract class DatasetUpload <T> {
	
	protected String uid;
	protected T data;
	protected DM meta;
	protected DatasetFileFormat format;
	protected Integer chunksNumber;
	protected Integer chunkIndex;
	
	public T getData() {
		return data;
	}
	@FormParam("data")
	@PartType(MediaType.APPLICATION_OCTET_STREAM)
	public void setData(T data) {
		this.data = data;
	}
	
	public Integer getChunksNumber() {
		return chunksNumber;
	}
	@FormParam("chunksNumber")
	public void setChunksNumber(Integer chunksNumber) {
		this.chunksNumber = chunksNumber;
	}
	public Integer getChunkIndex() {
		return chunkIndex;
	}
	@FormParam("chunkIndex")
	public void setChunkIndex(Integer chunkIndex) {
		this.chunkIndex = chunkIndex;
	}
	public String getUid() {
		return uid;
	}
	@FormParam("uid")
	public void setUid(String uid) {
		this.uid = uid;
	}

	public DatasetFileFormat getFormat() {
		return format;
	}
	@FormParam("format")
	public void setFormat(DatasetFileFormat format) {
		this.format = format;
	}

	public DM getMeta() {
		return meta;
	}
	@FormParam("meta")
	@PartType(MediaType.APPLICATION_JSON)
	public void setMeta(DM meta) {
		this.meta = meta;
	}
	
}
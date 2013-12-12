package org.fao.fenix.msd.dto.dm;

import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.fao.fenix.msd.dto.dm.type.DMStatus;

public class DMAvailability {
	
	private DMStatus status;
	private SortedSet<Integer> chunksIndex;
	private Integer chunksNumber;
	public DMStatus getStatus() {
		return status;
	}
	public void setStatus(DMStatus status) {
		this.status = status;
	}
	public SortedSet<Integer> getChunksIndex() {
		return chunksIndex;
	}
	public void setChunksIndex(Collection<Integer> chunksIndex) {
		if (this.chunksIndex!=null)
			this.chunksIndex.clear();
		else
			this.chunksIndex = new TreeSet<Integer>();
		if (chunksIndex!=null)
            this.chunksIndex.addAll(chunksIndex);
	}
	public Integer getChunksNumber() {
		return chunksNumber;
	}
	public void setChunksNumber(Integer chunksNumber) {
		this.chunksNumber = chunksNumber;
	}
	
	
	//Utils
	public void addChunkIndex(Integer index) {
        if (chunksIndex==null)
            chunksIndex = new TreeSet<Integer>();
		chunksIndex.add(index);
	}
	@JsonIgnore
	public void setDecodedStatus(String status) {
		this.status = DMStatus.getByCode(status);
	}
	
}

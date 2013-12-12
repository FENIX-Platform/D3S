package org.fao.fenix.search.dto;


public class SearchResponse {
	
	private Integer count;

	
	//GET-SET
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
	
	@Override
	public SearchResponse clone() throws CloneNotSupportedException {
		SearchResponse clone = new SearchResponse();
		
		clone.count = count;

		return clone;
	}
	
}

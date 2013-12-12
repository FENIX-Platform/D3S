package org.fao.fenix.search.bl.codec.bestMatch.operations;


public interface DecodeOperation {
	
	public Object[] apply(Object[] row) throws Exception;
	public boolean isApplicable();

}

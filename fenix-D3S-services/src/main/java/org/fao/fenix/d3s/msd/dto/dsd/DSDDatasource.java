package org.fao.fenix.d3s.msd.dto.dsd;

import java.util.HashMap;
import java.util.Map;

import org.fao.fenix.d3s.msd.dto.dsd.type.DSDDao;

public class DSDDatasource {


	private Map<String,String> reference = new HashMap<String,String>();
	private DSDDao dao;

	//GET-SET
	public Map<String, String> getReference() {
		return reference;
	}
	public void setReference(Map<String, String> reference) {
		this.reference = reference;
	}
	public DSDDao getDao() {
		return dao;
	}
	public void setDao(DSDDao dao) {
		this.dao = dao;
	}
	
	//Utils
	public void addReferenceParameter(String key, String value) {
        if (reference==null)
            reference = new HashMap<String, String>();
		reference.put(key, value);
	}
}

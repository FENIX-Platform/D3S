package org.fao.fenix.d3s.search.bl.codec.bestMatch.operations;

import java.util.Collection;
import java.util.Map;

import org.fao.fenix.d3s.msd.dao.cl.CodeListConverter;
import org.fao.fenix.commons.msd.dto.cl.Code;

import com.orientechnologies.orient.core.record.impl.ODocument;

public class CodeTranslationOperation implements DecodeOperation {
	//Init
	private Map<String,Collection<ODocument>> ooRelations;
	private Map<String,Collection<ODocument>> omRelations;
	private Map<String,Collection<ODocument>> moRelations;
	private int columnIndex = -1;
	private CodeListConverter clConverter;
	private boolean toApply;
	
//	public CodeTranslationOperation(LinkedHashMap<String, ResponseColumnStructure> structureFrom, LinkedHashMap<String, ResponseColumnStructure> structureTo, String dimensionName, CodecCodeListUtils clUtils, CodeListConverter clConverter, CodeListLoad clDao, OGraphDatabase database) throws Exception {
//		CodeSystem systemFrom = structureFrom.containsKey(dimensionName) ? structureFrom.get(dimensionName).getCodeSystem() : null;
//		CodeSystem systemTo = structureTo.containsKey(dimensionName) ? structureTo.get(dimensionName).getCodeSystem() : null;
//		
//		if (toApply = systemFrom!=null && systemTo!=null && !systemFrom.equals(systemTo)) {
//			Map<String,Collection<ODocument>>[] relations = clUtils.getRelationsBetween(
//					clDao.loadSystemO(systemFrom.getSystem(), systemFrom.getVersion(), database), 
//					clDao.loadSystemO(systemTo.getSystem(), systemTo.getVersion(), database),
//					database);
//			ooRelations = relations[0];
//			omRelations = relations[1];
//			moRelations = relations[2];
//			
//			Iterator<String> keyIterator = structureTo.keySet().iterator();
//			for (int i=0; keyIterator.hasNext(); i++)
//				if(keyIterator.next().equals(dimensionName))
//					columnIndex = i;
//			
//			this.clConverter = clConverter;
//			this.database = database;
//		}
//	}

	//Operation
	@Override
	public Object[] apply(Object[] row) throws Exception {
		if (!toApply)
			return row;
		String codeKey = toString((Code)row[columnIndex]);
		Collection<ODocument> relations = ooRelations.containsKey(codeKey) ? ooRelations.get(codeKey) : moRelations.get(codeKey);
		if (relations==null)
			if (omRelations.containsKey(codeKey)) {
				//TODO one to many only relation error management
			} else {
				//TODO no relation error management
			}
		if (relations.size()!=1)
			throw new Exception("Data decode error. More than one relation from original to destination code.");
		
		row[columnIndex] = clConverter.toCode((ODocument)relations.iterator().next().field("in"), false, CodeListConverter.NO_LEVELS, null);
		
		return row;
	}

	@Override public boolean isApplicable() { return toApply; }

	//Utils
	private String toString(Code code) { return code.getSystemKey()+'|'+code.getSystemVersion()+'|'+code.getCode(); }
	
}

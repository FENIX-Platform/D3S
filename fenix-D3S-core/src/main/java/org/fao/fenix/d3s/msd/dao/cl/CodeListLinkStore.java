package org.fao.fenix.d3s.msd.dao.cl;

import java.util.Collection;

import org.fao.fenix.commons.msd.dto.cl.*;
import org.fao.fenix.d3s.msd.dao.EdgesLabels;
import org.fao.fenix.d3s.msd.dao.common.CommonsStore;
import org.fao.fenix.d3s.msd.dao.dsd.DSDLoad;
import org.fao.fenix.d3s.msd.dao.dsd.DSDStore;
import org.fao.fenix.d3s.server.tools.orient.OrientDao;
import org.fao.fenix.d3s.server.tools.orient.OrientDatabase;
import org.fao.fenix.commons.msd.dto.cl.type.CSHierarchyType;

import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.record.impl.ODocument;

import javax.inject.Inject;

public class CodeListLinkStore extends OrientDao {
	@Inject private CodeListLinkLoad clLinkLoadDAO;
	@Inject private CodeListLoad clLoadDAO;
	@Inject private DSDLoad dsdLoadDAO;
	@Inject private CommonsStore cmStoreDAO;
	@Inject private DSDStore dsdStoreDAO;

	//UPDATE
	//conversion
	public int updateCodeConversion(CodeConversion conversion) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			return updateCodeConversion(conversion, database);
		} finally {
			if (database!=null)
				database.close();
		}
	}
	public int updateCodeConversion(CodeConversion conversion, OGraphDatabase database) throws Exception {
		ODocument from = clLoadDAO.loadCodeO(conversion.getFromCode().getSystemKey(), conversion.getFromCode().getSystemVersion(), conversion.getFromCode().getCode(), database);
		ODocument to = clLoadDAO.loadCodeO(conversion.getToCode().getSystemKey(), conversion.getToCode().getSystemVersion(), conversion.getToCode().getCode(), database);
		Collection<ODocument> conversions = clLinkLoadDAO.loadConversionsFromCodeToCodeO(from, to, database);
		for (ODocument conversionO : conversions) {
			((ODocument)conversionO.field("conversionRule")).delete();
			conversionO.field("conversionRule",dsdStoreDAO.storeValueOperator(conversion.getConversionRule(), database));
		}
		return conversions.size();
	}

	
	//DELETE
	//relations
	public int deleteCodeRelationship(CodeRelationship relation) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			return deleteCodeRelationship(relation, database);
		} finally {
			if (database!=null)
				database.close();
		}
	}
	public int deleteCodeRelationship(CodeRelationship relation, OGraphDatabase database) throws Exception {
		ODocument from = clLoadDAO.loadCodeO(relation.getFromCode().getSystemKey(), relation.getFromCode().getSystemVersion(), relation.getFromCode().getCode(), database);
		ODocument to = clLoadDAO.loadCodeO(relation.getToCode().getSystemKey(), relation.getToCode().getSystemVersion(), relation.getToCode().getCode(), database);
		return deleteCodeRelationship(from, to, database);
	}
	public int deleteCodeRelationship(ODocument codeFromO, ODocument codeToO, OGraphDatabase database) throws Exception {
		Collection<ODocument> relations = clLinkLoadDAO.loadRelationshipsFromCodeToCodeO(codeFromO, codeToO, database);
		for (ODocument relation : relations)
			database.removeEdge(relation);
		return relations.size();
	}	
	//conversions
	public int deleteCodeConversion(CodeConversion conversion) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			return deleteCodeConversion(conversion, database);
		} finally {
			if (database!=null)
				database.close();
		}
	}
	public int deleteCodeConversion(CodeConversion conversion, OGraphDatabase database) throws Exception {
		ODocument from = clLoadDAO.loadCodeO(conversion.getFromCode().getSystemKey(), conversion.getFromCode().getSystemVersion(), conversion.getFromCode().getCode(), database);
		ODocument to = clLoadDAO.loadCodeO(conversion.getToCode().getSystemKey(), conversion.getToCode().getSystemVersion(), conversion.getToCode().getCode(), database);
		return deleteCodeConversion(from, to, database);
	}
	public int deleteCodeConversion(ODocument codeFromO, ODocument codeToO, OGraphDatabase database) throws Exception {
		Collection<ODocument> conversions = clLinkLoadDAO.loadConversionsFromCodeToCodeO(codeFromO, codeToO, database);
		for (ODocument conversion : conversions) {
			((ODocument)conversion.field("conversionRule")).delete();
			database.removeEdge(conversion);
		}
		return conversions.size();
	}	
	//propaedeutics
	public int deleteCodePropaedeutics(CodePropaedeutic propaedeutic) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			return deleteCodePropaedeutics(propaedeutic, database);
		} finally {
			if (database!=null)
				database.close();
		}
	}
	public int deleteCodePropaedeutics(CodePropaedeutic propaedeutic, OGraphDatabase database) throws Exception {
		ODocument from = clLoadDAO.loadCodeO(propaedeutic.getFromCode().getSystemKey(), propaedeutic.getFromCode().getSystemVersion(), propaedeutic.getFromCode().getCode(), database);
		ODocument to = clLoadDAO.loadCodeO(propaedeutic.getToCode().getSystemKey(), propaedeutic.getToCode().getSystemVersion(), propaedeutic.getToCode().getCode(), database);
		return deleteCodePropaedeutics(from, to, database);
	}
	public int deleteCodePropaedeutics(ODocument codeFromO, ODocument codeToO, OGraphDatabase database) throws Exception {
		Collection<ODocument> propaedeutics = clLinkLoadDAO.loadPropaedeuticsFromCodeToCodeO(codeFromO, codeToO, database);
		for (ODocument propaedeutic : propaedeutics)
			database.removeEdge(propaedeutic);
		return propaedeutics.size();
	}	
	
	//DELETE FROM CODE
	//relations
	public int deleteCodeRelationshipFromCode(Code codeFrom) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			return deleteCodeRelationshipFromCode(codeFrom, database);
		} finally {
			if (database!=null)
				database.close();
		}
	}
	public int deleteCodeRelationshipFromCode(Code codeFrom, OGraphDatabase database) throws Exception {
		ODocument from = clLoadDAO.loadCodeO(codeFrom.getSystemKey(), codeFrom.getSystemVersion(), codeFrom.getCode(), database);
		return deleteCodeRelationshipFromCode(from, database);
	}
	public int deleteCodeRelationshipFromCode(ODocument codeFromO, OGraphDatabase database) throws Exception {
		Collection<ODocument> relations = clLinkLoadDAO.loadRelationshipsFromCodeO(codeFromO, database);
		for (ODocument relation : relations)
			database.removeEdge(relation);
		return relations.size();
	}	
	//conversions
	public int deleteCodeConversionFromCode(Code codeFrom) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			return deleteCodeConversionFromCode(codeFrom, database);
		} finally {
			if (database!=null)
				database.close();
		}
	}
	public int deleteCodeConversionFromCode(Code codeFrom, OGraphDatabase database) throws Exception {
		ODocument from = clLoadDAO.loadCodeO(codeFrom.getSystemKey(), codeFrom.getSystemVersion(), codeFrom.getCode(), database);
 		return deleteCodeConversionFromCode(from, database);
	}
	public int deleteCodeConversionFromCode(ODocument codeFromO, OGraphDatabase database) throws Exception {
		Collection<ODocument> conversions = clLinkLoadDAO.loadConversionsFromCodeO(codeFromO, database);
		for (ODocument conversion : conversions) {
			((ODocument)conversion.field("conversionRule")).delete();
			database.removeEdge(conversion);
		}
		return conversions.size();
	}	
	//propaedeutics
	public int deleteCodePropaedeuticsFromCode(Code codeFrom) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			return deleteCodePropaedeuticsFromCode(codeFrom, database);
		} finally {
			if (database!=null)
				database.close();
		}
	}
	public int deleteCodePropaedeuticsFromCode(Code codeFrom, OGraphDatabase database) throws Exception {
		ODocument from = clLoadDAO.loadCodeO(codeFrom.getSystemKey(), codeFrom.getSystemVersion(), codeFrom.getCode(), database);
		return deleteCodePropaedeuticsFromCode(from, database);
	}
	public int deleteCodePropaedeuticsFromCode(ODocument codeFromO, OGraphDatabase database) throws Exception {
		Collection<ODocument> propaedeutics = clLinkLoadDAO.loadPropaedeuticsFromCodeO(codeFromO, database);
		for (ODocument propaedeutic : propaedeutics)
			database.removeEdge(propaedeutic);
		return propaedeutics.size();
	}	
	
	//DELETE FROM CL
	//relations
	public int deleteCodeRelationshipFromCL(CodeSystem system) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			return deleteCodeRelationshipFromCL(system, database);
		} finally {
			if (database!=null)
				database.close();
		}
	}
	public int deleteCodeRelationshipFromCL(CodeSystem system, OGraphDatabase database) throws Exception {
		ODocument from = clLoadDAO.loadSystemO(system.getSystem(), system.getVersion(), database);
		return deleteCodeRelationshipFromCL(from, database);
	}
	public int deleteCodeRelationshipFromCL(ODocument systemFromO, OGraphDatabase database) throws Exception {
		Collection<ODocument> relations = clLinkLoadDAO.loadRelationshipsFromCLO(systemFromO, database);
		for (ODocument relation : relations)
			database.removeEdge(relation);
		return relations.size();
	}	
	//conversions
	public int deleteCodeConversionFromCL(CodeSystem system) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			return deleteCodeConversionFromCL(system, database);
		} finally {
			if (database!=null)
				database.close();
		}
	}
	public int deleteCodeConversionFromCL(CodeSystem system, OGraphDatabase database) throws Exception {
		ODocument from = clLoadDAO.loadSystemO(system.getSystem(), system.getVersion(), database);
 		return deleteCodeConversionFromCL(from, database);
	}
	public int deleteCodeConversionFromCL(ODocument systemFromO, OGraphDatabase database) throws Exception {
		Collection<ODocument> conversions = clLinkLoadDAO.loadConversionsFromCLO(systemFromO, database);
		for (ODocument conversion : conversions) {
			((ODocument)conversion.field("conversionRule")).delete();
			database.removeEdge(conversion);
		}
		return conversions.size();
	}	
	//propaedeutics
	public int deleteCodePropaedeuticsFromCL(CodeSystem system) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			return deleteCodePropaedeuticsFromCL(system, database);
		} finally {
			if (database!=null)
				database.close();
		}
	}
	public int deleteCodePropaedeuticsFromCL(CodeSystem system, OGraphDatabase database) throws Exception {
		ODocument from = clLoadDAO.loadSystemO(system.getSystem(), system.getVersion(), database);
		return deleteCodePropaedeuticsFromCL(from, database);
	}
	public int deleteCodePropaedeuticsFromCL(ODocument systemFromO, OGraphDatabase database) throws Exception {
		Collection<ODocument> propaedeutics = clLinkLoadDAO.loadPropaedeuticsFromCLO(systemFromO, database);
		for (ODocument propaedeutic : propaedeutics)
			database.removeEdge(propaedeutic);
		return propaedeutics.size();
	}	
	
	//DELETE FROM CODE TO CL
	//relations
	public int deleteCodeRelationshipFromCodeToCL(Code fromCode, CodeSystem toSystem) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			return deleteCodeRelationshipFromCodeToCL(fromCode, toSystem, database);
		} finally {
			if (database!=null)
				database.close();
		}
	}
	public int deleteCodeRelationshipFromCodeToCL(Code fromCode, CodeSystem toSystem, OGraphDatabase database) throws Exception {
		ODocument from = clLoadDAO.loadCodeO(fromCode.getSystemKey(), fromCode.getSystemVersion(), fromCode.getCode(), database);
		ODocument to = clLoadDAO.loadSystemO(toSystem.getSystem(), toSystem.getVersion(), database);
		return deleteCodeRelationshipFromCodeToCL(from, to, database);
	}
	public int deleteCodeRelationshipFromCodeToCL(ODocument codeFromO, ODocument systemToO, OGraphDatabase database) throws Exception {
		Collection<ODocument> relations = clLinkLoadDAO.loadRelationshipsFromCodeToCLO(codeFromO, systemToO, database);
		for (ODocument relation : relations)
			database.removeEdge(relation);
		return relations.size();
	}	
	//conversions
	public int deleteCodeConversionFromCodeToCL(Code fromCode, CodeSystem toSystem) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			return deleteCodeConversionFromCodeToCL(fromCode, toSystem, database);
		} finally {
			if (database!=null)
				database.close();
		}
	}
	public int deleteCodeConversionFromCodeToCL(Code fromCode, CodeSystem toSystem, OGraphDatabase database) throws Exception {
		ODocument from = clLoadDAO.loadCodeO(fromCode.getSystemKey(), fromCode.getSystemVersion(), fromCode.getCode(), database);
		ODocument to = clLoadDAO.loadSystemO(toSystem.getSystem(), toSystem.getVersion(), database);
		return deleteCodeConversionFromCodeToCL(from, to, database);
	}
	public int deleteCodeConversionFromCodeToCL(ODocument codeFromO, ODocument systemToO, OGraphDatabase database) throws Exception {
		Collection<ODocument> conversions = clLinkLoadDAO.loadConversionsFromCodeToCLO(codeFromO, systemToO, database);
		for (ODocument conversion : conversions) {
			((ODocument)conversion.field("conversionRule")).delete();
			database.removeEdge(conversion);
		}
		return conversions.size();
	}	
	//propaedeutics
	public int deleteCodePropaedeuticsFromCodeToCL(Code fromCode, CodeSystem toSystem) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			return deleteCodePropaedeuticsFromCodeToCL(fromCode, toSystem, database);
		} finally {
			if (database!=null)
				database.close();
		}
	}
	public int deleteCodePropaedeuticsFromCodeToCL(Code fromCode, CodeSystem toSystem, OGraphDatabase database) throws Exception {
		ODocument from = clLoadDAO.loadCodeO(fromCode.getSystemKey(), fromCode.getSystemVersion(), fromCode.getCode(), database);
		ODocument to = clLoadDAO.loadSystemO(toSystem.getSystem(), toSystem.getVersion(), database);
		return deleteCodePropaedeuticsFromCodeToCL(from, to, database);
	}
	public int deleteCodePropaedeuticsFromCodeToCL(ODocument codeFromO, ODocument systemToO, OGraphDatabase database) throws Exception {
		Collection<ODocument> propaedeutics = clLinkLoadDAO.loadPropaedeuticsFromCodeToCLO(codeFromO, systemToO, database);
		for (ODocument propaedeutic : propaedeutics)
			database.removeEdge(propaedeutic);
		return propaedeutics.size();
	}	
	
	//DELETE FROM CL TO CL
	//relations
	public int deleteCodeRelationshipFromCLtoCL(CodeSystem fromSystem, CodeSystem toSystem) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			return deleteCodeRelationshipFromCLtoCL(fromSystem, toSystem, database);
		} finally {
			if (database!=null)
				database.close();
		}
	}
	public int deleteCodeRelationshipFromCLtoCL(CodeSystem fromSystem, CodeSystem toSystem, OGraphDatabase database) throws Exception {
		ODocument from = clLoadDAO.loadSystemO(fromSystem.getSystem(), fromSystem.getVersion(), database);
		ODocument to = clLoadDAO.loadSystemO(toSystem.getSystem(), toSystem.getVersion(), database);
		return deleteCodeRelationshipFromCLtoCL(from, to, database);
	}
	public int deleteCodeRelationshipFromCLtoCL(ODocument systemFromO, ODocument systemToO, OGraphDatabase database) throws Exception {
		Collection<ODocument> relations = clLinkLoadDAO.loadRelationshipsFromCLtoCLO(systemFromO, systemToO, database);
		for (ODocument relation : relations)
			database.removeEdge(relation);
		return relations.size();
	}	
	//conversions
	public int deleteCodeConversionFromCLtoCL(CodeSystem fromSystem, CodeSystem toSystem) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			return deleteCodeConversionFromCLtoCL(fromSystem, toSystem, database);
		} finally {
			if (database!=null)
				database.close();
		}
	}
	public int deleteCodeConversionFromCLtoCL(CodeSystem fromSystem, CodeSystem toSystem, OGraphDatabase database) throws Exception {
		ODocument from = clLoadDAO.loadSystemO(fromSystem.getSystem(), fromSystem.getVersion(), database);
		ODocument to = clLoadDAO.loadSystemO(toSystem.getSystem(), toSystem.getVersion(), database);
		return deleteCodeConversionFromCLtoCL(from, to, database);
	}
	public int deleteCodeConversionFromCLtoCL(ODocument systemFromO, ODocument systemToO, OGraphDatabase database) throws Exception {
		Collection<ODocument> conversions = clLinkLoadDAO.loadConversionsFromCLtoCLO(systemFromO, systemToO, database);
		for (ODocument conversion : conversions) {
			((ODocument)conversion.field("conversionRule")).delete();
			database.removeEdge(conversion);
		}
		return conversions.size();
	}	
	//propaedeutics
	public int deleteCodePropaedeuticsFromCLtoCL(CodeSystem fromSystem, CodeSystem toSystem) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			return deleteCodePropaedeuticsFromCLtoCL(fromSystem, toSystem, database);
		} finally {
			if (database!=null)
				database.close();
		}
	}
	public int deleteCodePropaedeuticsFromCLtoCL(CodeSystem fromSystem, CodeSystem toSystem, OGraphDatabase database) throws Exception {
		ODocument from = clLoadDAO.loadSystemO(fromSystem.getSystem(), fromSystem.getVersion(), database);
		ODocument to = clLoadDAO.loadSystemO(toSystem.getSystem(), toSystem.getVersion(), database);
		return deleteCodePropaedeuticsFromCLtoCL(from, to, database);
	}
	public int deleteCodePropaedeuticsFromCLtoCL(ODocument systemFromO, ODocument systemToO, OGraphDatabase database) throws Exception {
		Collection<ODocument> propaedeutics = clLinkLoadDAO.loadPropaedeuticsFromCLtoCLO(systemFromO, systemToO, database);
		for (ODocument propaedeutic : propaedeutics)
			database.removeEdge(propaedeutic);
		return propaedeutics.size();
	}	

	
	
	//STORE
	//relationship
	public void storeCodeRelationship(Collection<CodeRelationship> relations) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			storeCodeRelationship(relations, database);
		} finally {
			if (database!=null)
				database.close();
		}
	}
	public void storeCodeRelationship(Collection<CodeRelationship> relations, OGraphDatabase database) throws Exception {
		for (CodeRelationship relation : relations)
			storeCodeRelationship(relation, database);
	}
	public void storeCodeRelationship(CodeRelationship relation) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			storeCodeRelationship(relation, database);
		} finally {
			if (database!=null)
				database.close();
		}
	}
	public ODocument storeCodeRelationship(CodeRelationship relation, OGraphDatabase database) throws Exception {
		ODocument from = clLoadDAO.loadCodeO(relation.getFromCode().getSystemKey(), relation.getFromCode().getSystemVersion(), relation.getFromCode().getCode(), database);
		ODocument to = clLoadDAO.loadCodeO(relation.getToCode().getSystemKey(), relation.getToCode().getSystemVersion(), relation.getToCode().getCode(), database);
		return storeCodeRelationship(from, to, relation, database);
	}
	public ODocument storeCodeRelationship(ODocument from, ODocument to, CodeRelationship relation, OGraphDatabase database) throws Exception {
		ODocument rel = database.createEdge(from,to,"CSRelationship");
		rel.field("type", relation.getType().getCode());
		return rel.field(OGraphDatabase.LABEL, EdgesLabels.relation).save();
	}
	//conversion
	public void storeCodeConversion(Collection<CodeConversion> conversions) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			storeCodeConversion(conversions, database);
		} finally {
			if (database!=null)
				database.close();
		}
	}
	public void storeCodeConversion(Collection<CodeConversion> conversions, OGraphDatabase database) throws Exception {
		for (CodeConversion conversion : conversions)
			storeCodeConversion(conversion, database);
	}
	public void storeCodeConversion(CodeConversion conversion) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			storeCodeConversion(conversion, database);
		} finally {
			if (database!=null)
				database.close();
		}
	}
	public ODocument storeCodeConversion(CodeConversion conversion, OGraphDatabase database) throws Exception {
		ODocument from = clLoadDAO.loadCodeO(conversion.getFromCode().getSystemKey(), conversion.getFromCode().getSystemVersion(), conversion.getFromCode().getCode(), database);
		ODocument to = clLoadDAO.loadCodeO(conversion.getToCode().getSystemKey(), conversion.getToCode().getSystemVersion(), conversion.getToCode().getCode(), database);
		return storeCodeConversion(from, to, conversion, database);
	}
	public ODocument storeCodeConversion(ODocument from, ODocument to, CodeConversion conversion, OGraphDatabase database) throws Exception {
		ODocument conv = database.createEdge(from,to,"CSConversion");
		conv.field("conversionRule",dsdStoreDAO.storeValueOperator(conversion.getConversionRule(), database));
		return conv.field(OGraphDatabase.LABEL, EdgesLabels.conversion).save();
	}
	//propaedeutic
	public void storeCodePropaedeutic(Collection<CodePropaedeutic> propaedeutics) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			storeCodePropaedeutic(propaedeutics, database);
		} finally {
			if (database!=null)
				database.close();
		}
	}
	public void storeCodePropaedeutic(Collection<CodePropaedeutic> propaedeutics, OGraphDatabase database) throws Exception {
		for (CodePropaedeutic propaedeutic : propaedeutics)
			storeCodePropaedeutic(propaedeutic, database);
	}
	public void storeCodePropaedeutic(CodePropaedeutic propaedeutic) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			storeCodePropaedeutic(propaedeutic, database);
		} finally {
			if (database!=null)
				database.close();
		}
	}
	public ODocument storeCodePropaedeutic(CodePropaedeutic propaedeutic, OGraphDatabase database) throws Exception {
		ODocument from = clLoadDAO.loadCodeO(propaedeutic.getFromCode().getSystemKey(), propaedeutic.getFromCode().getSystemVersion(), propaedeutic.getFromCode().getCode(), database);
		ODocument to = clLoadDAO.loadCodeO(propaedeutic.getToCode().getSystemKey(), propaedeutic.getToCode().getSystemVersion(), propaedeutic.getToCode().getCode(), database);
		return storeCodePropaedeutic(from, to, propaedeutic, database);
	}
	public ODocument storeCodePropaedeutic(ODocument from, ODocument to, CodePropaedeutic propaedeutic, OGraphDatabase database) throws Exception {
		ODocument propae = database.createEdge(from,to,"CSPropaedeutic");
		if (propaedeutic.getContextSystem()!=null)
			propae.field("contextSystem",dsdLoadDAO.loadContextSystem(propaedeutic.getContextSystem().getName(), database));
		return propae.field(OGraphDatabase.LABEL, EdgesLabels.propaedeutic).save();
	}


}

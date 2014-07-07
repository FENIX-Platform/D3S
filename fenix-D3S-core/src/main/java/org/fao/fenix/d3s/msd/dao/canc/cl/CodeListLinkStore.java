package org.fao.fenix.d3s.msd.dao.canc.cl;

import java.util.Collection;

import org.fao.fenix.commons.msd.dto.cl.*;
import org.fao.fenix.commons.msd.dto.full.cl.*;
import org.fao.fenix.commons.msd.dto.templates.canc.cl.*;
import org.fao.fenix.d3s.msd.dao.canc.EdgesLabels;
import org.fao.fenix.d3s.msd.dao.canc.common.CommonsStore;
import org.fao.fenix.d3s.msd.dao.canc.dsd.DSDLoad;
import org.fao.fenix.d3s.msd.dao.canc.dsd.DSDStore;
import org.fao.fenix.d3s.server.tools.orient.OrientDao;

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
		ODocument from = clLoadDAO.loadCodeO(conversion.getFromCode().getSystemKey(), conversion.getFromCode().getSystemVersion(), conversion.getFromCode().getCode());
		ODocument to = clLoadDAO.loadCodeO(conversion.getToCode().getSystemKey(), conversion.getToCode().getSystemVersion(), conversion.getToCode().getCode());
		Collection<ODocument> conversions = clLinkLoadDAO.loadConversionsFromCodeToCodeO(from, to);
		for (ODocument conversionO : conversions) {
			((ODocument)conversionO.field("conversionRule")).delete();
			conversionO.field("conversionRule",dsdStoreDAO.storeValueOperator(conversion.getConversionRule()));
		}
		return conversions.size();
	}

	
	//DELETE
	//relations
	public int deleteCodeRelationship(CodeRelationship relation) throws Exception {
		ODocument from = clLoadDAO.loadCodeO(relation.getFromCode().getSystemKey(), relation.getFromCode().getSystemVersion(), relation.getFromCode().getCode());
		ODocument to = clLoadDAO.loadCodeO(relation.getToCode().getSystemKey(), relation.getToCode().getSystemVersion(), relation.getToCode().getCode());
		return deleteCodeRelationship(from, to);
	}
	public int deleteCodeRelationship(ODocument codeFromO, ODocument codeToO) throws Exception {
		Collection<ODocument> relations = clLinkLoadDAO.loadRelationshipsFromCodeToCodeO(codeFromO, codeToO);
		for (ODocument relation : relations)
			getConnection().removeEdge(relation);
		return relations.size();
	}	
	//conversions
	public int deleteCodeConversion(CodeConversion conversion) throws Exception {
		ODocument from = clLoadDAO.loadCodeO(conversion.getFromCode().getSystemKey(), conversion.getFromCode().getSystemVersion(), conversion.getFromCode().getCode());
		ODocument to = clLoadDAO.loadCodeO(conversion.getToCode().getSystemKey(), conversion.getToCode().getSystemVersion(), conversion.getToCode().getCode());
		return deleteCodeConversion(from, to);
	}
	public int deleteCodeConversion(ODocument codeFromO, ODocument codeToO) throws Exception {
		Collection<ODocument> conversions = clLinkLoadDAO.loadConversionsFromCodeToCodeO(codeFromO, codeToO);
		for (ODocument conversion : conversions) {
			((ODocument)conversion.field("conversionRule")).delete();
			getConnection().removeEdge(conversion);
		}
		return conversions.size();
	}	
	//propaedeutics
	public int deleteCodePropaedeutics(CodePropaedeutic propaedeutic) throws Exception {
		ODocument from = clLoadDAO.loadCodeO(propaedeutic.getFromCode().getSystemKey(), propaedeutic.getFromCode().getSystemVersion(), propaedeutic.getFromCode().getCode());
		ODocument to = clLoadDAO.loadCodeO(propaedeutic.getToCode().getSystemKey(), propaedeutic.getToCode().getSystemVersion(), propaedeutic.getToCode().getCode());
		return deleteCodePropaedeutics(from, to);
	}
	public int deleteCodePropaedeutics(ODocument codeFromO, ODocument codeToO) throws Exception {
		Collection<ODocument> propaedeutics = clLinkLoadDAO.loadPropaedeuticsFromCodeToCodeO(codeFromO, codeToO);
		for (ODocument propaedeutic : propaedeutics)
			getConnection().removeEdge(propaedeutic);
		return propaedeutics.size();
	}	
	
	//DELETE FROM CODE
	//relations
	public int deleteCodeRelationshipFromCode(Code codeFrom) throws Exception {
		ODocument from = clLoadDAO.loadCodeO(codeFrom.getSystemKey(), codeFrom.getSystemVersion(), codeFrom.getCode());
		return deleteCodeRelationshipFromCode(from);
	}
	public int deleteCodeRelationshipFromCode(ODocument codeFromO) throws Exception {
		Collection<ODocument> relations = clLinkLoadDAO.loadRelationshipsFromCodeO(codeFromO);
		for (ODocument relation : relations)
			getConnection().removeEdge(relation);
		return relations.size();
	}	
	//conversions
	public int deleteCodeConversionFromCode(Code codeFrom) throws Exception {
		ODocument from = clLoadDAO.loadCodeO(codeFrom.getSystemKey(), codeFrom.getSystemVersion(), codeFrom.getCode());
 		return deleteCodeConversionFromCode(from);
	}
	public int deleteCodeConversionFromCode(ODocument codeFromO) throws Exception {
		Collection<ODocument> conversions = clLinkLoadDAO.loadConversionsFromCodeO(codeFromO);
		for (ODocument conversion : conversions) {
			((ODocument)conversion.field("conversionRule")).delete();
			getConnection().removeEdge(conversion);
		}
		return conversions.size();
	}	
	//propaedeutics
	public int deleteCodePropaedeuticsFromCode(Code codeFrom) throws Exception {
		ODocument from = clLoadDAO.loadCodeO(codeFrom.getSystemKey(), codeFrom.getSystemVersion(), codeFrom.getCode());
		return deleteCodePropaedeuticsFromCode(from);
	}
	public int deleteCodePropaedeuticsFromCode(ODocument codeFromO) throws Exception {
		Collection<ODocument> propaedeutics = clLinkLoadDAO.loadPropaedeuticsFromCodeO(codeFromO);
		for (ODocument propaedeutic : propaedeutics)
			getConnection().removeEdge(propaedeutic);
		return propaedeutics.size();
	}	
	
	//DELETE FROM CL
	//relations
	public int deleteCodeRelationshipFromCL(CodeSystem system) throws Exception {
		ODocument from = clLoadDAO.loadSystemO(system.getSystem(), system.getVersion());
		return deleteCodeRelationshipFromCL(from);
	}
	public int deleteCodeRelationshipFromCL(ODocument systemFromO) throws Exception {
		Collection<ODocument> relations = clLinkLoadDAO.loadRelationshipsFromCLO(systemFromO);
		for (ODocument relation : relations)
			getConnection().removeEdge(relation);
		return relations.size();
	}	
	//conversions
	public int deleteCodeConversionFromCL(CodeSystem system) throws Exception {
		ODocument from = clLoadDAO.loadSystemO(system.getSystem(), system.getVersion());
 		return deleteCodeConversionFromCL(from);
	}
	public int deleteCodeConversionFromCL(ODocument systemFromO) throws Exception {
		Collection<ODocument> conversions = clLinkLoadDAO.loadConversionsFromCLO(systemFromO);
		for (ODocument conversion : conversions) {
			((ODocument)conversion.field("conversionRule")).delete();
			getConnection().removeEdge(conversion);
		}
		return conversions.size();
	}	
	//propaedeutics
	public int deleteCodePropaedeuticsFromCL(CodeSystem system) throws Exception {
		ODocument from = clLoadDAO.loadSystemO(system.getSystem(), system.getVersion());
		return deleteCodePropaedeuticsFromCL(from);
	}
	public int deleteCodePropaedeuticsFromCL(ODocument systemFromO) throws Exception {
		Collection<ODocument> propaedeutics = clLinkLoadDAO.loadPropaedeuticsFromCLO(systemFromO);
		for (ODocument propaedeutic : propaedeutics)
			getConnection().removeEdge(propaedeutic);
		return propaedeutics.size();
	}	
	
	//DELETE FROM CODE TO CL
	//relations
	public int deleteCodeRelationshipFromCodeToCL(Code fromCode, CodeSystem toSystem) throws Exception {
		ODocument from = clLoadDAO.loadCodeO(fromCode.getSystemKey(), fromCode.getSystemVersion(), fromCode.getCode());
		ODocument to = clLoadDAO.loadSystemO(toSystem.getSystem(), toSystem.getVersion());
		return deleteCodeRelationshipFromCodeToCL(from, to);
	}
	public int deleteCodeRelationshipFromCodeToCL(ODocument codeFromO, ODocument systemToO) throws Exception {
		Collection<ODocument> relations = clLinkLoadDAO.loadRelationshipsFromCodeToCLO(codeFromO, systemToO);
		for (ODocument relation : relations)
			getConnection().removeEdge(relation);
		return relations.size();
	}	
	//conversions
	public int deleteCodeConversionFromCodeToCL(Code fromCode, CodeSystem toSystem) throws Exception {
		ODocument from = clLoadDAO.loadCodeO(fromCode.getSystemKey(), fromCode.getSystemVersion(), fromCode.getCode());
		ODocument to = clLoadDAO.loadSystemO(toSystem.getSystem(), toSystem.getVersion());
		return deleteCodeConversionFromCodeToCL(from, to);
	}
	public int deleteCodeConversionFromCodeToCL(ODocument codeFromO, ODocument systemToO) throws Exception {
		Collection<ODocument> conversions = clLinkLoadDAO.loadConversionsFromCodeToCLO(codeFromO, systemToO);
		for (ODocument conversion : conversions) {
			((ODocument)conversion.field("conversionRule")).delete();
			getConnection().removeEdge(conversion);
		}
		return conversions.size();
	}	
	//propaedeutics
	public int deleteCodePropaedeuticsFromCodeToCL(Code fromCode, CodeSystem toSystem) throws Exception {
		ODocument from = clLoadDAO.loadCodeO(fromCode.getSystemKey(), fromCode.getSystemVersion(), fromCode.getCode());
		ODocument to = clLoadDAO.loadSystemO(toSystem.getSystem(), toSystem.getVersion());
		return deleteCodePropaedeuticsFromCodeToCL(from, to);
	}
	public int deleteCodePropaedeuticsFromCodeToCL(ODocument codeFromO, ODocument systemToO) throws Exception {
		Collection<ODocument> propaedeutics = clLinkLoadDAO.loadPropaedeuticsFromCodeToCLO(codeFromO, systemToO);
		for (ODocument propaedeutic : propaedeutics)
			getConnection().removeEdge(propaedeutic);
		return propaedeutics.size();
	}	
	
	//DELETE FROM CL TO CL
	//relations
	public int deleteCodeRelationshipFromCLtoCL(CodeSystem fromSystem, CodeSystem toSystem) throws Exception {
		ODocument from = clLoadDAO.loadSystemO(fromSystem.getSystem(), fromSystem.getVersion());
		ODocument to = clLoadDAO.loadSystemO(toSystem.getSystem(), toSystem.getVersion());
		return deleteCodeRelationshipFromCLtoCL(from, to);
	}
	public int deleteCodeRelationshipFromCLtoCL(ODocument systemFromO, ODocument systemToO) throws Exception {
		Collection<ODocument> relations = clLinkLoadDAO.loadRelationshipsFromCLtoCLO(systemFromO, systemToO);
		for (ODocument relation : relations)
			getConnection().removeEdge(relation);
		return relations.size();
	}	
	//conversions
	public int deleteCodeConversionFromCLtoCL(CodeSystem fromSystem, CodeSystem toSystem) throws Exception {
		ODocument from = clLoadDAO.loadSystemO(fromSystem.getSystem(), fromSystem.getVersion());
		ODocument to = clLoadDAO.loadSystemO(toSystem.getSystem(), toSystem.getVersion());
		return deleteCodeConversionFromCLtoCL(from, to);
	}
	public int deleteCodeConversionFromCLtoCL(ODocument systemFromO, ODocument systemToO) throws Exception {
		Collection<ODocument> conversions = clLinkLoadDAO.loadConversionsFromCLtoCLO(systemFromO, systemToO);
		for (ODocument conversion : conversions) {
			((ODocument)conversion.field("conversionRule")).delete();
			getConnection().removeEdge(conversion);
		}
		return conversions.size();
	}	
	//propaedeutics
	public int deleteCodePropaedeuticsFromCLtoCL(CodeSystem fromSystem, CodeSystem toSystem) throws Exception {
		ODocument from = clLoadDAO.loadSystemO(fromSystem.getSystem(), fromSystem.getVersion());
		ODocument to = clLoadDAO.loadSystemO(toSystem.getSystem(), toSystem.getVersion());
		return deleteCodePropaedeuticsFromCLtoCL(from, to);
	}
	public int deleteCodePropaedeuticsFromCLtoCL(ODocument systemFromO, ODocument systemToO) throws Exception {
		Collection<ODocument> propaedeutics = clLinkLoadDAO.loadPropaedeuticsFromCLtoCLO(systemFromO, systemToO);
		for (ODocument propaedeutic : propaedeutics)
			getConnection().removeEdge(propaedeutic);
		return propaedeutics.size();
	}	

	
	
	//STORE
	//relationship
	public void storeCodeRelationship(Collection<CodeRelationship> relations) throws Exception {
		for (CodeRelationship relation : relations)
			storeCodeRelationship(relation);
	}
	public ODocument storeCodeRelationship(CodeRelationship relation) throws Exception {
		ODocument from = clLoadDAO.loadCodeO(relation.getFromCode().getSystemKey(), relation.getFromCode().getSystemVersion(), relation.getFromCode().getCode());
		ODocument to = clLoadDAO.loadCodeO(relation.getToCode().getSystemKey(), relation.getToCode().getSystemVersion(), relation.getToCode().getCode());
		return storeCodeRelationship(from, to, relation);
	}
	public ODocument storeCodeRelationship(ODocument from, ODocument to, CodeRelationship relation) throws Exception {
		ODocument rel = getConnection().createEdge(from,to,"CSRelationship");
		rel.field("type", relation.getType().getCode());
		return rel.field(OGraphDatabase.LABEL, EdgesLabels.relation).save();
	}
	//conversion
	public void storeCodeConversion(Collection<CodeConversion> conversions) throws Exception {
		for (CodeConversion conversion : conversions)
			storeCodeConversion(conversion);
	}
	public ODocument storeCodeConversion(CodeConversion conversion) throws Exception {
		ODocument from = clLoadDAO.loadCodeO(conversion.getFromCode().getSystemKey(), conversion.getFromCode().getSystemVersion(), conversion.getFromCode().getCode());
		ODocument to = clLoadDAO.loadCodeO(conversion.getToCode().getSystemKey(), conversion.getToCode().getSystemVersion(), conversion.getToCode().getCode());
		return storeCodeConversion(from, to, conversion);
	}
	public ODocument storeCodeConversion(ODocument from, ODocument to, CodeConversion conversion) throws Exception {
		ODocument conv = getConnection().createEdge(from,to,"CSConversion");
		conv.field("conversionRule",dsdStoreDAO.storeValueOperator(conversion.getConversionRule()));
		return conv.field(OGraphDatabase.LABEL, EdgesLabels.conversion).save();
	}
	//propaedeutic
	public void storeCodePropaedeutic(Collection<CodePropaedeutic> propaedeutics) throws Exception {
		for (CodePropaedeutic propaedeutic : propaedeutics)
			storeCodePropaedeutic(propaedeutic);
	}
	public ODocument storeCodePropaedeutic(CodePropaedeutic propaedeutic) throws Exception {
		ODocument from = clLoadDAO.loadCodeO(propaedeutic.getFromCode().getSystemKey(), propaedeutic.getFromCode().getSystemVersion(), propaedeutic.getFromCode().getCode());
		ODocument to = clLoadDAO.loadCodeO(propaedeutic.getToCode().getSystemKey(), propaedeutic.getToCode().getSystemVersion(), propaedeutic.getToCode().getCode());
		return storeCodePropaedeutic(from, to, propaedeutic);
	}
	public ODocument storeCodePropaedeutic(ODocument from, ODocument to, CodePropaedeutic propaedeutic) throws Exception {
		ODocument propae = getConnection().createEdge(from,to,"CSPropaedeutic");
		if (propaedeutic.getContextSystem()!=null)
			propae.field("contextSystem",dsdLoadDAO.loadContextSystem(propaedeutic.getContextSystem().getName()));
		return propae.field(OGraphDatabase.LABEL, EdgesLabels.propaedeutic).save();
	}


}

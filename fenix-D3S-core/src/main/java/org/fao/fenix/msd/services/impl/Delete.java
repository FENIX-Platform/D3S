package org.fao.fenix.msd.services.impl;

import org.fao.fenix.msd.dao.cl.CodeListLinkStore;
import org.fao.fenix.msd.dao.cl.CodeListStore;
import org.fao.fenix.msd.dao.common.CommonsStore;
import org.fao.fenix.msd.dao.dm.DMStore;
import org.fao.fenix.msd.dao.dsd.DSDStore;
import org.fao.fenix.msd.dto.cl.Code;
import org.fao.fenix.msd.dto.cl.CodeConversion;
import org.fao.fenix.msd.dto.cl.CodePropaedeutic;
import org.fao.fenix.msd.dto.cl.CodeRelationship;
import org.fao.fenix.msd.dto.cl.CodeSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Delete {
	@Autowired private CommonsStore cmStoreDAO;
	@Autowired private DSDStore dsdStoreDAO;
	@Autowired private DMStore dmStoreDAO;
	@Autowired private CodeListStore clStoreDAO;
	@Autowired private CodeListLinkStore clLinkStoreDAO;
	
	public int deleteContactIdentity(String contactID) throws Exception {
		return cmStoreDAO.deleteContatcIdentity(contactID);
	}
	
	public int deleteCodeList(String system, String version) throws Exception {
		return clStoreDAO.deleteCodeList(system, version);
	}
	
	public int deleteDatasetMetadata(String uid) throws Exception {
		return dmStoreDAO.deleteDatasetMetadata(uid);
	}

	public int deleteMetadataStructure(String uid) throws Exception {
		return dmStoreDAO.deleteMetadataStructure(uid);
	}

	public int deleteDimension(String name) throws Exception {
		return dsdStoreDAO.deleteDimension(name);
	}
	public int deleteContextSystem(String name) throws Exception {
		return dsdStoreDAO.deleteContext(name);
	}
	public int deleteKeyword(String keyword) throws Exception {
		return clStoreDAO.deleteKeyword(keyword);
	}

	public int deleteRelationships(CodeRelationship relation) throws Exception {
		return clLinkStoreDAO.deleteCodeRelationship(relation);
	}
	public int deleteRelationships(CodeSystem system) throws Exception {
		return clLinkStoreDAO.deleteCodeRelationshipFromCL(system);
	}
	public int deleteRelationships(CodeSystem fromSystem,CodeSystem toSystem) throws Exception {
		return clLinkStoreDAO.deleteCodeRelationshipFromCLtoCL(fromSystem, toSystem);
	}
	public int deleteRelationships(Code codeFrom) throws Exception {
		return clLinkStoreDAO.deleteCodeRelationshipFromCode(codeFrom);
	}
	public int deleteRelationships(Code fromCode,CodeSystem toSystem) throws Exception {
		return clLinkStoreDAO.deleteCodeRelationshipFromCodeToCL(fromCode, toSystem);
	}

	public int deleteConversions(CodeConversion conversion) throws Exception {
		return clLinkStoreDAO.deleteCodeConversion(conversion);
	}
	public int deleteConversions(CodeSystem system) throws Exception {
		return clLinkStoreDAO.deleteCodeConversionFromCL(system);
	}
	public int deleteConversions(CodeSystem fromSystem,CodeSystem toSystem) throws Exception {
		return clLinkStoreDAO.deleteCodeConversionFromCLtoCL(fromSystem, toSystem);
	}
	public int deleteConversions(Code codeFrom) throws Exception {
		return clLinkStoreDAO.deleteCodeConversionFromCode(codeFrom);
	}
	public int deleteConversions(Code fromCode,CodeSystem toSystem) throws Exception {
		return clLinkStoreDAO.deleteCodeConversionFromCodeToCL(fromCode, toSystem);
	}

	public int deletePropaedeutics(CodePropaedeutic relation) throws Exception {
		return clLinkStoreDAO.deleteCodePropaedeutics(relation);
	}
	public int deletePropaedeutics(CodeSystem system) throws Exception {
		return clLinkStoreDAO.deleteCodePropaedeuticsFromCL(system);
	}
	public int deletePropaedeutics(CodeSystem fromSystem,CodeSystem toSystem) throws Exception {
		return clLinkStoreDAO.deleteCodePropaedeuticsFromCLtoCL(fromSystem, toSystem);
	}
	public int deletePropaedeutics(Code codeFrom) throws Exception {
		return clLinkStoreDAO.deleteCodePropaedeuticsFromCode(codeFrom);
	}
	public int deletePropaedeutics(Code fromCode,CodeSystem toSystem) throws Exception {
		return clLinkStoreDAO.deleteCodePropaedeuticsFromCodeToCL(fromCode, toSystem);
	}
}

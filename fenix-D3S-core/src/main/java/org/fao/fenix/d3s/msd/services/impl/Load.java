package org.fao.fenix.d3s.msd.services.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.fao.fenix.commons.msd.dto.cl.type.DuplicateCodeException;
import org.fao.fenix.d3s.msd.dao.cl.CodeListConverter;
import org.fao.fenix.d3s.msd.dao.cl.CodeListLinkLoad;
import org.fao.fenix.d3s.msd.dao.cl.CodeListLoad;
import org.fao.fenix.d3s.msd.dao.common.CommonsLoad;
import org.fao.fenix.d3s.msd.dao.dm.DMLoad;
import org.fao.fenix.d3s.msd.dao.dsd.DSDLoad;
import org.fao.fenix.d3s.server.tools.SupportedLanguages;
import org.fao.fenix.commons.msd.dto.cl.Code;
import org.fao.fenix.commons.msd.dto.cl.CodeConversion;
import org.fao.fenix.commons.msd.dto.cl.CodePropaedeutic;
import org.fao.fenix.commons.msd.dto.cl.CodeRelationship;
import org.fao.fenix.commons.msd.dto.cl.CodeSystem;
import org.fao.fenix.commons.msd.dto.cl.type.CodeRelationshipType;
import org.fao.fenix.commons.msd.dto.common.ContactIdentity;
import org.fao.fenix.commons.msd.dto.common.Publication;
import org.fao.fenix.commons.msd.dto.dm.DM;
import org.fao.fenix.commons.msd.dto.dm.DMMeta;
import org.fao.fenix.commons.msd.dto.dsd.DSDContextSystem;
import org.fao.fenix.commons.msd.dto.dsd.DSDDatasource;
import org.fao.fenix.commons.msd.dto.dsd.DSDDimension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Load {
	@Autowired private CommonsLoad cmLoadDAO;
	@Autowired private DSDLoad dsdLoadDAO;
	@Autowired private DMLoad dmLoadDAO;
	@Autowired private CodeListLoad clLoadDAO;
	@Autowired private CodeListLinkLoad clLinkLoadDAO;
	
	//CONTACT IDENTITY
	public ContactIdentity getContactIdentity(String contactID) throws Exception { return cmLoadDAO.loadContactIdentity(contactID); }
	public Collection<ContactIdentity> getContactIdentities(String institution, String department, String name, String surname, String context) throws Exception {
		return cmLoadDAO.loadContactIdentities(institution, department, name, surname, context);
	}
	public Collection<ContactIdentity> getContactIdentities(String text) throws Exception {
		return cmLoadDAO.loadContactIdentitiesFulltext(text);
	}

    //PUBLICATION
    public Publication getPublication(String publicationID) throws Exception { return cmLoadDAO.loadPublication(publicationID); }

    //CODE LIST
	public Collection<CodeSystem> getCodeList() throws Exception { return getCodeList(true); }
	public Collection<CodeSystem> getCodeList(boolean all) throws Exception {
		return clLoadDAO.loadSystem(all);
	}
	public CodeSystem getCodeList(String system, String version) throws Exception { return getCodeList(system, version, true); }
	public CodeSystem getCodeList(String system, String version, boolean all) throws Exception {
		return clLoadDAO.loadSystem(system, version, all);
	}
	
	public Code loadCode(String system,String version, String code, int levels) throws Exception
	{
		return clLoadDAO.loadCode(system,version,code,levels);
	}
    public Collection<Code> loadCodes(String text, SupportedLanguages language) throws Exception {
        if (language==null)
            language = SupportedLanguages.english;
        if (text == null)
            return null;
        Collection<Code> result = clLoadDAO.loadCodesByTitle(text,language);

        return result!=null && result.size()>0 ? result : null;
    }
    public Collection<CodeSystem> loadCodeLists(String text, SupportedLanguages language) throws Exception {
        if (language==null)
            language = SupportedLanguages.english;
        if (text == null)
            return null;
        Collection<CodeSystem> result = clLoadDAO.loadCodeSystemsByTitle(text,language);

        return result!=null && result.size()>0 ? result : null;
    }


	public Collection<Code> loadCodes(String system,String version, Collection<String> codes, int levels) throws Exception
	{
		return clLoadDAO.loadCodes(system,version,codes,levels);
	}
	public Collection<Code> loadCodesLevel(String system, String version, int level) throws Exception
	{
		return clLoadDAO.loadCodeLevel(system, version, level);
	}
	
	public Map<String, Code> getCodesMap(String system, String version, String code, int levels) throws Exception {
		Map<String, Code> toRet = new HashMap<String, Code>();
		if (code != null) {
			Code rootCode = loadCode(system, version, code, levels);
			if (rootCode == null)
				return toRet;
			flatCodeBuilder(toRet, rootCode, levels);
		} else
            for (Code c : clLoadDAO.loadSystem(system, version, false).getRootCodes())
                flatCodeBuilder(toRet, c, levels);

        return toRet;
	}
	public Map<String, Code> getCodesMap(String system, String version, Collection<String> codesToGet, Integer levels) throws Exception {
		Map<String, Code> toRet = new HashMap<String, Code>();
		if (codesToGet == null)
			return toRet;
		Collection<Code> codes= clLoadDAO.loadCodes(system, version, codesToGet,levels);
		for (Code c:codes)
			flatCodeBuilder(toRet, c, levels);
		return toRet;
	}
	// Recursively builds the Map of codes to return, navigating through all the children
	private void flatCodeBuilder(Map<String, Code> codes, Code toAdd, int levels) {
		if (toAdd == null)
			return;
		codes.put(toAdd.getCode(), toAdd);
		if (toAdd.getChilds() != null && (levels == CodeListConverter.ALL_LEVELS || levels-->0))
			for (Code child : toAdd.getChilds())
				flatCodeBuilder(codes, child, levels);
        try {
            toAdd.setChilds(null);
        } catch (DuplicateCodeException e) { }
    }
	
	//METADATA
	public Collection<DM> getDatasetMetadata() throws Exception { return getDatasetMetadata(true); }
	public Collection<DM> getDatasetMetadata(boolean all) throws Exception {
		return dmLoadDAO.loadDatasetMetadata(all);
	}
	public DM getDatasetMetadata(String uid) throws Exception { return (DM) getDatasetMetadata(uid, true); }
	public DM getDatasetMetadata(String uid, boolean all) throws Exception {
        return dmLoadDAO.loadDatasetMetadata(uid, all);
	}
	public Collection<DM> getDatasetMetadataLike(String uid) throws Exception { System.out.println("L1");return getDatasetMetadataLike(uid, true); }
	public Collection<DM> getDatasetMetadataLike(String uid, boolean all) throws Exception {
		return dmLoadDAO.loadDatasetMetadataLike(uid, all);
	}
	public Collection<DM> getDatasetMetadata(String[] uids, boolean all) throws Exception {
		return dmLoadDAO.loadDatasetMetadata(uids, all);
	}
	public Collection<String> getDatasetMetadataEcho(String[] uids) throws Exception {
		return dmLoadDAO.loadEcho(uids);
	}

	//REGISTRY
	public Collection<String> getKeywords() throws Exception {
		return clLoadDAO.loadKeyword();
	}
	public Collection<DSDDatasource> getDatasources() throws Exception {
		return dsdLoadDAO.loadDatasource();
	}
	public Collection<DSDDimension> getDimensions() throws Exception {
		return dsdLoadDAO.loadDimension();
	}
	public Collection<DSDContextSystem> getContextSystems() throws Exception {
		return dsdLoadDAO.loadContextSystem();
	}
	
	//LINKS
	//relation
	public Collection<CodeRelationship> getRelationships(CodeSystem system) throws Exception {
		return clLinkLoadDAO.loadRelationshipsFromCL(system);
	}
	public Collection<CodeRelationship> getRelationships(CodeSystem systemFrom,CodeSystem systemTo) throws Exception {
		return clLinkLoadDAO.loadRelationshipsFromCLtoCL(systemFrom, systemTo);
	}
	public Collection<CodeRelationship> getRelationships(CodeSystem system, CodeRelationshipType type) throws Exception {
		return clLinkLoadDAO.loadRelationshipsFromCLType(system, type);
	}
	public Collection<CodeRelationship> getRelationships(Code code) throws Exception {
		return clLinkLoadDAO.loadRelationshipsFromCode(code);
	}
	public Collection<CodeRelationship> getRelationships(Code code, CodeSystem systemTo) throws Exception {
		return clLinkLoadDAO.loadRelationshipsFromCodeToCL(code, systemTo);
	}
	public Collection<CodeRelationship> getRelationships(Code codeFrom, Code codeTo) throws Exception {
		return clLinkLoadDAO.loadRelationshipsFromCodeToCode(codeFrom, codeTo);
	}
	public Collection<CodeRelationship> getRelationships(Code code, CodeRelationshipType type) throws Exception {
		return clLinkLoadDAO.loadRelationshipsFromCodeType(code, type);
	}
	//conversion
	public Collection<CodeConversion> getConversions(CodeSystem system) throws Exception {
		return clLinkLoadDAO.loadConversionsFromCL(system);
	}
	public Collection<CodeConversion> getConversions(CodeSystem systemFrom,CodeSystem systemTo) throws Exception {
		return clLinkLoadDAO.loadConversionsFromCLtoCL(systemFrom, systemTo);
	}
	public Collection<CodeConversion> getConversions(Code code) throws Exception {
		return clLinkLoadDAO.loadConversionsFromCode(code);
	}
	public Collection<CodeConversion> getConversions(Code code, CodeSystem systemTo) throws Exception {
		return clLinkLoadDAO.loadConversionsFromCodeToCL(code, systemTo);
	}
	public Collection<CodeConversion> getConversions(Code codeFrom, Code codeTo) throws Exception {
		return clLinkLoadDAO.loadConversionsFromCodeToCode(codeFrom, codeTo);
	}
	//propaedeutic
	public Collection<CodePropaedeutic> getPropaedeutics(CodeSystem system) throws Exception {
		return clLinkLoadDAO.loadPropaedeuticsFromCL(system);
	}
	public Collection<CodePropaedeutic> getPropaedeutics(CodeSystem systemFrom,CodeSystem systemTo) throws Exception {
		return clLinkLoadDAO.loadPropaedeuticsFromCLtoCL(systemFrom, systemTo);
	}
	public Collection<CodePropaedeutic> getPropaedeutics(Code code) throws Exception {
		return clLinkLoadDAO.loadPropaedeuticsFromCode(code);
	}
	public Collection<CodePropaedeutic> getPropaedeutics(Code code, CodeSystem systemTo) throws Exception {
		return clLinkLoadDAO.loadPropaedeuticsFromCodeToCL(code, systemTo);
	}

	public Collection<CodePropaedeutic> getPropaedeutics(Code codeFrom, Code codeTo) throws Exception {
		return clLinkLoadDAO.loadPropaedeuticsFromCodeToCode(codeFrom, codeTo);
	}



    //METADATA STRUCTURE
    public DMMeta getMetadataStructure(String uid) throws Exception { return getMetadataStructure(uid, false); }
    public DMMeta getMetadataStructure(String uid, boolean all) throws Exception {
        return dmLoadDAO.loadMetadataStructure(uid,all);
    }

}

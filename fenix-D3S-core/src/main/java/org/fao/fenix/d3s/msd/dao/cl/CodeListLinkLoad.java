package org.fao.fenix.d3s.msd.dao.cl;

import java.util.Collection;
import java.util.LinkedList;

import org.fao.fenix.commons.msd.dto.full.cl.Code;
import org.fao.fenix.commons.msd.dto.full.cl.CodeConversion;
import org.fao.fenix.commons.msd.dto.full.cl.CodeLink;
import org.fao.fenix.commons.msd.dto.full.cl.CodePropaedeutic;
import org.fao.fenix.commons.msd.dto.full.cl.CodeRelationship;
import org.fao.fenix.commons.msd.dto.full.cl.CodeSystem;
import org.fao.fenix.commons.msd.dto.type.cl.CodeRelationshipType;
import org.fao.fenix.d3s.server.tools.orient.OrientDao;

import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

import javax.inject.Inject;

public class CodeListLinkLoad extends OrientDao {
	
	@Inject private CodeListConverter converter;
	@Inject private CodeListLoad clLoadDao;
	
	private static OSQLSynchQuery<ODocument> queryLoadHierarchy_FromCL = new OSQLSynchQuery<ODocument>("select from CSHierarchy where out.system = ?");
	private static OSQLSynchQuery<ODocument> queryLoadPropaedeutic_FromCL = new OSQLSynchQuery<ODocument>("select from CSPropaedeutic where out.system = ?");
	private static OSQLSynchQuery<ODocument> queryLoadRelationship_FromCL = new OSQLSynchQuery<ODocument>("select from CSRelationship where out.system = ?");
	private static OSQLSynchQuery<ODocument> queryLoadConversion_FromCL = new OSQLSynchQuery<ODocument>("select from CSConversion where out.system = ?");
	
	private static OSQLSynchQuery<ODocument> queryLoadPropaedeutic_ToCL = new OSQLSynchQuery<ODocument>("select from CSPropaedeutic where in.system = ?");
	private static OSQLSynchQuery<ODocument> queryLoadRelationship_ToCL = new OSQLSynchQuery<ODocument>("select from CSRelationship where in.system = ?");
	private static OSQLSynchQuery<ODocument> queryLoadConversion_ToCL = new OSQLSynchQuery<ODocument>("select from CSConversion where in.system = ?");

	private static OSQLSynchQuery<ODocument> queryLoadPropaedeutic_FromCLtoCL = new OSQLSynchQuery<ODocument>("select from CSPropaedeutic where out.system = ? and in.system = ?");
	private static OSQLSynchQuery<ODocument> queryLoadRelationship_FromCLtoCL = new OSQLSynchQuery<ODocument>("select from CSRelationship where out.system = ? and in.system = ?");
	private static OSQLSynchQuery<ODocument> queryLoadConversion_FromCLtoCL = new OSQLSynchQuery<ODocument>("select from CSConversion where out.system = ? and in.system = ?");

	private static OSQLSynchQuery<ODocument> queryLoadPropaedeutic_FromCode = new OSQLSynchQuery<ODocument>("select from CSPropaedeutic where out = ?");
	private static OSQLSynchQuery<ODocument> queryLoadRelationship_FromCode = new OSQLSynchQuery<ODocument>("select from CSRelationship where out = ?");
	private static OSQLSynchQuery<ODocument> queryLoadConversion_FromCode = new OSQLSynchQuery<ODocument>("select from CSConversion where out = ?");

	private static OSQLSynchQuery<ODocument> queryLoadPropaedeutic_FromCodeToCode = new OSQLSynchQuery<ODocument>("select from CSPropaedeutic where out = ? and in = ?");
	private static OSQLSynchQuery<ODocument> queryLoadRelationship_FromCodeToCode = new OSQLSynchQuery<ODocument>("select from CSRelationship where out = ? and in = ?");
	private static OSQLSynchQuery<ODocument> queryLoadConversion_FromCodeToCode = new OSQLSynchQuery<ODocument>("select from CSConversion where out = ? and in = ?");

	private static OSQLSynchQuery<ODocument> queryLoadPropaedeutic_FromCodeToCL = new OSQLSynchQuery<ODocument>("select from CSPropaedeutic where out = ? and in.system = ?");
	private static OSQLSynchQuery<ODocument> queryLoadRelationship_FromCodeToCL = new OSQLSynchQuery<ODocument>("select from CSRelationship where out = ? and in.system = ?");
	private static OSQLSynchQuery<ODocument> queryLoadConversion_FromCodeToCL = new OSQLSynchQuery<ODocument>("select from CSConversion where out = ? and in.system = ?");

	private static OSQLSynchQuery<ODocument> queryLoadRelationship_FromCodeType = new OSQLSynchQuery<ODocument>("select from CSRelationship where out = ? and type = ?");
	private static OSQLSynchQuery<ODocument> queryLoadRelationship_FromCLType = new OSQLSynchQuery<ODocument>("select from CSRelationship where out.system = ? and type = ?");


	//FROM CL
    //hierarchy
    @SuppressWarnings("unchecked")
    public synchronized Collection<ODocument> loadHierarchiesFromCLO(ODocument systemO, OGraphDatabase database) throws Exception {
        queryLoadHierarchy_FromCL.reset();
        queryLoadHierarchy_FromCL.resetPagination();
        return (Collection<ODocument>)database.query(queryLoadHierarchy_FromCL, systemO);
    }

    //links
	public Collection<CodeLink> loadLinksFromCL(ODocument systemO) throws Exception {
		Collection<CodeLink> result = new LinkedList<CodeLink>();
		result.addAll(loadRelationshipsFromCL(systemO));
		result.addAll(loadConversionsFromCL(systemO));
		result.addAll(loadPropaedeuticsFromCL(systemO));
		return result;
	}
	public Collection<ODocument> loadLinksFromCLO(ODocument systemO) throws Exception {
        Collection<ODocument> result = loadRelationshipsFromCLO(systemO);
		result.addAll(loadConversionsFromCLO(systemO));
		result.addAll(loadPropaedeuticsFromCLO(systemO));
		return result;
	}
	//relationships
	public Collection<CodeRelationship> loadRelationshipsFromCL(CodeSystem system) throws Exception {
        return system!=null ? loadRelationshipsFromCL(clLoadDao.loadSystemO(system.getSystem(), system.getVersion())) : null;
    }
	public Collection<CodeRelationship> loadRelationshipsFromCL(ODocument systemO) throws Exception {
		Collection<CodeRelationship> relations = new LinkedList<CodeRelationship>();
		for (ODocument relationO : loadRelationshipsFromCLO(systemO))
			relations.add(converter.toRelationship(relationO));
		return relations;
	}
	@SuppressWarnings("unchecked")
	public synchronized Collection<ODocument> loadRelationshipsFromCLO(ODocument systemO) throws Exception {
		queryLoadRelationship_FromCL.reset();
		queryLoadRelationship_FromCL.resetPagination();
		return (Collection<ODocument>)getConnection().query(queryLoadRelationship_FromCL, systemO);
	}
	//conversions
	public Collection<CodeConversion> loadConversionsFromCL(CodeSystem system) throws Exception {
        ODocument systemO = clLoadDao.loadSystemO(system.getSystem(), system.getVersion());
        return loadConversionsFromCL(systemO);
	}
	public Collection<CodeConversion> loadConversionsFromCL(ODocument systemO) throws Exception {
		Collection<CodeConversion> conversions = new LinkedList<CodeConversion>();
		for (ODocument conversionO : loadConversionsFromCLO(systemO))
			conversions.add(converter.toConversion(conversionO));
		return conversions;
	}
	@SuppressWarnings("unchecked")
	public synchronized Collection<ODocument> loadConversionsFromCLO(ODocument systemO) throws Exception {
		queryLoadConversion_FromCL.reset();
		queryLoadConversion_FromCL.resetPagination();
		return (Collection<ODocument>)getConnection().query(queryLoadConversion_FromCL, systemO);
	}
	//propaedeutics
	public Collection<CodePropaedeutic> loadPropaedeuticsFromCL(CodeSystem system) throws Exception {
		ODocument systemO = clLoadDao.loadSystemO(system.getSystem(), system.getVersion());
		return loadPropaedeuticsFromCL(systemO);
	}
	public Collection<CodePropaedeutic> loadPropaedeuticsFromCL(ODocument systemO) throws Exception {
		Collection<CodePropaedeutic> propaedeutics = new LinkedList<CodePropaedeutic>();
		for (ODocument propaedeuticO : loadPropaedeuticsFromCLO(systemO))
			propaedeutics.add(converter.toPropaedeutic(propaedeuticO));
		return propaedeutics;
	}
	@SuppressWarnings("unchecked")
	public synchronized Collection<ODocument> loadPropaedeuticsFromCLO(ODocument systemO) throws Exception {
		queryLoadPropaedeutic_FromCL.reset();
		queryLoadPropaedeutic_FromCL.resetPagination();
		return (Collection<ODocument>)getConnection().query(queryLoadPropaedeutic_FromCL, systemO);
	}

	
	//TO CL
	//links
	public Collection<CodeLink> loadLinksToCL(CodeSystem system) throws Exception {
        ODocument systemO = clLoadDao.loadSystemO(system.getSystem(), system.getVersion());
        return loadLinksToCL(systemO);
	}
	public Collection<CodeLink> loadLinksToCL(ODocument systemO) throws Exception {
		Collection<CodeLink> result = new LinkedList<>();
		result.addAll(loadRelationshipsToCL(systemO));
		result.addAll(loadConversionsToCL(systemO));
		result.addAll(loadPropaedeuticsToCL(systemO));
		return result;
	}
	public Collection<ODocument> loadLinksToCLO(ODocument systemO) throws Exception {
		Collection<ODocument> result = loadRelationshipsToCLO(systemO);
		result.addAll(loadConversionsToCLO(systemO));
		result.addAll(loadPropaedeuticsToCLO(systemO));
		return result;
	}
	//relationships
	public Collection<CodeRelationship> loadRelationshipsToCL(CodeSystem system) throws Exception {
        ODocument systemO = clLoadDao.loadSystemO(system.getSystem(), system.getVersion());
        return loadRelationshipsToCL(systemO);
	}
	public Collection<CodeRelationship> loadRelationshipsToCL(ODocument systemO) throws Exception {
		Collection<CodeRelationship> relations = new LinkedList<CodeRelationship>();
		for (ODocument relationO : loadRelationshipsToCLO(systemO))
			relations.add(converter.toRelationship(relationO));
		return relations;
	}
	@SuppressWarnings("unchecked")
	public synchronized Collection<ODocument> loadRelationshipsToCLO(ODocument systemO) throws Exception {
		queryLoadRelationship_ToCL.reset();
		queryLoadRelationship_ToCL.resetPagination();
		return (Collection<ODocument>)getConnection().query(queryLoadRelationship_ToCL, systemO);
	}
	//conversions
	public Collection<CodeConversion> loadConversionsToCL(CodeSystem system) throws Exception {
        ODocument systemO = clLoadDao.loadSystemO(system.getSystem(), system.getVersion());
        return loadConversionsToCL(systemO);
	}
	public Collection<CodeConversion> loadConversionsToCL(ODocument systemO) throws Exception {
		Collection<CodeConversion> conversions = new LinkedList<CodeConversion>();
		for (ODocument conversionO : loadConversionsToCLO(systemO))
			conversions.add(converter.toConversion(conversionO));
		return conversions;
	}
	@SuppressWarnings("unchecked")
	public synchronized Collection<ODocument> loadConversionsToCLO(ODocument systemO) throws Exception {
		queryLoadConversion_ToCL.reset();
		queryLoadConversion_ToCL.resetPagination();
		return (Collection<ODocument>)getConnection().query(queryLoadConversion_ToCL, systemO);
	}
	//propaedeutics
	public Collection<CodePropaedeutic> loadPropaedeuticsToCL(CodeSystem system) throws Exception {
        ODocument systemO = clLoadDao.loadSystemO(system.getSystem(), system.getVersion());
        return loadPropaedeuticsToCL(systemO);
	}
	public Collection<CodePropaedeutic> loadPropaedeuticsToCL(ODocument systemO) throws Exception {
		Collection<CodePropaedeutic> propaedeutics = new LinkedList<CodePropaedeutic>();
		for (ODocument propaedeuticO : loadPropaedeuticsToCLO(systemO))
			propaedeutics.add(converter.toPropaedeutic(propaedeuticO));
		return propaedeutics;
	}
	@SuppressWarnings("unchecked")
	public synchronized Collection<ODocument> loadPropaedeuticsToCLO(ODocument systemO) throws Exception {
		queryLoadPropaedeutic_ToCL.reset();
		queryLoadPropaedeutic_ToCL.resetPagination();
		return (Collection<ODocument>)getConnection().query(queryLoadPropaedeutic_ToCL, systemO);
	}

	//FROM CL TO CL
	//links
	public Collection<CodeLink> loadLinksFromCLtoCL(CodeSystem systemFrom, CodeSystem systemTo) throws Exception {
        ODocument systemFromO = clLoadDao.loadSystemO(systemFrom.getSystem(), systemFrom.getVersion());
        ODocument systemToO = clLoadDao.loadSystemO(systemTo.getSystem(), systemTo.getVersion());
        return loadLinksFromCLtoCL(systemFromO, systemToO);
	}
	public Collection<CodeLink> loadLinksFromCLtoCL(ODocument systemFromO, ODocument systemToO) throws Exception {
		Collection<CodeLink> result = new LinkedList<CodeLink>();
		result.addAll(loadRelationshipsFromCLtoCL(systemFromO, systemToO));
		result.addAll(loadConversionsFromCLtoCL(systemFromO, systemToO));
		result.addAll(loadPropaedeuticsFromCLtoCL(systemFromO, systemToO));
		return result;
	}
	public Collection<ODocument> loadLinksFromCLtoCLO(ODocument systemFromO, ODocument systemToO) throws Exception {
		Collection<ODocument> result = loadRelationshipsFromCLtoCLO(systemFromO, systemToO);
		result.addAll(loadConversionsFromCLtoCLO(systemFromO, systemToO));
		result.addAll(loadPropaedeuticsFromCLtoCLO(systemFromO, systemToO));
		return result;
	}
	//relationships
	public Collection<CodeRelationship> loadRelationshipsFromCLtoCL(CodeSystem systemFrom, CodeSystem systemTo) throws Exception {
        ODocument systemFromO = clLoadDao.loadSystemO(systemFrom.getSystem(), systemFrom.getVersion());
        ODocument systemToO = clLoadDao.loadSystemO(systemTo.getSystem(), systemTo.getVersion());
        return loadRelationshipsFromCLtoCL(systemFromO, systemToO);
	}
	public Collection<CodeRelationship> loadRelationshipsFromCLtoCL(ODocument systemFromO, ODocument systemToO) throws Exception {
		Collection<CodeRelationship> relations = new LinkedList<CodeRelationship>();
		for (ODocument relationO : loadRelationshipsFromCLtoCLO(systemFromO, systemToO))
			relations.add(converter.toRelationship(relationO));
		return relations;
	}
	@SuppressWarnings("unchecked")
	public synchronized Collection<ODocument> loadRelationshipsFromCLtoCLO(ODocument systemFromO, ODocument systemToO) throws Exception {
		queryLoadRelationship_FromCLtoCL.reset();
		queryLoadRelationship_FromCLtoCL.resetPagination();
		return (Collection<ODocument>)getConnection().query(queryLoadRelationship_FromCLtoCL, systemFromO, systemToO);
	}
	//conversions
	public Collection<CodeConversion> loadConversionsFromCLtoCL(CodeSystem systemFrom, CodeSystem systemTo) throws Exception {
        ODocument systemFromO = clLoadDao.loadSystemO(systemFrom.getSystem(), systemFrom.getVersion());
        ODocument systemToO = clLoadDao.loadSystemO(systemTo.getSystem(), systemTo.getVersion());
        return loadConversionsFromCLtoCL(systemFromO, systemToO);
	}
	public Collection<CodeConversion> loadConversionsFromCLtoCL(ODocument systemFromO, ODocument systemToO) throws Exception {
		Collection<CodeConversion> conversions = new LinkedList<CodeConversion>();
		for (ODocument conversionO : loadConversionsFromCLtoCLO(systemFromO, systemToO))
			conversions.add(converter.toConversion(conversionO));
		return conversions;
	}
	@SuppressWarnings("unchecked")
	public synchronized Collection<ODocument> loadConversionsFromCLtoCLO(ODocument systemFromO, ODocument systemToO) throws Exception {
		queryLoadConversion_FromCLtoCL.reset();
		queryLoadConversion_FromCLtoCL.resetPagination();
		return (Collection<ODocument>)getConnection().query(queryLoadConversion_FromCLtoCL, systemFromO, systemToO);
	}
	//propaedeutics
	public Collection<CodePropaedeutic> loadPropaedeuticsFromCLtoCL(CodeSystem systemFrom, CodeSystem systemTo) throws Exception {
        ODocument systemFromO = clLoadDao.loadSystemO(systemFrom.getSystem(), systemFrom.getVersion());
        ODocument systemToO = clLoadDao.loadSystemO(systemTo.getSystem(), systemTo.getVersion());
        return loadPropaedeuticsFromCLtoCL(systemFromO, systemToO);
	}
	public Collection<CodePropaedeutic> loadPropaedeuticsFromCLtoCL(ODocument systemFromO, ODocument systemToO) throws Exception {
		Collection<CodePropaedeutic> propaedeutics = new LinkedList<CodePropaedeutic>();
		for (ODocument propaedeuticO : loadPropaedeuticsFromCLtoCLO(systemFromO, systemToO))
			propaedeutics.add(converter.toPropaedeutic(propaedeuticO));
		return propaedeutics;
	}
	@SuppressWarnings("unchecked")
	public synchronized Collection<ODocument> loadPropaedeuticsFromCLtoCLO(ODocument systemFromO, ODocument systemToO) throws Exception {
		queryLoadPropaedeutic_FromCLtoCL.reset();
		queryLoadPropaedeutic_FromCLtoCL.resetPagination();
		return (Collection<ODocument>)getConnection().query(queryLoadPropaedeutic_FromCLtoCL, systemFromO, systemToO);
	}

	//FROM CODE TO CL
	//links
	public Collection<CodeLink> loadLinksFromCodeToCL(Code code, CodeSystem systemTo) throws Exception {
        ODocument codeO = clLoadDao.loadCodeO(code.getSystemKey(), code.getSystemVersion(), code.getCode());
        ODocument systemToO = clLoadDao.loadSystemO(systemTo.getSystem(), systemTo.getVersion());
        return loadLinksFromCodeToCL(codeO, systemToO);
	}
	public Collection<CodeLink> loadLinksFromCodeToCL(ODocument codeO, ODocument systemToO) throws Exception {
		Collection<CodeLink> result = new LinkedList<CodeLink>();
		result.addAll(loadRelationshipsFromCodeToCL(codeO, systemToO));
		result.addAll(loadConversionsFromCodeToCL(codeO, systemToO));
		result.addAll(loadPropaedeuticsFromCodeToCL(codeO, systemToO));
		return result;
	}
	public Collection<ODocument> loadLinksFromCodeToCLO(ODocument codeO, ODocument systemToO) throws Exception {
		Collection<ODocument> result = loadRelationshipsFromCodeToCLO(codeO, systemToO);
		result.addAll(loadConversionsFromCodeToCLO(codeO, systemToO));
		result.addAll(loadPropaedeuticsFromCodeToCLO(codeO, systemToO));
		return result;
	}
	//relationships
	public Collection<CodeRelationship> loadRelationshipsFromCodeToCL(Code code, CodeSystem systemTo) throws Exception {
        ODocument codeO = clLoadDao.loadCodeO(code.getSystemKey(), code.getSystemVersion(), code.getCode());
        ODocument systemToO = clLoadDao.loadSystemO(systemTo.getSystem(), systemTo.getVersion());
        return loadRelationshipsFromCodeToCL(codeO, systemToO);
	}
	public Collection<CodeRelationship> loadRelationshipsFromCodeToCL(ODocument codeO, ODocument systemToO) throws Exception {
		Collection<CodeRelationship> relations = new LinkedList<CodeRelationship>();
		for (ODocument relationO : loadRelationshipsFromCodeToCLO(codeO, systemToO))
			relations.add(converter.toRelationship(relationO));
		return relations;
	}
	@SuppressWarnings("unchecked")
	public synchronized Collection<ODocument> loadRelationshipsFromCodeToCLO(ODocument codeO, ODocument systemToO) throws Exception {
		queryLoadRelationship_FromCodeToCL.reset();
		queryLoadRelationship_FromCodeToCL.resetPagination();
		return (Collection<ODocument>)getConnection().query(queryLoadRelationship_FromCodeToCL, codeO, systemToO);
	}
	//conversions
	public Collection<CodeConversion> loadConversionsFromCodeToCL(Code code, CodeSystem systemTo) throws Exception {
        ODocument codeO = clLoadDao.loadCodeO(code.getSystemKey(), code.getSystemVersion(), code.getCode());
        ODocument systemToO = clLoadDao.loadSystemO(systemTo.getSystem(), systemTo.getVersion());
        return loadConversionsFromCodeToCL(codeO, systemToO);
	}
	public Collection<CodeConversion> loadConversionsFromCodeToCL(ODocument codeO, ODocument systemToO) throws Exception {
		Collection<CodeConversion> conversions = new LinkedList<CodeConversion>();
		for (ODocument conversionO : loadConversionsFromCodeToCLO(codeO, systemToO))
			conversions.add(converter.toConversion(conversionO));
		return conversions;
	}
	@SuppressWarnings("unchecked")
	public synchronized Collection<ODocument> loadConversionsFromCodeToCLO(ODocument codeO, ODocument systemToO) throws Exception {
		queryLoadConversion_FromCodeToCL.reset();
		queryLoadConversion_FromCodeToCL.resetPagination();
		return (Collection<ODocument>)getConnection().query(queryLoadConversion_FromCodeToCL, codeO, systemToO);
	}
	//propaedeutics
	public Collection<CodePropaedeutic> loadPropaedeuticsFromCodeToCL(Code code, CodeSystem systemTo) throws Exception {
        ODocument codeO = clLoadDao.loadCodeO(code.getSystemKey(), code.getSystemVersion(), code.getCode());
        ODocument systemToO = clLoadDao.loadSystemO(systemTo.getSystem(), systemTo.getVersion());
        return loadPropaedeuticsFromCodeToCL(codeO, systemToO);
	}
	public Collection<CodePropaedeutic> loadPropaedeuticsFromCodeToCL(ODocument codeO, ODocument systemToO) throws Exception {
		Collection<CodePropaedeutic> propaedeutics = new LinkedList<CodePropaedeutic>();
		for (ODocument propaedeuticO : loadPropaedeuticsFromCodeToCLO(codeO, systemToO))
			propaedeutics.add(converter.toPropaedeutic(propaedeuticO));
		return propaedeutics;
	}
	@SuppressWarnings("unchecked")
	public synchronized Collection<ODocument> loadPropaedeuticsFromCodeToCLO(ODocument codeO, ODocument systemToO) throws Exception {
		queryLoadPropaedeutic_FromCodeToCL.reset();
		queryLoadPropaedeutic_FromCodeToCL.resetPagination();
		return (Collection<ODocument>)getConnection().query(queryLoadPropaedeutic_FromCodeToCL, codeO, systemToO);
	}

	//FROM CODE TO CODE
	//links
	public Collection<CodeLink> loadLinksFromCodeToCode(Code codeFrom, Code codeTo) throws Exception {
        ODocument codeFromO = clLoadDao.loadCodeO(codeFrom.getSystemKey(), codeFrom.getSystemVersion(), codeFrom.getCode());
        ODocument codeToO = clLoadDao.loadCodeO(codeTo.getSystemKey(), codeTo.getSystemVersion(), codeTo.getCode());
        return loadLinksFromCodeToCode(codeFromO, codeToO);
	}
	public Collection<CodeLink> loadLinksFromCodeToCode(ODocument codeFromO, ODocument codeToO) throws Exception {
		Collection<CodeLink> result = new LinkedList<>();
		result.addAll(loadRelationshipsFromCodeToCode(codeFromO, codeToO));
		result.addAll(loadConversionsFromCodeToCode(codeFromO, codeToO));
		result.addAll(loadPropaedeuticsFromCodeToCode(codeFromO, codeToO));
		return result;
	}
	public Collection<ODocument> loadLinksFromCodeToCodeO(ODocument codeFromO, ODocument codeToO) throws Exception {
		Collection<ODocument> result = loadRelationshipsFromCodeToCodeO(codeFromO, codeToO);
		result.addAll(loadConversionsFromCodeToCodeO(codeFromO, codeToO));
		result.addAll(loadPropaedeuticsFromCodeToCodeO(codeFromO, codeToO));
		return result;
	}
	//relationships
	public Collection<CodeRelationship> loadRelationshipsFromCodeToCode(Code codeFrom, Code codeTo) throws Exception {
        ODocument codeFromO = clLoadDao.loadCodeO(codeFrom.getSystemKey(), codeFrom.getSystemVersion(), codeFrom.getCode());
        ODocument codeToO = clLoadDao.loadCodeO(codeTo.getSystemKey(), codeTo.getSystemVersion(), codeTo.getCode());
        return loadRelationshipsFromCodeToCode(codeFromO, codeToO);
	}
	public Collection<CodeRelationship> loadRelationshipsFromCodeToCode(ODocument codeFromO, ODocument codeToO) throws Exception {
		Collection<CodeRelationship> relations = new LinkedList<CodeRelationship>();
		for (ODocument relationO : loadRelationshipsFromCodeToCodeO(codeFromO, codeToO))
			relations.add(converter.toRelationship(relationO));
		return relations;
	}
	@SuppressWarnings("unchecked")
	public synchronized Collection<ODocument> loadRelationshipsFromCodeToCodeO(ODocument codeFromO, ODocument codeToO) throws Exception {
		queryLoadRelationship_FromCodeToCode.reset();
		queryLoadRelationship_FromCodeToCode.resetPagination();
		return (Collection<ODocument>)getConnection().query(queryLoadRelationship_FromCodeToCode, codeFromO, codeToO);
	}
	//conversions
	public Collection<CodeConversion> loadConversionsFromCodeToCode(Code codeFrom, Code codeTo) throws Exception {
        ODocument codeFromO = clLoadDao.loadCodeO(codeFrom.getSystemKey(), codeFrom.getSystemVersion(), codeFrom.getCode());
        ODocument codeToO = clLoadDao.loadCodeO(codeTo.getSystemKey(), codeTo.getSystemVersion(), codeTo.getCode());
        return loadConversionsFromCodeToCode(codeFromO, codeToO);
	}
	public Collection<CodeConversion> loadConversionsFromCodeToCode(ODocument codeFromO, ODocument codeToO) throws Exception {
		Collection<CodeConversion> conversions = new LinkedList<CodeConversion>();
		for (ODocument conversionO : loadConversionsFromCodeToCodeO(codeFromO, codeToO))
			conversions.add(converter.toConversion(conversionO));
		return conversions;
	}
	@SuppressWarnings("unchecked")
	public synchronized Collection<ODocument> loadConversionsFromCodeToCodeO(ODocument codeFromO, ODocument codeToO) throws Exception {
		queryLoadConversion_FromCodeToCode.reset();
		queryLoadConversion_FromCodeToCode.resetPagination();
		return (Collection<ODocument>)getConnection().query(queryLoadConversion_FromCodeToCode, codeFromO, codeToO);
	}
	//propaedeutics
	public Collection<CodePropaedeutic> loadPropaedeuticsFromCodeToCode(Code codeFrom, Code codeTo) throws Exception {
        ODocument codeFromO = clLoadDao.loadCodeO(codeFrom.getSystemKey(), codeFrom.getSystemVersion(), codeFrom.getCode());
        ODocument codeToO = clLoadDao.loadCodeO(codeTo.getSystemKey(), codeTo.getSystemVersion(), codeTo.getCode());
        return loadPropaedeuticsFromCodeToCode(codeFromO, codeToO);
	}
	public Collection<CodePropaedeutic> loadPropaedeuticsFromCodeToCode(ODocument codeFromO, ODocument codeToO) throws Exception {
		Collection<CodePropaedeutic> propaedeutics = new LinkedList<CodePropaedeutic>();
		for (ODocument propaedeuticO : loadPropaedeuticsFromCodeToCodeO(codeFromO, codeToO))
			propaedeutics.add(converter.toPropaedeutic(propaedeuticO));
		return propaedeutics;
	}
	@SuppressWarnings("unchecked")
	public synchronized Collection<ODocument> loadPropaedeuticsFromCodeToCodeO(ODocument codeFromO, ODocument codeToO) throws Exception {
		queryLoadPropaedeutic_FromCodeToCode.reset();
		queryLoadPropaedeutic_FromCodeToCode.resetPagination();
		return (Collection<ODocument>)getConnection().query(queryLoadPropaedeutic_FromCodeToCode, codeFromO, codeToO);
	}
	
	//FROM CODE
	//links
	public Collection<CodeLink> loadLinksFromCode(Code code) throws Exception {
        ODocument codeO = clLoadDao.loadCodeO(code.getSystemKey(), code.getSystemVersion(), code.getCode());
        return loadLinksFromCode(codeO);
	}
	public Collection<CodeLink> loadLinksFromCode(ODocument codeO) throws Exception {
		Collection<CodeLink> result = new LinkedList<CodeLink>();
		result.addAll(loadRelationshipsFromCode(codeO));
		result.addAll(loadConversionsFromCode(codeO));
		result.addAll(loadPropaedeuticsFromCode(codeO));
		return result;
	}
	public Collection<ODocument> loadLinksFromCodeO(ODocument codeO) throws Exception {
		Collection<ODocument> result = loadRelationshipsFromCodeO(codeO);
		result.addAll(loadConversionsFromCodeO(codeO));
		result.addAll(loadPropaedeuticsFromCodeO(codeO));
		return result;
	}
	//relationships
	public Collection<CodeRelationship> loadRelationshipsFromCode(Code code) throws Exception {
        ODocument codeO = clLoadDao.loadCodeO(code.getSystemKey(), code.getSystemVersion(), code.getCode());
        return loadRelationshipsFromCode(codeO);
	}
	public Collection<CodeRelationship> loadRelationshipsFromCode(ODocument codeO) throws Exception {
		Collection<CodeRelationship> relations = new LinkedList<CodeRelationship>();
		for (ODocument relationO : loadRelationshipsFromCodeO(codeO))
			relations.add(converter.toRelationship(relationO));
		return relations;
	}
	@SuppressWarnings("unchecked")
	public synchronized Collection<ODocument> loadRelationshipsFromCodeO(ODocument codeO) throws Exception {
		queryLoadRelationship_FromCode.reset();
		queryLoadRelationship_FromCode.resetPagination();
		return (Collection<ODocument>)getConnection().query(queryLoadRelationship_FromCode, codeO);
	}
	//conversions
	public Collection<CodeConversion> loadConversionsFromCode(Code code) throws Exception {
        ODocument codeO = clLoadDao.loadCodeO(code.getSystemKey(), code.getSystemVersion(), code.getCode());
        return loadConversionsFromCode(codeO);
	}
	public Collection<CodeConversion> loadConversionsFromCode(ODocument codeO) throws Exception {
		Collection<CodeConversion> conversions = new LinkedList<CodeConversion>();
		for (ODocument conversionO : loadConversionsFromCodeO(codeO))
			conversions.add(converter.toConversion(conversionO));
		return conversions;
	}
	@SuppressWarnings("unchecked")
	public synchronized Collection<ODocument> loadConversionsFromCodeO(ODocument codeO) throws Exception {
		queryLoadConversion_FromCode.reset();
		queryLoadConversion_FromCode.resetPagination();
		return (Collection<ODocument>)getConnection().query(queryLoadConversion_FromCode, codeO);
	}
	//propaedeutics
	public Collection<CodePropaedeutic> loadPropaedeuticsFromCode(Code code) throws Exception {
        ODocument codeO = clLoadDao.loadCodeO(code.getSystemKey(), code.getSystemVersion(), code.getCode());
        return loadPropaedeuticsFromCode(codeO);
	}
	public Collection<CodePropaedeutic> loadPropaedeuticsFromCode(ODocument codeO) throws Exception {
		Collection<CodePropaedeutic> propaedeutics = new LinkedList<CodePropaedeutic>();
		for (ODocument propaedeuticO : loadPropaedeuticsFromCodeO(codeO))
			propaedeutics.add(converter.toPropaedeutic(propaedeuticO));
		return propaedeutics;
	}
	@SuppressWarnings("unchecked")
	public synchronized Collection<ODocument> loadPropaedeuticsFromCodeO(ODocument codeO) throws Exception {
		queryLoadPropaedeutic_FromCode.reset();
		queryLoadPropaedeutic_FromCode.resetPagination();
		return (Collection<ODocument>)getConnection().query(queryLoadPropaedeutic_FromCode, codeO);
	}

	//FROM CODE TYPE
	//relationships
	public Collection<CodeRelationship> loadRelationshipsFromCodeType(Code code, CodeRelationshipType type) throws Exception {
        ODocument codeO = clLoadDao.loadCodeO(code.getSystemKey(), code.getSystemVersion(), code.getCode());
        return loadRelationshipsFromCodeType(codeO, type);
	}
	public Collection<CodeRelationship> loadRelationshipsFromCodeType(ODocument codeO, CodeRelationshipType type) throws Exception {
		Collection<CodeRelationship> relations = new LinkedList<CodeRelationship>();
		for (ODocument relationO : loadRelationshipsFromCodeTypeO(codeO, type))
			relations.add(converter.toRelationship(relationO));
		return relations;
	}
	@SuppressWarnings("unchecked")
	public synchronized Collection<ODocument> loadRelationshipsFromCodeTypeO(ODocument codeO, CodeRelationshipType type) throws Exception {
		queryLoadRelationship_FromCodeType.reset();
		queryLoadRelationship_FromCodeType.resetPagination();
		return (Collection<ODocument>)getConnection().query(queryLoadRelationship_FromCodeType, codeO, type.getCode());
	}

	//FROM CODE LIST TYPE
	//relationships
	public Collection<CodeRelationship> loadRelationshipsFromCLType(CodeSystem system, CodeRelationshipType type) throws Exception {
        ODocument systemO = clLoadDao.loadSystemO(system.getSystem(), system.getVersion());
        return loadRelationshipsFromCLType(systemO, type);
	}
	public Collection<CodeRelationship> loadRelationshipsFromCLType(ODocument systemO, CodeRelationshipType type) throws Exception {
		Collection<CodeRelationship> relations = new LinkedList<CodeRelationship>();
		for (ODocument relationO : loadRelationshipsFromCLTypeO(systemO, type))
			relations.add(converter.toRelationship(relationO));
		return relations;
	}
	@SuppressWarnings("unchecked")
	public synchronized Collection<ODocument> loadRelationshipsFromCLTypeO(ODocument systemO, CodeRelationshipType type) throws Exception {
		queryLoadRelationship_FromCLType.reset();
		queryLoadRelationship_FromCLType.resetPagination();
		return (Collection<ODocument>)getConnection().query(queryLoadRelationship_FromCLType, systemO, type.getCode());
	}

}

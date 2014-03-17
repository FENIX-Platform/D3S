package org.fao.fenix.d3s.msd.dao.cl;

import java.util.Collection;
import java.util.LinkedList;

import org.fao.fenix.commons.msd.dto.cl.Code;
import org.fao.fenix.commons.msd.dto.cl.CodeConversion;
import org.fao.fenix.commons.msd.dto.cl.CodeLink;
import org.fao.fenix.commons.msd.dto.cl.CodePropaedeutic;
import org.fao.fenix.commons.msd.dto.cl.CodeRelationship;
import org.fao.fenix.commons.msd.dto.cl.CodeSystem;
import org.fao.fenix.commons.msd.dto.cl.type.CodeRelationshipType;
import org.fao.fenix.d3s.server.tools.orient.OrientDao;
import org.fao.fenix.d3s.server.tools.orient.OrientDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

@Component
public class CodeListLinkLoad extends OrientDao {
	
	@Autowired private CodeListConverter converter;
	@Autowired private CodeListLoad clLoadDao;
	
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
	public Collection<CodeLink> loadLinksFromCL(CodeSystem system) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			ODocument systemO = clLoadDao.loadSystemO(system.getSystem(), system.getVersion(), database);
			return loadLinksFromCL(systemO, database);
		} finally {
			if (database!=null)
				database.close();
		}
	}
	public Collection<CodeLink> loadLinksFromCL(ODocument systemO, OGraphDatabase database) throws Exception {
		Collection<CodeLink> result = new LinkedList<CodeLink>();
		result.addAll(loadRelationshipsFromCL(systemO, database));
		result.addAll(loadConversionsFromCL(systemO, database));
		result.addAll(loadPropaedeuticsFromCL(systemO, database));
		return result;
	}
	public Collection<ODocument> loadLinksFromCLO(ODocument systemO, OGraphDatabase database) throws Exception {
		Collection<ODocument> result = loadRelationshipsFromCLO(systemO, database);
		result.addAll(loadConversionsFromCLO(systemO, database));
		result.addAll(loadPropaedeuticsFromCLO(systemO, database));
		return result;
	}
	//relationships
	public Collection<CodeRelationship> loadRelationshipsFromCL(CodeSystem system) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			ODocument systemO = clLoadDao.loadSystemO(system.getSystem(), system.getVersion(), database);
			return loadRelationshipsFromCL(systemO, database);
		} finally {
			if (database!=null)
				database.close();
		}
	}
	public Collection<CodeRelationship> loadRelationshipsFromCL(ODocument systemO, OGraphDatabase database) throws Exception {
		Collection<CodeRelationship> relations = new LinkedList<CodeRelationship>();
		for (ODocument relationO : loadRelationshipsFromCLO(systemO, database))
			relations.add(converter.toRelationship(relationO, database));
		return relations;
	}
	@SuppressWarnings("unchecked")
	public synchronized Collection<ODocument> loadRelationshipsFromCLO(ODocument systemO, OGraphDatabase database) throws Exception {
		queryLoadRelationship_FromCL.reset();
		queryLoadRelationship_FromCL.resetPagination();
		return (Collection<ODocument>)database.query(queryLoadRelationship_FromCL, systemO);
	}
	//conversions
	public Collection<CodeConversion> loadConversionsFromCL(CodeSystem system) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			ODocument systemO = clLoadDao.loadSystemO(system.getSystem(), system.getVersion(), database);
			return loadConversionsFromCL(systemO, database);
		} finally {
			if (database!=null)
				database.close();
		}
	}
	public Collection<CodeConversion> loadConversionsFromCL(ODocument systemO, OGraphDatabase database) throws Exception {
		Collection<CodeConversion> conversions = new LinkedList<CodeConversion>();
		for (ODocument conversionO : loadConversionsFromCLO(systemO, database))
			conversions.add(converter.toConversion(conversionO, database));
		return conversions;
	}
	@SuppressWarnings("unchecked")
	public synchronized Collection<ODocument> loadConversionsFromCLO(ODocument systemO, OGraphDatabase database) throws Exception {
		queryLoadConversion_FromCL.reset();
		queryLoadConversion_FromCL.resetPagination();
		return (Collection<ODocument>)database.query(queryLoadConversion_FromCL, systemO);
	}
	//propaedeutics
	public Collection<CodePropaedeutic> loadPropaedeuticsFromCL(CodeSystem system) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			ODocument systemO = clLoadDao.loadSystemO(system.getSystem(), system.getVersion(), database);
			return loadPropaedeuticsFromCL(systemO, database);
		} finally {
			if (database!=null)
				database.close();
		}
	}
	public Collection<CodePropaedeutic> loadPropaedeuticsFromCL(ODocument systemO, OGraphDatabase database) throws Exception {
		Collection<CodePropaedeutic> propaedeutics = new LinkedList<CodePropaedeutic>();
		for (ODocument propaedeuticO : loadPropaedeuticsFromCLO(systemO, database))
			propaedeutics.add(converter.toPropaedeutic(propaedeuticO, database));
		return propaedeutics;
	}
	@SuppressWarnings("unchecked")
	public synchronized Collection<ODocument> loadPropaedeuticsFromCLO(ODocument systemO, OGraphDatabase database) throws Exception {
		queryLoadPropaedeutic_FromCL.reset();
		queryLoadPropaedeutic_FromCL.resetPagination();
		return (Collection<ODocument>)database.query(queryLoadPropaedeutic_FromCL, systemO);
	}

	
	//TO CL
	//links
	public Collection<CodeLink> loadLinksToCL(CodeSystem system) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			ODocument systemO = clLoadDao.loadSystemO(system.getSystem(), system.getVersion(), database);
			return loadLinksToCL(systemO, database);
		} finally {
			if (database!=null)
				database.close();
		}
	}
	public Collection<CodeLink> loadLinksToCL(ODocument systemO, OGraphDatabase database) throws Exception {
		Collection<CodeLink> result = new LinkedList<CodeLink>();
		result.addAll(loadRelationshipsToCL(systemO, database));
		result.addAll(loadConversionsToCL(systemO, database));
		result.addAll(loadPropaedeuticsToCL(systemO, database));
		return result;
	}
	public Collection<ODocument> loadLinksToCLO(ODocument systemO, OGraphDatabase database) throws Exception {
		Collection<ODocument> result = loadRelationshipsToCLO(systemO, database);
		result.addAll(loadConversionsToCLO(systemO, database));
		result.addAll(loadPropaedeuticsToCLO(systemO, database));
		return result;
	}
	//relationships
	public Collection<CodeRelationship> loadRelationshipsToCL(CodeSystem system) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			ODocument systemO = clLoadDao.loadSystemO(system.getSystem(), system.getVersion(), database);
			return loadRelationshipsToCL(systemO, database);
		} finally {
			if (database!=null)
				database.close();
		}
	}
	public Collection<CodeRelationship> loadRelationshipsToCL(ODocument systemO, OGraphDatabase database) throws Exception {
		Collection<CodeRelationship> relations = new LinkedList<CodeRelationship>();
		for (ODocument relationO : loadRelationshipsToCLO(systemO, database))
			relations.add(converter.toRelationship(relationO, database));
		return relations;
	}
	@SuppressWarnings("unchecked")
	public synchronized Collection<ODocument> loadRelationshipsToCLO(ODocument systemO, OGraphDatabase database) throws Exception {
		queryLoadRelationship_ToCL.reset();
		queryLoadRelationship_ToCL.resetPagination();
		return (Collection<ODocument>)database.query(queryLoadRelationship_ToCL, systemO);
	}
	//conversions
	public Collection<CodeConversion> loadConversionsToCL(CodeSystem system) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			ODocument systemO = clLoadDao.loadSystemO(system.getSystem(), system.getVersion(), database);
			return loadConversionsToCL(systemO, database);
		} finally {
			if (database!=null)
				database.close();
		}
	}
	public Collection<CodeConversion> loadConversionsToCL(ODocument systemO, OGraphDatabase database) throws Exception {
		Collection<CodeConversion> conversions = new LinkedList<CodeConversion>();
		for (ODocument conversionO : loadConversionsToCLO(systemO, database))
			conversions.add(converter.toConversion(conversionO, database));
		return conversions;
	}
	@SuppressWarnings("unchecked")
	public synchronized Collection<ODocument> loadConversionsToCLO(ODocument systemO, OGraphDatabase database) throws Exception {
		queryLoadConversion_ToCL.reset();
		queryLoadConversion_ToCL.resetPagination();
		return (Collection<ODocument>)database.query(queryLoadConversion_ToCL, systemO);
	}
	//propaedeutics
	public Collection<CodePropaedeutic> loadPropaedeuticsToCL(CodeSystem system) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			ODocument systemO = clLoadDao.loadSystemO(system.getSystem(), system.getVersion(), database);
			return loadPropaedeuticsToCL(systemO, database);
		} finally {
			if (database!=null)
				database.close();
		}
	}
	public Collection<CodePropaedeutic> loadPropaedeuticsToCL(ODocument systemO, OGraphDatabase database) throws Exception {
		Collection<CodePropaedeutic> propaedeutics = new LinkedList<CodePropaedeutic>();
		for (ODocument propaedeuticO : loadPropaedeuticsToCLO(systemO, database))
			propaedeutics.add(converter.toPropaedeutic(propaedeuticO, database));
		return propaedeutics;
	}
	@SuppressWarnings("unchecked")
	public synchronized Collection<ODocument> loadPropaedeuticsToCLO(ODocument systemO, OGraphDatabase database) throws Exception {
		queryLoadPropaedeutic_ToCL.reset();
		queryLoadPropaedeutic_ToCL.resetPagination();
		return (Collection<ODocument>)database.query(queryLoadPropaedeutic_ToCL, systemO);
	}

	//FROM CL TO CL
	//links
	public Collection<CodeLink> loadLinksFromCLtoCL(CodeSystem systemFrom, CodeSystem systemTo) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			ODocument systemFromO = clLoadDao.loadSystemO(systemFrom.getSystem(), systemFrom.getVersion(), database);
			ODocument systemToO = clLoadDao.loadSystemO(systemTo.getSystem(), systemTo.getVersion(), database);
			return loadLinksFromCLtoCL(systemFromO, systemToO, database);
		} finally {
			if (database!=null)
				database.close();
		}
	}
	public Collection<CodeLink> loadLinksFromCLtoCL(ODocument systemFromO, ODocument systemToO, OGraphDatabase database) throws Exception {
		Collection<CodeLink> result = new LinkedList<CodeLink>();
		result.addAll(loadRelationshipsFromCLtoCL(systemFromO, systemToO, database));
		result.addAll(loadConversionsFromCLtoCL(systemFromO, systemToO, database));
		result.addAll(loadPropaedeuticsFromCLtoCL(systemFromO, systemToO, database));
		return result;
	}
	public Collection<ODocument> loadLinksFromCLtoCLO(ODocument systemFromO, ODocument systemToO, OGraphDatabase database) throws Exception {
		Collection<ODocument> result = loadRelationshipsFromCLtoCLO(systemFromO, systemToO, database);
		result.addAll(loadConversionsFromCLtoCLO(systemFromO, systemToO, database));
		result.addAll(loadPropaedeuticsFromCLtoCLO(systemFromO, systemToO, database));
		return result;
	}
	//relationships
	public Collection<CodeRelationship> loadRelationshipsFromCLtoCL(CodeSystem systemFrom, CodeSystem systemTo) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			ODocument systemFromO = clLoadDao.loadSystemO(systemFrom.getSystem(), systemFrom.getVersion(), database);
			ODocument systemToO = clLoadDao.loadSystemO(systemTo.getSystem(), systemTo.getVersion(), database);
			return loadRelationshipsFromCLtoCL(systemFromO, systemToO, database);
		} finally {
			if (database!=null)
				database.close();
		}
	}
	public Collection<CodeRelationship> loadRelationshipsFromCLtoCL(ODocument systemFromO, ODocument systemToO, OGraphDatabase database) throws Exception {
		Collection<CodeRelationship> relations = new LinkedList<CodeRelationship>();
		for (ODocument relationO : loadRelationshipsFromCLtoCLO(systemFromO, systemToO, database))
			relations.add(converter.toRelationship(relationO, database));
		return relations;
	}
	@SuppressWarnings("unchecked")
	public synchronized Collection<ODocument> loadRelationshipsFromCLtoCLO(ODocument systemFromO, ODocument systemToO, OGraphDatabase database) throws Exception {
		queryLoadRelationship_FromCLtoCL.reset();
		queryLoadRelationship_FromCLtoCL.resetPagination();
		return (Collection<ODocument>)database.query(queryLoadRelationship_FromCLtoCL, systemFromO, systemToO);
	}
	//conversions
	public Collection<CodeConversion> loadConversionsFromCLtoCL(CodeSystem systemFrom, CodeSystem systemTo) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			ODocument systemFromO = clLoadDao.loadSystemO(systemFrom.getSystem(), systemFrom.getVersion(), database);
			ODocument systemToO = clLoadDao.loadSystemO(systemTo.getSystem(), systemTo.getVersion(), database);
			return loadConversionsFromCLtoCL(systemFromO, systemToO, database);
		} finally {
			if (database!=null)
				database.close();
		}
	}
	public Collection<CodeConversion> loadConversionsFromCLtoCL(ODocument systemFromO, ODocument systemToO, OGraphDatabase database) throws Exception {
		Collection<CodeConversion> conversions = new LinkedList<CodeConversion>();
		for (ODocument conversionO : loadConversionsFromCLtoCLO(systemFromO, systemToO, database))
			conversions.add(converter.toConversion(conversionO, database));
		return conversions;
	}
	@SuppressWarnings("unchecked")
	public synchronized Collection<ODocument> loadConversionsFromCLtoCLO(ODocument systemFromO, ODocument systemToO, OGraphDatabase database) throws Exception {
		queryLoadConversion_FromCLtoCL.reset();
		queryLoadConversion_FromCLtoCL.resetPagination();
		return (Collection<ODocument>)database.query(queryLoadConversion_FromCLtoCL, systemFromO, systemToO);
	}
	//propaedeutics
	public Collection<CodePropaedeutic> loadPropaedeuticsFromCLtoCL(CodeSystem systemFrom, CodeSystem systemTo) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			ODocument systemFromO = clLoadDao.loadSystemO(systemFrom.getSystem(), systemFrom.getVersion(), database);
			ODocument systemToO = clLoadDao.loadSystemO(systemTo.getSystem(), systemTo.getVersion(), database);
			return loadPropaedeuticsFromCLtoCL(systemFromO, systemToO, database);
		} finally {
			if (database!=null)
				database.close();
		}
	}
	public Collection<CodePropaedeutic> loadPropaedeuticsFromCLtoCL(ODocument systemFromO, ODocument systemToO, OGraphDatabase database) throws Exception {
		Collection<CodePropaedeutic> propaedeutics = new LinkedList<CodePropaedeutic>();
		for (ODocument propaedeuticO : loadPropaedeuticsFromCLtoCLO(systemFromO, systemToO, database))
			propaedeutics.add(converter.toPropaedeutic(propaedeuticO, database));
		return propaedeutics;
	}
	@SuppressWarnings("unchecked")
	public synchronized Collection<ODocument> loadPropaedeuticsFromCLtoCLO(ODocument systemFromO, ODocument systemToO, OGraphDatabase database) throws Exception {
		queryLoadPropaedeutic_FromCLtoCL.reset();
		queryLoadPropaedeutic_FromCLtoCL.resetPagination();
		return (Collection<ODocument>)database.query(queryLoadPropaedeutic_FromCLtoCL, systemFromO, systemToO);
	}

	//FROM CODE TO CL
	//links
	public Collection<CodeLink> loadLinksFromCodeToCL(Code code, CodeSystem systemTo) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			ODocument codeO = clLoadDao.loadCodeO(code.getSystemKey(), code.getSystemVersion(), code.getCode(), database);
			ODocument systemToO = clLoadDao.loadSystemO(systemTo.getSystem(), systemTo.getVersion(), database);
			return loadLinksFromCodeToCL(codeO, systemToO, database);
		} finally {
			if (database!=null)
				database.close();
		}
	}
	public Collection<CodeLink> loadLinksFromCodeToCL(ODocument codeO, ODocument systemToO, OGraphDatabase database) throws Exception {
		Collection<CodeLink> result = new LinkedList<CodeLink>();
		result.addAll(loadRelationshipsFromCodeToCL(codeO, systemToO, database));
		result.addAll(loadConversionsFromCodeToCL(codeO, systemToO, database));
		result.addAll(loadPropaedeuticsFromCodeToCL(codeO, systemToO, database));
		return result;
	}
	public Collection<ODocument> loadLinksFromCodeToCLO(ODocument codeO, ODocument systemToO, OGraphDatabase database) throws Exception {
		Collection<ODocument> result = loadRelationshipsFromCodeToCLO(codeO, systemToO, database);
		result.addAll(loadConversionsFromCodeToCLO(codeO, systemToO, database));
		result.addAll(loadPropaedeuticsFromCodeToCLO(codeO, systemToO, database));
		return result;
	}
	//relationships
	public Collection<CodeRelationship> loadRelationshipsFromCodeToCL(Code code, CodeSystem systemTo) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			ODocument codeO = clLoadDao.loadCodeO(code.getSystemKey(), code.getSystemVersion(), code.getCode(), database);
			ODocument systemToO = clLoadDao.loadSystemO(systemTo.getSystem(), systemTo.getVersion(), database);
			return loadRelationshipsFromCodeToCL(codeO, systemToO, database);
		} finally {
			if (database!=null)
				database.close();
		}
	}
	public Collection<CodeRelationship> loadRelationshipsFromCodeToCL(ODocument codeO, ODocument systemToO, OGraphDatabase database) throws Exception {
		Collection<CodeRelationship> relations = new LinkedList<CodeRelationship>();
		for (ODocument relationO : loadRelationshipsFromCodeToCLO(codeO, systemToO, database))
			relations.add(converter.toRelationship(relationO, database));
		return relations;
	}
	@SuppressWarnings("unchecked")
	public synchronized Collection<ODocument> loadRelationshipsFromCodeToCLO(ODocument codeO, ODocument systemToO, OGraphDatabase database) throws Exception {
		queryLoadRelationship_FromCodeToCL.reset();
		queryLoadRelationship_FromCodeToCL.resetPagination();
		return (Collection<ODocument>)database.query(queryLoadRelationship_FromCodeToCL, codeO, systemToO);
	}
	//conversions
	public Collection<CodeConversion> loadConversionsFromCodeToCL(Code code, CodeSystem systemTo) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			ODocument codeO = clLoadDao.loadCodeO(code.getSystemKey(), code.getSystemVersion(), code.getCode(), database);
			ODocument systemToO = clLoadDao.loadSystemO(systemTo.getSystem(), systemTo.getVersion(), database);
			return loadConversionsFromCodeToCL(codeO, systemToO, database);
		} finally {
			if (database!=null)
				database.close();
		}
	}
	public Collection<CodeConversion> loadConversionsFromCodeToCL(ODocument codeO, ODocument systemToO, OGraphDatabase database) throws Exception {
		Collection<CodeConversion> conversions = new LinkedList<CodeConversion>();
		for (ODocument conversionO : loadConversionsFromCodeToCLO(codeO, systemToO, database))
			conversions.add(converter.toConversion(conversionO, database));
		return conversions;
	}
	@SuppressWarnings("unchecked")
	public synchronized Collection<ODocument> loadConversionsFromCodeToCLO(ODocument codeO, ODocument systemToO, OGraphDatabase database) throws Exception {
		queryLoadConversion_FromCodeToCL.reset();
		queryLoadConversion_FromCodeToCL.resetPagination();
		return (Collection<ODocument>)database.query(queryLoadConversion_FromCodeToCL, codeO, systemToO);
	}
	//propaedeutics
	public Collection<CodePropaedeutic> loadPropaedeuticsFromCodeToCL(Code code, CodeSystem systemTo) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			ODocument codeO = clLoadDao.loadCodeO(code.getSystemKey(), code.getSystemVersion(), code.getCode(), database);
			ODocument systemToO = clLoadDao.loadSystemO(systemTo.getSystem(), systemTo.getVersion(), database);
			return loadPropaedeuticsFromCodeToCL(codeO, systemToO, database);
		} finally {
			if (database!=null)
				database.close();
		}
	}
	public Collection<CodePropaedeutic> loadPropaedeuticsFromCodeToCL(ODocument codeO, ODocument systemToO, OGraphDatabase database) throws Exception {
		Collection<CodePropaedeutic> propaedeutics = new LinkedList<CodePropaedeutic>();
		for (ODocument propaedeuticO : loadPropaedeuticsFromCodeToCLO(codeO, systemToO, database))
			propaedeutics.add(converter.toPropaedeutic(propaedeuticO, database));
		return propaedeutics;
	}
	@SuppressWarnings("unchecked")
	public synchronized Collection<ODocument> loadPropaedeuticsFromCodeToCLO(ODocument codeO, ODocument systemToO, OGraphDatabase database) throws Exception {
		queryLoadPropaedeutic_FromCodeToCL.reset();
		queryLoadPropaedeutic_FromCodeToCL.resetPagination();
		return (Collection<ODocument>)database.query(queryLoadPropaedeutic_FromCodeToCL, codeO, systemToO);
	}

	//FROM CODE TO CODE
	//links
	public Collection<CodeLink> loadLinksFromCodeToCode(Code codeFrom, Code codeTo) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			ODocument codeFromO = clLoadDao.loadCodeO(codeFrom.getSystemKey(), codeFrom.getSystemVersion(), codeFrom.getCode(), database);
			ODocument codeToO = clLoadDao.loadCodeO(codeTo.getSystemKey(), codeTo.getSystemVersion(), codeTo.getCode(), database);
			return loadLinksFromCodeToCode(codeFromO, codeToO, database);
		} finally {
			if (database!=null)
				database.close();
		}
	}
	public Collection<CodeLink> loadLinksFromCodeToCode(ODocument codeFromO, ODocument codeToO, OGraphDatabase database) throws Exception {
		Collection<CodeLink> result = new LinkedList<CodeLink>();
		result.addAll(loadRelationshipsFromCodeToCode(codeFromO, codeToO, database));
		result.addAll(loadConversionsFromCodeToCode(codeFromO, codeToO, database));
		result.addAll(loadPropaedeuticsFromCodeToCode(codeFromO, codeToO, database));
		return result;
	}
	public Collection<ODocument> loadLinksFromCodeToCodeO(ODocument codeFromO, ODocument codeToO, OGraphDatabase database) throws Exception {
		Collection<ODocument> result = loadRelationshipsFromCodeToCodeO(codeFromO, codeToO, database);
		result.addAll(loadConversionsFromCodeToCodeO(codeFromO, codeToO, database));
		result.addAll(loadPropaedeuticsFromCodeToCodeO(codeFromO, codeToO, database));
		return result;
	}
	//relationships
	public Collection<CodeRelationship> loadRelationshipsFromCodeToCode(Code codeFrom, Code codeTo) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			ODocument codeFromO = clLoadDao.loadCodeO(codeFrom.getSystemKey(), codeFrom.getSystemVersion(), codeFrom.getCode(), database);
			ODocument codeToO = clLoadDao.loadCodeO(codeTo.getSystemKey(), codeTo.getSystemVersion(), codeTo.getCode(), database);
			return loadRelationshipsFromCodeToCode(codeFromO, codeToO, database);
		} finally {
			if (database!=null)
				database.close();
		}
	}
	public Collection<CodeRelationship> loadRelationshipsFromCodeToCode(ODocument codeFromO, ODocument codeToO, OGraphDatabase database) throws Exception {
		Collection<CodeRelationship> relations = new LinkedList<CodeRelationship>();
		for (ODocument relationO : loadRelationshipsFromCodeToCodeO(codeFromO, codeToO, database))
			relations.add(converter.toRelationship(relationO, database));
		return relations;
	}
	@SuppressWarnings("unchecked")
	public synchronized Collection<ODocument> loadRelationshipsFromCodeToCodeO(ODocument codeFromO, ODocument codeToO, OGraphDatabase database) throws Exception {
		queryLoadRelationship_FromCodeToCode.reset();
		queryLoadRelationship_FromCodeToCode.resetPagination();
		return (Collection<ODocument>)database.query(queryLoadRelationship_FromCodeToCode, codeFromO, codeToO);
	}
	//conversions
	public Collection<CodeConversion> loadConversionsFromCodeToCode(Code codeFrom, Code codeTo) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			ODocument codeFromO = clLoadDao.loadCodeO(codeFrom.getSystemKey(), codeFrom.getSystemVersion(), codeFrom.getCode(), database);
			ODocument codeToO = clLoadDao.loadCodeO(codeTo.getSystemKey(), codeTo.getSystemVersion(), codeTo.getCode(), database);
			return loadConversionsFromCodeToCode(codeFromO, codeToO, database);
		} finally {
			if (database!=null)
				database.close();
		}
	}
	public Collection<CodeConversion> loadConversionsFromCodeToCode(ODocument codeFromO, ODocument codeToO, OGraphDatabase database) throws Exception {
		Collection<CodeConversion> conversions = new LinkedList<CodeConversion>();
		for (ODocument conversionO : loadConversionsFromCodeToCodeO(codeFromO, codeToO, database))
			conversions.add(converter.toConversion(conversionO, database));
		return conversions;
	}
	@SuppressWarnings("unchecked")
	public synchronized Collection<ODocument> loadConversionsFromCodeToCodeO(ODocument codeFromO, ODocument codeToO, OGraphDatabase database) throws Exception {
		queryLoadConversion_FromCodeToCode.reset();
		queryLoadConversion_FromCodeToCode.resetPagination();
		return (Collection<ODocument>)database.query(queryLoadConversion_FromCodeToCode, codeFromO, codeToO);
	}
	//propaedeutics
	public Collection<CodePropaedeutic> loadPropaedeuticsFromCodeToCode(Code codeFrom, Code codeTo) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			ODocument codeFromO = clLoadDao.loadCodeO(codeFrom.getSystemKey(), codeFrom.getSystemVersion(), codeFrom.getCode(), database);
			ODocument codeToO = clLoadDao.loadCodeO(codeTo.getSystemKey(), codeTo.getSystemVersion(), codeTo.getCode(), database);
			return loadPropaedeuticsFromCodeToCode(codeFromO, codeToO, database);
		} finally {
			if (database!=null)
				database.close();
		}
	}
	public Collection<CodePropaedeutic> loadPropaedeuticsFromCodeToCode(ODocument codeFromO, ODocument codeToO, OGraphDatabase database) throws Exception {
		Collection<CodePropaedeutic> propaedeutics = new LinkedList<CodePropaedeutic>();
		for (ODocument propaedeuticO : loadPropaedeuticsFromCodeToCodeO(codeFromO, codeToO, database))
			propaedeutics.add(converter.toPropaedeutic(propaedeuticO, database));
		return propaedeutics;
	}
	@SuppressWarnings("unchecked")
	public synchronized Collection<ODocument> loadPropaedeuticsFromCodeToCodeO(ODocument codeFromO, ODocument codeToO, OGraphDatabase database) throws Exception {
		queryLoadPropaedeutic_FromCodeToCode.reset();
		queryLoadPropaedeutic_FromCodeToCode.resetPagination();
		return (Collection<ODocument>)database.query(queryLoadPropaedeutic_FromCodeToCode, codeFromO, codeToO);
	}
	
	//FROM CODE
	//links
	public Collection<CodeLink> loadLinksFromCode(Code code) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			ODocument codeO = clLoadDao.loadCodeO(code.getSystemKey(), code.getSystemVersion(), code.getCode(), database);
			return loadLinksFromCode(codeO, database);
		} finally {
			if (database!=null)
				database.close();
		}
	}
	public Collection<CodeLink> loadLinksFromCode(ODocument codeO, OGraphDatabase database) throws Exception {
		Collection<CodeLink> result = new LinkedList<CodeLink>();
		result.addAll(loadRelationshipsFromCode(codeO, database));
		result.addAll(loadConversionsFromCode(codeO, database));
		result.addAll(loadPropaedeuticsFromCode(codeO, database));
		return result;
	}
	public Collection<ODocument> loadLinksFromCodeO(ODocument codeO, OGraphDatabase database) throws Exception {
		Collection<ODocument> result = loadRelationshipsFromCodeO(codeO, database);
		result.addAll(loadConversionsFromCodeO(codeO, database));
		result.addAll(loadPropaedeuticsFromCodeO(codeO, database));
		return result;
	}
	//relationships
	public Collection<CodeRelationship> loadRelationshipsFromCode(Code code) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			ODocument codeO = clLoadDao.loadCodeO(code.getSystemKey(), code.getSystemVersion(), code.getCode(), database);
			return loadRelationshipsFromCode(codeO, database);
		} finally {
			if (database!=null)
				database.close();
		}
	}
	public Collection<CodeRelationship> loadRelationshipsFromCode(ODocument codeO, OGraphDatabase database) throws Exception {
		Collection<CodeRelationship> relations = new LinkedList<CodeRelationship>();
		for (ODocument relationO : loadRelationshipsFromCodeO(codeO, database))
			relations.add(converter.toRelationship(relationO, database));
		return relations;
	}
	@SuppressWarnings("unchecked")
	public synchronized Collection<ODocument> loadRelationshipsFromCodeO(ODocument codeO, OGraphDatabase database) throws Exception {
		queryLoadRelationship_FromCode.reset();
		queryLoadRelationship_FromCode.resetPagination();
		return (Collection<ODocument>)database.query(queryLoadRelationship_FromCode, codeO);
	}
	//conversions
	public Collection<CodeConversion> loadConversionsFromCode(Code code) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			ODocument codeO = clLoadDao.loadCodeO(code.getSystemKey(), code.getSystemVersion(), code.getCode(), database);
			return loadConversionsFromCode(codeO, database);
		} finally {
			if (database!=null)
				database.close();
		}
	}
	public Collection<CodeConversion> loadConversionsFromCode(ODocument codeO, OGraphDatabase database) throws Exception {
		Collection<CodeConversion> conversions = new LinkedList<CodeConversion>();
		for (ODocument conversionO : loadConversionsFromCodeO(codeO, database))
			conversions.add(converter.toConversion(conversionO, database));
		return conversions;
	}
	@SuppressWarnings("unchecked")
	public synchronized Collection<ODocument> loadConversionsFromCodeO(ODocument codeO, OGraphDatabase database) throws Exception {
		queryLoadConversion_FromCode.reset();
		queryLoadConversion_FromCode.resetPagination();
		return (Collection<ODocument>)database.query(queryLoadConversion_FromCode, codeO);
	}
	//propaedeutics
	public Collection<CodePropaedeutic> loadPropaedeuticsFromCode(Code code) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			ODocument codeO = clLoadDao.loadCodeO(code.getSystemKey(), code.getSystemVersion(), code.getCode(), database);
			return loadPropaedeuticsFromCode(codeO, database);
		} finally {
			if (database!=null)
				database.close();
		}
	}
	public Collection<CodePropaedeutic> loadPropaedeuticsFromCode(ODocument codeO, OGraphDatabase database) throws Exception {
		Collection<CodePropaedeutic> propaedeutics = new LinkedList<CodePropaedeutic>();
		for (ODocument propaedeuticO : loadPropaedeuticsFromCodeO(codeO, database))
			propaedeutics.add(converter.toPropaedeutic(propaedeuticO, database));
		return propaedeutics;
	}
	@SuppressWarnings("unchecked")
	public synchronized Collection<ODocument> loadPropaedeuticsFromCodeO(ODocument codeO, OGraphDatabase database) throws Exception {
		queryLoadPropaedeutic_FromCode.reset();
		queryLoadPropaedeutic_FromCode.resetPagination();
		return (Collection<ODocument>)database.query(queryLoadPropaedeutic_FromCode, codeO);
	}

	//FROM CODE TYPE
	//relationships
	public Collection<CodeRelationship> loadRelationshipsFromCodeType(Code code, CodeRelationshipType type) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			ODocument codeO = clLoadDao.loadCodeO(code.getSystemKey(), code.getSystemVersion(), code.getCode(), database);
			return loadRelationshipsFromCodeType(codeO, type, database);
		} finally {
			if (database!=null)
				database.close();
		}
	}
	public Collection<CodeRelationship> loadRelationshipsFromCodeType(ODocument codeO, CodeRelationshipType type, OGraphDatabase database) throws Exception {
		Collection<CodeRelationship> relations = new LinkedList<CodeRelationship>();
		for (ODocument relationO : loadRelationshipsFromCodeTypeO(codeO, type, database))
			relations.add(converter.toRelationship(relationO, database));
		return relations;
	}
	@SuppressWarnings("unchecked")
	public synchronized Collection<ODocument> loadRelationshipsFromCodeTypeO(ODocument codeO, CodeRelationshipType type, OGraphDatabase database) throws Exception {
		queryLoadRelationship_FromCodeType.reset();
		queryLoadRelationship_FromCodeType.resetPagination();
		return (Collection<ODocument>)database.query(queryLoadRelationship_FromCodeType, codeO, type.getCode());
	}

	//FROM CODE LIST TYPE
	//relationships
	public Collection<CodeRelationship> loadRelationshipsFromCLType(CodeSystem system, CodeRelationshipType type) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			ODocument systemO = clLoadDao.loadSystemO(system.getSystem(), system.getVersion(), database);
			return loadRelationshipsFromCLType(systemO, type, database);
		} finally {
			if (database!=null)
				database.close();
		}
	}
	public Collection<CodeRelationship> loadRelationshipsFromCLType(ODocument systemO, CodeRelationshipType type, OGraphDatabase database) throws Exception {
		Collection<CodeRelationship> relations = new LinkedList<CodeRelationship>();
		for (ODocument relationO : loadRelationshipsFromCLTypeO(systemO, type, database))
			relations.add(converter.toRelationship(relationO, database));
		return relations;
	}
	@SuppressWarnings("unchecked")
	public synchronized Collection<ODocument> loadRelationshipsFromCLTypeO(ODocument systemO, CodeRelationshipType type, OGraphDatabase database) throws Exception {
		queryLoadRelationship_FromCLType.reset();
		queryLoadRelationship_FromCLType.resetPagination();
		return (Collection<ODocument>)database.query(queryLoadRelationship_FromCLType, systemO, type.getCode());
	}

}

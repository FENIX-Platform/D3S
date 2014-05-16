package org.fao.fenix.d3s.msd.dao.dm;

import java.util.*;

import com.orientechnologies.orient.core.index.OIndexException;
import org.fao.fenix.d3s.msd.dao.common.CommonsStore;
import org.fao.fenix.d3s.server.tools.orient.OrientDao;
import org.fao.fenix.d3s.server.tools.orient.OrientDatabase;
import org.fao.fenix.d3s.msd.dao.cl.CodeListLoad;
import org.fao.fenix.d3s.msd.dao.dsd.DSDStore;
import org.fao.fenix.commons.msd.dto.cl.Code;
import org.fao.fenix.commons.msd.dto.cl.CodeSystem;
import org.fao.fenix.commons.msd.dto.dm.DM;
import org.fao.fenix.commons.msd.dto.dm.DMAvailability;
import org.fao.fenix.commons.msd.dto.dm.DMDataSource;
import org.fao.fenix.commons.msd.dto.dm.DMMeta;

import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;

import javax.inject.Inject;

public class DMStore extends OrientDao {
    @Inject private DMLoad dmLoadDAO;
    @Inject private DMIndexStore dmIndexStoreDAO;
    @Inject private DSDStore dsdStoreDAO;
    @Inject private CommonsStore cmStoreDAO;
	@Inject private CodeListLoad clLoadDAO;
	@Inject private DMConverter dmConverter;

	// UPDATE
	public int updateMetadataStructure(DMMeta mm, boolean append) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		int count = 0;
		try {
			count = append ? appendMetadataStructure(mm, database) : updateMetadataStructure(mm, database);
		} finally {
			if (database != null)
				database.close();
		}
		return count;
	}

	public int updateMetadataStructure(DMMeta mm, OGraphDatabase database) throws Exception {
        //Select record
        ODocument mmO = dmLoadDAO.loadMetadataStructureO(mm.getUid(), database);
		if (mmO == null)
			return 0;

        //content normalization
        if (mm.getAvailableDatasetsUID()!=null) {
            Collection<ODocument> datasets = new LinkedList<ODocument>();
            for (String uid : mm.getAvailableDatasetsUID())
                dmLoadDAO.loadDatasetMetadataO(uid, database);
            mm.put("availableDatasets",datasets);
        }

        //Update document
        database.createVertex("DMMeta").fields(mm);
		mmO.save();
		return 1;
	}

    public int appendMetadataStructure(DMMeta mm, OGraphDatabase database) throws Exception {
        ODocument mmO = dmLoadDAO.loadMetadataStructureO(mm.getUid(), database);
        if (mmO == null)
            return 0;

        //TODO

        mmO.save();
        return 1;
    }

	public int updateDatasetMetadata(DM dm, boolean append) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		int count = 0;
		try {
			count = append ? appendDatasetMetadata(dm, database)
					: updateDatasetMetadata(dm, database);
		} finally {
			if (database != null)
				database.close();
		}
		return count;
	}

	public int updateDatasetMetadata(DM dm, OGraphDatabase database) throws Exception {
		ODocument dmmain = dmLoadDAO.loadDatasetMetadataO(dm.getUid(), database);
		if (dmmain == null)
			return 0;

		//main
		dmmain.field("uidParent", dm.getParentUid());
		dmmain.field("language", dm.getLanguage());
		//description
		dmmain.field("title", dm.getTitle());
		dmmain.field("summary", dm.getSummary());
		dmmain.field("abstract", dm.getDescription());
		dmmain.field("supplemental", dm.getSupplemental());
		//content
		dmmain.field("creationDate", dm.getCreationDate());
        dmmain.field("region", dm.getGeographicExtent() != null ? clLoadDAO.loadCodeO(dm.getGeographicExtent().getSystemKey(), dm.getGeographicExtent().getSystemVersion(), dm.getGeographicExtent().getCode(), database):null, OType.LINK);
        dmmain.field("basePeriod", dm.getBasePeriod()!=null ? new ODocument("CMPeriod").fields(dm.getBasePeriod().toMap()) : null, OType.EMBEDDED);
        dmmain.field("referencePeriod", dm.getReferencePeriod()!=null ? new ODocument("CMPeriod").fields(dm.getReferencePeriod().toMap()) : null, OType.EMBEDDED);
        dmmain.field("weightReferencePeriod", dm.getWeightReferencePeriod()!=null ? new ODocument("CMPeriod").fields(dm.getWeightReferencePeriod().toMap()) : null, OType.EMBEDDED);
		dmmain.field("coverageSectors", dm.getCoverageSectors()==null ? null : loadHeterogeneousCodes(dm.getCoverageSectors(), database));
		dmmain.field("codingSystems", dm.getCodingSystems()==null ? null : loadCodeLists(dm.getCodingSystems(), database));
		dmmain.field("classificationSystems", dm.getClassificationSystems());
		dmmain.field("comparability", dm.getComparability());
		dmmain.field("statisticalUnit", dm.getStatisticalUnit());
        dmmain.field("unitOfMeasureDetails", dm.getUnitOfMeasureDetails());
        dmmain.field("um", dm.getUnitOfMeasure()==null ? null : clLoadDAO.loadCodeO(dm.getUnitOfMeasure().getSystemKey(), dm.getUnitOfMeasure().getSystemVersion(), dm.getUnitOfMeasure().getCode(), database));
        dmmain.field("coverageGeographical", dm.getCoverageGeographical()==null ? null : clLoadDAO.loadCodeO(dm.getCoverageGeographical().getSystemKey(), dm.getCoverageGeographical().getSystemVersion(), dm.getCoverageGeographical().getCode(), database));
		//process
		dmmain.field("collectionPeriodicity", dm.getCollectionPeriodicity()==null ? null : clLoadDAO.loadCodeO(dm.getCollectionPeriodicity().getSystemKey(), dm.getCollectionPeriodicity().getSystemVersion(), dm.getCollectionPeriodicity().getCode(), database));
		dmmain.field("processMethodologyDocuments", dm.getProcessMethodologyDocuments()==null ? null : cmStoreDAO.storePublication(dm.getProcessMethodologyDocuments(), database));
		dmmain.field("compilationProcessing", dm.getCompilationProcessing());
		dmmain.field("collectionProcessing", dm.getCollectionProcessing());
		dmmain.field("indexType", dm.getIndexType());
        dmmain.field("processConceptsDocuments", dm.getProcessConceptsDocuments()==null ? null : cmStoreDAO.storePublication(dm.getProcessConceptsDocuments(), database));
        dmmain.field("sourceType", dm.getSourceType());
        dmmain.field("processConcepts", dm.getProcessConcepts());
        dmmain.field("aggregationProcessing", dm.getAggregationProcessing());
        dmmain.field("dataAdjustment", dm.getDataAdjustment());
        //quality
		dmmain.field("accuracy", dm.getAccuracy());
		dmmain.field("accuracyAssessment", dm.getAccuracyAssessment());
		dmmain.field("completeness", dm.getCompleteness());
        dmmain.field("qualityMethodologyDocuments", dm.getQualityMethodologyDocuments()==null ? null : cmStoreDAO.storePublication(dm.getQualityMethodologyDocuments(), database));
		//distribution
		dmmain.field("disseminationPeriodicity", dm.getDisseminationPeriodicity()==null ? null : clLoadDAO.loadCodeO(dm.getDisseminationPeriodicity().getSystemKey(), dm.getDisseminationPeriodicity().getSystemVersion(), dm.getDisseminationPeriodicity().getCode(), database));
		dmmain.field("userAccess", dm.getUserAccess());
		dmmain.field("disseminationFormat", dm.getDisseminationFormat());
		dmmain.field("revisionPolicy", dm.getRevisionPolicy());
        dmmain.field("publications", dm.getPublications()==null ? null : cmStoreDAO.storePublication(dm.getPublications(), database));
        dmmain.field("news", dm.getNews()==null ? null : cmStoreDAO.storePublication(dm.getNews(), database));
        dmmain.field("embargoTimeSpec", dm.getEmbargoTimeSpec());
        dmmain.field("confidentialityDataTreatment", dm.getConfidentialityDataTreatment());
        dmmain.field("confidentialityPolicy", dm.getConfidentialityPolicy());
        dmmain.field("releasePolicy", dm.getReleasePolicy());
        dmmain.field("dataSources", dm.getDataSources()==null ? null : storeDatasource(dm.getDataSources(), database));
        dmmain.field("otherDisseminatedData", dm.getOtherDisseminatedData());
        //contacts
		dmmain.field("source", dm.getSources()==null ? null : cmStoreDAO.storeContactIdentity(dm.getSources(), database));
		dmmain.field("owner", dm.getOwner()==null ? null : cmStoreDAO.storeContactIdentity(dm.getOwner(), database));
		dmmain.field("provider", dm.getProvider()==null ? null : cmStoreDAO.storeContactIdentity(dm.getProvider(), database));
		dmmain.field("compiler", dm.getCompiler()==null ? null : cmStoreDAO.storeContactIdentity(dm.getCompiler(), database));
		dmmain.field("contacts", dm.getContacts()==null ? null : cmStoreDAO.storeContactIdentity(dm.getContacts(), database));
		//status
		dmmain.field("coverageTime", dm.getCoverageTime());
		dmmain.field("accessibility", dm.getAccessibility());
		dmmain.field("updateDate", dm.getUpdateDate());
		dmmain.field("referenceUpdateDate", dm.getReferenceUpdateDate());
		dmmain.field("nextUpdateDate", dm.getNextUpdateDate());
		dmmain.field("completenessPercentage", dm.getCompletenessPercentage());
        dmmain.field("updatePeriodicity", dm.getUpdatePeriodicity()==null ? null : clLoadDAO.loadCodeO(dm.getUpdatePeriodicity().getSystemKey(), dm.getUpdatePeriodicity().getSystemVersion(), dm.getUpdatePeriodicity().getCode(), database));
		//other
		dmmain.field("transferOptions", dm.getTransferOptions()==null ? null : cmStoreDAO.storeLink(dm.getTransferOptions(), database));
		dmmain.field("categories", dm.getCategories()==null ? null : loadHeterogeneousCodes(dm.getCategories(), database));
		dmmain.field("statisticalPopulation", dm.getStatisticalPopulation());
		dmmain.field("freeExtension", dm.getFreeExtension());
		dmmain.field("dataType", dm.getDataType() != null ? dm.getDataType().getCode() : null);
		dmmain.field("dataKind", dm.getDataKind() != null ? dm.getDataKind().getCode() : null);
		dmmain.field("copyrightPolicy", dm.getCopyright() != null ? dm.getCopyright().getCode() : null);
		//upload
		dmmain.field("availability", dm.getAvailability()==null ? null : storeAvailability(dm.getAvailability(), database));

		//DSD
        if (dm.getDsd() != null)
            dmmain.field("dsd", dsdStoreDAO.storeDSD(dm.getDsd(), database));

        //Store indexed data, save and rebuild index
        dmIndexStoreDAO.indexDatasetMetadata(dmmain, database, false);

        dmmain.save();
        return 1;
	}

	public int appendDatasetMetadata(DM dm, OGraphDatabase database) throws Exception {
		ODocument dmmain = dmLoadDAO.loadDatasetMetadataO(dm.getUid(), database);
		if (dmmain == null)
			return 0;

		//TODO remove old records

		//main
		if (dm.getParentUid() != null)
			dmmain.field("uidParent", dm.getParentUid());
		if (dm.getLanguage() != null)
			dmmain.field("language", dm.getLanguage());
		//description                                                    		if (dm.getWeightReferencePeriod() != null)
			dmmain.field("weightReferencePeriod", new ODocument("CMPeriod").fields(dm.getWeightReferencePeriod().toMap()));

		if (dm.getTitle() != null)
			dmmain.field("title", dm.getTitle());
		if (dm.getSummary() != null)
			dmmain.field("summary", dm.getSummary());
		if (dm.getDescription() != null)
			dmmain.field("abstract", dm.getDescription());
		if (dm.getSupplemental() != null)
			dmmain.field("supplemental", dm.getSupplemental());
		//content
		if (dm.getCreationDate() != null)
			dmmain.field("creationDate", dm.getCreationDate());
		if (dm.getGeographicExtent() != null)
			dmmain.field("region", clLoadDAO.loadCodeO(dm.getGeographicExtent().getSystemKey(), dm.getGeographicExtent().getSystemVersion(), dm.getGeographicExtent().getCode(), database));
		if (dm.getBasePeriod() != null)
			dmmain.field("basePeriod", new ODocument("CMPeriod").fields(dm.getBasePeriod().toMap()));
		if (dm.getReferencePeriod() != null)
			dmmain.field("referencePeriod", new ODocument("CMPeriod").fields(dm.getReferencePeriod().toMap()));
		if (dm.getWeightReferencePeriod() != null)
			dmmain.field("weightReferencePeriod", new ODocument("CMPeriod").fields(dm.getWeightReferencePeriod().toMap()));
		if (dm.getCoverageSectors() != null)
			dmmain.field("coverageSectors",loadHeterogeneousCodes(dm.getCoverageSectors(), database));
		if (dm.getClassificationSystems() != null)
			dmmain.field("codingSystems",loadCodeLists(dm.getCodingSystems(), database));
		if (dm.getClassificationSystems() != null)
			dmmain.field("classificationSystems", dm.getClassificationSystems());
		if (dm.getComparability() != null)
			dmmain.field("comparability", dm.getComparability());
		if (dm.getStatisticalUnit() != null)
			dmmain.field("statisticalUnit", dm.getStatisticalUnit());
        if (dm.getUnitOfMeasureDetails() != null)
            dmmain.field("unitOfMeasureDetails", dm.getUnitOfMeasureDetails());
        if (dm.getUnitOfMeasure() != null)
            dmmain.field("um", clLoadDAO.loadCodeO(dm.getUnitOfMeasure().getSystemKey(), dm.getUnitOfMeasure().getSystemVersion(), dm.getUnitOfMeasure().getCode(), database));
        if (dm.getCoverageGeographical() != null)
            dmmain.field("coverageGeographical", clLoadDAO.loadCodeO(dm.getCoverageGeographical().getSystemKey(), dm.getCoverageGeographical().getSystemVersion(), dm.getCoverageGeographical().getCode(), database));
		//process
		if (dm.getCollectionPeriodicity() != null)
			dmmain.field("collectionPeriodicity", clLoadDAO.loadCodeO(dm.getCollectionPeriodicity().getSystemKey(), dm.getCollectionPeriodicity().getSystemVersion(), dm.getCollectionPeriodicity().getCode(), database));
		if (dm.getProcessMethodologyDocuments() != null)
			dmmain.field("processMethodologyDocuments", cmStoreDAO.storePublication(dm.getProcessMethodologyDocuments(), database));
		if (dm.getCompilationProcessing() != null)
			dmmain.field("compilationProcessing", dm.getCompilationProcessing());
		if (dm.getCollectionProcessing() != null)
			dmmain.field("collectionProcessing", dm.getCollectionProcessing());
		if (dm.getIndexType() != null)
			dmmain.field("indexType", dm.getIndexType());
        if (dm.getProcessConceptsDocuments() != null)
            dmmain.field("processConceptsDocuments", cmStoreDAO.storePublication(dm.getProcessConceptsDocuments(), database));
        if (dm.getSourceType() != null)
            dmmain.field("sourceType", dm.getSourceType());
        if (dm.getProcessConcepts() != null)
            dmmain.field("processConcepts", dm.getProcessConcepts());
        if (dm.getAggregationProcessing() != null)
            dmmain.field("aggregationProcessing", dm.getAggregationProcessing());
        if (dm.getDataAdjustment() != null)
            dmmain.field("dataAdjustment", dm.getDataAdjustment());
        //quality
		if (dm.getAccuracy() != null)
			dmmain.field("accuracy", dm.getAccuracy());
		if (dm.getAccuracyAssessment() != null)
			dmmain.field("accuracyAssessment", dm.getAccuracyAssessment());
		if (dm.getCompleteness() != null)
			dmmain.field("completeness", dm.getCompleteness());
        if (dm.getQualityMethodologyDocuments() != null)
            dmmain.field("qualityMethodologyDocuments", cmStoreDAO.storePublication(dm.getQualityMethodologyDocuments(), database));
		//distribution
		if (dm.getDisseminationPeriodicity() != null)
			dmmain.field("disseminationPeriodicity", clLoadDAO.loadCodeO(dm.getDisseminationPeriodicity().getSystemKey(), dm.getDisseminationPeriodicity().getSystemVersion(), dm.getDisseminationPeriodicity().getCode(), database));
		if (dm.getUserAccess() != null)
			dmmain.field("userAccess", dm.getUserAccess());
		if (dm.getDisseminationFormat() != null)
			dmmain.field("disseminationFormat", dm.getDisseminationFormat());
		if (dm.getRevisionPolicy() != null)
			dmmain.field("revisionPolicy", dm.getRevisionPolicy());
        if (dm.getPublications() != null)
            dmmain.field("publications", cmStoreDAO.storePublication(dm.getPublications(), database));
        if (dm.getNews() != null)
            dmmain.field("news", cmStoreDAO.storePublication(dm.getNews(), database));
        if (dm.getEmbargoTimeSpec() != null)
            dmmain.field("embargoTimeSpec", dm.getEmbargoTimeSpec());
        if (dm.getConfidentialityDataTreatment() != null)
            dmmain.field("confidentialityDataTreatment", dm.getConfidentialityDataTreatment());
        if (dm.getConfidentialityPolicy() != null)
            dmmain.field("confidentialityPolicy", dm.getConfidentialityPolicy());
        if (dm.getReleasePolicy() != null)
            dmmain.field("releasePolicy", dm.getReleasePolicy());
        if (dm.getDataSources() != null)
            dmmain.field("dataSources", storeDatasource(dm.getDataSources(), database));
        if (dm.getOtherDisseminatedData() != null)
            dmmain.field("otherDisseminatedData", dm.getOtherDisseminatedData());

        //contacts
		if (dm.getSources() != null)
			dmmain.field("source", cmStoreDAO.storeContactIdentity(dm.getSources(), database));
		if (dm.getOwner() != null)
			dmmain.field("owner", cmStoreDAO.storeContactIdentity(dm.getOwner(), database));
		if (dm.getProvider() != null)
			dmmain.field("provider", cmStoreDAO.storeContactIdentity(dm.getProvider(), database));
		if (dm.getCompiler() != null)
			dmmain.field("compiler", cmStoreDAO.storeContactIdentity(dm.getCompiler(), database));
		if (dm.getContacts() != null)
			dmmain.field("contacts", cmStoreDAO.storeContactIdentity(dm.getContacts(), database));
		//status
		if (dm.getCoverageTime() != null)
			dmmain.field("coverageTime", dm.getCoverageTime());
		if (dm.getAccessibility() != null)
			dmmain.field("accessibility", dm.getAccessibility());
		if (dm.getUpdateDate() != null)
			dmmain.field("updateDate", dm.getUpdateDate());
		if (dm.getReferenceUpdateDate() != null)
			dmmain.field("referenceUpdateDate", dm.getReferenceUpdateDate());
		if (dm.getNextUpdateDate() != null)
			dmmain.field("nextUpdateDate", dm.getNextUpdateDate());
		if (dm.getCompletenessPercentage() != null)
			dmmain.field("completenessPercentage", dm.getCompletenessPercentage());
        if (dm.getUpdatePeriodicity() != null)
            dmmain.field("updatePeriodicity", clLoadDAO.loadCodeO(dm.getUpdatePeriodicity().getSystemKey(), dm.getUpdatePeriodicity().getSystemVersion(), dm.getUpdatePeriodicity().getCode(), database));
		//other
		if (dm.getTransferOptions() != null)
			dmmain.field("transferOptions", cmStoreDAO.storeLink(dm.getTransferOptions(), database));
		if (dm.getCategories() != null)
			dmmain.field("categories",loadHeterogeneousCodes(dm.getCategories(), database));
		if (dm.getStatisticalPopulation() != null)
			dmmain.field("statisticalPopulation", dm.getStatisticalPopulation());
		if (dm.getFreeExtension() != null)
			dmmain.field("freeExtension", dm.getFreeExtension());
		if (dm.getDataType() != null)
			dmmain.field("dataType", dm.getDataType() != null ? dm.getDataType().getCode() : null);
		if (dm.getDataKind() != null)
			dmmain.field("dataKind", dm.getDataKind() != null ? dm.getDataKind().getCode() : null);
		if (dm.getCopyright() != null)
			dmmain.field("copyrightPolicy", dm.getCopyright() != null ? dm.getCopyright().getCode() : null);
		//upload
		if (dm.getAvailability() != null)
			dmmain.field("availability", storeAvailability(dm.getAvailability(), database));

		//DSD
		if (dm.getDsd() != null)
			dmmain.field("dsd", dsdStoreDAO.storeDSD(dm.getDsd(), database));

        //Store indexed data, save and rebuild index
        dmIndexStoreDAO.indexDatasetMetadata(dmmain, database, false);

        dmmain.save();
		return 1;
	}

	// Assign categories
	public int addCategoriesToDataset(String datasetUID, Collection<Code> listOfCodes) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		int count = 0;
		try {
			count = addCategoriesToDataset(datasetUID, listOfCodes, database);
		} finally {
			if (database != null)
				database.close();
		}
		return count;
	}

	public int addCategoriesToDataset(String datasetUID,
			Collection<Code> listOfCodes, OGraphDatabase database)
			throws Exception {
		ODocument dmmain = dmLoadDAO.loadDatasetMetadataO(datasetUID, database);

		if (listOfCodes == null || listOfCodes.size() == 0 || dmmain == null)
			return 0;

		Collection<ODocument> codesExisting = dmmain.field("categories");
		Collection<ODocument> result = new HashSet<ODocument>();

		ODocument codeO;
		for (Code code : listOfCodes) {
			codeO = clLoadDAO.loadCodeO(code.getSystemKey(),
					code.getSystemVersion(), code.getCode(), database);
			if (codeO == null)
				throw new Exception("Category code not found: " + code);
			result.add(codeO);
		}
		if (codesExisting != null)
			result.addAll(codesExisting);

		dmmain.field("categories", result);
		dmmain.save();
		return 1;
	}

	// DELETE
	public int deleteMetadataStructure(String uid) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		int count = 0;
		try {
			count = deleteMetadataStructure(uid, database);
		} finally {
			if (database != null)
				database.close();
		}
		return count;
	}

	public int deleteMetadataStructure(String uid, OGraphDatabase database) throws Exception {
		ODocument dmO = dmLoadDAO.loadMetadataStructureO(uid, database);
        if (dmO!=null) {
            dmO.delete();
            return 1;
        }
        return 0;
	}

	public int deleteDatasetMetadata(String uid) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		int count = 0;
		try {
			count = deleteDatasetMetadata(uid, database);
		} finally {
			if (database != null)
				database.close();
		}
		return count;
	}

	public int deleteDatasetMetadata(String uid, OGraphDatabase database) throws Exception {
		ODocument dmO = dmLoadDAO.loadDatasetMetadataO(uid, database);
        dmIndexStoreDAO.dropIndexDatasetMetadata(dmO);
		return deleteGraph(dmO, new String[] { "CMContactIdentity", "DSDContextSystem", "DSDDimension", "CSVersion", "CSCode" });
	}

	public void disconnectCodeList(ODocument systemO, OGraphDatabase database) throws Exception {
		for (ODocument dm : dmLoadDAO.loadDatasetsObyRegionCL(systemO, database)) {
			dm.field("region", null, OType.LINK);
			dm.save();
		}
		for (ODocument dm : dmLoadDAO.loadDatasetsObyCategoryCL(systemO, database)) {
			dm.field("categories", null, OType.LINKLIST);
			dm.save();
		}

		dsdStoreDAO.disconnectCodeList(systemO, database);
	}

	// STORE
	public String storeMetadataStructure(DMMeta mm) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			return (String) storeMetadataStructure(mm, database).field("metadataUID");
		} finally {
			if (database != null)
				database.close();
		}
	}

	public ODocument storeMetadataStructure(DMMeta mm, OGraphDatabase database) throws Exception {
        //content normalization
        if (mm.getUid()==null)
            mm.setUid(createUID());
        if (mm.getAvailableDatasetsUID()!=null) {
            Collection<ODocument> datasets = new LinkedList<ODocument>();
            for (String uid : mm.getAvailableDatasetsUID())
                dmLoadDAO.loadDatasetMetadataO(uid, database);
            mm.put("availableDatasets",datasets);
        }

        //Create document
        ODocument mmO = database.createVertex("DMMeta").fields(mm);

		//Return saved document
		return mmO.save();
	}

	public String storeDatasetMetadata(DM dm) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			return (String) storeDatasetMetadata(dm, database).field("uid");
		} finally {
			if (database != null)
				database.close();
		}
	}

	public ODocument storeDatasetMetadata(DM dm, OGraphDatabase database) throws Exception {
        ODocument dmmain = dmLoadDAO.loadDatasetMetadataO(dm.getUid(), database);
        if (dmmain != null)
            throw new OIndexException("Found duplicated key");

        dmmain = database.createVertex("DMMain");
		String uid = dm.getUid() != null ? dm.getUid() : createUID();
		//main
		dmmain.field("uid", uid);
		dmmain.field("uidParent", dm.getParentUid());
		dmmain.field("language", dm.getLanguage());
		//description
		dmmain.field("title", dm.getTitle());
		dmmain.field("summary", dm.getSummary());
		dmmain.field("abstract", dm.getDescription());
		dmmain.field("supplemental", dm.getSupplemental());
		//content
		dmmain.field("creationDate", dm.getCreationDate());
		if (dm.getGeographicExtent() != null)
			dmmain.field("region", clLoadDAO.loadCodeO(dm.getGeographicExtent().getSystemKey(), dm.getGeographicExtent().getSystemVersion(), dm.getGeographicExtent().getCode(), database));
		dmmain.field("basePeriod", dm.getBasePeriod()!=null ? new ODocument("CMPeriod").fields(dm.getBasePeriod().toMap()) : null, OType.EMBEDDED);
		dmmain.field("referencePeriod", dm.getReferencePeriod()!=null ? new ODocument("CMPeriod").fields(dm.getReferencePeriod().toMap()) : null, OType.EMBEDDED);
		dmmain.field("weightReferencePeriod", dm.getWeightReferencePeriod()!=null ? new ODocument("CMPeriod").fields(dm.getWeightReferencePeriod().toMap()) : null, OType.EMBEDDED);
		if (dm.getCoverageSectors() != null)
			dmmain.field("coverageSectors",loadHeterogeneousCodes(dm.getCoverageSectors(), database));
		if (dm.getCodingSystems() != null)
			dmmain.field("codingSystems",loadCodeLists(dm.getCodingSystems(), database));
		dmmain.field("classificationSystems", dm.getClassificationSystems());
		dmmain.field("comparability", dm.getComparability());
        dmmain.field("statisticalUnit", dm.getStatisticalUnit());
        dmmain.field("unitOfMeasureDetails", dm.getUnitOfMeasureDetails());
        if (dm.getUnitOfMeasure() != null)
            dmmain.field("um", clLoadDAO.loadCodeO(dm.getUnitOfMeasure().getSystemKey(), dm.getUnitOfMeasure().getSystemVersion(), dm.getUnitOfMeasure().getCode(), database));
        if (dm.getCoverageGeographical() != null)
            dmmain.field("coverageGeographical", clLoadDAO.loadCodeO(dm.getCoverageGeographical().getSystemKey(), dm.getCoverageGeographical().getSystemVersion(), dm.getCoverageGeographical().getCode(), database));
		//process
		if (dm.getCollectionPeriodicity() != null)
			dmmain.field("collectionPeriodicity", clLoadDAO.loadCodeO(dm.getCollectionPeriodicity().getSystemKey(), dm.getCollectionPeriodicity().getSystemVersion(), dm.getCollectionPeriodicity().getCode(), database));
		if (dm.getProcessMethodologyDocuments() != null)
			dmmain.field("processMethodologyDocuments", cmStoreDAO.storePublication(dm.getProcessMethodologyDocuments(), database));
		dmmain.field("compilationProcessing", dm.getCompilationProcessing());
		dmmain.field("collectionProcessing", dm.getCollectionProcessing());
		dmmain.field("indexType", dm.getIndexType());
        if (dm.getProcessConceptsDocuments() != null)
            dmmain.field("processConceptsDocuments", cmStoreDAO.storePublication(dm.getProcessConceptsDocuments(), database));
        dmmain.field("sourceType", dm.getSourceType());
        dmmain.field("processConcepts", dm.getProcessConcepts());
        dmmain.field("aggregationProcessing", dm.getAggregationProcessing());
        dmmain.field("dataAdjustment", dm.getDataAdjustment());
		//quality
		dmmain.field("accuracy", dm.getAccuracy());
		dmmain.field("accuracyAssessment", dm.getAccuracyAssessment());
		dmmain.field("completeness", dm.getCompleteness());
        if (dm.getQualityMethodologyDocuments() != null)
            dmmain.field("qualityMethodologyDocuments", cmStoreDAO.storePublication(dm.getQualityMethodologyDocuments(), database));
		//distribution
		if (dm.getDisseminationPeriodicity() != null)
			dmmain.field("disseminationPeriodicity", clLoadDAO.loadCodeO(dm.getDisseminationPeriodicity().getSystemKey(), dm.getDisseminationPeriodicity().getSystemVersion(), dm.getDisseminationPeriodicity().getCode(), database));
		dmmain.field("userAccess", dm.getUserAccess());
		dmmain.field("disseminationFormat", dm.getDisseminationFormat());
		dmmain.field("revisionPolicy", dm.getRevisionPolicy());
        if (dm.getPublications() != null)
            dmmain.field("publications", cmStoreDAO.storePublication(dm.getPublications(), database));
        if (dm.getNews() != null)
            dmmain.field("news", cmStoreDAO.storePublication(dm.getNews(), database));
        dmmain.field("embargoTimeSpec", dm.getEmbargoTimeSpec());
        dmmain.field("confidentialityDataTreatment", dm.getConfidentialityDataTreatment());
        dmmain.field("confidentialityPolicy", dm.getConfidentialityPolicy());
        dmmain.field("releasePolicy", dm.getReleasePolicy());
        dmmain.field("dataSources", dm.getDataSources()==null ? null : storeDatasource(dm.getDataSources(), database));
        dmmain.field("otherDisseminatedData", dm.getOtherDisseminatedData());
		//contacts
		if (dm.getSources() != null)
			dmmain.field("source", cmStoreDAO.storeContactIdentity(dm.getSources(), database));
		if (dm.getOwner() != null)
			dmmain.field("owner", cmStoreDAO.storeContactIdentity(dm.getOwner(), database));
		if (dm.getProvider() != null)
			dmmain.field("provider", cmStoreDAO.storeContactIdentity(dm.getProvider(), database));
		if (dm.getCompiler() != null)
			dmmain.field("compiler", cmStoreDAO.storeContactIdentity(dm.getCompiler(), database));
		if (dm.getContacts() != null)
			dmmain.field("contacts", cmStoreDAO.storeContactIdentity(dm.getContacts(), database));
		//status
		dmmain.field("coverageTime", dm.getCoverageTime());
		dmmain.field("accessibility", dm.getAccessibility());
		dmmain.field("updateDate", dm.getUpdateDate());
		dmmain.field("referenceUpdateDate", dm.getReferenceUpdateDate());
		dmmain.field("nextUpdateDate", dm.getNextUpdateDate());
		dmmain.field("completenessPercentage", dm.getCompletenessPercentage());
        if (dm.getUpdatePeriodicity() != null)
            dmmain.field("updatePeriodicity", clLoadDAO.loadCodeO(dm.getUpdatePeriodicity().getSystemKey(), dm.getUpdatePeriodicity().getSystemVersion(), dm.getUpdatePeriodicity().getCode(), database));
		//other
		if (dm.getTransferOptions() != null)
			dmmain.field("transferOptions", cmStoreDAO.storeLink(dm.getTransferOptions(), database));
		if (dm.getCategories() != null)
			dmmain.field("categories",loadHeterogeneousCodes(dm.getCategories(), database));
		dmmain.field("statisticalPopulation", dm.getStatisticalPopulation());
		dmmain.field("freeExtension", dm.getFreeExtension());
		dmmain.field("dataType", dm.getDataType() != null ? dm.getDataType().getCode() : null);
		dmmain.field("dataKind", dm.getDataKind() != null ? dm.getDataKind().getCode() : null);
		dmmain.field("copyrightPolicy", dm.getCopyright() != null ? dm.getCopyright().getCode() : null);
		//upload
		if (dm.getAvailability() != null)
			dmmain.field("availability", storeAvailability(dm.getAvailability(), database));

		//DSD
        if (dm.getDsd()!=null)
		    dmmain.field("dsd", dsdStoreDAO.storeDSD(dm.getDsd(), database));

        //Store indexed data
        dmIndexStoreDAO.indexDatasetMetadata(dmmain, database, false);

		//Return saved document
		return dmmain.save();
	}

	private Collection<ODocument> storeDatasource(Collection<DMDataSource> dataSources, OGraphDatabase database) throws Exception {
        if (dataSources !=null) {
            Collection<ODocument> dataSourcesO = new LinkedList<ODocument>();
            for (DMDataSource dataSource : dataSources)
                dataSourcesO.add(storeDatasource(dataSource,database));
            return dataSourcesO;
        } else
            return null;
	}

	private ODocument storeDatasource(DMDataSource dataSource, OGraphDatabase database) throws Exception {
        ODocument datasourceO = database.createVertex("DMDataSource");
        datasourceO.field("type", dataSource.getType() != null ? dataSource.getType().getCode() : null);
        datasourceO.field("reference", dataSource.getReference());
        datasourceO.field("title", dataSource.getTitle());
        return datasourceO.save();
	}

	private ODocument storeAvailability(DMAvailability availability, OGraphDatabase database) throws Exception {
		ODocument dmavailability = database.createVertex("DMAvailability");
		dmavailability.field("status", availability.getStatus().getCode());
		dmavailability.field("chunksNumber", availability.getChunksNumber());
		dmavailability.field("chunksIndex", availability.getChunksIndex());
		return dmavailability.save();
	}



    // Utils
	private Collection<ODocument> loadHeterogeneousCodes (Collection<Code> codes, OGraphDatabase database) throws Exception {
		Collection<ODocument> codesO = new LinkedList<ODocument>();
        ODocument codeO;
		for (Code code : codes)
            if ((codeO = clLoadDAO.loadCodeO(code.getSystemKey(), code.getSystemVersion(), code.getCode(), database))!=null)
                codesO.add(codeO);
            else
                throw new Exception("Code not found: "+code);
		return codesO;
	}
	private Collection<ODocument> loadCodeLists (Collection<CodeSystem> systems, OGraphDatabase database) throws Exception {
		Collection<ODocument> systemsO = new LinkedList<ODocument>();
        ODocument systemO;
		for (CodeSystem system : systems)
            if ((systemO = clLoadDAO.loadSystemO(system.getSystem(), system.getVersion(), database))!=null)
			    systemsO.add(systemO);
            else
                throw new Exception("Code list not found: "+system);
		return systemsO;
	}
	
	public static String createUID() { return 'D'+new com.eaio.uuid.UUID().toString(); }

}

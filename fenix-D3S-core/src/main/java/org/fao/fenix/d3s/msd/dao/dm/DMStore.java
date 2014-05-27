package org.fao.fenix.d3s.msd.dao.dm;

import java.util.*;

import com.orientechnologies.orient.core.index.OIndexException;
import org.fao.fenix.commons.utils.UIDUtils;
import org.fao.fenix.d3s.msd.dao.common.CommonsStore;
import org.fao.fenix.d3s.server.tools.orient.OrientDao;
import org.fao.fenix.d3s.server.tools.orient.OrientDatabase;
import org.fao.fenix.d3s.msd.dao.cl.CodeListLoad;
import org.fao.fenix.d3s.msd.dao.dsd.DSDStore;
import org.fao.fenix.commons.msd.dto.full.cl.Code;
import org.fao.fenix.commons.msd.dto.full.cl.CodeSystem;
import org.fao.fenix.commons.msd.dto.full.dm.DM;
import org.fao.fenix.commons.msd.dto.full.dm.DMAvailability;
import org.fao.fenix.commons.msd.dto.full.dm.DMDataSource;
import org.fao.fenix.commons.msd.dto.full.dm.DMMeta;

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
    @Inject private UIDUtils uidUtil;

	// UPDATE
	public int updateMetadataStructure(DMMeta mm, boolean append) throws Exception {
		return append ? appendMetadataStructure(mm) : updateMetadataStructure(mm);
	}

	public int updateMetadataStructure(DMMeta mm) throws Exception {
        //Select record
        ODocument mmO = dmLoadDAO.loadMetadataStructureO(mm.getUid());
		if (mmO == null)
			return 0;

        //content normalization
        if (mm.getAvailableDatasetsUID()!=null) {
            Collection<ODocument> datasets = new LinkedList<ODocument>();
            for (String uid : mm.getAvailableDatasetsUID())
                dmLoadDAO.loadDatasetMetadataO(uid);
            mm.put("availableDatasets",datasets);
        }

        //Update document
        getConnection().createVertex("DMMeta").fields(mm);
		mmO.save();
		return 1;
	}

    public int appendMetadataStructure(DMMeta mm) throws Exception {
        ODocument mmO = dmLoadDAO.loadMetadataStructureO(mm.getUid());
        if (mmO == null)
            return 0;

        //TODO

        mmO.save();
        return 1;
    }

	public int updateDatasetMetadata(DM dm, boolean append) throws Exception {
		return append ? appendDatasetMetadata(dm) : updateDatasetMetadata(dm);
	}

	public int updateDatasetMetadata(DM dm) throws Exception {
		ODocument dmmain = dmLoadDAO.loadDatasetMetadataO(dm.getUid());
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
        dmmain.field("region", dm.getGeographicExtent() != null ? clLoadDAO.loadCodeO(dm.getGeographicExtent().getSystemKey(), dm.getGeographicExtent().getSystemVersion(), dm.getGeographicExtent().getCode()):null, OType.LINK);
        dmmain.field("basePeriod", dm.getBasePeriod()!=null ? new ODocument("CMPeriod").fields(dm.getBasePeriod().toMap()) : null, OType.EMBEDDED);
        dmmain.field("referencePeriod", dm.getReferencePeriod()!=null ? new ODocument("CMPeriod").fields(dm.getReferencePeriod().toMap()) : null, OType.EMBEDDED);
        dmmain.field("weightReferencePeriod", dm.getWeightReferencePeriod()!=null ? new ODocument("CMPeriod").fields(dm.getWeightReferencePeriod().toMap()) : null, OType.EMBEDDED);
		dmmain.field("coverageSectors", dm.getCoverageSectors()==null ? null : loadHeterogeneousCodes(dm.getCoverageSectors()));
		dmmain.field("codingSystems", dm.getCodingSystems()==null ? null : loadCodeLists(dm.getCodingSystems()));
		dmmain.field("classificationSystems", dm.getClassificationSystems());
		dmmain.field("comparability", dm.getComparability());
		dmmain.field("statisticalUnit", dm.getStatisticalUnit());
        dmmain.field("unitOfMeasureDetails", dm.getUnitOfMeasureDetails());
        dmmain.field("um", dm.getUnitOfMeasure()==null ? null : clLoadDAO.loadCodeO(dm.getUnitOfMeasure().getSystemKey(), dm.getUnitOfMeasure().getSystemVersion(), dm.getUnitOfMeasure().getCode()));
        dmmain.field("coverageGeographical", dm.getCoverageGeographical()==null ? null : clLoadDAO.loadCodeO(dm.getCoverageGeographical().getSystemKey(), dm.getCoverageGeographical().getSystemVersion(), dm.getCoverageGeographical().getCode()));
		//process
		dmmain.field("collectionPeriodicity", dm.getCollectionPeriodicity()==null ? null : clLoadDAO.loadCodeO(dm.getCollectionPeriodicity().getSystemKey(), dm.getCollectionPeriodicity().getSystemVersion(), dm.getCollectionPeriodicity().getCode()));
		dmmain.field("processMethodologyDocuments", dm.getProcessMethodologyDocuments()==null ? null : cmStoreDAO.storePublication(dm.getProcessMethodologyDocuments()));
		dmmain.field("compilationProcessing", dm.getCompilationProcessing());
		dmmain.field("collectionProcessing", dm.getCollectionProcessing());
		dmmain.field("indexType", dm.getIndexType());
        dmmain.field("processConceptsDocuments", dm.getProcessConceptsDocuments()==null ? null : cmStoreDAO.storePublication(dm.getProcessConceptsDocuments()));
        dmmain.field("sourceType", dm.getSourceType());
        dmmain.field("processConcepts", dm.getProcessConcepts());
        dmmain.field("aggregationProcessing", dm.getAggregationProcessing());
        dmmain.field("dataAdjustment", dm.getDataAdjustment());
        //quality
		dmmain.field("accuracy", dm.getAccuracy());
		dmmain.field("accuracyAssessment", dm.getAccuracyAssessment());
		dmmain.field("completeness", dm.getCompleteness());
        dmmain.field("qualityMethodologyDocuments", dm.getQualityMethodologyDocuments()==null ? null : cmStoreDAO.storePublication(dm.getQualityMethodologyDocuments()));
		//distribution
		dmmain.field("disseminationPeriodicity", dm.getDisseminationPeriodicity()==null ? null : clLoadDAO.loadCodeO(dm.getDisseminationPeriodicity().getSystemKey(), dm.getDisseminationPeriodicity().getSystemVersion(), dm.getDisseminationPeriodicity().getCode()));
		dmmain.field("userAccess", dm.getUserAccess());
		dmmain.field("disseminationFormat", dm.getDisseminationFormat());
		dmmain.field("revisionPolicy", dm.getRevisionPolicy());
        dmmain.field("publications", dm.getPublications()==null ? null : cmStoreDAO.storePublication(dm.getPublications()));
        dmmain.field("news", dm.getNews()==null ? null : cmStoreDAO.storePublication(dm.getNews()));
        dmmain.field("embargoTimeSpec", dm.getEmbargoTimeSpec());
        dmmain.field("confidentialityDataTreatment", dm.getConfidentialityDataTreatment());
        dmmain.field("confidentialityPolicy", dm.getConfidentialityPolicy());
        dmmain.field("releasePolicy", dm.getReleasePolicy());
        dmmain.field("dataSources", dm.getDataSources()==null ? null : storeDatasource(dm.getDataSources()));
        dmmain.field("otherDisseminatedData", dm.getOtherDisseminatedData());
        //contacts
		dmmain.field("source", dm.getSources()==null ? null : cmStoreDAO.storeContactIdentity(dm.getSources()));
		dmmain.field("owner", dm.getOwner()==null ? null : cmStoreDAO.storeContactIdentity(dm.getOwner()));
		dmmain.field("provider", dm.getProvider()==null ? null : cmStoreDAO.storeContactIdentity(dm.getProvider()));
		dmmain.field("compiler", dm.getCompiler()==null ? null : cmStoreDAO.storeContactIdentity(dm.getCompiler()));
		dmmain.field("contacts", dm.getContacts()==null ? null : cmStoreDAO.storeContactIdentity(dm.getContacts()));
		//status
		dmmain.field("coverageTime", dm.getCoverageTime());
		dmmain.field("accessibility", dm.getAccessibility());
		dmmain.field("updateDate", dm.getUpdateDate());
		dmmain.field("referenceUpdateDate", dm.getReferenceUpdateDate());
		dmmain.field("nextUpdateDate", dm.getNextUpdateDate());
		dmmain.field("completenessPercentage", dm.getCompletenessPercentage());
        dmmain.field("updatePeriodicity", dm.getUpdatePeriodicity()==null ? null : clLoadDAO.loadCodeO(dm.getUpdatePeriodicity().getSystemKey(), dm.getUpdatePeriodicity().getSystemVersion(), dm.getUpdatePeriodicity().getCode()));
		//other
		dmmain.field("transferOptions", dm.getTransferOptions()==null ? null : cmStoreDAO.storeLink(dm.getTransferOptions()));
		dmmain.field("categories", dm.getCategories()==null ? null : loadHeterogeneousCodes(dm.getCategories()));
		dmmain.field("statisticalPopulation", dm.getStatisticalPopulation());
		dmmain.field("freeExtension", dm.getFreeExtension());
		dmmain.field("dataType", dm.getDataType() != null ? dm.getDataType().getCode() : null);
		dmmain.field("dataKind", dm.getDataKind() != null ? dm.getDataKind().getCode() : null);
		dmmain.field("copyrightPolicy", dm.getCopyright() != null ? dm.getCopyright().getCode() : null);
		//upload
		dmmain.field("availability", dm.getAvailability()==null ? null : storeAvailability(dm.getAvailability()));

		//DSD
        if (dm.getDsd() != null)
            dmmain.field("dsd", dsdStoreDAO.storeDSD(dm.getDsd()));

        //Store indexed data, save and rebuild index
        dmIndexStoreDAO.indexDatasetMetadata(dmmain, false);

        dmmain.save();
        return 1;
	}

	public int appendDatasetMetadata(DM dm) throws Exception {
		ODocument dmmain = dmLoadDAO.loadDatasetMetadataO(dm.getUid());
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
			dmmain.field("region", clLoadDAO.loadCodeO(dm.getGeographicExtent().getSystemKey(), dm.getGeographicExtent().getSystemVersion(), dm.getGeographicExtent().getCode()));
		if (dm.getBasePeriod() != null)
			dmmain.field("basePeriod", new ODocument("CMPeriod").fields(dm.getBasePeriod().toMap()));
		if (dm.getReferencePeriod() != null)
			dmmain.field("referencePeriod", new ODocument("CMPeriod").fields(dm.getReferencePeriod().toMap()));
		if (dm.getWeightReferencePeriod() != null)
			dmmain.field("weightReferencePeriod", new ODocument("CMPeriod").fields(dm.getWeightReferencePeriod().toMap()));
		if (dm.getCoverageSectors() != null)
			dmmain.field("coverageSectors",loadHeterogeneousCodes(dm.getCoverageSectors()));
		if (dm.getClassificationSystems() != null)
			dmmain.field("codingSystems",loadCodeLists(dm.getCodingSystems()));
		if (dm.getClassificationSystems() != null)
			dmmain.field("classificationSystems", dm.getClassificationSystems());
		if (dm.getComparability() != null)
			dmmain.field("comparability", dm.getComparability());
		if (dm.getStatisticalUnit() != null)
			dmmain.field("statisticalUnit", dm.getStatisticalUnit());
        if (dm.getUnitOfMeasureDetails() != null)
            dmmain.field("unitOfMeasureDetails", dm.getUnitOfMeasureDetails());
        if (dm.getUnitOfMeasure() != null)
            dmmain.field("um", clLoadDAO.loadCodeO(dm.getUnitOfMeasure().getSystemKey(), dm.getUnitOfMeasure().getSystemVersion(), dm.getUnitOfMeasure().getCode()));
        if (dm.getCoverageGeographical() != null)
            dmmain.field("coverageGeographical", clLoadDAO.loadCodeO(dm.getCoverageGeographical().getSystemKey(), dm.getCoverageGeographical().getSystemVersion(), dm.getCoverageGeographical().getCode()));
		//process
		if (dm.getCollectionPeriodicity() != null)
			dmmain.field("collectionPeriodicity", clLoadDAO.loadCodeO(dm.getCollectionPeriodicity().getSystemKey(), dm.getCollectionPeriodicity().getSystemVersion(), dm.getCollectionPeriodicity().getCode()));
		if (dm.getProcessMethodologyDocuments() != null)
			dmmain.field("processMethodologyDocuments", cmStoreDAO.storePublication(dm.getProcessMethodologyDocuments()));
		if (dm.getCompilationProcessing() != null)
			dmmain.field("compilationProcessing", dm.getCompilationProcessing());
		if (dm.getCollectionProcessing() != null)
			dmmain.field("collectionProcessing", dm.getCollectionProcessing());
		if (dm.getIndexType() != null)
			dmmain.field("indexType", dm.getIndexType());
        if (dm.getProcessConceptsDocuments() != null)
            dmmain.field("processConceptsDocuments", cmStoreDAO.storePublication(dm.getProcessConceptsDocuments()));
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
            dmmain.field("qualityMethodologyDocuments", cmStoreDAO.storePublication(dm.getQualityMethodologyDocuments()));
		//distribution
		if (dm.getDisseminationPeriodicity() != null)
			dmmain.field("disseminationPeriodicity", clLoadDAO.loadCodeO(dm.getDisseminationPeriodicity().getSystemKey(), dm.getDisseminationPeriodicity().getSystemVersion(), dm.getDisseminationPeriodicity().getCode()));
		if (dm.getUserAccess() != null)
			dmmain.field("userAccess", dm.getUserAccess());
		if (dm.getDisseminationFormat() != null)
			dmmain.field("disseminationFormat", dm.getDisseminationFormat());
		if (dm.getRevisionPolicy() != null)
			dmmain.field("revisionPolicy", dm.getRevisionPolicy());
        if (dm.getPublications() != null)
            dmmain.field("publications", cmStoreDAO.storePublication(dm.getPublications()));
        if (dm.getNews() != null)
            dmmain.field("news", cmStoreDAO.storePublication(dm.getNews()));
        if (dm.getEmbargoTimeSpec() != null)
            dmmain.field("embargoTimeSpec", dm.getEmbargoTimeSpec());
        if (dm.getConfidentialityDataTreatment() != null)
            dmmain.field("confidentialityDataTreatment", dm.getConfidentialityDataTreatment());
        if (dm.getConfidentialityPolicy() != null)
            dmmain.field("confidentialityPolicy", dm.getConfidentialityPolicy());
        if (dm.getReleasePolicy() != null)
            dmmain.field("releasePolicy", dm.getReleasePolicy());
        if (dm.getDataSources() != null)
            dmmain.field("dataSources", storeDatasource(dm.getDataSources()));
        if (dm.getOtherDisseminatedData() != null)
            dmmain.field("otherDisseminatedData", dm.getOtherDisseminatedData());

        //contacts
		if (dm.getSources() != null)
			dmmain.field("source", cmStoreDAO.storeContactIdentity(dm.getSources()));
		if (dm.getOwner() != null)
			dmmain.field("owner", cmStoreDAO.storeContactIdentity(dm.getOwner()));
		if (dm.getProvider() != null)
			dmmain.field("provider", cmStoreDAO.storeContactIdentity(dm.getProvider()));
		if (dm.getCompiler() != null)
			dmmain.field("compiler", cmStoreDAO.storeContactIdentity(dm.getCompiler()));
		if (dm.getContacts() != null)
			dmmain.field("contacts", cmStoreDAO.storeContactIdentity(dm.getContacts()));
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
            dmmain.field("updatePeriodicity", clLoadDAO.loadCodeO(dm.getUpdatePeriodicity().getSystemKey(), dm.getUpdatePeriodicity().getSystemVersion(), dm.getUpdatePeriodicity().getCode()));
		//other
		if (dm.getTransferOptions() != null)
			dmmain.field("transferOptions", cmStoreDAO.storeLink(dm.getTransferOptions()));
		if (dm.getCategories() != null)
			dmmain.field("categories",loadHeterogeneousCodes(dm.getCategories()));
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
			dmmain.field("availability", storeAvailability(dm.getAvailability()));

		//DSD
		if (dm.getDsd() != null)
			dmmain.field("dsd", dsdStoreDAO.storeDSD(dm.getDsd()));

        //Store indexed data, save and rebuild index
        dmIndexStoreDAO.indexDatasetMetadata(dmmain, false);

        dmmain.save();
		return 1;
	}

	// Assign categories
	public int addCategoriesToDataset(String datasetUID, Collection<Code> listOfCodes) throws Exception {
		ODocument dmmain = dmLoadDAO.loadDatasetMetadataO(datasetUID);

		if (listOfCodes == null || listOfCodes.size() == 0 || dmmain == null)
			return 0;

		Collection<ODocument> codesExisting = dmmain.field("categories");
		Collection<ODocument> result = new HashSet<ODocument>();

		ODocument codeO;
		for (Code code : listOfCodes) {
			codeO = clLoadDAO.loadCodeO(code.getSystemKey(),
					code.getSystemVersion(), code.getCode());
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
		ODocument dmO = dmLoadDAO.loadMetadataStructureO(uid);
        if (dmO!=null) {
            dmO.delete();
            return 1;
        }
        return 0;
	}

	public int deleteDatasetMetadata(String uid) throws Exception {
		ODocument dmO = dmLoadDAO.loadDatasetMetadataO(uid);
        dmIndexStoreDAO.dropIndexDatasetMetadata(dmO);
		return deleteGraph(dmO, new String[] { "CMContactIdentity", "DSDContextSystem", "DSDDimension", "CSVersion", "CSCode" });
	}

	public void disconnectCodeList(ODocument systemO) throws Exception {
		for (ODocument dm : dmLoadDAO.loadDatasetsObyRegionCL(systemO)) {
			dm.field("region", null, OType.LINK);
			dm.save();
		}
		for (ODocument dm : dmLoadDAO.loadDatasetsObyCategoryCL(systemO)) {
			dm.field("categories", null, OType.LINKLIST);
			dm.save();
		}

		dsdStoreDAO.disconnectCodeList(systemO);
	}

	// STORE
	public ODocument storeMetadataStructure(DMMeta mm) throws Exception {
        //content normalization
        if (mm.getUid()==null)
            mm.setUid(uidUtil.newId("D3S","metadataStructure"));
        if (mm.getAvailableDatasetsUID()!=null) {
            Collection<ODocument> datasets = new LinkedList<ODocument>();
            for (String uid : mm.getAvailableDatasetsUID())
                dmLoadDAO.loadDatasetMetadataO(uid);
            mm.put("availableDatasets",datasets);
        }

        //Create document
        ODocument mmO = getConnection().createVertex("DMMeta").fields(mm);

		//Return saved document
		return mmO.save();
	}

	public ODocument storeDatasetMetadata(DM dm) throws Exception {
        ODocument dmmain = dmLoadDAO.loadDatasetMetadataO(dm.getUid());
        if (dmmain != null)
            throw new OIndexException("Found duplicated key");

        dmmain = getConnection().createVertex("DMMain");
		String uid = dm.getUid() != null ? dm.getUid() : uidUtil.newId("D3S","dataset");
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
			dmmain.field("region", clLoadDAO.loadCodeO(dm.getGeographicExtent().getSystemKey(), dm.getGeographicExtent().getSystemVersion(), dm.getGeographicExtent().getCode()));
		dmmain.field("basePeriod", dm.getBasePeriod()!=null ? new ODocument("CMPeriod").fields(dm.getBasePeriod().toMap()) : null, OType.EMBEDDED);
		dmmain.field("referencePeriod", dm.getReferencePeriod()!=null ? new ODocument("CMPeriod").fields(dm.getReferencePeriod().toMap()) : null, OType.EMBEDDED);
		dmmain.field("weightReferencePeriod", dm.getWeightReferencePeriod()!=null ? new ODocument("CMPeriod").fields(dm.getWeightReferencePeriod().toMap()) : null, OType.EMBEDDED);
		if (dm.getCoverageSectors() != null)
			dmmain.field("coverageSectors",loadHeterogeneousCodes(dm.getCoverageSectors()));
		if (dm.getCodingSystems() != null)
			dmmain.field("codingSystems",loadCodeLists(dm.getCodingSystems()));
		dmmain.field("classificationSystems", dm.getClassificationSystems());
		dmmain.field("comparability", dm.getComparability());
        dmmain.field("statisticalUnit", dm.getStatisticalUnit());
        dmmain.field("unitOfMeasureDetails", dm.getUnitOfMeasureDetails());
        if (dm.getUnitOfMeasure() != null)
            dmmain.field("um", clLoadDAO.loadCodeO(dm.getUnitOfMeasure().getSystemKey(), dm.getUnitOfMeasure().getSystemVersion(), dm.getUnitOfMeasure().getCode()));
        if (dm.getCoverageGeographical() != null)
            dmmain.field("coverageGeographical", clLoadDAO.loadCodeO(dm.getCoverageGeographical().getSystemKey(), dm.getCoverageGeographical().getSystemVersion(), dm.getCoverageGeographical().getCode()));
		//process
		if (dm.getCollectionPeriodicity() != null)
			dmmain.field("collectionPeriodicity", clLoadDAO.loadCodeO(dm.getCollectionPeriodicity().getSystemKey(), dm.getCollectionPeriodicity().getSystemVersion(), dm.getCollectionPeriodicity().getCode()));
		if (dm.getProcessMethodologyDocuments() != null)
			dmmain.field("processMethodologyDocuments", cmStoreDAO.storePublication(dm.getProcessMethodologyDocuments()));
		dmmain.field("compilationProcessing", dm.getCompilationProcessing());
		dmmain.field("collectionProcessing", dm.getCollectionProcessing());
		dmmain.field("indexType", dm.getIndexType());
        if (dm.getProcessConceptsDocuments() != null)
            dmmain.field("processConceptsDocuments", cmStoreDAO.storePublication(dm.getProcessConceptsDocuments()));
        dmmain.field("sourceType", dm.getSourceType());
        dmmain.field("processConcepts", dm.getProcessConcepts());
        dmmain.field("aggregationProcessing", dm.getAggregationProcessing());
        dmmain.field("dataAdjustment", dm.getDataAdjustment());
		//quality
		dmmain.field("accuracy", dm.getAccuracy());
		dmmain.field("accuracyAssessment", dm.getAccuracyAssessment());
		dmmain.field("completeness", dm.getCompleteness());
        if (dm.getQualityMethodologyDocuments() != null)
            dmmain.field("qualityMethodologyDocuments", cmStoreDAO.storePublication(dm.getQualityMethodologyDocuments()));
		//distribution
		if (dm.getDisseminationPeriodicity() != null)
			dmmain.field("disseminationPeriodicity", clLoadDAO.loadCodeO(dm.getDisseminationPeriodicity().getSystemKey(), dm.getDisseminationPeriodicity().getSystemVersion(), dm.getDisseminationPeriodicity().getCode()));
		dmmain.field("userAccess", dm.getUserAccess());
		dmmain.field("disseminationFormat", dm.getDisseminationFormat());
		dmmain.field("revisionPolicy", dm.getRevisionPolicy());
        if (dm.getPublications() != null)
            dmmain.field("publications", cmStoreDAO.storePublication(dm.getPublications()));
        if (dm.getNews() != null)
            dmmain.field("news", cmStoreDAO.storePublication(dm.getNews()));
        dmmain.field("embargoTimeSpec", dm.getEmbargoTimeSpec());
        dmmain.field("confidentialityDataTreatment", dm.getConfidentialityDataTreatment());
        dmmain.field("confidentialityPolicy", dm.getConfidentialityPolicy());
        dmmain.field("releasePolicy", dm.getReleasePolicy());
        dmmain.field("dataSources", dm.getDataSources()==null ? null : storeDatasource(dm.getDataSources()));
        dmmain.field("otherDisseminatedData", dm.getOtherDisseminatedData());
		//contacts
		if (dm.getSources() != null)
			dmmain.field("source", cmStoreDAO.storeContactIdentity(dm.getSources()));
		if (dm.getOwner() != null)
			dmmain.field("owner", cmStoreDAO.storeContactIdentity(dm.getOwner()));
		if (dm.getProvider() != null)
			dmmain.field("provider", cmStoreDAO.storeContactIdentity(dm.getProvider()));
		if (dm.getCompiler() != null)
			dmmain.field("compiler", cmStoreDAO.storeContactIdentity(dm.getCompiler()));
		if (dm.getContacts() != null)
			dmmain.field("contacts", cmStoreDAO.storeContactIdentity(dm.getContacts()));
		//status
		dmmain.field("coverageTime", dm.getCoverageTime());
		dmmain.field("accessibility", dm.getAccessibility());
		dmmain.field("updateDate", dm.getUpdateDate());
		dmmain.field("referenceUpdateDate", dm.getReferenceUpdateDate());
		dmmain.field("nextUpdateDate", dm.getNextUpdateDate());
		dmmain.field("completenessPercentage", dm.getCompletenessPercentage());
        if (dm.getUpdatePeriodicity() != null)
            dmmain.field("updatePeriodicity", clLoadDAO.loadCodeO(dm.getUpdatePeriodicity().getSystemKey(), dm.getUpdatePeriodicity().getSystemVersion(), dm.getUpdatePeriodicity().getCode()));
		//other
		if (dm.getTransferOptions() != null)
			dmmain.field("transferOptions", cmStoreDAO.storeLink(dm.getTransferOptions()));
		if (dm.getCategories() != null)
			dmmain.field("categories",loadHeterogeneousCodes(dm.getCategories()));
		dmmain.field("statisticalPopulation", dm.getStatisticalPopulation());
		dmmain.field("freeExtension", dm.getFreeExtension());
		dmmain.field("dataType", dm.getDataType() != null ? dm.getDataType().getCode() : null);
		dmmain.field("dataKind", dm.getDataKind() != null ? dm.getDataKind().getCode() : null);
		dmmain.field("copyrightPolicy", dm.getCopyright() != null ? dm.getCopyright().getCode() : null);
		//upload
		if (dm.getAvailability() != null)
			dmmain.field("availability", storeAvailability(dm.getAvailability()));

		//DSD
        if (dm.getDsd()!=null)
		    dmmain.field("dsd", dsdStoreDAO.storeDSD(dm.getDsd()));

        //Store indexed data
        dmIndexStoreDAO.indexDatasetMetadata(dmmain, false);

		//Return saved document
		return dmmain.save();
	}

	private Collection<ODocument> storeDatasource(Collection<DMDataSource> dataSources) throws Exception {
        if (dataSources !=null) {
            Collection<ODocument> dataSourcesO = new LinkedList<ODocument>();
            for (DMDataSource dataSource : dataSources)
                dataSourcesO.add(storeDatasource(dataSource));
            return dataSourcesO;
        } else
            return null;
	}

	private ODocument storeDatasource(DMDataSource dataSource) throws Exception {
        ODocument datasourceO = getConnection().createVertex("DMDataSource");
        datasourceO.field("type", dataSource.getType() != null ? dataSource.getType().getCode() : null);
        datasourceO.field("reference", dataSource.getReference());
        datasourceO.field("title", dataSource.getTitle());
        return datasourceO.save();
	}

	private ODocument storeAvailability(DMAvailability availability) throws Exception {
		ODocument dmavailability = getConnection().createVertex("DMAvailability");
		dmavailability.field("status", availability.getStatus().getCode());
		dmavailability.field("chunksNumber", availability.getChunksNumber());
		dmavailability.field("chunksIndex", availability.getChunksIndex());
		return dmavailability.save();
	}



    // Utils
	private Collection<ODocument> loadHeterogeneousCodes (Collection<Code> codes) throws Exception {
		Collection<ODocument> codesO = new LinkedList<ODocument>();
        ODocument codeO;
		for (Code code : codes)
            if ((codeO = clLoadDAO.loadCodeO(code.getSystemKey(), code.getSystemVersion(), code.getCode()))!=null)
                codesO.add(codeO);
            else
                throw new Exception("Code not found: "+code);
		return codesO;
	}
	private Collection<ODocument> loadCodeLists (Collection<CodeSystem> systems) throws Exception {
		Collection<ODocument> systemsO = new LinkedList<ODocument>();
        ODocument systemO;
		for (CodeSystem system : systems)
            if ((systemO = clLoadDAO.loadSystemO(system.getSystem(), system.getVersion()))!=null)
			    systemsO.add(systemO);
            else
                throw new Exception("Code list not found: "+system);
		return systemsO;
	}
	


}

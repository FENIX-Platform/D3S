package org.fao.fenix.msd.dao.dm;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;

import org.fao.fenix.msd.dao.cl.CodeListConverter;
import org.fao.fenix.msd.dao.common.CommonsConverter;
import org.fao.fenix.msd.dao.dsd.DSDConverter;
import org.fao.fenix.msd.dto.common.Period;
import org.fao.fenix.msd.dto.dm.DM;
import org.fao.fenix.msd.dto.dm.DMAvailability;
import org.fao.fenix.msd.dto.dm.DMDataSource;
import org.fao.fenix.msd.dto.dm.DMMeta;
import org.fao.fenix.msd.dto.dm.type.*;
import org.fao.fenix.server.tools.orient.OrientDao;
import org.fao.fenix.server.utils.JSONUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.record.impl.ODocument;

@Component
public class DMConverter {
	
	@Autowired private CommonsConverter cmConverter;
	@Autowired private CodeListConverter clConverter;
	@Autowired private DSDConverter dsdConverter;

	//Metadata conversion
	public Collection<DM> toDM (Collection<ODocument> dmsO, boolean all) {
		Collection<DM> dms = new LinkedList<DM>();
		for (ODocument dmO : dmsO)
			dms.add(toDM(dmO, all));
		return dms;
	}

	@SuppressWarnings("unchecked")
	public DM toDM (ODocument dmO, boolean all) {
		DM dm = new DM();
		
		dm.setUid((String)dmO.field("uid"));
		dm.setParentUid((String)dmO.field("uidParent"));
		dm.setLanguage((String)dmO.field("language"));
		//description
		dm.setTitle((Map<String,String>)dmO.field("title"));
		dm.setSummary((Map<String,String>)dmO.field("summary"));
		dm.setDescription((Map<String,String>)dmO.field("abstract"));
		dm.setSupplemental((Map<String,String>)dmO.field("supplemental"));
		//content
		dm.setCreationDate((Date)dmO.field("creationDate"));
		if (dmO.field("region")!=null)
			dm.setGeographicExtent(clConverter.toCode((ODocument)dmO.field("region"), false, CodeListConverter.NO_LEVELS));
        if (dmO.field("basePeriod")!=null)
            dm.setBasePeriod(toPeriod((ODocument)dmO.field("basePeriod")));
        if (dmO.field("referencePeriod")!=null)
            dm.setReferencePeriod(toPeriod((ODocument)dmO.field("referencePeriod")));
        if (dmO.field("weightReferencePeriod")!=null)
            dm.setWeightReferencePeriod(toPeriod((ODocument)dmO.field("weightReferencePeriod")));
		if (dmO.field("coverageSectors")!=null)
			dm.setCoverageSectors(clConverter.toCode((Collection<ODocument>)dmO.field("coverageSectors"), false));
		if (dmO.field("codingSystems")!=null)
			dm.setCodingSystems(clConverter.toSystem((Collection<ODocument>)dmO.field("codingSystems"), false));
		dm.setClassificationSystems((Map<String,String>)dmO.field("classificationSystems"));
		dm.setComparability((Map<String,String>)dmO.field("comparability"));
		dm.setStatisticalUnit((Map<String,String>)dmO.field("statisticalUnit"));
		dm.setUnitOfMeasureDetails((Map<String,String>)dmO.field("unitOfMeasureDetails"));
        if (dmO.field("um")!=null)
            dm.setUnitOfMeasure(clConverter.toCode((ODocument)dmO.field("um"), false, CodeListConverter.NO_LEVELS));
        if (dmO.field("coverageGeographical")!=null)
            dm.setCoverageGeographical(clConverter.toCode((ODocument)dmO.field("coverageGeographical"), false, CodeListConverter.NO_LEVELS));
		//process
		if (dmO.field("collectionPeriodicity")!=null)
			dm.setCollectionPeriodicity(clConverter.toCode((ODocument)dmO.field("collectionPeriodicity"), false, CodeListConverter.NO_LEVELS));
		if (dmO.field("processMethodologyDocuments")!=null)
			dm.setProcessMethodologyDocuments(cmConverter.toPublication((Collection<ODocument>)dmO.field("processMethodologyDocuments")));
		dm.setCompilationProcessing((Map<String,String>)dmO.field("compilationProcessing"));
		dm.setCollectionProcessing((Map<String,String>)dmO.field("collectionProcessing"));
		dm.setIndexType((Map<String,String>)dmO.field("indexType"));
        if (dmO.field("processConceptsDocuments")!=null)
            dm.setProcessConceptsDocuments(cmConverter.toPublication((Collection<ODocument>)dmO.field("processConceptsDocuments")));
        dm.setSourceType((Map<String,String>)dmO.field("sourceType"));
        dm.setProcessConcepts((Map<String,String>)dmO.field("processConcepts"));
        dm.setAggregationProcessing((Map<String,String>)dmO.field("aggregationProcessing"));
        dm.setDataAdjustment((Map<String,String>)dmO.field("dataAdjustment"));
		//quality
		dm.setAccuracy((Map<String,String>)dmO.field("accuracy"));
		dm.setAccuracyAssessment((Map<String,String>)dmO.field("accuracyAssessment"));
		dm.setCompleteness((Map<String,String>)dmO.field("completeness"));
        if (dmO.field("qualityMethodologyDocuments")!=null)
            dm.setQualityMethodologyDocuments(cmConverter.toPublication((Collection<ODocument>)dmO.field("qualityMethodologyDocuments")));
		//distribution
		if (dmO.field("disseminationPeriodicity")!=null)
			dm.setDisseminationPeriodicity(clConverter.toCode((ODocument)dmO.field("disseminationPeriodicity"), false, CodeListConverter.NO_LEVELS));
		dm.setUserAccess((Map<String,String>)dmO.field("userAccess"));
		dm.setDisseminationFormat((Map<String,String>)dmO.field("disseminationFormat"));
		dm.setRevisionPolicy((Map<String,String>)dmO.field("revisionPolicy"));
		dm.setConfidentialityDataTreatment((Map<String,String>)dmO.field("confidentialityDataTreatment"));
		dm.setConfidentialityPolicy((Map<String,String>)dmO.field("confidentialityPolicy"));
		dm.setReleasePolicy((Map<String,String>)dmO.field("releasePolicy"));
		dm.setOtherDisseminatedData((Map<String,String>)dmO.field("otherDisseminatedData"));
        if (dmO.field("publications")!=null)
            dm.setPublications(cmConverter.toPublication((Collection<ODocument>)dmO.field("publications")));
        if (dmO.field("news")!=null)
            dm.setNews(cmConverter.toPublication((Collection<ODocument>)dmO.field("news")));
        dm.setEmbargoTimeSpec((Map<String,String>)dmO.field("embargoTimeSpec"));
        if (dmO.field("dataSources")!=null)
            dm.setDataSources(toDatasource((Collection<ODocument>)dmO.field("dataSources")));
		//contacts
		if (dmO.field("source")!=null)
			dm.setSources(cmConverter.toContactIdentity((Collection<ODocument>)dmO.field("source")));
		if (dmO.field("owner")!=null)
			dm.setOwner(cmConverter.toContactIdentity((ODocument)dmO.field("owner")));
		if (dmO.field("provider")!=null)
			dm.setProvider(cmConverter.toContactIdentity((ODocument)dmO.field("provider")));
		if (dmO.field("compiler")!=null)
			dm.setCompiler(cmConverter.toContactIdentity((ODocument)dmO.field("compiler")));
		if (dmO.field("contacts")!=null)
			dm.setContacts(cmConverter.toContactIdentity((Collection<ODocument>)dmO.field("contacts")));
		//status
		dm.setCoverageTime((Map<String,String>)dmO.field("coverageTime"));
		dm.setAccessibility((Map<String,String>)dmO.field("accessibility"));
		dm.setUpdateDate((Date)dmO.field("updateDate"));
		dm.setReferenceUpdateDate((Date)dmO.field("referenceUpdateDate"));
		dm.setNextUpdateDate((Date)dmO.field("nextUpdateDate"));
		dm.setCompletenessPercentage((Float)dmO.field("completenessPercentage"));
        if (dmO.field("updatePeriodicity")!=null)
            dm.setUpdatePeriodicity(clConverter.toCode((ODocument)dmO.field("updatePeriodicity"), false, CodeListConverter.NO_LEVELS));
		//other
		if (dmO.field("transferOptions")!=null)
			dm.setTransferOptions(cmConverter.toLink((Collection<ODocument>)dmO.field("transferOptions")));
		if (dmO.field("categories")!=null)
			dm.setCategories(clConverter.toCode((Collection<ODocument>)dmO.field("categories"), false));
		dm.setStatisticalPopulation((Map<String,String>)dmO.field("statisticalPopulation"));
		dm.setDataType(dmO.field("dataType")!=null ? DMDataType.getByCode((String)dmO.field("dataType")) : null);
		dm.setDataKind(dmO.field("dataKind")!=null ? DMDataKind.getByCode((String)dmO.field("dataKind")) : null);
		dm.setCopyright(dmO.field("copyrightPolicy")!=null ? DMCopyrightType.getByCode((String)dmO.field("copyrightPolicy")) : null);
		dm.setFreeExtension(dmO.field("freeExtension"));

		//upload
		if (dmO.field("availability")!=null)
			dm.setAvailability(toAvailability((ODocument)dmO.field("availability")));

		//DSD
		if (dmO.field("dsd")!=null)
			dm.setDsd(dsdConverter.toDSD((ODocument)dmO.field("dsd"), all));

		return dm;
	}

	@SuppressWarnings("unchecked")
	public Period toPeriod (ODocument periodO) {
		Period period = new Period();
		period.setFrom((Date) periodO.field("from"));
		period.setTo((Date) periodO.field("to"));
		return period;
	}

	@SuppressWarnings("unchecked")
	public Collection<DMDataSource> toDatasource (Collection<ODocument> datasourcesO) {
        Collection<DMDataSource> dataSources = new LinkedList<DMDataSource>();
        for (ODocument datasourceO : datasourcesO)
            dataSources.add(toDatasource(datasourceO));
		return dataSources;
	}

	@SuppressWarnings("unchecked")
	public DMDataSource toDatasource (ODocument datasourceO) {
        DMDataSource dataSource = new DMDataSource();
		dataSource.setType(datasourceO.field("type") != null ? DMDataSourceType.getByCode((String) datasourceO.field("type")) : null);
		dataSource.setTitle((Map<String, String>) datasourceO.field("title"));
		dataSource.setReference((Map<String, String>) datasourceO.field("reference"));
		return dataSource;
	}

	@SuppressWarnings("unchecked")
	public DMAvailability toAvailability (ODocument availabilityO) {
		DMAvailability availability = new DMAvailability();
		availability.setStatus(availabilityO.field("status")!=null ? DMStatus.getByCode((String)availabilityO.field("status")) : null);
		availability.setChunksNumber((Integer)availabilityO.field("chunksNumber"));
		availability.setChunksIndex((Collection<Integer>)availabilityO.field("chunksIndex"));
		return availability;
	}


    @SuppressWarnings("unchecked")
    public DMMeta toMetadataStructure (ODocument mmO, OGraphDatabase database, Boolean all) throws Exception {
        //Convert to map
        DMMeta mm = new DMMeta();
        mm.putAll(JSONUtils.toMap(mmO.toJSON()));
        //Fill with connected datasets metadata if needed
        Collection<ODocument> datasets = (Collection<ODocument>)mm.remove("availableDatasets");
        if (all && datasets!=null)
            mm.put("availableDatasets",toDM(datasets,false));
        //Return map
        return mm;
    }

}

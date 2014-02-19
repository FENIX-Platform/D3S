package org.fao.fenix.msd.dto.dm;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.fao.fenix.msd.dto.cl.Code;
import org.fao.fenix.msd.dto.cl.CodeSystem;
import org.fao.fenix.msd.dto.common.ContactIdentity;
import org.fao.fenix.msd.dto.common.Link;
import org.fao.fenix.msd.dto.common.Period;
import org.fao.fenix.msd.dto.common.Publication;
import org.fao.fenix.msd.dto.dm.type.DMCopyrightType;
import org.fao.fenix.msd.dto.dm.type.DMDataKind;
import org.fao.fenix.msd.dto.dm.type.DMDataType;
import org.fao.fenix.msd.dto.dm.type.DMLayerType;
import org.fao.fenix.msd.dto.dsd.DSD;
import org.fao.fenix.server.utils.DataUtils;

public class DM implements Comparable<DM> {
    //main
    private String parentUid;
    private String uid;
    private String language;

    //description
	private Map<String,String> title;
    private Map<String,String> summary;
    private Map<String,String> description;
	private Map<String,String> supplemental;

	//content
    private Date creationDate;
	private Code geographicExtent;
    private Period referencePeriod;
    private Period basePeriod;
    private Collection<Code> coverageSectors;
    private Map<String,String> comparability;
    private Map<String,String> statisticalUnit;
    private Code unitOfMeasure;
    private Code coverageGeographical;
    private Map<String,String> unitOfMeasureDetails;

    private Collection<CodeSystem> codingSystems;
    private Period weightReferencePeriod;
    private Map<String,String> classificationSystems;

    //process
    private Code collectionPeriodicity;
    private Collection<Publication> processMethodologyDocuments;
    private Map<String,String> compilationProcessing;
    private Map<String,String> collectionProcessing;
    private Map<String,String> indexType;
    private Collection<Publication> processConceptsDocuments;
    private Map<String,String> processConcepts;
    private Map<String,String> sourceType;
    private Map<String,String> aggregationProcessing;

    private Map<String,String> dataAdjustment;

    //quality
    private Map<String,String> accuracy;
    private Map<String,String> accuracyAssessment;
    private Map<String,String> completeness;
    private Collection<Publication> qualityMethodologyDocuments;

    //distribution
    private Code disseminationPeriodicity;
    private Map<String,String> userAccess;
    private Map<String,String> disseminationFormat;
    private Map<String,String> revisionPolicy;
    private Map<String,String> embargoTimeSpec;
    private Collection<Publication> publications;
    private Collection<Publication> news;

    private Map<String,String> confidentialityDataTreatment;
    private Map<String,String> confidentialityPolicy;
    private Map<String,String> releasePolicy;
    private Collection<DMDataSource> dataSources;
    private Map<String,String> otherDisseminatedData;

    //contacts
    private ContactIdentity provider;
    private ContactIdentity owner;
    private Collection<ContactIdentity> sources;
    private Collection<ContactIdentity> contacts;

    private ContactIdentity compiler;

    //status
    private Map<String,String> coverageTime;
    private Map<String,String> accessibility;
    private Date updateDate;
    private Date referenceUpdateDate;
    private Date nextUpdateDate;
    private Float completenessPercentage;
    private Code updatePeriodicity;

    //other
    private Collection<Link> transferOptions;
    private Collection<Code> categories;
    private Map<String,String> statisticalPopulation;
    private Object freeExtension;
    private DMDataType dataType;
    private DMLayerType layerType;
    private DMDataKind dataKind;
    private DMCopyrightType copyright;

    //upload
    private DMAvailability availability;


    //dataset structure
    private DSD dsd;

    //GET-SET
    public String getParentUid() {
        return parentUid;
    }

    public void setParentUid(String parentUid) {
        this.parentUid = parentUid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Map<String, String> getTitle() {
        return title;
    }

    public void setTitle(Map<String, String> title) {
        this.title = title;
    }

    public Map<String, String> getSummary() {
        return summary;
    }

    public void setSummary(Map<String, String> summary) {
        this.summary = summary;
    }

    public Map<String, String> getDescription() {
        return description;
    }

    public void setDescription(Map<String, String> description) {
        this.description = description;
    }

    public Map<String, String> getSupplemental() {
        return supplemental;
    }

    public void setSupplemental(Map<String, String> supplemental) {
        this.supplemental = supplemental;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = DataUtils.roundDate(creationDate);
    }

    public Code getGeographicExtent() {
        return geographicExtent;
    }

    public void setGeographicExtent(Code geographicExtent) {
        this.geographicExtent = geographicExtent;
    }

    public Collection<Code> getCoverageSectors() {
        return coverageSectors;
    }

    public void setCoverageSectors(Collection<Code> coverageSectors) {
        this.coverageSectors = coverageSectors;
    }

    public Map<String, String> getClassificationSystems() {
        return classificationSystems;
    }

    public void setClassificationSystems(Map<String, String> classificationSystems) {
        this.classificationSystems = classificationSystems;
    }

    public Collection<CodeSystem> getCodingSystems() {
        return codingSystems;
    }

    public void setCodingSystems(Collection<CodeSystem> codingSystems) {
        this.codingSystems = codingSystems;
    }

    public Map<String, String> getStatisticalUnit() {
        return statisticalUnit;
    }

    public void setStatisticalUnit(Map<String, String> statisticalUnit) {
        this.statisticalUnit = statisticalUnit;
    }

    public Period getReferencePeriod() {
        return referencePeriod;
    }

    public void setReferencePeriod(Period referencePeriod) {
        this.referencePeriod = referencePeriod;
    }

    public Period getBasePeriod() {
        return basePeriod;
    }

    public void setBasePeriod(Period basePeriod) {
        this.basePeriod = basePeriod;
    }

    public Map<String, String> getComparability() {
        return comparability;
    }

    public void setComparability(Map<String, String> comparability) {
        this.comparability = comparability;
    }

    public Map<String, String> getCompilationProcessing() {
        return compilationProcessing;
    }

    public void setCompilationProcessing(Map<String, String> compilationProcessing) {
        this.compilationProcessing = compilationProcessing;
    }

    public Map<String, String> getCollectionProcessing() {
        return collectionProcessing;
    }

    public void setCollectionProcessing(Map<String, String> collectionProcessing) {
        this.collectionProcessing = collectionProcessing;
    }

    public Code getCollectionPeriodicity() {
        return collectionPeriodicity;
    }

    public void setCollectionPeriodicity(Code collectionPeriodicity) {
        this.collectionPeriodicity = collectionPeriodicity;
    }

    public Collection<Publication> getProcessMethodologyDocuments() {
        return processMethodologyDocuments;
    }

    public void setProcessMethodologyDocuments(Collection<Publication> processMethodologyDocuments) {
        this.processMethodologyDocuments = processMethodologyDocuments;
    }

    public Map<String, String> getIndexType() {
        return indexType;
    }

    public void setIndexType(Map<String, String> indexType) {
        this.indexType = indexType;
    }

    public Map<String, String> getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(Map<String, String> accuracy) {
        this.accuracy = accuracy;
    }

    public Map<String, String> getAccuracyAssessment() {
        return accuracyAssessment;
    }

    public void setAccuracyAssessment(Map<String, String> accuracyAssessment) {
        this.accuracyAssessment = accuracyAssessment;
    }

    public Map<String, String> getCompleteness() {
        return completeness;
    }

    public void setCompleteness(Map<String, String> completeness) {
        this.completeness = completeness;
    }

    public DMAvailability getAvailability() {
        return availability;
    }

    public void setAvailability(DMAvailability availability) {
        this.availability = availability;
    }

    public Code getDisseminationPeriodicity() {
        return disseminationPeriodicity;
    }

    public void setDisseminationPeriodicity(Code disseminationPeriodicity) {
        this.disseminationPeriodicity = disseminationPeriodicity;
    }

    public Map<String, String> getUserAccess() {
        return userAccess;
    }

    public void setUserAccess(Map<String, String> userAccess) {
        this.userAccess = userAccess;
    }

    public Map<String, String> getDisseminationFormat() {
        return disseminationFormat;
    }

    public void setDisseminationFormat(Map<String, String> disseminationFormat) {
        this.disseminationFormat = disseminationFormat;
    }

    public Map<String, String> getRevisionPolicy() {
        return revisionPolicy;
    }

    public void setRevisionPolicy(Map<String, String> revisionPolicy) {
        this.revisionPolicy = revisionPolicy;
    }

    public ContactIdentity getProvider() {
        return provider;
    }

    public void setProvider(ContactIdentity provider) {
        this.provider = provider;
    }

    public ContactIdentity getOwner() {
        return owner;
    }

    public void setOwner(ContactIdentity owner) {
        this.owner = owner;
    }

    public Collection<ContactIdentity> getSources() {
        return sources;
    }

    public void setSources(Collection<ContactIdentity> sources) {
        this.sources = sources;
    }

    public Collection<ContactIdentity> getContacts() {
        return contacts;
    }

    public void setContacts(Collection<ContactIdentity> contacts) {
        this.contacts = contacts;
    }

    public Map<String, String> getCoverageTime() {
        return coverageTime;
    }

    public void setCoverageTime(Map<String, String> coverageTime) {
        this.coverageTime = coverageTime;
    }

    public Map<String, String> getAccessibility() {
        return accessibility;
    }

    public void setAccessibility(Map<String, String> accessibility) {
        this.accessibility = accessibility;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = DataUtils.roundDate(updateDate);
    }

    public Date getReferenceUpdateDate() {
        return referenceUpdateDate;
    }

    public void setReferenceUpdateDate(Date referenceUpdateDate) {
        this.referenceUpdateDate = DataUtils.roundDate(referenceUpdateDate);
    }

    public Date getNextUpdateDate() {
        return nextUpdateDate;
    }

    public void setNextUpdateDate(Date nextUpdateDate) {
        this.nextUpdateDate = DataUtils.roundDate(nextUpdateDate);
    }

    public Float getCompletenessPercentage() {
        return completenessPercentage;
    }

    public void setCompletenessPercentage(Float completenessPercentage) {
        this.completenessPercentage = completenessPercentage;
    }

    public Collection<Link> getTransferOptions() {
        return transferOptions;
    }

    public void setTransferOptions(Collection<Link> transferOptions) {
        this.transferOptions = transferOptions;
    }

    public Collection<Code> getCategories() {
        return categories;
    }

    public void setCategories(Collection<Code> categories) {
        this.categories = categories;
    }

    public Map<String, String> getStatisticalPopulation() {
        return statisticalPopulation;
    }

    public void setStatisticalPopulation(Map<String, String> statisticalPopulation) {
        this.statisticalPopulation = statisticalPopulation;
    }

    public Object getFreeExtension() {
        return freeExtension;
    }

    public void setFreeExtension(Object freeExtension) {
        this.freeExtension = freeExtension;
    }

    public DMDataType getDataType() {
        return dataType;
    }

    public void setDataType(DMDataType dataType) {
        this.dataType = dataType;
    }

    public DMLayerType getLayerType() {
        return layerType;
    }

    public void setLayerType(DMLayerType layerType) {
        this.layerType = layerType;
    }

    public DMDataKind getDataKind() {
        return dataKind;
    }

    public void setDataKind(DMDataKind dataKind) {
        this.dataKind = dataKind;
    }

    public DMCopyrightType getCopyright() {
        return copyright;
    }

    public void setCopyright(DMCopyrightType copyright) {
        this.copyright = copyright;
    }

    public DSD getDsd() {
        return dsd;
    }

    public void setDsd(DSD dsd) {
        this.dsd = dsd;
    }

    public Code getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(Code unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public Code getCoverageGeographical() {
        return coverageGeographical;
    }

    public void setCoverageGeographical(Code coverageGeographical) {
        this.coverageGeographical = coverageGeographical;
    }

    public Collection<Publication> getProcessConceptsDocuments() {
        return processConceptsDocuments;
    }

    public void setProcessConceptsDocuments(Collection<Publication> processConceptsDocuments) {
        this.processConceptsDocuments = processConceptsDocuments;
    }

    public Map<String, String> getProcessConcepts() {
        return processConcepts;
    }

    public void setProcessConcepts(Map<String, String> processConcepts) {
        this.processConcepts = processConcepts;
    }

    public Map<String, String> getSourceType() {
        return sourceType;
    }

    public void setSourceType(Map<String, String> sourceType) {
        this.sourceType = sourceType;
    }

    public Collection<Publication> getQualityMethodologyDocuments() {
        return qualityMethodologyDocuments;
    }

    public void setQualityMethodologyDocuments(Collection<Publication> qualityMethodologyDocuments) {
        this.qualityMethodologyDocuments = qualityMethodologyDocuments;
    }

    public Map<String, String> getEmbargoTimeSpec() {
        return embargoTimeSpec;
    }

    public void setEmbargoTimeSpec(Map<String, String> embargoTimeSpec) {
        this.embargoTimeSpec = embargoTimeSpec;
    }

    public Collection<Publication> getPublications() {
        return publications;
    }

    public void setPublications(Collection<Publication> publications) {
        this.publications = publications;
    }

    public Code getUpdatePeriodicity() {
        return updatePeriodicity;
    }

    public void setUpdatePeriodicity(Code updatePeriodicity) {
        this.updatePeriodicity = updatePeriodicity;
    }
    public Map<String, String> getUnitOfMeasureDetails() {
        return unitOfMeasureDetails;
    }

    public void setUnitOfMeasureDetails(Map<String, String> unitOfMeasureDetails) {
        this.unitOfMeasureDetails = unitOfMeasureDetails;
    }

    public Map<String, String> getAggregationProcessing() {
        return aggregationProcessing;
    }

    public void setAggregationProcessing(Map<String, String> aggregationProcessing) {
        this.aggregationProcessing = aggregationProcessing;
    }

    public Period getWeightReferencePeriod() {
        return weightReferencePeriod;
    }

    public void setWeightReferencePeriod(Period weightReferencePeriod) {
        this.weightReferencePeriod = weightReferencePeriod;
    }

    public Map<String, String> getDataAdjustment() {
        return dataAdjustment;
    }

    public void setDataAdjustment(Map<String, String> dataAdjustment) {
        this.dataAdjustment = dataAdjustment;
    }

    public Collection<Publication> getNews() {
        return news;
    }

    public void setNews(Collection<Publication> news) {
        this.news = news;
    }

    public Map<String, String> getConfidentialityDataTreatment() {
        return confidentialityDataTreatment;
    }

    public void setConfidentialityDataTreatment(Map<String, String> confidentialityDataTreatment) {
        this.confidentialityDataTreatment = confidentialityDataTreatment;
    }

    public Map<String, String> getConfidentialityPolicy() {
        return confidentialityPolicy;
    }

    public void setConfidentialityPolicy(Map<String, String> confidentialityPolicy) {
        this.confidentialityPolicy = confidentialityPolicy;
    }

    public Map<String, String> getReleasePolicy() {
        return releasePolicy;
    }

    public void setReleasePolicy(Map<String, String> releasePolicy) {
        this.releasePolicy = releasePolicy;
    }

    public Collection<DMDataSource> getDataSources() {
        return dataSources;
    }

    public void setDataSources(Collection<DMDataSource> dataSources) {
        this.dataSources = dataSources;
    }

    public ContactIdentity getCompiler() {
        return compiler;
    }

    public void setCompiler(ContactIdentity compiler) {
        this.compiler = compiler;
    }

    public Map<String, String> getOtherDisseminatedData() {
        return otherDisseminatedData;
    }

    public void setOtherDisseminatedData(Map<String, String> otherDisseminatedData) {
        this.otherDisseminatedData = otherDisseminatedData;
    }

    //Utils

    //description
	public void addTitle(String language, String title) {
        if (this.title==null)
            this.title = new HashMap<String,String>();
		this.title.put(language, title);
	}
    public void addSummary(String language, String summary) {
        if (this.summary==null)
            this.summary = new HashMap<String,String>();
		this.summary.put(language, summary);
	}
    public void addDescription(String language, String description) {
        if (this.description==null)
            this.description = new HashMap<String,String>();
		this.description.put(language, description);
	}
    public void addSupplemental(String language, String supplemental) {
        if (this.supplemental==null)
            this.supplemental = new HashMap<String,String>();
		this.supplemental.put(language, supplemental);
	}

    //content
    public void addCoverageSector(Code sector) {
        if (coverageSectors==null)
            coverageSectors = new LinkedList<Code>();
        coverageSectors.add(sector);
    }
    public void addCodingSystem(CodeSystem system) {
        if (codingSystems==null)
            codingSystems = new LinkedList<CodeSystem>();
        codingSystems.add(system);
    }
    public void addClassificationSystem(String language, String classificationSystem) {
        if (classificationSystems==null)
            classificationSystems = new HashMap<String,String>();
        classificationSystems.put(language, classificationSystem);
    }
    public void addComparability(String language, String comparability) {
        if (this.comparability==null)
            this.comparability = new HashMap<String,String>();
        this.comparability.put(language, comparability);
    }
    public void addStatisticalUnit(String language, String unit) {
        if (statisticalUnit==null)
            statisticalUnit = new HashMap<String,String>();
        statisticalUnit.put(language, unit);
    }
    public void addUnitOfMeasureDetails(String language, String unit) {
        if (unitOfMeasureDetails==null)
            unitOfMeasureDetails = new HashMap<String,String>();
        unitOfMeasureDetails.put(language, unit);
    }

    //process
    public void addProcessMethodologyDocument(Publication doc) {
        if (processMethodologyDocuments==null)
            processMethodologyDocuments = new LinkedList<Publication>();
        processMethodologyDocuments.add(doc);
    }
    public void addProcessConceptsDocument(Publication doc) {
        if (processConceptsDocuments==null)
            processConceptsDocuments = new LinkedList<Publication>();
        processConceptsDocuments.add(doc);
    }
    public void addCompilationProcessing(String language, String processing) {
        if (compilationProcessing==null)
            compilationProcessing = new HashMap<String,String>();
        compilationProcessing.put(language, processing);
    }
    public void addProcessConcepts(String language, String processing) {
        if (processConcepts==null)
            processConcepts = new HashMap<String,String>();
        processConcepts.put(language, processing);
    }
    public void addCollectionProcessing(String language, String processing) {
        if (collectionProcessing==null)
            collectionProcessing = new HashMap<String,String>();
        collectionProcessing.put(language, processing);
    }
    public void addIndexType(String language, String type) {
        if (indexType==null)
            indexType = new HashMap<String,String>();
        indexType.put(language, type);
    }
    public void addSourceType(String language, String type) {
        if (sourceType==null)
            sourceType = new HashMap<String,String>();
        sourceType.put(language, type);
    }
    public void addAggregationProcessing(String language, String aggregationProcess) {
        if (aggregationProcessing==null)
            aggregationProcessing = new HashMap<String,String>();
        aggregationProcessing.put(language, aggregationProcess);
    }
    public void addDataAdjustment(String language, String label) {
        if (dataAdjustment==null)
            dataAdjustment = new HashMap<String,String>();
        dataAdjustment.put(language, label);
    }

    //quality
    public void addAccuracy(String language, String accuracy) {
        if (this.accuracy==null)
            this.accuracy = new HashMap<String,String>();
        this.accuracy.put(language, accuracy);
    }
    public void addAccuracyAssessment(String language, String assessment) {
        if (accuracyAssessment==null)
            accuracyAssessment = new HashMap<String,String>();
        accuracyAssessment.put(language, assessment);
    }
    public void addCompletness(String language, String completness) {
        if (this.completeness==null)
            this.completeness = new HashMap<String,String>();
        this.completeness.put(language, completness);
    }
    public void addQualityMethodologyDocument(Publication doc) {
        if (qualityMethodologyDocuments==null)
            qualityMethodologyDocuments = new LinkedList<Publication>();
        qualityMethodologyDocuments.add(doc);
    }

    //distribution
    public void addUserAccess(String language, String access) {
        if (userAccess==null)
            userAccess = new HashMap<String,String>();
        userAccess.put(language, access);
    }
    public void addDisseminationFormat(String language, String format) {
        if (disseminationFormat==null)
            disseminationFormat = new HashMap<String,String>();
        disseminationFormat.put(language, format);
    }
    public void addRevisionPolicy(String language, String policy) {
        if (revisionPolicy==null)
            revisionPolicy = new HashMap<String,String>();
        revisionPolicy.put(language, policy);
    }
    public void addEmbargoTimeSpec(String language, String spec) {
        if (embargoTimeSpec==null)
            embargoTimeSpec = new HashMap<String,String>();
        embargoTimeSpec.put(language, spec);
    }
    public void addConfidentialityDataTreatment(String language, String label) {
        if (confidentialityDataTreatment==null)
            confidentialityDataTreatment = new HashMap<String,String>();
        confidentialityDataTreatment.put(language, label);
    }
    public void addConfidentialityPolicy(String language, String label) {
        if (confidentialityPolicy==null)
            confidentialityPolicy = new HashMap<String,String>();
        confidentialityPolicy.put(language, label);
    }
    public void addReleasePolicy(String language, String label) {
        if (releasePolicy==null)
            releasePolicy = new HashMap<String,String>();
        releasePolicy.put(language, label);
    }
    public void addOtherDisseminatedData(String language, String label) {
        if (otherDisseminatedData==null)
            otherDisseminatedData = new HashMap<String,String>();
        otherDisseminatedData.put(language, label);
    }
    public void addPublication(Publication doc) {
        if (publications==null)
            publications = new LinkedList<Publication>();
        publications.add(doc);
    }
    public void addNews(Publication doc) {
        if (news==null)
            news = new LinkedList<Publication>();
        news.add(doc);
    }
    public void addDataSource(DMDataSource ds) {
        if (dataSources==null)
            dataSources = new LinkedList<DMDataSource>();
        dataSources.add(ds);
    }

    //contacts
    public void addSource(ContactIdentity contact) {
        if (sources==null)
            sources = new LinkedList<ContactIdentity>();
        sources.add(contact);
    }
    public void addContact(ContactIdentity contact) {
        if (contacts==null)
            contacts = new LinkedList<ContactIdentity>();
        contacts.add(contact);
    }

    //status
    public void addCoverageTime(String language, String time) {
        if (this.coverageTime==null)
            this.coverageTime = new HashMap<String,String>();
        this.coverageTime.put(language, time);
    }
    public void addAccessibility(String language, String accessibility) {
        if (this.accessibility==null)
            this.accessibility = new HashMap<String,String>();
        this.accessibility.put(language, accessibility);
    }

    //other
    public void addStatisticalPopulation(String language, String population) {
        if (statisticalPopulation==null)
            statisticalPopulation = new HashMap<String,String>();
        statisticalPopulation.put(language, population);
    }
    public void addTransferOption(Link transferOption) {
        if (transferOptions==null)
            transferOptions = new LinkedList<Link>();
		transferOptions.add(transferOption);
	}
	public void addCategory(Code category) {
        if (categories==null)
            categories = new LinkedList<Code>();
		categories.add(category);
	}
	@JsonIgnore
	public void setDecodedDataType(String code) {
		dataType = DMDataType.getByCode(code);
	}
	@JsonIgnore
	public void setDecodedLayerType(String code) {
		layerType = DMLayerType.getByCode(code);
	}
	@JsonIgnore
	public void setDecodedDataKind(String code) {
		dataKind = DMDataKind.getByCode(code);
	}
	@JsonIgnore
	public void setDecodedCopyright(String code) {
		copyright = DMCopyrightType.getByCode(code);
	}



    //Compare

    @Override
    public int hashCode() {
        return uid!=null ? uid.hashCode() : -1;    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public boolean equals(Object obj) {
        return obj!=null && obj instanceof DM && uid!=null && uid.equals(((DM)obj).uid);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public int compareTo(DM o) {
        if(uid==null) return -1;
        if (o==null) return 1;
        return uid.compareTo(o.uid);
    }


    //Utils


    @Override
    public String toString() {
        return "Dataset "+uid;
    }
}

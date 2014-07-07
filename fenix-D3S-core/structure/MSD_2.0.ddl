CONNECT remote:localhost/msd_1.0 admin admin;


CREATE CLASS MeAccessibility;
CREATE CLASS MeComparability;
CREATE CLASS MeContent;
CREATE CLASS SeCodeList;
CREATE CLASS SeReferencePopulation;
CREATE CLASS SeCoverage;
CREATE CLASS MeDataQuality;
CREATE CLASS MeDocuments;
CREATE CLASS MeIdentification;
CREATE CLASS MeInstitutionalMandate;
CREATE CLASS MeMaintenance;
CREATE CLASS MeReferenceSystem;
CREATE CLASS MeResourceDimensions;
CREATE CLASS MeSpatialRepresentation;
CREATE CLASS MeStatisticalProcessing;

CREATE CLASS OjResponsibleParty;
CREATE CLASS OjContact;
CREATE CLASS OjCitation;
CREATE CLASS OjCodeList;
CREATE CLASS OjMeasure;

CREATE CLASS Period;
CREATE CLASS Code;



CREATE PROPERTY Period.from INTEGER;
CREATE PROPERTY Period.to INTEGER;

CREATE PROPERTY OjCodeList.linkedSource LINK Code;
CREATE PROPERTY OjCodeList.codeList STRING;
CREATE PROPERTY OjCodeList.version STRING;
CREATE PROPERTY OjCodeList.code STRING;
CREATE PROPERTY OjCodeList.title EMBEDDEDMAP STRING;
CREATE PROPERTY OjCodeList.contactInfo LINK OjResponsibleParty;
CREATE PROPERTY OjCodeList.link STRING;

CREATE PROPERTY OjMeasure.extent EMBEDDEDMAP STRING;
CREATE PROPERTY OjMeasure.composed BOOLEAN;
CREATE PROPERTY OjMeasure.measurementSystem EMBEDDEDMAP STRING;
CREATE PROPERTY OjMeasure.name STRING;
CREATE PROPERTY OjMeasure.conversionToStandard DOUBLE;


CREATE PROPERTY OjResponsibleParty.organisation EMBEDDEDMAP STRING;
CREATE PROPERTY OjResponsibleParty.organisationUnit EMBEDDEDMAP STRING;
CREATE PROPERTY OjResponsibleParty.name STRING;
CREATE PROPERTY OjResponsibleParty.position EMBEDDEDMAP STRING;
CREATE PROPERTY OjResponsibleParty.role STRING;
CREATE PROPERTY OjResponsibleParty.specify EMBEDDEDMAP STRING;
CREATE PROPERTY OjResponsibleParty.contactInfo EMBEDDED OjContact;

CREATE PROPERTY OjContact.phone STRING;
CREATE PROPERTY OjContact.address STRING;
CREATE PROPERTY OjContact.emailAddress STRING;
CREATE PROPERTY OjContact.hoursOfService STRING;
CREATE PROPERTY OjContact.contactInstruction STRING;

CREATE PROPERTY OjCitation.documentKind STRING;
CREATE PROPERTY OjCitation.title EMBEDDEDMAP STRING;
CREATE PROPERTY OjCitation.date DATE;
CREATE PROPERTY OjCitation.documentContact LINK OjResponsibleParty;
CREATE PROPERTY OjCitation.notes EMBEDDEDMAP STRING;
CREATE PROPERTY OjCitation.link STRING;
CREATE PROPERTY OjCitation.periodicity LINK OjCodeList;
CREATE PROPERTY OjCitation.ISBN STRING;
CREATE PROPERTY OjCitation.ISSN STRING;


CREATE PROPERTY Code.codeList LINK MeIdentification;
CREATE PROPERTY Code.code STRING;
CREATE PROPERTY Code.level INTEGER;
CREATE PROPERTY Code.title EMBEDDEDMAP STRING;
CREATE PROPERTY Code.description EMBEDDEDMAP STRING;
CREATE PROPERTY Code.validityPeriod EMBEDDED Period;
CREATE PROPERTY Code.parents LINKLIST Code;
CREATE PROPERTY Code.children LINKLIST Code;
CREATE PROPERTY Code.relations LINKLIST Code;


CREATE PROPERTY MeIdentification.uid STRING;
CREATE PROPERTY MeIdentification.parentsIdentifier EMBEDDEDSET STRING;
CREATE PROPERTY MeIdentification.languages LINKLIST OjCodeList;
CREATE PROPERTY MeIdentification.languageDetail EMBEDDEDMAP STRING;
CREATE PROPERTY MeIdentification.title EMBEDDEDMAP STRING;
CREATE PROPERTY MeIdentification.creationDate DATE;
CREATE PROPERTY MeIdentification.characterSet LINK OjCodeList;
CREATE PROPERTY MeIdentification.metadataStandardName EMBEDDEDMAP STRING;
CREATE PROPERTY MeIdentification.metadataStandardVersion EMBEDDEDMAP STRING;
CREATE PROPERTY MeIdentification.metadataLanguage LINKLIST OjCodeList;
CREATE PROPERTY MeIdentification.contacts LINKLIST OjResponsibleParty;
CREATE PROPERTY MeIdentification.noDataValue STRING;
CREATE PROPERTY MeIdentification.meContent LINK MeContent;


CREATE PROPERTY MeContent.resourceRepresentationType STRING;
CREATE PROPERTY MeContent.keyWords EMBEDDEDLIST STRING;
CREATE PROPERTY MeContent.description EMBEDDEDMAP STRING;
CREATE PROPERTY MeContent.seReferencePopulation LINK SeReferencePopulation;
CREATE PROPERTY MeContent.seCoverage LINK SeCoverage;
CREATE PROPERTY MeContent.seCodeList LINK SeCodeList;

CREATE PROPERTY SeReferencePopulation.statisticalPopulation EMBEDDEDMAP STRING;
CREATE PROPERTY SeReferencePopulation.statisticalUnit EMBEDDEDMAP STRING;
CREATE PROPERTY SeReferencePopulation.referencePeriod LINK OjCodeList;
CREATE PROPERTY SeReferencePopulation.referenceArea LINK OjCodeList;

CREATE PROPERTY SeCoverage.coverageSector LINKLIST OjCodeList;
CREATE PROPERTY SeCoverage.coverageSectorDetails EMBEDDEDMAP STRING;
CREATE PROPERTY SeCoverage.coverageTime EMBEDDED Period;
CREATE PROPERTY SeCoverage.coverageGeographic LINKLIST OjCodeList;

CREATE PROPERTY SeCodeList.numberOfLevels INTEGER;
CREATE PROPERTY SeCodeList.typeOfCodeList STRING;



DISCONNECT;
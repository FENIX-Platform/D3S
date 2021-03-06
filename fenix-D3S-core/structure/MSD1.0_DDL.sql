CONNECT remote:localhost/msd_1.0 admin admin;


CREATE CLASS Synchro;
CREATE CLASS CMContactIdentity EXTENDS V;
CREATE CLASS CMContact EXTENDS V;
CREATE CLASS CMLink EXTENDS V;
CREATE CLASS CMValueOperator EXTENDS V;
CREATE CLASS CMPeriod;
CREATE CLASS CMPublication EXTENDS CMLink;

CREATE CLASS CSVersion EXTENDS V;
CREATE CLASS CSKeyword EXTENDS V;
CREATE CLASS CSCode EXTENDS V;
CREATE CLASS CSRelationship EXTENDS E;
CREATE CLASS CSConversion EXTENDS E;
CREATE CLASS CSPropaedeutic EXTENDS E;
CREATE CLASS CSHierarchy EXTENDS E;

CREATE CLASS DSDMain EXTENDS V;
CREATE CLASS DSDDatasource EXTENDS V;
CREATE CLASS DSDContextSystem EXTENDS V;
CREATE CLASS DSDColumn EXTENDS V;
CREATE CLASS DSDDimension EXTENDS V;

CREATE CLASS DMMain EXTENDS V;
CREATE CLASS DMMeta EXTENDS V;
CREATE CLASS DMAvailability EXTENDS V;
CREATE CLASS DMDataSource EXTENDS V;




CREATE PROPERTY CMContactIdentity.textKey STRING;
CREATE PROPERTY CMContactIdentity.institution STRING;
CREATE PROPERTY CMContactIdentity.department STRING;
CREATE PROPERTY CMContactIdentity.name STRING;
CREATE PROPERTY CMContactIdentity.surname STRING;
CREATE PROPERTY CMContactIdentity.title EMBEDDEDMAP STRING;
CREATE PROPERTY CMContactIdentity.abstract EMBEDDEDMAP STRING;
CREATE PROPERTY CMContactIdentity.supplemental EMBEDDEDMAP STRING;
CREATE PROPERTY CMContactIdentity.contactList LINKLIST CMContact;
CREATE PROPERTY CMContactIdentity.region LINK CSCode;
CREATE PROPERTY CMContactIdentity.role LINK CSCode;
CREATE PROPERTY CMContactIdentity.context LINK DSDContextSystem;
ALTER PROPERTY CMContactIdentity.textKey MANDATORY true;

CREATE PROPERTY CMContact.type STRING;
CREATE PROPERTY CMContact.contact STRING;
ALTER PROPERTY CMContact.type MANDATORY true;
ALTER PROPERTY CMContact.contact MANDATORY true;

CREATE PROPERTY CMLink.link STRING;
CREATE PROPERTY CMLink.title EMBEDDEDMAP STRING;
CREATE PROPERTY CMLink.abstract EMBEDDEDMAP STRING;
ALTER PROPERTY CMLink.link MANDATORY true;

CREATE PROPERTY CMPublication.date DATE;

CREATE PROPERTY CMValueOperator.implementation STRING;
CREATE PROPERTY CMValueOperator.rule STRING;
CREATE PROPERTY CMValueOperator.fixedParameters EMBEDDEDMAP;
CREATE PROPERTY CMValueOperator.dimension LINK DSDDimension;
ALTER PROPERTY CMValueOperator.implementation MANDATORY true;
ALTER PROPERTY CMValueOperator.dimension MANDATORY true;

CREATE PROPERTY CMPeriod.from DATE;
CREATE PROPERTY CMPeriod.to DATE;



CREATE PROPERTY CSVersion.system STRING;
CREATE PROPERTY CSVersion.version STRING;
CREATE PROPERTY CSVersion.title EMBEDDEDMAP STRING;
CREATE PROPERTY CSVersion.abstract EMBEDDEDMAP STRING;
CREATE PROPERTY CSVersion.startDate DATE;
CREATE PROPERTY CSVersion.endDate DATE;
CREATE PROPERTY CSVersion.virtualDate DATE;
CREATE PROPERTY CSVersion.sharingPolicy STRING;
CREATE PROPERTY CSVersion.levelsNumber INTEGER;
CREATE PROPERTY CSVersion.rootCodes LINKLIST CSCode;
CREATE PROPERTY CSVersion.keywords LINKLIST CSKeyword;
CREATE PROPERTY CSVersion.category LINK CSCode;
CREATE PROPERTY CSVersion.region LINK CSCode;
CREATE PROPERTY CSVersion.source LINK CMContactIdentity;
CREATE PROPERTY CSVersion.provider LINK CMContactIdentity;
ALTER PROPERTY CSVersion.system MANDATORY true;
ALTER PROPERTY CSVersion.version MANDATORY true;

CREATE PROPERTY CSKeyword.keyword STRING;
ALTER PROPERTY CSKeyword.keyword MANDATORY true;

CREATE PROPERTY CSCode.code STRING;
CREATE PROPERTY CSCode.level INTEGER;
CREATE PROPERTY CSCode.title EMBEDDEDMAP STRING;
CREATE PROPERTY CSCode.index_title_EN STRING;
CREATE PROPERTY CSCode.index_title_ES STRING;
CREATE PROPERTY CSCode.index_title_FR STRING;
CREATE PROPERTY CSCode.index_title_DE STRING;
CREATE PROPERTY CSCode.abstract EMBEDDEDMAP STRING;
CREATE PROPERTY CSCode.supplemental EMBEDDEDMAP STRING;
CREATE PROPERTY CSCode.system LINK CSVersion;
CREATE PROPERTY CSCode.parents LINKLIST CSCode;
CREATE PROPERTY CSCode.childs LINKLIST CSCode;
CREATE PROPERTY CSCode.exclusionList LINKLIST CSCode;
ALTER PROPERTY CSCode.code MANDATORY true;
ALTER PROPERTY CSCode.system MANDATORY true;

CREATE PROPERTY CSConversion.conversionRule LINK CMValueOperator;
ALTER PROPERTY CSConversion.conversionRule MANDATORY true;

CREATE PROPERTY CSRelationship.type STRING;
ALTER PROPERTY CSRelationship.type MANDATORY true;

CREATE PROPERTY CSHierarchy.type STRING;
ALTER PROPERTY CSHierarchy.type MANDATORY true;

CREATE PROPERTY CSPropaedeutic.contextSystem LINK DSDContextSystem;
ALTER PROPERTY CSPropaedeutic.contextSystem MANDATORY true;


CREATE PROPERTY DSDMain.startDate DATE;
CREATE PROPERTY DSDMain.endDate DATE;
CREATE PROPERTY DSDMain.supplemental EMBEDDEDMAP STRING;
CREATE PROPERTY DSDMain.columns LINKLIST DSDColumn;
CREATE PROPERTY DSDMain.contextSystem LINK DSDContextSystem;
CREATE PROPERTY DSDMain.datasource LINK DSDDatasource;
CREATE PROPERTY DSDMain.aggregationRules LINKLIST CMValueOperator;
ALTER PROPERTY DSDMain.columns MANDATORY true;
ALTER PROPERTY DSDMain.contextSystem MANDATORY true;
ALTER PROPERTY DSDMain.datasource MANDATORY true;

CREATE PROPERTY DSDDatasource.dao STRING;
CREATE PROPERTY DSDDatasource.reference EMBEDDEDMAP STRING;
ALTER PROPERTY DSDDatasource.dao MANDATORY true;
ALTER PROPERTY DSDDatasource.reference MANDATORY true;

CREATE PROPERTY DSDContextSystem.name STRING;
ALTER PROPERTY DSDContextSystem.name MANDATORY true;

CREATE PROPERTY DSDColumn.column STRING;
CREATE PROPERTY DSDColumn.title EMBEDDEDMAP STRING;
CREATE PROPERTY DSDColumn.supplemental EMBEDDEDMAP STRING;
CREATE PROPERTY DSDColumn.datatype STRING;
CREATE PROPERTY DSDColumn.codesLevel INTEGER;
CREATE PROPERTY DSDColumn.values EMBEDDEDLIST;
CREATE PROPERTY DSDColumn.virtualColumn STRING;
CREATE PROPERTY DSDColumn.dimension LINK DSDDimension;
CREATE PROPERTY DSDColumn.codeSystem LINK CSVersion;
ALTER PROPERTY DSDColumn.column MANDATORY true;
ALTER PROPERTY DSDColumn.dimension MANDATORY true;

CREATE PROPERTY DSDDimension.name STRING;
CREATE PROPERTY DSDDimension.title EMBEDDEDMAP STRING;
ALTER PROPERTY DSDDimension.name MANDATORY true;


CREATE PROPERTY DMMain.uid STRING;
CREATE PROPERTY DMMain.title EMBEDDEDMAP STRING;
CREATE PROPERTY DMMain.abstract EMBEDDEDMAP STRING;
CREATE PROPERTY DMMain.supplemental EMBEDDEDMAP STRING;
CREATE PROPERTY DMMain.creationDate DATE;
CREATE PROPERTY DMMain.updateDate DATE;
CREATE PROPERTY DMMain.dataType STRING;
CREATE PROPERTY DMMain.layerType STRING;
CREATE PROPERTY DMMain.dataKind STRING;
CREATE PROPERTY DMMain.periodicity STRING;
CREATE PROPERTY DMMain.copyrightPolicy STRING;
CREATE PROPERTY DMMain.language STRING;
CREATE PROPERTY DMMain.completeness  EMBEDDEDMAP STRING;
CREATE PROPERTY DMMain.accuracy EMBEDDEDMAP STRING;
CREATE PROPERTY DMMain.processing  EMBEDDEDMAP STRING;
CREATE PROPERTY DMMain.classificationSystems EMBEDDEDMAP STRING;
CREATE PROPERTY DMMain.dataAdjustment EMBEDDEDMAP STRING;
CREATE PROPERTY DMMain.confidentialityDataTreatment EMBEDDEDMAP STRING;
CREATE PROPERTY DMMain.confidentialityPolicy EMBEDDEDMAP STRING;
CREATE PROPERTY DMMain.releasePolicy EMBEDDEDMAP STRING;
CREATE PROPERTY DMMain.basePeriod EMBEDDED CMPeriod;
CREATE PROPERTY DMMain.referencePeriod EMBEDDED CMPeriod;
CREATE PROPERTY DMMain.weightReferencePeriod EMBEDDED CMPeriod;
CREATE PROPERTY DMMain.dsd LINK DSDMain;
CREATE PROPERTY DMMain.availability LINK DMAvailability;
CREATE PROPERTY DMMain.region LINK CSCode;
CREATE PROPERTY DMMain.categories LINKLIST CSCode;
CREATE PROPERTY DMMain.source LINKLIST CMContactIdentity;
CREATE PROPERTY DMMain.owner LINK CMContactIdentity;
CREATE PROPERTY DMMain.provider LINK CMContactIdentity;
CREATE PROPERTY DMMain.compiler LINK CMContactIdentity;
CREATE PROPERTY DMMain.contacts LINKLIST CMContactIdentity;
CREATE PROPERTY DMMain.transferOptions LINKLIST CMLink;
CREATE PROPERTY DMMain.um LINK CSCode;
CREATE PROPERTY DMMain.coverageGeographical LINK CSCode;
CREATE PROPERTY DMMain.disseminationPeriodicity LINK CSCode;
CREATE PROPERTY DMMain.updatePeriodicity LINK CSCode;
CREATE PROPERTY DMMain.processConceptsDocuments LINKLIST CMPublication;
CREATE PROPERTY DMMain.processMethodologyDocuments LINKLIST CMPublication;
CREATE PROPERTY DMMain.qualityMethodologyDocuments LINKLIST CMPublication;
CREATE PROPERTY DMMain.publications LINKLIST CMPublication;
CREATE PROPERTY DMMain.news LINKLIST CMPublication;
CREATE PROPERTY DMMain.dataSources LINKLIST DMDataSource;
ALTER PROPERTY DMMain.uid MANDATORY true;

CREATE PROPERTY DMMeta.metadataUID STRING;
CREATE PROPERTY DMMeta.availableDatasets LINKLIST DMMain;
ALTER PROPERTY DMMeta.metadataUID MANDATORY true;

CREATE PROPERTY DMAvailability.status STRING;
CREATE PROPERTY DMAvailability.chunksNumber INTEGER;
CREATE PROPERTY DMAvailability.chunksIndex EMBEDDEDLIST INTEGER;
ALTER PROPERTY DMAvailability.status MANDATORY true;

CREATE PROPERTY DMDataSource.type STRING;
CREATE PROPERTY DMDataSource.title EMBEDDEDMAP STRING;
CREATE PROPERTY DMDataSource.reference EMBEDDEDMAP STRING;




CREATE INDEX CMContactIdentity.textKey FULLTEXT;
CREATE INDEX CMContactIdentity.context.name NOTUNIQUE;

CREATE INDEX Index_CSVersion_key ON CSVersion (system,version) UNIQUE;
CREATE INDEX Index_CSCode_key ON CSCode (system,code) UNIQUE;
CREATE INDEX CSCode.index_title_EN FULLTEXT;
CREATE INDEX CSCode.index_title_ES FULLTEXT;
CREATE INDEX CSCode.index_title_FR FULLTEXT;
CREATE INDEX CSCode.index_title_DE FULLTEXT;

CREATE INDEX DSDContextSystem.name UNIQUE;
CREATE INDEX DSDDimension.name UNIQUE;

CREATE INDEX DMMeta.metadataUID UNIQUE;



DISCONNECT;
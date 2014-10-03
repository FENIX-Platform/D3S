package org.fao.fenix.d3s.msd.triggers;

import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.Set;

public class ResourceLinksManager extends LinksManager {


    @Override
    protected RESULT onUpdate(ODocument document, ODatabase connection) throws Exception {
        if (document!=null && "MeIdentification".equals(document.getClassName())) {
            linkCodes(document,
                "language",
                "characterSet",
                "metadataLanguage",
                "meAccessibility.seDataDissemination.seReleasePolicy.disseminationPeriodicity",
                "meAccessibility.seConfidentiality.confidentialityStatus",
                "meContent.seReferencePopulation.referencePeriod",
                "meContent.seReferencePopulation.referenceArea",
                "meContent.seCoverage.coverageSectors",
                "meContent.seCoverage.coverageGeographic",
                "meMaintenance.seUpdate.updatePeriodicity",
                "meResourceStructure.seResourceDimensions.dimensionSubject",
                "meResourceStructure.seResourceRecords.originOfCollectedValue",
                "meResourceStructure.seResourceRecords.observationStatus",
                "meReferenceSystem.seProjection.projection",
                "meReferenceSystem.seEllipsoid.ellipsoid",
                "meReferenceSystem.seDatum.datum",
                "meSpatialRepresentation.typeOfProduct",
                "meSpatialRepresentation.seBoundingBox.seVectorSpatialRepresentation.topologyLevel",
                "meStatisticalProcessing.seDataSource.sePrimaryDataCollection.typeOfCollection",
                "meStatisticalProcessing.seDataSource.sePrimaryDataCollection.collectionPeriodicity",
                "meStatisticalProcessing.seDataSource.seSecondaryDataCollection.originOfCollectedData",
                "meStatisticalProcessing.seDataSource.seSecondaryDataCollection.organization",
                "meStatisticalProcessing.seDataCompilation.dataAdjustment"
            ); //Codes linking
            return RESULT.RECORD_CHANGED; //Return changed status
        } else
            return RESULT.RECORD_NOT_CHANGED;
    }



}

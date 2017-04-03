package org.fao.fenix.export.d3s.legacy.md.layout.utils;


public enum SpecialBean {

    metadataLanguage,
    language,
    characterSet,
    disseminationPeriodicity,
    confidentialityStatus,
    referencePeriod,
    referenceArea,
    coverageSectors,
    coverageGeographic,
    updatePeriodicity,
    projection,
    ellipsoid,
    datum,
    typeOfProduct,
    processing,
    topologyLevel,
    typeOfCollection,
    collectionPeriodicity,
    originOfCollectedData,
    geo,
    dataAdjustment,


    //GIFT
    ResourceType,
    AssessmentMethod,
    RepeatedDietary,
    SurveyAdministrationMethod,
    statisticalPopulation,
    DataAlreadyCorrected,
    FoodCoverageTotal,
    DrinkingWater,
    SupplementInformation,
    QuantitiesReported,
    MacroDietaryComponents,
    MicroDietaryComponents,
    Age,
    Sex,
    BodyWeight,
    BodyHeight,
    PhysicalActivityLevel,
    InterviewDate,
    GeographicalLocalization,
    SocioDemographic,
    EducationLiteracy,
    Ethnicity;


    public static boolean isSpecialBean(String beanName) {

        for (SpecialBean c : SpecialBean.values()) {
            if (c.name().equals(beanName)) {
                return true;
            }
        }

        return false;
    }

    }

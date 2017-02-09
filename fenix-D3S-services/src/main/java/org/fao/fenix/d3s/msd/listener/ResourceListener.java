package org.fao.fenix.d3s.msd.listener;


import org.fao.fenix.commons.msd.dto.data.Resource;
import org.fao.fenix.commons.msd.dto.full.DSD;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;

public interface ResourceListener {


    //metadata
    void insertingMetadata (MeIdentification metadata);
    void insertedMetadata (MeIdentification metadata);
    void updatingMetadata (MeIdentification metadata);
    void updatedMetadata (MeIdentification metadata);
    void appendingMetadata (MeIdentification metadata);
    void appendedMetadata (MeIdentification metadata);
    void removingMetadata (MeIdentification metadata);
    void removedMetadata (String uid, String version);

    //resource
    void insertingResource (Resource resource);
    void insertedResource (MeIdentification metadata);
    void updatingResource (Resource resource);
    void updatedResource (MeIdentification metadata);
    void appendingResource (Resource resource);
    void appendedResource (MeIdentification metadata);
    void removingResource (MeIdentification metadata);
    void removedResource (String uid, String version);

    //DSD
    <T extends DSD> void updatingDSD (T dsd, MeIdentification metadata);
    <T extends DSD> void updatedDSD (MeIdentification metadata);
    <T extends DSD> void appendingDSD (T dsd, MeIdentification metadata);
    <T extends DSD> void appendedDSD (MeIdentification metadata);
    <T extends DSD> void removingDSD (MeIdentification metadata);
    <T extends DSD> void removedDSD (MeIdentification metadata);

    //data
    void removingData (MeIdentification metadata);
    void removedData (MeIdentification metadata);



}

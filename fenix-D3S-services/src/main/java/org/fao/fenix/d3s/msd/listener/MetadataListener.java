package org.fao.fenix.d3s.msd.listener;


import org.fao.fenix.commons.msd.dto.full.MeIdentification;

public interface MetadataListener {

    void insert(MeIdentification metadata) throws Exception;
    void update(MeIdentification currentMetadata, MeIdentification metadata) throws Exception;
    void append(MeIdentification currentMetadata, MeIdentification metadata) throws Exception;
    void remove(MeIdentification metadata) throws Exception;

}

package org.fao.fenix.d3s.msd.listener;


import org.fao.fenix.commons.msd.dto.full.MeIdentification;

public interface MetadataListener {

    boolean insert(MeIdentification metadata) throws Exception;
    boolean update(MeIdentification currentMetadata, MeIdentification metadata) throws Exception;
    boolean append(MeIdentification currentMetadata, MeIdentification metadata) throws Exception;
    boolean remove(MeIdentification metadata) throws Exception;

}

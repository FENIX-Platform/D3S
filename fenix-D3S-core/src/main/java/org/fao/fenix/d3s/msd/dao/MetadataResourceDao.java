package org.fao.fenix.d3s.msd.dao;

import org.fao.fenix.commons.msd.dto.full.Code;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class MetadataResourceDao extends ResourceDao {

    @Override
    public Collection loadData(MeIdentification metadata) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Collection insertData(MeIdentification metadata, Collection data) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Collection updateData(MeIdentification metadata, Collection data, boolean overwrite) throws Exception {
        throw new UnsupportedOperationException();
    }
}

package org.fao.fenix.d3s.msd.dao;

import org.fao.fenix.commons.msd.dto.full.Code;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class MetadataResourceDao extends ResourceDao<Code> {

    @Override
    public Collection<Code> loadData(MeIdentification metadata) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Collection<Code> insertData(MeIdentification metadata, Collection<Code> data) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Collection<Code> updateData(MeIdentification metadata, Collection<Code> data, boolean overwrite) throws Exception {
        throw new UnsupportedOperationException();
    }
}

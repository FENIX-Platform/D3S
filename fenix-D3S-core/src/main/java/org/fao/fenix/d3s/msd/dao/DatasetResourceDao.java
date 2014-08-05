package org.fao.fenix.d3s.msd.dao;

import org.fao.fenix.commons.msd.dto.full.Code;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.fenix.d3s.wds.WDSDaoFactory;
import org.fao.fenix.d3s.wds.WDSDatasetDao;

import javax.inject.Inject;
import java.util.*;

public class DatasetResourceDao extends ResourceDao<Object[]> {
    @Inject private WDSDaoFactory wdsFactory;

    @Override
    public Collection<Object[]> loadData(MeIdentification metadata) throws Exception {
        if (metadata!=null) {
            WDSDatasetDao wdsDao = getDao(metadata);
            if (wdsDao==null)
                throw new ClassNotFoundException("Cannot load data. DAO not found");

            Iterator<Object[]> data = wdsDao.loadData(metadata);
            if (data!=null) {
                Collection<Object[]> buffer = new LinkedList<>();
                while (data.hasNext())
                    buffer.add(data.next());
                return buffer;
            }
        }

        return null;
    }

    @Override
    protected Collection<Object[]> insertData(MeIdentification metadata, Collection<Object[]> data) throws Exception {
        return data!=null ? null : null;
    }

    @Override
    protected Collection<Object[]> updateData(MeIdentification metadata, Collection<Object[]> data, boolean overwrite) throws Exception {
        return data!=null ? null : null;
    }



    //Utils
    //TODO
    private WDSDatasetDao getDao(MeIdentification metadata) throws Exception {
        try {
            return (WDSDatasetDao) wdsFactory.getInstance("CountrySTAT");
        } catch (ClassCastException ex) {
            return null;
        }
    }

}

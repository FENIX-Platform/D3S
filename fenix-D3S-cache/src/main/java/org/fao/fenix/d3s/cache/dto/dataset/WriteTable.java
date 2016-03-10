package org.fao.fenix.d3s.cache.dto.dataset;

import org.fao.fenix.commons.msd.dto.full.DSDDataset;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;

import java.util.*;

public class WriteTable extends Table {


    public static WriteTable[] getInstances(MeIdentification<DSDDataset> ... metadataArray) {
        Collection<WriteTable> tables = new LinkedList<>();
        if (metadataArray!=null)
            for (MeIdentification<DSDDataset> metadata : metadataArray)
                if (metadata!=null)
                    tables.add(new WriteTable(metadata));

        return tables.size()>0 ? tables.toArray(new WriteTable[tables.size()]) : null;
    }


    public WriteTable(String tableName, DSDDataset structure) {
        DSDDataset dsd = structure!=null ? structure.clone() : null;
        if (dsd!=null)
            dsd.extend();
        init(tableName, dsd);
    }
    public WriteTable(MeIdentification<DSDDataset> metadata) {
        DSDDataset dsd = metadata!=null ? metadata.getDsd() : null;
        if (dsd!=null)
            dsd.extend();
        init(getTableName(metadata), dsd);
    }




}

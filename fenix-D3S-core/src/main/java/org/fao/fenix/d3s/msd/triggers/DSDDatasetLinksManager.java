package org.fao.fenix.d3s.msd.triggers;

import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.Collection;

public class DSDDatasetLinksManager extends LinksManager {


    @Override
    protected RESULT onUpdate(ODocument document, ODatabase connection) throws Exception {
        if (document!=null && "DSDDataset".equals(document.getClassName())) {
            Collection<ODocument> columns = document.field("columns");
            if (columns!=null)
                for (ODocument column : columns) {
                    linkCodes((Collection<ODocument>)column.field("domain.codes"));
                    linkCodes((Collection<ODocument>)column.field("values.codes"));
                }
            return RESULT.RECORD_CHANGED; //Return changed status
        } else
            return RESULT.RECORD_NOT_CHANGED;
    }

}

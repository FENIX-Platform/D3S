package org.fao.fenix.d3s.msd.triggers;

import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;

import javax.enterprise.context.ApplicationScoped;
import java.util.Collection;

@ApplicationScoped
public class DSDDatasetLinksManager extends LinksManager {


    @Override
    protected RESULT onUpdate(ODocument document, ODatabase connection) throws Exception {
        if (document!=null && "DSDDataset".equals(document.getClassName())) {
            linkCodes(document,
                    "columns.domain.codes",
                    "columns.values.codes"
            );
        }
        return RESULT.RECORD_CHANGED;
    }


    //INIT

    @Override
    public void init(OClass meIdentityClassO) throws Exception {

    }
}

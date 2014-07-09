package org.fao.fenix.d3s.msd.index;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.fao.fenix.d3s.server.tools.orient.DocumentTrigger;

import java.util.Map;
import java.util.Set;

public class DMMainIndexManager extends DocumentTrigger {


    @Override
    protected void init(ODatabaseDocumentTx connection, Set<OClass> includes, Map<String, Object> initParameters) {

    }

    @Override
    protected RESULT onUpdate(ODocument documentO, OClass classO, Set<String> updated) {
        for (String updatedProperty : updated) {
            OProperty indexProperty = classO.getProperty("index_" + updatedProperty);
            if (indexProperty!=null) { //if changed field has index property TODO verificare per i nodi annidati

            }
        }

        return RESULT.RECORD_CHANGED;
    }

    @Override
    public void onUnregister() {

    }
}

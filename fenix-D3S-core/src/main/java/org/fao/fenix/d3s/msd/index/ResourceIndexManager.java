package org.fao.fenix.d3s.msd.index;

import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.fao.fenix.d3s.server.tools.orient.DocumentTrigger;

import java.util.Set;

public class ResourceIndexManager extends DocumentTrigger {

    @Override
    protected RESULT onUpdate(ODocument documentO, ODatabase connection, OClass classO, Set<String> updated) {
        //TODO
        if (updated!=null) { //Updated record
            for (String updatedProperty : updated) {
                if (updatedProperty.equals("characterSet")) {
                    ODocument ojCodeList = documentO.field(updatedProperty);
                    String codelist = ojCodeList.field("codeList");
                    String version = ojCodeList.field("version");
                    //MeIdentification codeList =

                }
            }
        } else { //New record
        }


/*            OProperty indexProperty = classO.getProperty("index_" + updatedProperty);
            if (indexProperty!=null) { //if changed field has index property TODO verificare per i nodi annidati

            }
*/
         return RESULT.RECORD_NOT_CHANGED;
    }
}

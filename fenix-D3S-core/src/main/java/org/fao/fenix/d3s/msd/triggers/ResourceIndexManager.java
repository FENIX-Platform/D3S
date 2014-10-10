package org.fao.fenix.d3s.msd.triggers;

import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.Set;

public class ResourceIndexManager extends LinksManager {


    @Override
    protected RESULT onUpdate(ODocument document, ODatabase connection) throws Exception {
        if (document!=null && "MeIdentification".equals(document.getClassName())) {
            Set<String> updates = getDirtyFields(document);
            //index_id
            if (updates==null || updates.size()==0 || updates.contains("uid") || updates.contains("version")) {
                String uid = document.field("uid");
                String version = document.field("version");
                document.field("index_id", (uid!=null ? uid : "")+(version!=null ? version : ""));

                document.save();
                return RESULT.RECORD_CHANGED; //Return changed status
            }
        }
        return RESULT.RECORD_NOT_CHANGED;
    }
}

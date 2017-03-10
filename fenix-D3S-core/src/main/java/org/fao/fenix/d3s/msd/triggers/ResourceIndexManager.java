package org.fao.fenix.d3s.msd.triggers;

import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.fao.fenix.commons.find.dto.condition.ConditionFilter;
import org.fao.fenix.commons.msd.dto.type.ResponsiblePartyRole;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.*;

@ApplicationScoped
public class ResourceIndexManager extends LinksManager {
    @Inject ResourceLinksManager resourceLinksManager;
    @Inject DSDDatasetLinksManager dsdDatasetLinksManager;


    //LOGIC
    @Override
    protected RESULT onUpdate(ODocument document, ODatabase connection) throws Exception {
        resourceLinksManager.onUpdate(document, connection);
        dsdDatasetLinksManager.onUpdate(document, connection);

        if (document!=null && "MeIdentification".equals(document.getClassName())) {
            //ID
            String uid = document.field("uid");
            String version = document.field("version");
            if (uid != null)
                document.field("index|id", uid + (version != null && !version.trim().equals("") ? '|' + version : ""));
        }

        return RESULT.RECORD_CHANGED;
    }


    //INIT
    @Override
    public void init(OClass meIdentityClassO) throws Exception {

    }
}

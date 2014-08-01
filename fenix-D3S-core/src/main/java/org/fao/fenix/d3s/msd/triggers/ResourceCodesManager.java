package org.fao.fenix.d3s.msd.triggers;

import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.fao.fenix.commons.msd.dto.full.Code;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.fenix.d3s.msd.dao.CodeListResourceDao;
import org.fao.fenix.d3s.server.tools.orient.DocumentTrigger;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;

public class ResourceCodesManager extends DocumentTrigger {
    @Inject private CodeListResourceDao codeListDao;
    @Inject private ResourceFieldsProperties fieldsProperties;


    @Override
    protected RESULT onUpdate(ODocument document, ODatabase connection) throws Exception {
        if (document!=null && "MeIdentification".equals(document.getClassName())) {
            //Codes linking
            for (String ojCodeField : fieldsProperties.getOjCodeFields())
                linkCodes((ODocument)document.field(ojCodeField));
            for (String ojCodeField : fieldsProperties.getOjCodeCollectionFields())
                linkCodes((Collection<ODocument>)document.field(ojCodeField));
            //Resource index informations refresh
            createIndexes(document);
            //Return changed status
            return RESULT.RECORD_CHANGED;
        } else
            return RESULT.RECORD_NOT_CHANGED;
    }


    //Utils


    private void createIndexes (ODocument document) throws Exception {
        Set<String> updates = getDirtyFields(document);
        //index_id
        if (updates==null || updates.size()==0 || updates.contains("uid") || updates.contains("version")) {
            String uid = document.field("uid");
            String version = document.field("version");
            document.field("index_id", (uid!=null ? uid : "")+(version!=null ? version : ""));
        }

    }



    private void linkCodes(Collection<ODocument> ojCodeDocuments) throws Exception {
        if (ojCodeDocuments!=null)
            for (ODocument ojCodeDocument : ojCodeDocuments)
                linkCodes(ojCodeDocument);
    }
    private void linkCodes(ODocument ojCodeDocument) throws Exception {
        if (ojCodeDocument!=null) {
            MeIdentification resource = codeListDao.loadMetadata((String) ojCodeDocument.field("codeList"), (String) ojCodeDocument.field("version"));
            if (resource != null) {
                ojCodeDocument.field("linkedCodeList", resource.getORID());

                Collection<ODocument> codesO = ojCodeDocument.field("codes");
                if (codesO != null && codesO.size() > 0)
                    for (ODocument codeO : codesO)
                        if (codeO.field("code") != null) {
                            Collection<Code> code = codeListDao.loadData(resource, new String[]{(String)codeO.field("code")});
                            if (code!=null && code.size()>0)
                                codeO.field("linkedCode", code.iterator().next().getORID());
                        }
            }
        }
    }

}

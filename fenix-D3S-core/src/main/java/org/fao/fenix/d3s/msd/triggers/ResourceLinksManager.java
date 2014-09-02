package org.fao.fenix.d3s.msd.triggers;

import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.fao.fenix.commons.msd.dto.full.Code;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.fenix.d3s.msd.dao.CodeListResourceDao;
import org.fao.fenix.d3s.server.tools.orient.DocumentTrigger;

import javax.inject.Inject;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;
import java.util.Stack;

public class ResourceLinksManager extends DocumentTrigger {
    @Inject private CodeListResourceDao codeListDao;
    @Inject private ResourceLinkableProperties fieldsProperties;


    @Override
    protected RESULT onUpdate(ODocument document, ODatabase connection) throws Exception {
        if (document!=null && "MeIdentification".equals(document.getClassName())) {
            //Codes linking
            //TODO
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
    private Collection<ODocument> getFields(ODocument root, String path) {
        return root!=null && path!=null ? getFields(root,path.split("."),-1,new LinkedList<ODocument>()) : null;
    }
    private Collection<ODocument> getFields(Object field, String[] path, int index, Collection<ODocument> fields) {
        if (field instanceof Collection)
            for (Object fieldElement : ((Collection)field))
                getFields(fieldElement, path, index, fields);
        else if (field!=null && field instanceof ODocument)
            if (index==path.length-1)
                fields.add((ODocument)field);
            else
                getFields(((ODocument)field).field(path[index+1]), path, index+1, fields);

        return fields;
    }


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

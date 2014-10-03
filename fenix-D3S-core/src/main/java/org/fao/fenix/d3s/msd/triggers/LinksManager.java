package org.fao.fenix.d3s.msd.triggers;

import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.fao.fenix.commons.msd.dto.full.Code;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.fenix.d3s.msd.dao.CodeListResourceDao;
import org.fao.fenix.d3s.server.tools.orient.DocumentTrigger;

import javax.inject.Inject;
import java.util.Collection;
import java.util.LinkedList;

public abstract class LinksManager extends DocumentTrigger {
    @Inject private CodeListResourceDao codeListDao;


    //Logic

    protected void linkCodes (ODocument document, String ... fields) throws Exception {
        for (String ojCodeField : fields)
            linkCodes(getFields(document, ojCodeField));
    }

    protected void linkCodes(Collection<ODocument> ojCodeDocuments) throws Exception {
        if (ojCodeDocuments!=null)
            for (ODocument ojCodeDocument : ojCodeDocuments)
                linkCodes(ojCodeDocument);
    }
    protected void linkCodes(ODocument ojCodeDocument) throws Exception {
        if (ojCodeDocument!=null) {
            MeIdentification resource = codeListDao.loadMetadata((String) ojCodeDocument.field("idCodeList"), (String) ojCodeDocument.field("version"));
            if (resource != null) {
                ojCodeDocument.field("linkedCodeList", resource.getORID());

                Collection<ODocument> codesO = ojCodeDocument.field("codes");
                if (codesO != null && codesO.size() > 0)
                    for (ODocument codeO : codesO)
                        if (codeO.field("code") != null) {
                            Collection<Code> code = codeListDao.loadData(resource, new String[]{(String)codeO.field("code")});
                            if (code!=null && code.size()>0)
                                codeO.field("linkedCode", code.iterator().next().getORID());
                            //else
                                //throw new Exception("Validation error: code '" + ojCodeDocument.field("idCodeList") + " - " + ojCodeDocument.field("version") + " - " + codeO.field("code") + "' not found");
                        }
            } else {
                ojCodeDocument.field("linkedCodeList", null, OType.LINK);
                Collection<ODocument> codesO = ojCodeDocument.field("codes");
                if (codesO != null && codesO.size() > 0)
                    for (ODocument codeO : codesO)
                        codeO.field("linkedCode", null, OType.LINK);
            }
        }
    }


    //Utils

    private Collection<ODocument> getFields(ODocument root, String path) {
        return root!=null && path!=null ? getFields(root,path.split("\\."),-1,new LinkedList<ODocument>()) : null;
    }
    private Collection<ODocument> getFields(Object field, String[] path, int index, Collection<ODocument> fields) {
        if (field instanceof Collection)
            for (Object fieldElement : ((Collection)field))
                getFields(fieldElement, path, index, fields);
        else if (field!=null && field instanceof ODocument)
            if (++index==path.length)
                fields.add((ODocument)field);
            else
                getFields(((ODocument)field).field(path[index]), path, index, fields);

        return fields;
    }

}

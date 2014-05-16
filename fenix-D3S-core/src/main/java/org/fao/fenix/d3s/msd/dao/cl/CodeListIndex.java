package org.fao.fenix.d3s.msd.dao.cl;

import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.fao.fenix.commons.msd.dto.cl.CodeSystem;
import org.fao.fenix.d3s.server.tools.SupportedLanguages;
import org.fao.fenix.d3s.server.tools.orient.OrientDao;
import org.fao.fenix.d3s.server.tools.orient.OrientDatabase;

import javax.inject.Inject;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class CodeListIndex extends OrientDao {

    @Inject private CodeListLoad loadDao;

    public int rebuildIndex (CodeSystem cl) throws Exception {
        OGraphDatabase database = getDatabase(OrientDatabase.msd);
        try {
            return rebuildIndex(cl,database);
        } finally {
            if (database!=null)
                database.close();
        }
    }

    public int rebuildIndex(CodeSystem cl, OGraphDatabase database) throws Exception {
        Collection<ODocument> clsO;
        if (cl!=null) {
            clsO = new LinkedList<>();
            ODocument clO = loadDao.loadSystemO(cl.getSystem(), cl.getVersion(), database);
            if (clO!=null)
                clsO.add(clO);
        } else
            clsO = loadDao.loadSystemO(database);

        for (ODocument clO : clsO)
            rebuildIndex(clO, true);

        return clsO.size();
    }

    public void rebuildIndex(ODocument clO, boolean all) {
        if (all) {
            Collection<ODocument> rootCodes = clO.field("rootCodes");
            if (rootCodes!=null)
                for (ODocument codeO : rootCodes) {
                    rebuildCodeIndex(codeO, all);
                    codeO.save();
                }
        }
    }

    public void rebuildCodeIndex(ODocument codeO, boolean all) {
        //Code index information reset
        Map<String,String> title = codeO.field("title");
        if (title==null)
            title = new HashMap<>();
        for (SupportedLanguages language : SupportedLanguages.values())
            codeO.field("index_title_"+language.getCode(),title.get(language.getCode()), OType.STRING);

        Collection<ODocument> children = codeO.field("childs");
        if (all && children!=null)
            for (ODocument child : children)
                rebuildCodeIndex(child, all);

    }


}

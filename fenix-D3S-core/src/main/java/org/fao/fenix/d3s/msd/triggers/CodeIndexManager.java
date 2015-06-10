package org.fao.fenix.d3s.msd.triggers;

import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;

import javax.enterprise.context.ApplicationScoped;
import java.util.Map;

@ApplicationScoped
public class CodeIndexManager extends LinksManager {


    @Override
    protected RESULT onUpdate(ODocument document, ODatabase connection) throws Exception {
        if (document!=null && "Code".equals(document.getClassName())) {
            String code = document.field("code");
            Map<String, String> title = document.field("title");
            Map<String, String> description = document.field("description");
            //Update code label index
            StringBuilder indexLabel = new StringBuilder(code);
            if (title!=null)
                for (String t : title.values())
                    indexLabel.append(' ').append(t);
            if (description!=null)
                for (String d : description.values())
                    indexLabel.append(' ').append(d);
            document.field("indexLabel",indexLabel.toString());
        }
        return RESULT.RECORD_CHANGED;
    }


    //INIT

    @Override
    public void init(OClass meIdentityClassO) throws Exception {

    }
}

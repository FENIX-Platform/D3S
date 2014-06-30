package org.fao.fenix.d3s.server.tools.orient;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.hook.ORecordHook;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.record.ORecord;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.server.OServer;
import com.orientechnologies.orient.server.config.OServerParameterConfiguration;

import java.util.*;

public abstract class DocumentTrigger implements ORecordHook {
    private Set<OClass> classes = new HashSet<>();
    private OServer oServer;

    public void config(OServer oServer, OServerParameterConfiguration[] iParams) {
        this.oServer = oServer;
        //Retrieve init parameters
        Map<String,Object> initParameters = new HashMap<>();
        if (iParams!=null)
            for (OServerParameterConfiguration param : iParams)
                initParameters.put(param.name,param.value);
        //Include classes and init trigger
        ODatabaseDocumentTx connection = null;
        try {
            OSchema msdSchema = (connection = getConnection()).getMetadata().getSchema();

            if (initParameters.containsKey("classes"))
                for (String className : ((String) initParameters.get("classes")).split(","))
                    classes.add(msdSchema.getClass(className.trim()));

            init(connection, classes, initParameters);
        } finally {
            if (connection!=null)
                connection.close();
        }

    }

    @Override
    public RESULT onTrigger(TYPE type, ORecord<?> oRecord) {
        OClass classO = null;
        if (    (type==TYPE.AFTER_CREATE || type==TYPE.AFTER_UPDATE) &&
                oRecord instanceof ODocument &&
                oRecord.isDirty() &&
                classes.contains(classO = ((ODocument)oRecord).getSchemaClass()) || classes.size()==0
            )
            return onUpdate((ODocument)oRecord, classO, new HashSet<>(Arrays.asList(((ODocument)oRecord).getDirtyFields())));
        else
            return RESULT.RECORD_NOT_CHANGED;
    }

    @Override
    public DISTRIBUTED_EXECUTION_MODE getDistributedExecutionMode() {
        return DISTRIBUTED_EXECUTION_MODE.SOURCE_NODE;
    }



    //Specific trigger actions
    protected abstract void init(ODatabaseDocumentTx connection, Set<OClass> includes, Map<String,Object> initParameters);
    protected abstract RESULT onUpdate(ODocument documentO, OClass classO, Set<String> updated);



    //Utils
    protected ODatabaseDocumentTx getConnection() {
        return oServer.getDatabasePool().acquire("plocal:"+oServer.getDatabaseDirectory()+"/msd_1.0","admin","admin");
    }


}

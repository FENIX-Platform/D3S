package org.fao.fenix.d3s.server.tools.orient;

import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.db.ODatabaseComplex;
import com.orientechnologies.orient.core.db.ODatabaseLifecycleListener;
import com.orientechnologies.orient.core.hook.ORecordHook;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.ORecord;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.*;

public abstract class DocumentTrigger extends OrientDao implements ORecordHook, ODatabaseLifecycleListener {
    //ODatabaseLifecycleListener implementation
    @Override
    public void onUnregister() {

    }

    @Override
    public void onCreate(ODatabase oDatabase) {
        ((ODatabaseComplex<?>)oDatabase).registerHook(this);
    }

    @Override
    public void onOpen(ODatabase oDatabase) {
        ((ODatabaseComplex<?>)oDatabase).registerHook(this);
    }

    @Override
    public void onClose(ODatabase oDatabase) {

    }

    //ORecordHook implementation
    @Override
    public RESULT onTrigger(TYPE type, ORecord<?> oRecord) {
        try {
            return type==TYPE.AFTER_CREATE || type==TYPE.AFTER_UPDATE ? onUpdate((ODocument)oRecord, getConnection()) : RESULT.RECORD_NOT_CHANGED;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public DISTRIBUTED_EXECUTION_MODE getDistributedExecutionMode() {
        return DISTRIBUTED_EXECUTION_MODE.SOURCE_NODE;
    }



    //Specific trigger actions
    protected abstract RESULT onUpdate(ODocument document, ODatabase connection) throws Exception;


    //Utils
    protected Set<String> getDirtyFields(ODocument document) {
        return document!=null ? new HashSet<>(Arrays.asList((document).getDirtyFields())) : null;
    }
}

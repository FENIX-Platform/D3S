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

    @Override
    public RESULT onTrigger(TYPE type, ORecord<?> oRecord) {
        switch (type) {
            case AFTER_CREATE: return onUpdate((ODocument)oRecord, getConnection(), ((ODocument) oRecord).getSchemaClass(), null);
            case AFTER_UPDATE: return onUpdate((ODocument)oRecord, getConnection(), ((ODocument) oRecord).getSchemaClass(), new HashSet<>(Arrays.asList(((ODocument)oRecord).getDirtyFields())));
            default: return RESULT.RECORD_NOT_CHANGED;
        }
    }

    @Override
    public DISTRIBUTED_EXECUTION_MODE getDistributedExecutionMode() {
        return DISTRIBUTED_EXECUTION_MODE.SOURCE_NODE;
    }



    //Specific trigger actions
    protected abstract RESULT onUpdate(ODocument documentO, ODatabase connection, OClass classO, Set<String> updated);

}

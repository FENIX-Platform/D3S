package org.fao.fenix.d3s.server.tools.orient;

import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.db.ODatabaseInternal;
import com.orientechnologies.orient.core.db.ODatabaseLifecycleListener;
import com.orientechnologies.orient.core.hook.ORecordHook;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.ORecord;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;

import javax.inject.Inject;
import java.util.*;

public abstract class DocumentTrigger extends OrientDao implements ORecordHook, ODatabaseLifecycleListener {

    @Override
    public void onUnregister() {

    }

    @Override
    public PRIORITY getPriority() {
        return PRIORITY.REGULAR;
    }

    @Override
    public void onCreate(ODatabaseInternal oDatabase) {
        oDatabase.registerHook(this);
    }

    @Override
    public void onOpen(ODatabaseInternal oDatabase) {
        oDatabase.registerHook(this);
    }

    @Override
    public void onClose(ODatabaseInternal oDatabase) {
        oDatabase.unregisterHook(this);
    }

    @Override
    public void onCreateClass(ODatabaseInternal oDatabaseInternal, OClass oClass) {
        //Nothing to do here
    }

    @Override
    public void onDropClass(ODatabaseInternal oDatabaseInternal, OClass oClass) {
        //Nothing to do here
    }

    //ORecordHook implementation
    @Override
    public RESULT onTrigger(TYPE type, ORecord oRecord) {
        OObjectDatabaseTx localConnection = null;
        try {
            OObjectDatabaseTx connection = getConnection();
            if (connection==null)
                dbParameters.setConnection(connection = localConnection = client.getODatabase(OrientDatabase.msd));

            return type==TYPE.AFTER_CREATE || type==TYPE.AFTER_UPDATE ? onUpdate((ODocument)oRecord, connection) : RESULT.RECORD_NOT_CHANGED;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (localConnection!=null) {
                localConnection.close();
                dbParameters.setConnection(null);
            }
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

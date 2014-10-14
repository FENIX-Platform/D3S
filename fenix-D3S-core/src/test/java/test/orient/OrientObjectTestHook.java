package test.orient;

import java.util.Collection;
import java.util.Date;

import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.object.db.OObjectDatabasePool;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.junit.Test;

import com.orientechnologies.orient.core.Orient;
import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.db.ODatabaseComplex;
import com.orientechnologies.orient.core.db.ODatabaseLifecycleListener;
import com.orientechnologies.orient.core.hook.ORecordHook;
import com.orientechnologies.orient.core.record.ORecord;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;

public class OrientObjectTestHook {
    private OObjectDatabasePool oPool = OObjectDatabasePool.global(10,300);

    @Test
    public void testHooks() {

        Orient.instance().addDbLifecycleListener(new OrientObjectHook());
        /*
        OObjectDatabaseTx db = new OObjectDatabaseTx("plocal:database/databases/testhooks");
        db.open("admin", "admin");
        */
        OObjectDatabaseTx db = oPool.acquire("plocal:database/databases/msd_2.0","admin","admin");
        db.getEntityManager().registerEntityClasses("org.fao.fenix.commons.msd.dto.full");
        Collection<MeIdentification> data = (Collection<MeIdentification>)db.query(new OSQLSynchQuery<MeIdentification>("select from MeIdentification"));

        db.begin();
        for (MeIdentification resource : data) {
            db.save(resource);
        }
        db.commit();
        db.close();
    }

    public class OrientObjectHook implements ORecordHook, ODatabaseLifecycleListener {

        @Override
        public void onUnregister() {

        }

        @Override
        public RESULT onTrigger(TYPE iType, ORecord<?> iRecord) {
            return RESULT.RECORD_NOT_CHANGED;
        }

        @Override
        public DISTRIBUTED_EXECUTION_MODE getDistributedExecutionMode() {
            return null;
        }

        @Override
        public void onCreate(ODatabase iDatabase) {
            ((ODatabaseComplex<?>) iDatabase).registerHook(this);
        }

        @Override
        public void onOpen(ODatabase iDatabase) {
            ((ODatabaseComplex<?>) iDatabase).registerHook(this);
        }

        @Override
        public void onClose(ODatabase iDatabase) {
            ((ODatabaseComplex<?>) iDatabase).unregisterHook(this);
        }
    }

    public class Demo {

        private String description;
        private Date   createdAt;

        public void setDescription(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

        public void setCreatedAt(Date createdAt) {
            this.createdAt = createdAt;
        }

        public Date getCreatedAt() {
            return createdAt;
        }
    }
}
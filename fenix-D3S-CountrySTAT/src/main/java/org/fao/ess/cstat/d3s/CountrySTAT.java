package org.fao.ess.cstat.d3s;


import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.db.record.ODatabaseRecord;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.fenix.d3s.wds.dataset.DatasetStructure;
import org.fao.fenix.d3s.wds.dataset.WDSDatasetDao;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Iterator;
import java.util.Map;

@ApplicationScoped
public class CountrySTAT extends WDSDatasetDao {
    @Inject private OrientClient dbClient;
    private boolean initialized = false;

    public boolean init() {
        return !initialized;
    }
    @Override
    public void init(Map<String, String> properties) throws Exception {
        if (!initialized)
            dbClient.init(properties.get("url"),properties.get("usr"),properties.get("psw"));
    }


    @Override
    public void consume(Object... args) {
        ODatabaseRecordThreadLocal.INSTANCE.set((ODatabaseRecord)args[0]);
    }

    @Override
    public void consumed(Object... args) {
        ODatabaseRecordThreadLocal.INSTANCE.set((ODatabaseRecord)args[1]);
        ((ODatabaseRecord)args[0]).close();
    }


    @Override
    public Iterator<Object[]> loadData(MeIdentification resource, DatasetStructure datasetStructure) throws Exception {
        ODatabaseRecord originalConnection = ODatabaseRecordThreadLocal.INSTANCE.get();

        try {
            ODatabaseDocumentTx connection = dbClient.getConnection();
            if (connection != null) {
                final String[] ids = new String[] {"ITEM", "TIME", "VALUE", "FLAG"};

                final Iterator<ODocument> data = (Iterator<ODocument>)connection.query(new OSQLSynchQuery<ODocument>("select from Dataset where datasetID = ?"), "233CPD010").iterator();

                return getConsumerIterator(new Iterator<Object[]>() {
                    String[] fields = ids;
                    @Override
                    public boolean hasNext() {
                        return data.hasNext();
                    }

                    @Override
                    public Object[] next() {
                        ODocument record = data.next();
                        Object[] result = new Object[fields.length];
                        for (int i=0; i<result.length; i++)
                            result[i] = record.field(fields[i]);
                        return result;
                    }

                    @Override
                    public void remove() {
                        data.remove();
                    }
                }, ODatabaseRecordThreadLocal.INSTANCE.get(), originalConnection);
            } else
                return null;
        } finally {
            ODatabaseRecordThreadLocal.INSTANCE.set(originalConnection);
        }
    }

    @Override
    protected void storeData(MeIdentification resource, Iterator<Object[]> data, boolean overwrite, DatasetStructure structure) throws Exception {
        throw new UnsupportedOperationException();
    }

}






/*

    @Override
    public Iterator<Object[]> loadData(MeIdentification resource, DatasetStructure datasetStructure) throws Exception {
        ODatabaseRecord originalConnection = ODatabaseRecordThreadLocal.INSTANCE.get();

        try {
            ODatabaseDocumentTx connection = dbClient.getConnection();
            if (connection != null) {
                final String[] ids = new String[] {"ITEM", "TIME", "VALUE", "FLAG"};

                final Iterator<ODocument> data = (Iterator<ODocument>)connection.query(new OSQLSynchQuery<ODocument>("select from Dataset where datasetID = ?"), "233CPD010").iterator();

                return getConsumerIterator(new Iterator<Object[]>() {
                    String[] fields = ids;
                    @Override
                    public boolean hasNext() {
                        return data.hasNext();
                    }

                    @Override
                    public Object[] next() {
                        ODocument record = data.next();
                        Object[] result = new Object[fields.length];
                        for (int i=0; i<result.length; i++)
                            result[i] = record.field(fields[i]);
                        return result;
                    }

                    @Override
                    public void remove() {
                        data.remove();
                    }
                }, ODatabaseRecordThreadLocal.INSTANCE.get(), originalConnection);
            } else
                return null;
        } finally {
            ODatabaseRecordThreadLocal.INSTANCE.set(originalConnection);
        }
    }

 */
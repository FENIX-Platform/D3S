package org.fao.ess.cstat.d3s;


import com.orientechnologies.orient.core.db.ODatabaseDocumentInternal;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.intent.OIntentMassiveInsert;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import org.fao.fenix.commons.msd.dto.templates.standardDsd.dataset.MeIdentificationDSDFull;
import org.fao.fenix.d3s.wds.dataset.DatasetStructure;
import org.fao.fenix.d3s.wds.dataset.WDSDatasetDao;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.*;

@ApplicationScoped
public class CountrySTAT extends WDSDatasetDao {
    @Inject private OrientClient dbClient;
    private boolean initialized = false;

    @Override
    public boolean init() {
        return !initialized;
    }
    @Override
    public void init(Map<String, String> properties) throws Exception {
        if (!initialized)
            dbClient.init(properties.get("url"),properties.get("usr"),properties.get("psw"));
        initialized = true;
    }


    @Override
    public void consume(Object... args) {
        ODatabaseRecordThreadLocal.INSTANCE.set((ODatabaseDocumentInternal)args[0]);
    }

    @Override
    public void consumed(Object... args) {
        ODatabaseRecordThreadLocal.INSTANCE.set((ODatabaseDocumentInternal)args[1]);
        ((ODatabaseDocumentInternal)args[0]).close();
    }


    @Override
    public Iterator<Object[]> loadData(MeIdentificationDSDFull resource, DatasetStructure structure) throws Exception {
        String uid = resource.getUid();

        ODatabaseDocumentInternal originalConnection = ODatabaseRecordThreadLocal.INSTANCE.get();

        try {
            ODatabaseDocumentTx connection = dbClient.getConnection();
            if (connection != null && structure.selectColumns!=null) {
                final Iterator<ODocument> data = (Iterator<ODocument>)connection.query(new OSQLSynchQuery<ODocument>("select from Dataset where datasetID = ? order by @rid"), uid).iterator();
                final String[] ids = new String[structure.selectColumns.length];
                for (int i=0; i<ids.length; i++)
                    ids[i] = structure.selectColumns[i].getId();

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
    protected void storeData(MeIdentificationDSDFull resource, Iterator<Object[]> data, boolean overwrite, DatasetStructure structure) throws Exception {
        String datasetID = resource!=null ? resource.getUid() : null;
        if (data!=null && data.hasNext() && datasetID!=null) {

            ODatabaseDocumentInternal originalConnection = ODatabaseRecordThreadLocal.INSTANCE.get();
            ODatabaseDocumentTx connection = dbClient.getConnection();
            if (connection == null)
                throw new Exception("Cannot connect to CountrySTAT database");

            try {
                //Prepare data in append append mode
                if (!overwrite && structure.keyColumnsIndexes.length > 0) {
                    Iterator<Object[]> existingData = loadData(resource, structure);
                    if (existingData != null && existingData.hasNext()) {
                        StringBuilder keyBuffer = new StringBuilder();
                        Map<String, Object[]> buffer = new LinkedHashMap<>();

                        for (Object[] row = data.next(); data.hasNext(); row = data.next()) {
                            for (int i : structure.keyColumnsIndexes)
                                keyBuffer.append(row[i]);
                            buffer.put(keyBuffer.toString(), row);
                        }

                        for (Object[] row = existingData.next(); existingData.hasNext(); row = existingData.next()) {
                            for (int i : structure.keyColumnsIndexes)
                                keyBuffer.append(row[i]);
                            buffer.put(keyBuffer.toString(), row);
                        }

                        data = buffer.values().iterator();
                    }
                }

                //Write data
                connection.declareIntent(new OIntentMassiveInsert());
                connection.begin();
                connection.command(new OCommandSQL("delete from Dataset where datasetID = ?")).execute(datasetID);
                Collection<Integer> rowsError = new LinkedList<>();
                for (int rowIndex=1; data.hasNext(); rowIndex++) {
                    Object[] row = data.next();
                    ODocument rowO = new ODocument("Dataset");
                    rowO.field("datasetID", datasetID);
                    try {
                        for (int i = 0; i < structure.selectColumns.length; i++)
                            rowO.field(structure.selectColumns[i].getId(), row[i]);
                        rowO.save();
                    } catch (Exception ex) {
                        rowsError.add(rowIndex);
                    }
                }
                if (rowsError.size()>0)
                    throw new Exception("Row insert error on rows:"+toPointList(rowsError));

                connection.commit();

            } catch (Exception ex) {
                if (connection!=null)
                    connection.rollback();
                throw ex;
            } finally {
                if (connection!=null)
                    connection.close();
                ODatabaseRecordThreadLocal.INSTANCE.set(originalConnection);
            }
        }
    }

    private String toPointList(Collection<Integer> list) {
        StringBuilder buffer = new StringBuilder();
        for (Integer text : list)
            buffer.append("\n- ").append(text);
        return buffer.toString();
    }

    @Override
    public void deleteData(MeIdentificationDSDFull resource) throws Exception {
        String datasetID = resource!=null ? resource.getUid() : null;
        if (datasetID!=null) {

            ODatabaseDocumentInternal originalConnection = ODatabaseRecordThreadLocal.INSTANCE.get();
            ODatabaseDocumentTx connection = dbClient.getConnection();
            if (connection == null)
                throw new Exception("Cannot connect to CountrySTAT database");

            try {
                connection.command(new OCommandSQL("delete from Dataset where datasetID = ?")).execute(datasetID);
            } finally {
                if (connection != null)
                    connection.close();
                ODatabaseRecordThreadLocal.INSTANCE.set(originalConnection);
            }
        }
    }


}







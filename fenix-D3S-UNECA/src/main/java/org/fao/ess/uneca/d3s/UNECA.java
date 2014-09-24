package org.fao.ess.uneca.d3s;

import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.fenix.commons.utils.DatabaseUtils;
import org.fao.fenix.d3s.wds.dataset.DatasetStructure;
import org.fao.fenix.d3s.wds.dataset.WDSDatasetDao;

import javax.inject.Inject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;

public class UNECA extends WDSDatasetDao {
    @Inject DataSource dataSource;
    @Inject DatabaseUtils databaseUtils;
    private boolean initialized = false;

    @Override
    public boolean init() {
        return !initialized;
    }

    @Override
    public void init(Map<String, String> properties) throws Exception {
        if (!initialized)
            dataSource.init(properties.get("url"),properties.get("usr"),properties.get("psw"));
        initialized = true;
    }

    @Override
    public void consume(Object... args) {
        //Nothing to do in this method
    }

    @Override
    public void consumed(Object... args) {
        Connection connection = args!=null && args.length>0 ? (Connection)args[0] : null;
        if (connection!=null)
            try { connection.close(); } catch (SQLException e) { }
    }
    //
    @Override
    protected Iterator<Object[]> loadData(MeIdentification resource, DatasetStructure structure) throws Exception {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement("select \"Domain_Code\",\"Topic_Code\",\"AreaCode\",\"DimensionA_Code\",\"DimensionB_Code\",\"DimensionC_Code\",\"Indicator_Code\",\"UnitCode\",\"Time\",\"Value\",\"MajorDatasourceid\",\"DataStatustype\",\"BaseYear\",\"Note\" FROM data WHERE \"Metadata_ID\" = ?");
            statement.setString(1,resource.getUid());

            return getConsumerIterator(databaseUtils.getDataIterator(statement.executeQuery()), connection);
        } catch (Exception ex) {
            if (connection!=null)
                try { connection.close(); } catch (SQLException e) { }
            throw ex;
        }
    }

    @Override
    protected void storeData(MeIdentification resource, Iterator<Object[]> data, boolean overwrite, DatasetStructure structure) throws Exception {
        throw new UnsupportedOperationException();
    }

}

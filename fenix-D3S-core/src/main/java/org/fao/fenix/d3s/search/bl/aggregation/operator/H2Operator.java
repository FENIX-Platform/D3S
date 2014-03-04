package org.fao.fenix.d3s.search.bl.aggregation.operator;

import org.fao.fenix.d3s.msd.dto.common.ValueOperator;
import org.fao.fenix.d3s.server.tools.h2.H2Database;
import org.h2.api.AggregateFunction;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.util.HashSet;
import java.util.Set;

public abstract class H2Operator extends Operator implements AggregateFunction {

    @Override
    public void init(ValueOperator operatorInfo) throws Exception {
        super.init(operatorInfo);

        Component customAnnotation = this.getClass().getAnnotation(Component.class);
        if (customAnnotation!=null)
            operatorAlias = customAnnotation.value().replace('.','_');
        else
            throw new Exception("Undefined operator name for H2 custom function.");

        registerH2Function();
    }

    public boolean isInitialized() { return h2Initialized.contains(operatorAlias); }
    public String getAggregationBaseName() { return operatorAlias; }


    //Utils
    private static Set<String> h2Initialized = new HashSet<String>();
    private String operatorAlias;
    private void registerH2Function() throws Exception {
        if (!h2Initialized.contains(operatorAlias)) {
            String className = this.getClass().getName();
            OperatorColumns columnParametersAnnotation = this.getClass().getAnnotation(OperatorColumns.class);

            H2Database database = H2Database.aggregation;
            Connection connection = database.getConnection();
            connection.createStatement().execute("CREATE AGGREGATE IF NOT EXISTS "+ operatorAlias +" FOR \""+className+'"');
            if (columnParametersAnnotation!=null)
                connection.createStatement().execute("CREATE ALIAS IF NOT EXISTS "+ operatorAlias +"_prepare FOR \""+className+".prepareValueForAggregation\"");
            database.closeConnection(connection);

            h2Initialized.add(operatorAlias);
        }
    }



}

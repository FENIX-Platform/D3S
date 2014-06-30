package org.fao.fenix.d3s.search.bl.aggregation;

import org.fao.fenix.commons.msd.dto.templates.canc.common.ValueOperator;
import org.fao.fenix.d3s.search.bl.aggregation.operator.H2Operator;
import org.fao.fenix.d3s.search.bl.aggregation.operator.Operator;
import org.fao.fenix.d3s.search.bl.aggregation.operator.OperatorFactory;
import org.fao.fenix.d3s.server.tools.h2.H2Dao;
import org.fao.fenix.d3s.server.tools.h2.H2Database;
import org.fao.fenix.d3s.search.SearchStep;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

@Dependent
public class H2AggregationDao extends H2Dao {
    @Inject private OperatorFactory operatorFactory;

    private String tableName;
    private SearchStep structure;

    public void init(SearchStep structure) throws Exception {
        this.structure = structure;
        tableName = createTemporaryTable(H2Database.aggregation, structure.datasetName, structure.structure, structure.sqlStructure);
    }

    public void appendData(Iterable<Object[]> data) throws Exception {
        String query = getInsertQuery(tableName, structure.sqlStructure);
        Connection connection = null;
        boolean autocommit = false;
        try {
            connection = getConnection(H2Database.aggregation);
            autocommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            PreparedStatement statement = connection.prepareStatement(query);
            for (Object[] row : data) {
                for (int i=0;i<structure.sqlStructure.length;i++)
                    statement.setObject(i+1,row[i], structure.sqlStructure[i]);
                statement.addBatch();
            }
            statement.executeBatch();
            connection.commit();
        } catch (Exception ex) {
            try { connection.rollback(); } catch (Exception e) {};
            throw ex;
        } finally {
            try { connection.setAutoCommit(autocommit); } catch (Exception e) {};
            closeConnection(connection);
        }
    }

    public Iterable<Object[]> aggregate(Map<String,ValueOperator> aggregator) throws Exception {
        Map<String, H2Operator> operatorMap = new HashMap<String, H2Operator>();
        for (Map.Entry<String, ValueOperator> operatorInfoEntry : aggregator.entrySet())
            operatorMap.put(operatorInfoEntry.getKey(), (H2Operator) operatorFactory.getInstance(structure, operatorInfoEntry.getValue()));

        String query = getAggregateQuery(operatorMap);
        PreparedStatement statement = getConnection(H2Database.aggregation).prepareStatement(query);
        final ResultSet rs = statement.executeQuery();

        return new Iterable<Object[]>() {
            @Override
            public Iterator<Object[]> iterator() {
                return new Iterator<Object[]>() {
                    private boolean nextTaken=false, next=false;
                    private int columnsNumber=-1;
                    {try {columnsNumber = rs.getMetaData().getColumnCount();} catch (Exception e) {}}
                    @Override
                    public boolean hasNext() {
                        if (!nextTaken && columnsNumber>0) {
                            try {
                                if (!(next=rs.next())) {
                                    rs.close();
                                    dropTable(H2Database.aggregation, tableName);
                                }
                            } catch (Exception e) {next=false;}
                            nextTaken = true;
                        }
                        return next;
                    }

                    @Override
                    public Object[] next() {
                        if (hasNext())
                            try {
                                Object[] row = new Object[columnsNumber];
                                for (int i=0; i<columnsNumber; i++)
                                    row[i]=rs.getObject(i+1);
                                return row;
                            } catch (Exception e) { } finally {
                                nextTaken = false;
                            }
                        return null;
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }




    //Utils

    private String getAggregateQuery(Map<String, H2Operator> operatorMap) throws Exception {
        //Prepare key columns and to aggregate columns flags
        boolean[] isKey = new boolean[structure.columnsNumber];
        for (int ki : structure.keyIndexes)
            isKey[ki] = true;
        boolean[] isToAggregate = new boolean[structure.columnsNumber];
        for (String columnName : operatorMap.keySet())
            isToAggregate[structure.columnsName.get(columnName)] = true;

        //Prepare a query based on current structure considering if a column is a key or is to aggregate with a specific operator
        StringBuilder query = new StringBuilder("select");
        for (int i=0; i<structure.columnsNumber; i++) {
            String columnName = structure.structure[i].getColumnId();
            if (isKey[i]) {
                query.append(' ').append(columnName).append(',');
            } else if (isToAggregate[i]) {
                H2Operator operator = operatorMap.get(columnName);
                String baseFunctionName = operator.getAggregationBaseName();
                String[] parameterColumns = operator.getColumnParametersName();

                query.append(' ').append(baseFunctionName).append(" (");
                if (parameterColumns!=null) {
                    query.append(baseFunctionName+"_prepare (");
                    query.append(columnName).append(',');
                    for (String parameterColumn : parameterColumns)
                        query.append(parameterColumn).append(',');
                    query.setLength(query.length()-1);
                    query.append(')');
                } else {
                    query.append(columnName);
                }
                query.append(") as ").append(columnName).append(',');
            } else {
                H2Operator operator = (H2Operator)operatorFactory.getInstance(null, new ValueOperator("standard.NoKey",null,null));
                query.append(' ').append(operator.getAggregationBaseName()).append(" (").append(columnName).append(") as ").append(columnName).append(',');
            }
        }
        query.setLength(query.length()-1);

        //append group by to the query if needed
        query.append(" from ").append(tableName);
        if (structure.keyIndexes.length>0) {
            query.append(" group by ");
            for (int i : structure.keyIndexes)
                query.append(structure.structure[i].getColumnId()).append(',');
            query.setLength(query.length()-1);
        }

        //Return query
        return query.toString();
    }

    private String getInsertQuery(String tableName, int[] structure) {
        StringBuilder query = new StringBuilder("INSERT INTO ").append(tableName).append(" VALUES (");
        for (int i=0; i<structure.length; i++)
            query.append("?,");
        query.setLength(query.length()-1);
        return query.append(')').toString();
    }

}

package org.fao.fenix.server.tools.orient;

import com.orientechnologies.orient.core.command.OCommandContext;
import com.orientechnologies.orient.core.command.OCommandExecutor;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OSQLEngine;
import com.orientechnologies.orient.core.sql.functions.OSQLFunction;
import com.orientechnologies.orient.core.sql.functions.OSQLFunctionAbstract;
import com.orientechnologies.orient.core.sql.operator.ODefaultQueryOperatorFactory;
import org.fao.fenix.msd.dto.common.ValueOperator;
import org.fao.fenix.msd.dto.dsd.DSDColumn;
import org.fao.fenix.search.bl.aggregation.operator.Operator;
import org.fao.fenix.search.dto.SearchFilter;

public class OrientCustomFunctions extends OSQLFunctionAbstract {

    public static OSQLFunction registerCustomOperator (Operator operatorInstance, ValueOperator parameters) {
        String name = parameters.getKey();
        OSQLFunction function = OSQLEngine.getInstance().getFunction(name);
        if (function==null) {

        }
        return function;
    }

    private Operator operatorInstance;

    public OrientCustomFunctions(Operator operatorInstance, ValueOperator parameters) {
        super(parameters.getKey(),0,0); //TODO numero parametri
        this.operatorInstance = operatorInstance;
        OSQLEngine.getInstance().registerFunction(parameters.getKey(),this);
        //TODO
    }


    @Override
    public Object execute(OIdentifiable oIdentifiable, Object o, Object[] objects, OCommandContext oCommandContext) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getSyntax() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }


}

package org.fao.fenix.d3s.server.tools.orient;

import com.orientechnologies.orient.core.command.OCommandContext;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.sql.OSQLEngine;
import com.orientechnologies.orient.core.sql.functions.OSQLFunction;
import com.orientechnologies.orient.core.sql.functions.OSQLFunctionAbstract;
import org.fao.fenix.d3s.msd.dto.common.ValueOperator;
import org.fao.fenix.d3s.search.bl.aggregation.operator.Operator;

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

package org.fao.fenix.d3s.search.bl.aggregation.operator;

//import javax.enterprise.context.ApplicationScoped;
import org.fao.fenix.commons.msd.dto.common.ValueOperator;
import org.fao.fenix.d3s.search.SearchStep;
import org.fao.fenix.d3s.server.tools.rest.CDIUtils;

import javax.inject.Inject;
import javax.inject.Singleton;

//@ApplicationScoped
@Singleton
public class OperatorFactory extends SearchStep {
    @Inject private CDIUtils cdi;

    public Operator getInstance(SearchStep source, ValueOperator operatorInfo) throws Exception {
        Operator instance = cdi.getBean(getImplementationClass(operatorInfo.getImplementation()));
        if (source!=null)
            instance.cloneResult(source);
        instance.init(operatorInfo);
        return instance;
    }
    private Class<? extends Operator> getImplementationClass(String implementationName) throws ClassNotFoundException {
        return (Class<? extends Operator>)Class.forName(Operator.class.getPackage().getName()+'.'+implementationName);
    }

    /*
        public static String[] getColumnParametersName(ValueOperator operatorInfo) throws Exception {
            Operator instance = cdi.getBean(getImplementationClass(operatorInfo.getImplementation()));
            return instance.getColumnParametersName();
        }
        public static String[] getBusinessParametersName(ValueOperator operatorInfo) throws Exception {
            Operator instance = cdi.getBean(getImplementationClass(operatorInfo.getImplementation()));
            return instance.getBusinessParametersName();
        }
    */

}

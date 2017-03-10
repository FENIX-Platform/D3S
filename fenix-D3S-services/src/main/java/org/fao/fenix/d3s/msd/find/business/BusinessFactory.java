package org.fao.fenix.d3s.msd.find.business;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.*;

@ApplicationScoped
public class BusinessFactory {
    @Inject  Instance<Business> businessInstances;

    //Retrieve metadata listeners
    public Business getBusiness(String businessName) {
        businessName = (businessName == null)? "union":businessName;
        for (Iterator<Business> i = businessInstances.select().iterator(); i.hasNext(); ) {
            Business instance = i.next();
            if (instance.getClass().getAnnotation(org.fao.fenix.commons.utils.find.Business.class).value().equals(businessName))
                return instance;
        }
        return null;
    }

}
package org.fao.fenix.d3s.wds;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class WDSDaoFactory {
    @Inject Instance<WDSDao> factory;

    public WDSDao getInstance(String daoName) {
        try {
            return factory.select(WDSDao.getDaoClass(daoName)).iterator().next();
        } catch (Exception e) {
            return null;
        }
    }
}

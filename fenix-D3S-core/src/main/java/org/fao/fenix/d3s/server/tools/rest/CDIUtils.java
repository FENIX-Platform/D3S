package org.fao.fenix.d3s.server.tools.rest;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

@ApplicationScoped
public class CDIUtils {
    @Inject BeanManager beanManager;

    public Object getBean(String className) throws ClassNotFoundException {
        return getBean(Class.forName(className));
    }
    public <T> T getBean(Class<T> beanClass) {
        Bean<T> bean = (Bean<T>) beanManager.resolve(beanManager.getBeans(beanClass));
        return (T) beanManager.getReference(bean, bean.getBeanClass(), beanManager.createCreationalContext(bean));
    }
}

package org.fao.fenix.export.d3s;

import org.fao.fenix.commons.utils.Language;
import org.fao.fenix.commons.utils.Properties;
import org.fao.fenix.d3s.msd.services.rest.ResourcesService;
import org.fao.fenix.d3s.server.dto.DatabaseStandards;
import org.fao.fenix.d3s.server.init.InitListener;
import org.fao.fenix.d3s.server.init.MainController;
import org.fao.fenix.export.services.ServletFenixExport;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener()
public class ExportManager implements ServletContextListener, InitListener {
    @Inject private MainController d3sController;
    @Inject private ServletFenixExport servletFenixExport;
    @Inject Instance<ResourcesService> resourcesServicesFactory;

    private static ExportManager manager;

    //INIT

    @Override
    public void init(Properties initParameters) throws Exception {
        servletFenixExport.init(initParameters.getProperty("export.tmp.folder","export/tmp"));
        manager = this;
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        d3sController.registerListener(this);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) { }


    private ResourcesService getResourcesServiceInstance(String language) {
        DatabaseStandards.setLanguageInfo(new Language[]{ Language.getInstance(language) });
        return manager.resourcesServicesFactory.get();
    }
    public static ResourcesService getResourcesService(String language) {
        return manager.getResourcesServiceInstance(language);
    }
}

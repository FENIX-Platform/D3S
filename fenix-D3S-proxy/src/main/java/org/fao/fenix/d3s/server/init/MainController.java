package org.fao.fenix.d3s.server.init;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Properties;

import javax.enterprise.context.ApplicationScoped;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.fao.fenix.commons.utils.WebContext;
import org.fao.fenix.d3s.server.services.rest.Service;


@WebListener
@ApplicationScoped
public class MainController implements ServletContextListener, WebContext {

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        context = servletContextEvent.getServletContext();
        Properties properties = new Properties();
        for (Object propertyName : Collections.list(context.getInitParameterNames()))
            properties.setProperty((String)propertyName, context.getInitParameter((String)propertyName));
        initModules(properties);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }


	//STARTUP SEQUENCE
	public static void initModules(Properties properties) {
		Service.init(properties);
	}


    //Utils
    private static Properties initParameters = new Properties();
    @Override public Properties getInitParameters() { return initParameters; }
    @Override public String getInitParameter(String key) { return initParameters.getProperty(key); }

    private static ServletContext context;
    @Override public InputStream getWebrootFileStream(String path) throws IOException { return context.getResourceAsStream(path); }
}

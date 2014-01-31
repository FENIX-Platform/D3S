package org.fao.fenix.server.init;

import java.util.Collections;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

import org.fao.fenix.server.services.rest.Service;


@WebListener
public class MainController implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext context = servletContextEvent.getServletContext();
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


}

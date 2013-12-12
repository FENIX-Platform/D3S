package org.fao.fenix.server.init;

import java.util.Collections;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

import org.fao.fenix.server.services.rest.Service;


@WebServlet(urlPatterns="/startupSequence",loadOnStartup=1)
public class MainController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	@SuppressWarnings("unchecked")
	@Override
	public void init(ServletConfig config) throws ServletException {
		try {
			ServletContext context = config.getServletContext();
			Properties properties = new Properties();
			for (Object propertyName : Collections.list(context.getInitParameterNames()))
				properties.setProperty((String)propertyName, context.getInitParameter((String)propertyName));
			initModules(properties);
				
			
		} catch (Exception e) {
			throw new ServletException("Initialization sequence error. Restart the server. Couse exception is:\n", e);
		}
	}
	
	//STARTUP SEQUENCE
	public static void initModules(Properties properties) throws Exception {
		Service.init(properties);
	}


}

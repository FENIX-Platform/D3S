package org.fao.fenix.server.init;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
//import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

import org.fao.fenix.backup.services.impl.BackupRegistry;
import org.fao.fenix.cache.Cache;
import org.fao.fenix.msd.dao.dm.DMIndexStore;
import org.fao.fenix.msd.dao.dm.DMStore;
import org.fao.fenix.search.SearchStep;
import org.fao.fenix.search.bl.aggregation.operator.H2Operator;
import org.fao.fenix.search.services.impl.SearchOperation;
import org.fao.fenix.server.services.rest.CrossDomainInterceptor;
import org.fao.fenix.server.services.rest.ServiceRegistry;
import org.fao.fenix.server.tools.Properties;
import org.fao.fenix.server.tools.orient.OrientServer;
import org.fao.fenix.server.tools.resteasy.RestClient;
import org.fao.fenix.server.tools.resteasy.RestServer;
import org.fao.fenix.server.tools.spring.SpringContext;
import org.fao.fenix.wds.Dao;


//@WebServlet(urlPatterns="/startupSequence",loadOnStartup=1)
public class MainController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		try {
			startupModules();
		} catch (Exception e) {
			throw new ServletException("Initialization sequence error. Restart the server. Couse exception is:\n", e);
		}
	}

	//INIT PARAMETERS
	private static File customPropertiesFile = new File("config/mainConfig.properties");
	private static Properties properties;
	public static Properties getInitParameters() throws Exception {
		if (properties==null) {
			properties = new Properties();
			properties.load(OrientServer.class.getResourceAsStream("/org/fao/fenix/config/mainConfig.properties"));
			if (customPropertiesFile.exists() && customPropertiesFile.isFile() && customPropertiesFile.canRead())
				properties.load(new FileInputStream(customPropertiesFile));
		}
		return properties;
	}
	public static void setInitParameters(Map<String,String> parameters) throws Exception {
		Properties properties = getInitParameters();
		if (parameters!=null)
			properties.putAll(parameters);
		properties.store(new FileOutputStream(customPropertiesFile, false), String.format("Updated on %tD", new Date()));
	}
	
	//STARTUP SEQUENCE
	public static void initModules() throws Exception {
        Properties parameters = getInitParameters();
		SpringContext.init(parameters);
		RestServer.init(parameters);
		CrossDomainInterceptor.init(parameters);
		OrientServer.init(parameters);
		SearchStep.init(parameters);
		SearchOperation.init(parameters);
        BackupRegistry.init(parameters);
    }
	public static void startupModules() throws Exception {
		initModules();
		
		RestServer.addServices(ServiceRegistry.getResourceClasses());
		RestServer.startServer();
//		SpringContext.addPostProcessor(RestServer.createSpringProcessor());
		OrientServer.startServer();
	}

    public static void startupOperations() {
        //Update metadata dynamic index structure
        //SpringContext.getBean(DMIndexStore.class).createDynamicIndexStructure(OrientServer.getMsdDatabase());
    }
	
	
	//STANDALONE STARTUP
	public static void main(String[] args) throws Exception {
		String operation = args!=null && args.length>0 ? args[0] : null;
		
		if (operation==null) {
			startupModules();
            startupOperations();
			System.out.println("\n\nServer started succesfully.");
		} else if (operation.equals("stop")) {
			String stopURL = "http://localhost:"+getInitParameters().getProperty("rest.server.port")+"/server";
			try {
				RestClient.delete(stopURL, null, null);
				System.out.println("Server stopped succesfully.");
			} catch (Exception ex) {
				System.err.println("Stopping server error: "+ex.getMessage());
			}
		} else {
			initModules();
			if ("console".equalsIgnoreCase(operation)) {
				if (args.length==2)
					OrientServer.startConsole(args[1]);
				else
					OrientServer.startConsole();
			}
		}
			
	}
	
	public static void shutdownModules() throws Exception {
		OrientServer.stopServer();
		RestServer.stopServer();
	}


}

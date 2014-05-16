package org.fao.fenix.d3s.server.init;

import java.io.*;
import java.net.URL;
import java.util.Date;
import java.util.Map;

import org.fao.fenix.d3s.search.services.impl.SearchOperation;
import org.fao.fenix.d3s.search.SearchStep;
import org.fao.fenix.d3s.server.tools.Properties;
import org.fao.fenix.d3s.server.tools.orient.OrientServer;
import org.fao.fenix.d3s.server.tools.rest.Server;


public class MainController {

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

        Server.init(parameters);
		OrientServer.init(parameters);
		SearchStep.init(parameters);
		SearchOperation.init(parameters);
    }
	public static void startupModules() throws Exception {
        Server.start(); //TODO
        OrientServer.startServer();
	}

    public static void startupOperations() {
        //e.g. update metadata dynamic index structure
    }
	
	
	//STANDALONE STARTUP
	public static void main(String[] args) throws Exception {
		String operation = args!=null && args.length>0 ? args[0] : null;
		
		if (operation==null) {
            initModules();
			startupModules();
            startupOperations();
			System.out.println("\n\nServer started succesfully.");
		} else if (operation.equals("stop")) {
            URL stopURL = new URL("http://localhost:"+getInitParameters().getProperty("rest.server.port","7777")+"/shutdown");
			try {
                BufferedReader in = new BufferedReader(new InputStreamReader(stopURL.openConnection().getInputStream()));
                System.out.println("Stopping D3S server:");
                for (String line = in.readLine(); line!=null; line = in.readLine())
                    System.out.println(line);
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

        Server.stop();
	}


}

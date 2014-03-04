package org.fao.fenix.d3s.server.tools.resteasy;

import java.util.*;

import javax.servlet.http.HttpServlet;

import org.fao.fenix.d3s.server.services.rest.ServiceRegistry;
import org.jboss.resteasy.plugins.server.tjws.TJWSEmbeddedJaxrsServer;
import org.jboss.resteasy.spi.ResteasyDeployment;

public class RestServer {

	private static boolean initialized;
	private static boolean active;
	private static TJWSEmbeddedJaxrsServer server = new TJWSEmbeddedJaxrsServer();
	private static List<Class> services = new LinkedList<Class>();
	
	public static void startServer() throws Exception {
		if (initialized && !active) {
            deployServices();
			server.start();
            //createSpringProcessor();
			active = true;
		}
	}
	
	public static void stopServer() throws Exception {
		if (active) {
			server.stop();
			active = false;
		}
	}

	public static void addServices(Collection<Class<?>> servicesClass) throws Exception {
		if (servicesClass!=null) {
			services.addAll(servicesClass);
			if (active)
				deployServices();
		}
	}
	
	public static void addServlets(Map<String,Class<? extends HttpServlet>> servletsClass) throws Exception {
		if (servletsClass!=null && initialized)
			for (Map.Entry<String,Class<? extends HttpServlet>> servletClassEntry : servletsClass.entrySet())
				server.addServlet(servletClassEntry.getKey(), servletClassEntry.getValue().newInstance());
	}

	private static void deployServices() throws Exception {
        ResteasyDeployment dep = new ResteasyDeployment();
        dep.setApplication(new ServiceRegistry());
        server.setDeployment(dep);
	}

	public static void init(Properties initProperties) throws Exception {
		stopServer();
		server.setPort(Integer.parseInt(initProperties.getProperty("rest.server.port")));
//        server.setMaxKeepAliveConnections(1000);
//        server.setKeepAliveTimeout(3000);
		initialized = true;
	}

	public static String getPort() {
		return initialized ? server.getPort() : null;
	}

}

package org.fao.fenix.server.services.rest;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.fao.fenix.msd.services.rest.*;

@ApplicationPath("service")
public class ServiceRegistry extends Application {
	private static Set<Class<?>> classes = new HashSet<Class<?>>();
	static {
		classes.add(LoadCodeList.class);
		classes.add(LoadCommons.class);
		classes.add(StoreCommons.class);
        classes.add(LoadDM.class);
        classes.add(StoreDM.class);
	}
	@Override
	public Set<Class<?>> getClasses() {
		return classes;
	}
}

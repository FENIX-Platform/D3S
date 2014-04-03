package org.fao.fenix.d3s.server.services.rest;


import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.fao.fenix.d3s.backup.services.rest.BackupService;
import org.fao.fenix.d3s.cl.services.rest.MergeCL;
import org.fao.fenix.d3s.dataset.services.rest.Upload;
import org.fao.fenix.d3s.msd.services.rest.*;
import org.fao.fenix.d3s.search.services.rest.Search;
import org.fao.fenix.d3s.server.tools.resteasy.CSVProvider;

@ApplicationPath("/")
public class ServiceRegistry extends Application {

	@Override public Set<Class<?>> getClasses() { return ServiceRegistry.getResourceClasses(); }
	
	public static Set<Class<?>> getResourceClasses() {
		Set<Class<?>> classes = new HashSet<Class<?>>();
		classes.add(Server.class);
		//MSD
		classes.add(DeleteCodeList.class);
		classes.add(StoreCodeList.class);
		classes.add(LoadCodeList.class);
		classes.add(LoadDM.class);
		classes.add(StoreDM.class);
		classes.add(LoadDSD.class);
		classes.add(StoreDSD.class);
		classes.add(LoadCommons.class);
		classes.add(StoreCommons.class);
		classes.add(MSD.class);
		//SEARCH
		classes.add(Search.class);
		//DATASET
		classes.add(Upload.class);
		//CODE LISTS LOGIC
		classes.add(MergeCL.class);
		//CROSS DOMAIN
		classes.add(CrossDomainInterceptor.class);
        //ERROR MANAGEMENT
		classes.add(DefaultErrorManager.class);
        //BACKUP
        classes.add(BackupService.class);

        //CUSTOM PROVIDERS
        classes.add(CSVProvider.class);

		return classes;
	}

}

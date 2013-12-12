package org.fao.fenix.server.services.rest;


import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.fao.fenix.backup.services.rest.BackupService;
import org.fao.fenix.cl.services.rest.MergeCL;
import org.fao.fenix.dataset.services.rest.Upload;
import org.fao.fenix.msd.services.rest.DeleteCodeList;
import org.fao.fenix.msd.services.rest.LoadCodeList;
import org.fao.fenix.msd.services.rest.LoadCommons;
import org.fao.fenix.msd.services.rest.LoadDM;
import org.fao.fenix.msd.services.rest.LoadDSD;
import org.fao.fenix.msd.services.rest.StoreCodeList;
import org.fao.fenix.msd.services.rest.StoreCommons;
import org.fao.fenix.msd.services.rest.StoreDM;
import org.fao.fenix.msd.services.rest.StoreDSD;
import org.fao.fenix.search.services.rest.Search;

@ApplicationPath("service")
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
		//SEARCH
		classes.add(Search.class);
		//DATASET
		classes.add(Upload.class);
		//CODE LISTS LOGIC
		classes.add(MergeCL.class);
		//CROSS DOMAIN
		classes.add(CrossDomainInterceptor.class);
        //BACKUP
        classes.add(BackupService.class);

		return classes;
	}

}

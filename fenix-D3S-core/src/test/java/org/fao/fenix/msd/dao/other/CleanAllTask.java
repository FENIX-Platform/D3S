package org.fao.fenix.msd.dao.other;

import org.fao.fenix.msd.dao.Cleaner;
import org.fao.fenix.server.init.MainController;
import org.fao.fenix.server.tools.spring.SpringContext;

public class CleanAllTask {
	
	
	public static void main(String[] args) throws Exception {
		MainController.startupModules();
		SpringContext.getBean(Cleaner.class).cleanALL();
		MainController.shutdownModules();
	}

}

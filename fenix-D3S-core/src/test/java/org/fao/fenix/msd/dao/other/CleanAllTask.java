package org.fao.fenix.msd.dao.other;

import org.fao.fenix.d3s.msd.dao.Cleaner;
import org.fao.fenix.d3s.server.init.MainController;
import org.fao.fenix.d3s.server.tools.spring.SpringContext;

public class CleanAllTask {
	
	
	public static void main(String[] args) throws Exception {
		MainController.startupModules();
		SpringContext.getBean(Cleaner.class).cleanALL();
		MainController.shutdownModules();
	}

}

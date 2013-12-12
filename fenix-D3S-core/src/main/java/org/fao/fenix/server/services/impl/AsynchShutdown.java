package org.fao.fenix.server.services.impl;

import org.fao.fenix.server.init.MainController;
import org.fao.fenix.server.tools.asynch.Asynch;
import org.springframework.stereotype.Component;

@Component
public class AsynchShutdown extends Asynch {

	
	@Override
	public void action() {
		try { MainController.shutdownModules(); } catch (Exception e) { }
	}
	
	

}

package org.fao.fenix.d3s.server.services.impl;

import org.fao.fenix.d3s.server.init.MainController;
import org.fao.fenix.d3s.server.tools.asynch.Asynch;

public class AsynchShutdown extends Asynch {

	
	@Override
	public void action() {
		try { MainController.shutdownModules(); } catch (Exception e) { }
	}
	
	

}

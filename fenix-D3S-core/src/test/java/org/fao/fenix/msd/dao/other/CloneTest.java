package org.fao.fenix.msd.dao.other;

import org.fao.fenix.server.init.MainController;
import org.fao.fenix.server.tools.orient.OrientServer;
import org.fao.fenix.server.tools.orient.SchemaClone;

public class CloneTest {
	
	
	public static void main(String[] args) throws Exception {
		MainController.startupModules();
		//SchemaClone.cloneGraphDatabase(OrientServer.getMsdDatabase(), OrientServer.getDatabase("database/databases/msd_1.1", "admin", "admin"));
		SchemaClone.cloneGraphDatabase(OrientServer.getMsdDatabase(), OrientServer.getDatabase("database/databases/msd_1.1", "admin", "admin"));
		//OrientServer.getMsdDatabase().getMetadata().getSchema().getClass("CSCode").truncate();
		MainController.shutdownModules();
	}

}

package org.fao.fenix.d3s.msd.dao;

import org.fao.fenix.d3s.server.tools.orient.OrientDao;
import org.fao.fenix.d3s.server.tools.orient.OrientDatabase;

import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.metadata.schema.OClass;

public class Cleaner extends OrientDao {
		
	public void cleanALL() throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			cleanClassGroup("DSD", database);
			cleanClassGroup("DM", database);
			cleanClassGroup("CS", database);
			cleanClassGroup("CM", database);
		} finally {
			if (database!=null)
				database.close();
		}
	}
	public void cleanCommons() throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			cleanClassGroup("CM", database);
		} finally {
			if (database!=null)
				database.close();
		}
	}
	public void cleanCodeList() throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			cleanClassGroup("CS", database);
		} finally {
			if (database!=null)
				database.close();
		}
	}
	public void cleanDSD() throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			cleanClassGroup("DSD", database);
		} finally {
			if (database!=null)
				database.close();
		}
	}
	public void cleanDM() throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			cleanClassGroup("DM", database);
		} finally {
			if (database!=null)
				database.close();
		}
	}
	
	public void cleanClassGroup(String classPrefix, OGraphDatabase database) throws Exception {
		for (OClass oclass : getClassList(database))
			if (oclass.getName().startsWith(classPrefix))
				oclass.truncate();
	} 
	
	

}

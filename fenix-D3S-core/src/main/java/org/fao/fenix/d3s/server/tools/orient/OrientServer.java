package org.fao.fenix.d3s.server.tools.orient;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.fao.fenix.d3s.server.dto.OrientStatus;

import com.orientechnologies.orient.console.OConsoleDatabaseApp;
import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.db.graph.OGraphDatabasePool;
import com.orientechnologies.orient.server.OServer;
import com.orientechnologies.orient.server.OServerMain;
import org.fao.fenix.commons.utils.FileUtils;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OrientServer {
    //Support factory references
    private static OrientServer instance;
    public OrientServer() {
        instance = this;
    }
    public static OrientServer getInstance() {
        return instance;
    }



	private String databaseFolderPath;

    private String serverConfig;
    private OrientStatus status = new OrientStatus();
    private OServer server;

	public void startServer() throws Exception {
		if (status.isInitialized() && !status.isActive()) {
			server = OServerMain.create();
			server.startup(serverConfig);
			server.activate();
			status.setActive(true);
		}
	}
	
	public void stopServer() throws Exception {
		if (status.isActive()) {
			server.shutdown();
			status.setActive(false);
		}
	}
	
	public OrientStatus getStatus() {
		return status;
	}

	public void init(Properties initProperties) throws Exception {
		stopServer();
		
		System.setProperty("ORIENTDB_HOME", initProperties.getProperty("database.home.folder"));
        serverConfig = readD3SConfigFile(
                initProperties.getProperty("database.config.file"),
                initProperties.getProperty("database.home.folder"),
                initProperties.getProperty("database.port.binary"),
                initProperties.getProperty("database.port.http")
            );

		databaseFolderPath = initProperties.getProperty("databases.folder");
        if (!databaseFolderPath.endsWith("/"))
            databaseFolderPath += '/';

		status.setInitialized(true);
	}
	
	private Map<String, OGraphDatabasePool> dataBasePoolMap = new HashMap<String, OGraphDatabasePool>();
	private OGraphDatabasePool getDataBasePoolInstance(String path, String user, String password) {
		OGraphDatabasePool dataBasePool = dataBasePoolMap.get(path);
		if (dataBasePool==null)
			dataBasePoolMap.put(path, dataBasePool=new OGraphDatabasePool(path, user, password));
		return dataBasePool;
	}

    public OGraphDatabase getMsdDatabase() { return getDatabase(OrientDatabase.msd); }
	public OGraphDatabase getCacheL1Database() { return getDatabase(OrientDatabase.cacheL1); }
	public OGraphDatabase getCacheL2Database() { return getDatabase(OrientDatabase.cacheL2); }
	public OGraphDatabase getDatabase(OrientDatabase database) { return getDatabase(getDatabasePath(database), "admin", "admin"); }
	public OGraphDatabase getDatabase(OrientDatabase database, String user, String password) { return getDatabase(getDatabasePath(database), user, password); }
    public OGraphDatabase getDatabase(String path, String user, String password) {
        return status.isActive() ? getDataBasePoolInstance(path, user, password).acquire() : null;
    }


    //Utils
    private String getDatabasePath(OrientDatabase database) {
        return "plocal:"+databaseFolderPath+database.getDatabaseName();
    }

    private String readD3SConfigFile(String configFilePath, String databaseHome, String binaryPortNumber, String httpPortNumber) throws FileNotFoundException {
        try {
            File file = new File(configFilePath);
            char[] buffer = new char[(int)file.length()];
            new FileReader(file).read(buffer);
            return new String(buffer).replace("${binaryPort}", binaryPortNumber).replace("${httpPort}", httpPortNumber).replaceAll("\\$\\{dbHome\\}",databaseHome);
        } catch (Exception ex) {
            return null;
        }
    }

}

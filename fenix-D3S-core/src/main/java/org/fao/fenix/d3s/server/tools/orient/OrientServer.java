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

public class OrientServer {

	public static void startConsole(String ... params) throws Exception {
		//OGremlinConsole.main(params);
		OConsoleDatabaseApp.main(params);
	}

	private static String databaseFolderPath;

    private static String serverConfig;
    private static OrientStatus status = new OrientStatus();
    private static OServer server;

	public static void startServer() throws Exception {
		if (status.isInitialized() && !status.isActive()) {
			server = OServerMain.create();
			server.startup(serverConfig);
			server.activate();
			status.setActive(true);
		}
	}
	
	public static void stopServer() throws Exception {
		if (status.isActive()) {
			server.shutdown();
			status.setActive(false);
		}
	}
	
	public static OrientStatus getsStatus() {
		return status;
	}

	public static void init(Properties initProperties) throws Exception {
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
	
	private static Map<String, OGraphDatabasePool> dataBasePoolMap = new HashMap<String, OGraphDatabasePool>();
	private static OGraphDatabasePool getDataBasePoolInstance(String path, String user, String password) {
		OGraphDatabasePool dataBasePool = dataBasePoolMap.get(path);
		if (dataBasePool==null)
			dataBasePoolMap.put(path, dataBasePool=new OGraphDatabasePool(path, user, password));
		return dataBasePool;
	}

    public static OGraphDatabase getMsdDatabase() { return getDatabase(OrientDatabase.msd); }
	public static OGraphDatabase getCacheL1Database() { return getDatabase(OrientDatabase.cacheL1); }
	public static OGraphDatabase getCacheL2Database() { return getDatabase(OrientDatabase.cacheL2); }
	public static OGraphDatabase getDatabase(OrientDatabase database) { return getDatabase(getDatabasePath(database), "admin", "admin"); }
	public static OGraphDatabase getDatabase(OrientDatabase database, String user, String password) { return getDatabase(getDatabasePath(database), user, password); }
    public static OGraphDatabase getDatabase(String path, String user, String password) { return status.isActive() ? getDataBasePoolInstance(path, user, password).acquire() : null; }


    public static void main(String ... args) {
        databaseFolderPath = "database/databases/";
        status = new OrientStatus();
        status.setActive(true);
        status.setInitialized(true);

        try {
            //OGraphDatabase database;
//            database = getDatabase("local:database/databases/msd_1.0", "admin", "admin");
//            System.out.print("OK:"+database.countVertexes());
            //database = getDatabase("remote:localhost:2425/CountrySTAT_1.0", "admin", "admin");
            //System.out.print("OK:"+database.countVertexes());

            createDatabase(OrientDatabase.test);
//            dropDatabase(OrientDatabase.test);
//            createDatabase(OrientDatabase.test);
//            executeDDL(OrientDatabase.test,FileUtils.readTextFile("database/backup/structure/msd_1.0.ddl"));

            System.out.print("OK...");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }



    public static void dropDatabase(OrientDatabase database) {
        OGraphDatabase db = new OGraphDatabase(getDatabasePath(database));
        if (db.exists()) {
            db.open("admin", "admin");
            db.drop();
        }
        db.close();
    }

    public static OGraphDatabase createDatabase(OrientDatabase database) {
        OGraphDatabase db = new OGraphDatabase(getDatabasePath(database));
        if (!db.exists())
            db.create();
        return db;
    }

    public static void executeDDL (OrientDatabase database, String ddl) throws Exception {
        ddl = ddl.replaceAll("\\%database\\%",getRemoteDatabasePath(database));
        File tmpFile = new File("tmp.ddl");
        new FileUtils().writeTextFile(tmpFile,ddl);
        Process pr = Runtime.getRuntime().exec("java -jar lib/D3S-1.0.jar console "+tmpFile.getName());
        int exitVal = pr.waitFor();
        tmpFile.delete();
        if (exitVal!=0)
            throw new Exception("Error in DDL execution");
        //Reset database connections pool
        OrientServer.dataBasePoolMap.remove(getDatabasePath(database));
    }


    //Utils
    private static String getDatabasePath(OrientDatabase database) {
        return "plocal:"+databaseFolderPath+database.getDatabaseName();
    }
    private static String getRemoteDatabasePath(OrientDatabase database) {
        return "remote:localhost:2424/"+database.getDatabaseName();
    }

    private static String readD3SConfigFile(String configFilePath, String databaseHome, String binaryPortNumber, String httpPortNumber) throws FileNotFoundException {
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

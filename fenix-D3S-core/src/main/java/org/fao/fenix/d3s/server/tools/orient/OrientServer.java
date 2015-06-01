package org.fao.fenix.d3s.server.tools.orient;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Properties;

import com.orientechnologies.orient.core.Orient;
import com.orientechnologies.orient.core.hook.ORecordHook;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.object.db.OObjectDatabasePool;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.fenix.d3s.msd.triggers.*;
import org.fao.fenix.d3s.server.dto.DatabaseStandards;
import org.fao.fenix.d3s.server.dto.OrientStatus;

import com.orientechnologies.orient.server.OServer;
import com.orientechnologies.orient.server.OServerMain;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

@ApplicationScoped
public class OrientServer {
    private String databaseFolderPath;

    private String serverConfig;
    private OrientStatus status = new OrientStatus();
    private OServer server;

    @Inject private Instance<ORecordHook> triggersFactory;
    @Inject private DatabaseStandards dbParameters;


    //FACTORY SUPPPORT

    private static OrientServer instance;
    public OrientServer() {
        instance = this;
    }
    public static OrientServer getInstance() {
        return instance;
    }


    //SERVER MANAGEMENT

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


    //INIT

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

        registerPersistentEntities();

        registerTriggers();
	}

    public void registerPersistentEntities() throws Exception {
        OObjectDatabaseTx connection = null;
        try {
            connection = getODatabase(OrientDatabase.msd);
            connection.setAutomaticSchemaGeneration(false);
            connection.getEntityManager().registerEntityClasses("org.fao.fenix.commons.msd.dto.full");
        } finally {
            if (connection!=null)
                connection.close();
        }
    }

    public void registerTriggers() throws Exception {

        OObjectDatabaseTx connection = null;
        try {
            dbParameters.setConnection(connection = getODatabase(OrientDatabase.msd));
            OClass meIdentificationClassO = connection.getMetadata().getSchema().getClass(MeIdentification.class.getSimpleName());

            LinksManager[] triggers = new LinksManager[]{
                    triggersFactory.select(DSDDatasetLinksManager.class).iterator().next(),
                    triggersFactory.select(ResourceLinksManager.class).iterator().next(),
                    triggersFactory.select(ResourceIndexManager.class).iterator().next(),
                    triggersFactory.select(CodeIndexManager.class).iterator().next(),
            };

            Orient.instance().addDbLifecycleListener(triggers[2]); //TODO all triggers will be added when Orient will support trigger order
            Orient.instance().addDbLifecycleListener(triggers[3]); //TODO all triggers will be added when Orient will support trigger order
            for (LinksManager trigger : triggers)
                trigger.init(meIdentificationClassO);

        } finally {
            if (connection!=null)
                connection.close();
        }


    }


    //DATABASE CONNECTION
    private OObjectDatabasePool oPool = OObjectDatabasePool.global(10,300);
    public OObjectDatabaseTx getODatabase(OrientDatabase database) {
        return oPool.acquire(database.getURL(databaseFolderPath),"admin","admin");
    }


    //UTILS

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

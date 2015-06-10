package org.fao.fenix.d3s.cache.tools.h2;

import org.h2.tools.Server;

import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

@Singleton
public class H2Server implements org.fao.fenix.d3s.cache.tools.Server {

    Server consoleServer;

    @Override
    public void start() throws Exception {
        startConsole();
    }

    @Override
    public void stop() {
        try {
            stopConsole();
        } catch (Exception ex) {
            System.err.println("H2 console shutdown error: "+ex.getMessage());
        }
    }


    //Internal logic
    private void startConsole() throws SQLException, IOException {
        String portNumber = "";
        System.out.print("\nStarting up H2 console server... ");
        if (consoleServer ==null) {
            Properties initProperties = org.fao.fenix.commons.utils.Properties.getInstance(
                    "/org/fao/fenix/config/h2.properties",
                    "file:"+CONFIG_FOLDER_PATH + "h2.properties"
            );

            File databaseFolder = new File(initProperties.getProperty("databases.path"));
            if (!databaseFolder.exists())
                databaseFolder.mkdirs();

            StringBuilder serverParameters = new StringBuilder()
                    .append("-webPort ").append(initProperties.getProperty("console.port"))
                    .append(",-webAllowOthers true")
                    .append(",-baseDir ").append(databaseFolder.getAbsolutePath());
            consoleServer = Server.createWebServer(
                    "-webPort", portNumber=initProperties.getProperty("console.port"),
                    "-webAllowOthers",
                    "-baseDir", databaseFolder.getAbsolutePath()
            );
        }
        if (!consoleServer.isRunning(false))
            consoleServer.start();
        System.out.println("done on port "+portNumber);
    }
    private void stopConsole() {
        System.out.print("\nShutting down H2 console server... ");
        if (consoleServer !=null && consoleServer.isRunning(false))
            consoleServer.stop();
        System.out.println("done.");
    }



}

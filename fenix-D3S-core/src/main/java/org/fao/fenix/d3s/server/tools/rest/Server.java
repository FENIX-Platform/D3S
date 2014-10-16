package org.fao.fenix.d3s.server.tools.rest;

import org.glassfish.embeddable.*;
import org.glassfish.embeddable.archive.ScatteredArchive;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Properties;

public class Server implements Runnable {
    private static GlassFish glassfish = null;

    public static void init(Properties initProperties) throws GlassFishException {
        File tmpFolder = new File("work/tmp");
        tmpFolder.mkdirs();

        GlassFishProperties glassfishProperties = new GlassFishProperties();
        glassfishProperties.setPort("http-listener", Integer.parseInt(initProperties.getProperty("rest.server.port","7777")));
        glassfishProperties.setProperty("rest.embedded.tmpdir", tmpFolder.getAbsolutePath());

        glassfish = GlassFishRuntime.bootstrap().newGlassFish(glassfishProperties);
    }

    public static void main(String ... args) {

    }
    public static void start() {


        try {
            glassfish.start();

            ScatteredArchive application = new ScatteredArchive("D3S", ScatteredArchive.Type.WAR);
            addClassPath(application);

            Deployer deployer = glassfish.getDeployer();
            deployer.deploy(application.toURI(), "--contextroot=/");
        } catch (GlassFishException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private static void addClassPath(ScatteredArchive application) throws IOException {
        File libFolder = new File("lib");
        File targetLibFolder = new File("target/lib");
        File targetClassesFolder = new File("target/classes");

        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith("fenix");
            }
        };

        if (libFolder.exists())
            for (File library : libFolder.listFiles(filter))
                application.addClassPath(library);
        if (targetLibFolder.exists())
            for (File library : targetLibFolder.listFiles(filter))
                application.addClassPath(library);
        if (targetClassesFolder.exists())
            application.addClassPath(targetClassesFolder);


    }


    public static void stop() throws GlassFishException {
        new Thread(new Server()).start();
    }

    @Override
    public void run() {
        if (glassfish!=null)
            try {
                Thread.sleep(4000);
                glassfish.dispose();
            } catch (Exception e) {
                e.printStackTrace();
                Runtime.getRuntime().exit(-1);
            }
    }
}

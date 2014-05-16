package org.fao.fenix.d3s.server.tools.rest;

import org.glassfish.embeddable.*;
import org.glassfish.embeddable.archive.ScatteredArchive;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class Server implements Runnable {
    private static GlassFish glassfish = null;

    public static void init(Properties initProperties) throws GlassFishException {
        File tmpFolder = new File("tmp");
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
            application.addClassPath(new File("target/classes"));//TODO
            //application.addClassPath(new File("target/fenix-D3S-core-1.0.1-SNAPSHOT.jar"));

            Deployer deployer = glassfish.getDeployer();
            deployer.deploy(application.toURI(), "--contextroot=/");
        } catch (GlassFishException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private static void addClassPath(File libFolder) {

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

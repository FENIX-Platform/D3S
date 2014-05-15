package org.fao.fenix.d3s.server.tools.glassfish;

import org.glassfish.embeddable.*;
import org.glassfish.embeddable.archive.ScatteredArchive;

import java.io.File;
import java.io.IOException;

public class Server {

    public static void main(String ... args) {
        File tmpFolder = new File("tmp");
        tmpFolder.mkdirs();

        GlassFishProperties glassfishProperties = new GlassFishProperties();
        glassfishProperties.setPort("http-listener", 7777);
        glassfishProperties.setProperty("glassfish.embedded.tmpdir", tmpFolder.getAbsolutePath());

        GlassFish glassfish = null;
        try {
            glassfish = GlassFishRuntime.bootstrap().newGlassFish(glassfishProperties);
            glassfish.start();

            ScatteredArchive application = new ScatteredArchive("D3S", ScatteredArchive.Type.WAR);
            application.addClassPath(new File("target/classes"));
            //application.addClassPath(new File("target/fenix-D3S-core-1.0.1-SNAPSHOT.jar"));

            Deployer deployer = glassfish.getDeployer();
            deployer.deploy(application.toURI(), "--contextroot=app");

            //glassfish.dispose();
        } catch (GlassFishException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}

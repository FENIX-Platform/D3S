package org.fao.fenix.d3s.server.init;


//import org.fao.fenix.d3s.search.SearchStep;
//import org.fao.fenix.d3s.search.services.impl.SearchOperation;
import org.fao.fenix.commons.utils.Properties;
import org.fao.fenix.d3s.msd.dao.DatasetResourceDao;
import org.fao.fenix.d3s.server.tools.orient.OrientServer;
import org.fao.fenix.d3s.server.tools.rest.Server;
import org.fao.fenix.d3s.wds.WDSDaoFactory;
import org.glassfish.embeddable.GlassFishException;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collections;

@WebListener
public class MainController implements ServletContextListener {
    @Inject private OrientServer orientClient;
    @Inject private WDSDaoFactory wdsDaoFactory;
    @Inject private DatasetResourceDao datasetResourceDao;



    //STANDALONE STARTUP
    public static void main(String[] args) throws Exception {
        String operation = args!=null && args.length>0 ? args[0] : null;

        if (operation==null) {
            Server.init(getInitParameters());
            Server.start();
            System.out.println("\n\nServer started succesfully.");
        } else if (operation.equals("stop")) {
            URL stopURL = new URL("http://localhost:"+getInitParameters().getProperty("rest.server.port","7777")+"/shutdown");
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(stopURL.openConnection().getInputStream()));
                System.out.println("Stopping D3S server:");
                for (String line = in.readLine(); line!=null; line = in.readLine())
                    System.out.println(line);
            } catch (Exception ex) {
                System.err.println("Stopping server error: "+ex.getMessage());
            }
        }

    }


    //WEB CONTEXT INITIALIZATION

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        //Append Web context parameters
        ServletContext context = servletContextEvent.getServletContext();
        for (Object key : Collections.list(context.getInitParameterNames()))
            initParameters.setProperty((String)key, context.getInitParameter((String)key));

        try {
            //Init modules
            wdsDaoFactory.init();
            //SearchStep.init(initParameters);
            //SearchOperation.init(initParameters);
            orientClient.init(initParameters);
            //Startup modules
            orientClient.startServer();
            //Connect cache plugins
            datasetResourceDao.init(initParameters.getProperty("cache.dataset.plugin"));


        } catch (Exception e) {
            try {
                e.printStackTrace();
                Server.stop();
            } catch (GlassFishException e1) {
                e1.printStackTrace();
            }
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        initParameters.clear();
    }


    //SHUTDOWN MANAGEMENT
    public void shutdown() throws Exception {
        orientClient.stopServer();
        Server.stop();
    }



    //Utils
    private static File customPropertiesFile = new File("config/mainConfig.properties");
    private static Properties initParameters;
    public static Properties getInitParameters() throws Exception {
        if (initParameters ==null)
            initParameters = Properties.getInstance(
                    "/org/fao/fenix/config/mainConfig.properties",
                    "file:config/mainConfig.properties"
            );
        return initParameters;
    }
    public String getInitParameter(String key) throws Exception { return getInitParameters().getProperty(key); }
}
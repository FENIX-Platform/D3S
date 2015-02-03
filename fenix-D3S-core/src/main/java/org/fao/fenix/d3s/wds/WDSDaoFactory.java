package org.fao.fenix.d3s.wds;

import org.fao.fenix.commons.utils.Properties;
import org.fao.fenix.d3s.server.tools.orient.OrientServer;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@ApplicationScoped
public class WDSDaoFactory {

    private Map<String, Map<String, String>> datasourcesProperties = new HashMap<>();
    private Map<String, Class<? extends WDSDao>> datasourcesClass = new HashMap<>();

    @Inject Instance<WDSDao> factory;



    public WDSDao getInstance(String daoName) {
        try {
            Class<? extends WDSDao> daoClass = datasourcesClass.get(daoName);
            WDSDao dao = daoClass!=null ? factory.select(daoClass).iterator().next() : null;
            if (dao!=null && dao.init())
                dao.init(datasourcesProperties.get(daoName));
            return dao;
        } catch (Exception e) { }
        return null;
    }


    //Datasources initialization
    public void init() throws Exception {
        for (Map.Entry<String,String> rawProperty: getInitParameters().toMap().entrySet()) {
            String rawPropertyName = rawProperty.getKey();
            String datasourceName = rawPropertyName.substring(0, rawPropertyName.indexOf('.'));
            String propertyName = rawPropertyName.substring(datasourceName.length() + 1);

            Map<String, String> datasourceProperties = datasourcesProperties.get(datasourceName);
            if (datasourceProperties==null)
                datasourcesProperties.put(datasourceName, datasourceProperties=new HashMap<>());
            datasourceProperties.put(propertyName.toLowerCase(), rawProperty.getValue());

            if ("class".equals(propertyName.toLowerCase()))
                datasourcesClass.put(datasourceName, (Class<? extends WDSDao>)Class.forName(rawProperty.getValue()));
        }
    }

    private File customPropertiesFile = new File("config/datasources.properties");
    private Properties initParameters;
    private Properties getInitParameters() throws Exception {
        if (initParameters ==null) {
            initParameters = new Properties();
            initParameters.load(OrientServer.class.getResourceAsStream("/org/fao/fenix/config/datasources.properties"));
            if (customPropertiesFile.exists() && customPropertiesFile.isFile() && customPropertiesFile.canRead())
                initParameters.load(new FileInputStream(customPropertiesFile));
        }
        return initParameters;
    }

}

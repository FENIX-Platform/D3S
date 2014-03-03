package org.fao.fenix.server.tools.jmx;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import java.lang.management.ManagementFactory;
import java.rmi.registry.LocateRegistry;

public class TestServer {

    public static void main(String[] args) throws Exception {

        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        String domain = mbs.getDefaultDomain();
        ObjectName name = new ObjectName(domain +":type=Hello");

        Hello mbean = new Hello();
        mbs.registerMBean(mbean, name);

        LocateRegistry.createRegistry(9876);
        JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:9876/server");
        JMXConnectorServer cs = JMXConnectorServerFactory.newJMXConnectorServer(url, null, mbs);
        cs.start();



        System.out.println("Waiting forever...");
        Thread.sleep(Long.MAX_VALUE);
    }



}

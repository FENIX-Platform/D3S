package org.fao.fenix.server.tools.jmx;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

public class Client {

    public static void main(String[] args) {
        JMXConnector jmxc = null;
        try {
            // Create an RMI connector client and
            // connect it to the RMI connector server
            JMXServiceURL url = new JMXServiceURL( "service:jmx:rmi:///jndi/rmi://localhost:9876/server");
            jmxc = JMXConnectorFactory.connect(url, null);

            // Get an MBeanServerConnection
            MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();

            // Create HelloMBean
            String domain = mbsc.getDefaultDomain();
            ObjectName name = new ObjectName(domain +":type=Hello");
            HelloMBean proxy = JMX.newMBeanProxy( mbsc, name, HelloMBean.class, true);

            //... and use it
            System.out.println("ce provo");
            proxy.sayHello();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close MBeanServer connection
            try { jmxc.close(); } catch (Exception ex) {}
        }
    }

}
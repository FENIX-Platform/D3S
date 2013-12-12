package org.fao.fenix.server.tools;

import org.apache.catalina.Context;
import org.apache.catalina.Host;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;

/**
 * Created with IntelliJ IDEA.
 * User: meco
 * Date: 10/12/13
 * Time: 8.41
 * To change this template use File | Settings | File Templates.
 */
public class TomcatTest {

    public static void main (String ... args) throws LifecycleException {
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8081);
        Context context = tomcat.addContext("",System.getProperty("java.io.tmpdir"));
        tomcat.start();
        tomcat.getServer().await();
    }
}
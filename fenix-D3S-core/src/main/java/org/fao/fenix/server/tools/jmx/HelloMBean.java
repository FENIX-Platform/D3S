package org.fao.fenix.server.tools.jmx;

/**
 * Created by meco on 25/02/14.
 */
public interface HelloMBean {

    public void sayHello();
    public int add(int x, int y);

    public String getName();

    public int getCacheSize();
    public void setCacheSize(int size);
}
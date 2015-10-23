package org.fao.fenix.d3s.cache.tools.monitor;

import java.io.Serializable;


public class ThreadStatus implements Serializable {
    public long id;
    public String state;
    public String name;
    public String stackTrace;
    public boolean alive;
    public boolean interrupted;



    public ThreadStatus(Thread thread) {
        id = thread.getId();
        state = thread.getState().name();
        name = thread.getName();
        alive = thread.isAlive();
        interrupted = thread.isInterrupted();

        StringBuilder st = new StringBuilder();
        for (StackTraceElement element : thread.getStackTrace())
            st.append(element.toString()).append('\n');
        stackTrace = st.toString();
    }


}

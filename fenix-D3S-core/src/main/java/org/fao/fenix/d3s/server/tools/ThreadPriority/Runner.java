package org.fao.fenix.d3s.server.tools.ThreadPriority;

public class Runner implements Runnable {
    @Override
    public void run() {
        Thread currentThread = Thread.currentThread();
        for (int i=0; i<100; i++) {
            System.out.println("thread: " + currentThread.getId() + " - priority: " + currentThread.getPriority());
            for (int c=0; c<1000000; c++)
                Math.random();
        }
    }
}


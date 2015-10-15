package org.fao.fenix.d3s.server.tools.ThreadPriority;

public class Starter {
    public static void main(String[] args) {
        Runnable target = new Runner();
        Thread[] threads = new Thread[42];
        for (int i=0; i<42; i++)
            (threads[i] = new Thread(target)).setPriority(i+1);

        for (int i=0; i<42; i++)
            threads[i].start();
    }
}

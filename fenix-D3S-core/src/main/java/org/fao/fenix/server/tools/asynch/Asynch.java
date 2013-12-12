package org.fao.fenix.server.tools.asynch;

public abstract class Asynch implements Runnable {
	private long delay = 3 * 1000; // 3 sec.

	public final void start() {
		new Thread(this).start();
	}
	
	public abstract void action() throws Exception;

	public void run() {
		try { Thread.sleep(delay); } catch (InterruptedException e) { }
		// try { Startup.shutdownModules(); } catch (Exception e) {}
		try { action(); } catch (Exception e) { }
	}

}
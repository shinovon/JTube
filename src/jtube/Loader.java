package jtube;
import jtube.models.ILoader;

public class Loader {
	
	private static LoaderThread t0;
	private static LoaderThread t1;
	private static LoaderThread t2;
	protected static Object lock1 = new Object();
	protected static Object lock2 = new Object();
	protected static ILoader[] tasks = new ILoader[32];
	protected static int tasksIdx;
	
	protected static void init() {
		int loadPriority = 5;
		if(!Settings.isLowEndDevice() && Settings.asyncLoading) {
			t0 = new LoaderThread(loadPriority, 0);
			t1 = new LoaderThread(loadPriority, 1);
			t2 = new LoaderThread(loadPriority, 2);
			t0.start();
			t1.start();
			t2.start();
		} else {
			t0 = new LoaderThread(loadPriority, 0);
			t0.start();
		}
	}
	
	public static void add(ILoader v) {
		try {
			if(v == null) throw new NullPointerException("l");
			tasks[tasksIdx++] = v;
			if(tasksIdx >= tasks.length - 1) {
				ILoader[] tmp = tasks;
				tasks = new ILoader[tmp.length + 16];
				System.arraycopy(tmp, 0, tasks, 0, tmp.length);
			}
		} catch (Exception e) {
			 e.printStackTrace();
		}
	}
	
	public static void start() {
		synchronized(lock1) {
			lock1.notifyAll();
		}
	}

	public static void stop() {
		if(t0 != null) t0.doInterrupt();
		if(t1 != null) t1.doInterrupt();
		if(t2 != null) t2.doInterrupt();
		synchronized(tasks) {
			for(int i = 0; i < tasks.length; i++) {
				tasks[i] = null;
			}
			tasksIdx = 0;
		}
	}
}

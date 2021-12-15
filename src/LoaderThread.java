import java.util.Vector;

import models.ILoader;

public class LoaderThread extends Thread {
	
	private Object lock;
	private Object lock2;
	private boolean myInterrupt;
	private Vector vector;

	public LoaderThread(int priority, Object lock, Vector v, Object lock2) {
		super();
		this.lock = lock;
		this.vector = v;
		this.lock2 = lock2;
		setPriority(priority);
	}
	
	public void run() {
		try {
			while(true) {
				synchronized(lock) {
					lock.wait();
				}
				checkInterrupted();
				int len = vector.size();
				if(len == 0) {
					synchronized(lock2) {
						lock2.notifyAll();
					}
					len = vector.size();
				}
				for(int i = 0; i < len; i++) {
					if(checkInterrupted()) break;
					ILoader l;
					try {
						l = ((ILoader) vector.elementAt(0));
						vector.removeElementAt(0);
					} catch (ArrayIndexOutOfBoundsException e) {
						break;
					}
					try {
						l.load();
					} catch (RuntimeException e) {
						System.out.println(e.toString());
						String msg = e.getMessage();
						if(msg != null && (msg.endsWith("interrupt") || msg.endsWith("interrupted"))) {
							break;
						} else {
							throw e;
						}
					}
					if(checkInterrupted()) break;
				}
			}
		} catch (Exception e) {
			App.error(this, Errors.LoaderThread_run, e);
			e.printStackTrace();
		}
	}

	boolean checkInterrupted() {
		if(myInterrupt) {
			synchronized(lock2) {
				lock2.notifyAll();
			}
			myInterrupt = false;
			return true;
		}
		return false;
	}

	public void pleaseInterrupt() {
		myInterrupt = true;
	}

}

import java.util.Vector;

import models.ILoader;

public class LoaderThread extends Thread {
	
	private Object lock;
	private boolean myInterrupt;
	private Vector vector;

	public LoaderThread(int priority, Object lock, Vector v) {
		super();
		this.lock = lock;
		this.vector = v;
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
				for(int i = 0; i < len; i++) {
					if(checkInterrupted()) break;
					ILoader l = ((ILoader) vector.elementAt(0));
					vector.removeElementAt(0);
					l.load();
					if(checkInterrupted()) break;
				}
			}
		} catch (Exception e) {
			App.error(this, Errors.LoaderThread_run, e.toString());
			e.printStackTrace();
		}
	}

	// для того чтобы можно было использовать поток много раз
	private boolean checkInterrupted() {
		if(myInterrupt) {
			myInterrupt = false;
			return true;
		}
		return false;
	}

	public void pleaseInterrupt() {
		myInterrupt = true;
	}

}

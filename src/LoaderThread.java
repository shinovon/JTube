/*
Copyright (c) 2022 Arman Jussupgaliyev

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
import java.util.Vector;

import models.ILoader;

public class LoaderThread extends Thread {
	
	private Object lock;
	private Object lock2;
	private boolean myInterrupt;
	private Vector vector;

	public LoaderThread(int priority, Object lock, Vector v, Object lock2, int i) {
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
				if(checkInterrupted()) continue;
				int len = vector.size();
				if(len == 0) {
					synchronized(lock2) {
						lock2.notifyAll();
					}
				}
				while((len = vector.size()) > 0) {
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
						if(!e.getClass().equals(RuntimeException.class)) {
							throw e;
						}
						String msg = e.getMessage();
						if(msg != null && (msg.endsWith("interrupt") || msg.endsWith("interrupted"))) {
							break;
						} else {
							throw e;
						}
					}
					if(checkInterrupted()) break;
				}
				synchronized(lock2) {
					lock2.notifyAll();
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

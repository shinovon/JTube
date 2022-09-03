import models.ILoader;

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

public class LoaderThread extends Thread {
	
	private boolean interruptSuccess;
	private App app;
	private Object lock1;
	private Object lock2;
	private boolean interrupt;
	private int i;

	public LoaderThread(int priority, int i, App app) {
		super("Loader-"+i);
		this.i = i;
		this.app = app;
		this.lock1 = app.loadLock1;
		this.lock2 = app.loadLock2;
		setPriority(priority);
	}
	
	public void run() {
		try {
			while(true) {
				synchronized(lock1) {
					lock1.wait();
				}
				checkInterrupted();
				ILoader l = null;
				while(app.loadTasks[0] != null) {
					synchronized(lock2) {
						l = app.loadTasks[0];
						if(l == null) break;
						System.arraycopy(app.loadTasks, 1, app.loadTasks, 0, app.loadTasks.length - 1);
						app.loadTasks[app.loadTasks.length - 1] = null;
						app.loadTasksIdx--;
					}
					if(checkInterrupted()) break;
					try {
						System.out.println("Loader-" + i + ": LOADING " + l + "...");
						l.load();
					} catch (Exception e) {
						e.printStackTrace();
					}
					Thread.sleep(1);
				}
				System.out.println("Loader-" + i + ": DONE");
			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				App.error(this, Errors.LoaderThread_run, e);
			} catch (Error e2) {
				e.printStackTrace();
			}
		}
	}

	boolean checkInterrupted() {
		if(interrupt) {
			interrupt = false;
			synchronized(lock2) {
				lock2.notifyAll();
			}
			System.out.println("Loader-" + i + ": INTERRUPT SUCCESS");
			interruptSuccess = true;
			return true;
		}
		return false;
	}

	public void doInterrupt() {
		System.out.println("Loader-" + i + ": INTERRUPT REQUEST");
		interruptSuccess = false;
		interrupt = true;
	}
	
	public boolean isInterruptSuccess() {
		if(interruptSuccess) {
			interruptSuccess = false;
			return true;
		}
		return false;
	}

}

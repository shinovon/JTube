package jtube;
import jtube.models.ILoader;

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
	private Object lock1;
	private Object lock2;
	private boolean interrupt;

	public LoaderThread(int priority, int i) {
		super("Loader-"+i);
		this.lock1 = Loader.lock1;
		this.lock2 = Loader.lock2;
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
				while(Loader.tasks[0] != null) {
					if(checkInterrupted()) break;
					synchronized(Loader.tasks) {
						l = Loader.tasks[0];
						if(l == null) break;
						System.arraycopy(Loader.tasks, 1, Loader.tasks, 0, Loader.tasks.length - 1);
						Loader.tasks[Loader.tasks.length - 1] = null;
						Loader.tasksIdx--;
					}
					if(l == Loader.emptyTask) continue;
					try {
						l.load();
					} catch (Exception e) {
					}
					Thread.sleep(1);
				}
				synchronized(lock2) {
					lock2.notify();
				}
			}
		} catch (Exception e) {
			try {
				App.error(this, Errors.LoaderThread_run, e);
			} catch (Error e2) {
			}
		}
	}

	public boolean checkInterrupted() {
		if(interrupt) {
			interrupt = false;
			interruptSuccess = true;
			return true;
		}
		return false;
	}

	public void doInterrupt() {
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

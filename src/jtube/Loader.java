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
	public static final ILoader emptyTask = new ILoader() {
		public void load() {}
	};
	
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
		}
	}
		
	public static void cancel(ILoader v) {
		try {
			for(int i = 0; i < tasks.length; i++) {
				if(tasks[i] == v) {
					tasks[i] = emptyTask;
					break;
				}
			}
		} catch (Exception e) {
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
	
	public static void synchronize() {
		try {
			start();
			synchronized(lock2) {
				lock2.wait();
			}
			if(t0 != null) {
				start();
				synchronized(t0.threadLock) {
					t0.threadLock.wait();
				}
			}
			if(t1 != null) {
				start();
				synchronized(t1.threadLock) {
					t1.threadLock.wait();
				}
			}
			if(t2 != null) {
				start();
				synchronized(t2.threadLock) {
					t2.threadLock.wait();
				}
			}
		} catch (Exception e) {
		}
	}
}

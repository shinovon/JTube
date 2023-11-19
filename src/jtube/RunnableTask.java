/*
Copyright (c) 2023 Arman Jussupgaliyev

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

import jtube.ui.AppUI;

public class RunnableTask implements Runnable {

	public static final int ID = 1;
	public static final int SEARCH = 2;
	public static final int REFRESH = 3;
	public static final int SWITCH = 4;
	public static final int WATCH = 5;
	public static final int MAIN = 6;
	public static final int SUBS = 7;
//	public static final int LIB = 8;
	
	int type;
	String arg;
	
	public RunnableTask(int i) {
		this.type = i;
	}
	
	public RunnableTask(String s, int i) {
		this.arg = s;
		this.type = i;
	}

	public void run() {
		switch(type) {
		case ID:
			try {
				App.openURL(arg);
			} catch (IllegalArgumentException e) {
				AppUI.inst.openVideo(arg);
			}
			break;
		case SEARCH:
			AppUI.inst.search(arg);
			break;
		case REFRESH:
			AppUI.inst.refresh();
			break;
		case SWITCH:
			AppUI.inst.switchMain();
			break;
		case WATCH:
			App.watch(arg);
			break;
		case MAIN:
			AppUI.inst.loadMain();
			break;
		case SUBS:
			AppUI.inst.loadSubs();
			break;
//		case LIB:
//			AppUI.inst.loadLib();
//			break;
		}
	}

}

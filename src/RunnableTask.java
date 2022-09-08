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
import ui.AppUI;

public class RunnableTask implements Runnable {

	public static final int ID = 1;
	public static final int SEARCH = 2;
	public static final int REFRESH = 3;
	public static final int SWITCH = 4;
	
	String s;
	int i;
	
	public RunnableTask(int i) {
		this.i = i;
	}
	
	public RunnableTask(String s, int i) {
		this.s = s;
		this.i = i;
	}

	public void run() {
		switch(i) {
		case 1:
			try {
				App.openURL(s);
			} catch (IllegalArgumentException e) {
				AppUI.inst.openVideo(s);
			}
			break;
		case 2:
			AppUI.inst.search(s);
			break;
		case 3:
			AppUI.inst.refresh();
			break;
		case 4:
			AppUI.inst.switchMain();
			break;
		}
	}

}

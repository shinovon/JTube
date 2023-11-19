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
package jtube.ui;

import jtube.ui.screens.NavigationScreen;

public class SearchSuggestionsThread extends Thread {
	
	private int i;
	private boolean b;

	public final void run() {
		try {
			while(true) {
				if(b) {
					if(i > 0) i--;
					else {
						UIScreen screen = AppUI.inst.current;
						if(screen instanceof NavigationScreen) {
							((NavigationScreen) screen).loadSuggestions();
						}
						b = false;
					}
				}
				Thread.sleep(500L);
				Thread.yield();
			}
		} catch (InterruptedException e) {
		}
	}
	
	public void schedule() {
		b = true;
		i = 1;
	}

}

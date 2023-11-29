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
package jtube.ui.items;

import javax.microedition.lcdui.Canvas;

import jtube.ui.UIItem;
import jtube.ui.screens.NavigationScreen;

public abstract class AbstractButton extends UIItem {
	
	protected boolean hover;

	protected abstract void action();
	
	protected void press(int x, int y) {
		hover = true;
	}
	
	protected void release(int x, int y) {
		hover = false;
	}
	
	protected void tap(int x, int y, int time) {
		hover = false;
		if(contextActions() != null && screen instanceof NavigationScreen && time >= 500 && time <= 5000) {
			((NavigationScreen) screen).openItemMenu(this);
			return;
		}
		if(time <= 200 && time >= 5) {
			action();
		}
	}

	protected boolean keyPress(int i) {
		if(i == -5 || i == Canvas.KEY_NUM5) {
			hover = true;
			action();
			return true;
		}
		return false;
	}

	protected boolean keyRelease(int i) {
		if(i == -5 || i == Canvas.KEY_NUM5) {
			hover = false;
			return true;
		}
		return false;
	}
	
	public void defocus() {
		super.defocus();
		hover = false;
	}

}

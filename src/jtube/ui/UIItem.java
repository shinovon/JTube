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
package jtube.ui;

import javax.microedition.lcdui.Graphics;

public abstract class UIItem {
	
	protected static AppUI ui = AppUI.inst;
	
	protected UIScreen screen;
	protected boolean inFocus;
	protected int y;
	protected int index;
	public boolean hidden = true;

	public UIItem(UIScreen screen) {
		this.screen = screen;
	}

	public UIItem() {
	}
	
	public abstract void paint(Graphics g, int w, int x, int y, int sc);

	public abstract int getHeight();

	protected abstract void layout(int w);

	protected void press(int x, int y) {}
	protected void release(int x, int y) {}
	protected void tap(int x, int y, int time) {}
	protected boolean keyPress(int i) { return false; }
	protected boolean keyRelease(int i) { return false; }
	
	public void focus() {
		inFocus = true;
	}
	
	public void defocus() {
		inFocus = false;
	}
	
	protected final void repaint() {
		if(screen != null) screen.repaint(this);
	}
	
	public void relayout() {
		if(screen == null) return;
		screen.relayout();
	}
	
	public boolean isSeenOnScreen() {
		if(screen == null) return false;
		return screen.isItemSeenOnScreen(this);
	}
	
	protected void onHide() {
		hidden = true;
	}
	
	protected void onShow() {
		hidden = false;
	}
	
	public int getOKLabel() {
		return -1;
	}

	public boolean canBeFocused() {
		return true;
	}
	
	public int[] contextActions() {
		return null;
	}
	
	public void contextAction(int i) {}

}

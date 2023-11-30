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

public abstract class UIScreen {
	
	protected static AppUI ui;
	
	protected String label;
	
	protected float scroll;
	protected int width;
	protected int height;
	
	protected UIScreen parent;
	
	/**
	 * Locks any input
	 */
	public boolean busy;

	protected UIScreen(String label) {
		this.label = label;
	}
	
	protected abstract void paint(Graphics g, int w, int h);
	
	public void repaint() {
		ui.repaint();
	}
	
	public void repaint(UIItem item) {
		ui.repaint();
	}

	protected void press(int x, int y) {}
	protected void release(int x, int y) {}
	protected void tap(int x, int y, int time) {}
	protected boolean keyPress(int i) { return false; }
	protected boolean keyRelease(int i) { return false; }
	protected void keyRepeat(int i) {}
	
	/** -1: назад */
	public void screenCommand(int i) {}
	
	/**
	 * @return true если успешно, false если уже некуда скроллить
	 */
	protected boolean scroll(int units) {
		return false;
	}

	public boolean hasScrollBar() {
		return false;
	}

	public void setScrollBarY(int y) {
	}
	
	protected void relayout() {
	}

	protected boolean isItemSeenOnScreen(UIItem i) {
		return true;
	}

	protected void show() {}

	public void hide() {}

}

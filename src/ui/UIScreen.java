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
package ui;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Graphics;

public abstract class UIScreen {
	
	static {
		if(AppUI.inst == null) {
			System.out.println("UIScreen class initialized before AppUI?!?!");
		}
	}
	
	protected static AppUI ui = AppUI.inst;
	
	private String label;
	protected UIScreen parent;
	
	protected float scroll;
	protected int width;
	protected int height;

	protected UIScreen(String label, UIScreen parent) {
		this.label = label;
		this.parent = parent;
	}
	
	protected abstract void paint(Graphics g, int w, int h);
	
	public String getTitle() {
		return label;
	}
	
	public void setTitle(String s) {
		this.label = s;
		ui.updateScreenTitle(this);
	}
	
	public UIScreen getParent() {
		return parent;
	}
	
	public boolean backCommand() {
		return parent != null;
	}
	
	public void repaint() {
		ui.repaint();
	}
	
	public void repaint(UIItem item) {
		ui.repaint();
	}

	public int getHeight() {
		return height;
	}


	protected void press(int x, int y) {}
	protected void release(int x, int y) {}
	protected void tap(int x, int y, int time) {}
	protected void keyPress(int i) {}
	protected void keyRelease(int i) {}
	protected void keyRepeat(int i) {}
	
	/** -1: назад */
	public void screenCommand(int i) {}
	
	/**
	 * @return true если успешно, false если уже некуда скроллить
	 */
	protected boolean scroll(int units) {
		return false;
	}

	public int getWidth() {
		return width;
	}

	public boolean hasScrollBar() {
		return false;
	}

	public void setScrollBarY(int y) {
	}

	protected final void addCommand(Command c) {
		ui.addCommand(c);
	}
	
	protected void relayout() {
	}

	protected boolean isItemSeenOnScreen(UIItem i) {
		return true;
	}

	protected void show() {}

	protected void hide() {}
	
	public boolean blockScrolling() {
		return false;
	}

}

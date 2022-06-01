package ui;

import javax.microedition.lcdui.Graphics;

public abstract class UIItem {
	
	private UIScreen screen;
	private boolean inFocus;
	private int y;

	public UIItem(UIScreen screen) {
		this.screen = screen;
	}

	public UIItem() {
	}
	
	public abstract void paint(Graphics g, int w, int x, int y, int sc);

	public abstract int getHeight();

	protected abstract void layout(int w);
	
	protected void tap(int x, int y, int time) {}
	public void keyPress(int i) {}
	public void keyRelease(int i) {}
	
	public void focus() {
		inFocus = true;
	}
	
	public void defocus() {
		inFocus = false;
	}
	
	public boolean isInFocus() {
		return inFocus;
	}
	
	public void setScreen(UIScreen screen) {
		this.screen = screen;
	}
	
	public UIScreen getScreen() {
		return screen;
	}
	
	public final int getWidth() {
		return screen.getWidth();
	}
	
	protected final void repaint() {
		if(screen != null)
		screen.repaint(this);
	}

	public void setY(int y) {
		this.y = y;
	}
	
	public int getY() {
		return y;
	}

}

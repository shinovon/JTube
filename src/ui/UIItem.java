package ui;

import javax.microedition.lcdui.Graphics;

public abstract class UIItem {
	
	protected static AppUI ui = AppUI.inst;
	
	private UIScreen screen;
	protected boolean inFocus;
	private int y;

	protected int index;

	boolean hidden = true;

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
	protected void keyPress(int i) {}
	protected void keyRelease(int i) {}
	
	public void focus() {
		inFocus = true;
	}
	
	public void defocus() {
		inFocus = false;
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

	public void setIndex(int i) {
		index = i;
	}
	
	public int getIndex() {
		return index;
	}
	
	public void relayout() {
		if(screen == null) return;
		screen.relayout();
	}
	
	public boolean isSeenOnScreen() {
		if(screen == null) return false;
		return screen.isItemSeenOnScreen(this);
	}
	
	protected boolean isHidden() {
		return hidden;
	}
	
	protected void onHide() {
		hidden = true;
	}
	
	protected void onShow() {
		hidden = false;
	}

}
